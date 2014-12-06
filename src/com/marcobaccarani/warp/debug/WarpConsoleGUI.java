package com.marcobaccarani.warp.debug;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.WarpTextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldFilter;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.marcobaccarani.warp.WarpEngine;

public class WarpConsoleGUI implements TextFieldFilter, Disposable {
	private WarpConsole console = new WarpConsole();
	private Stage stage;
	
	private boolean show;
	private float screenRatio = 0.5f;
	
	private Table table; 
	private TextArea textArea;
	private WarpTextField textField;
	
	private ArrayList<String> cmdHistory = new ArrayList<String>();
	private int cmdHistoryIndex;
	
	private int toggleConsoleKey = Input.Keys.BACKSLASH;
	private char toggleConsoleChar = '\\';
	private String prompt = "] ";	
	private float keyRepeatInitialTime = 0.4f;
	private float keyRepeatTime = 0.08f;
	private int speed = 2000;
	private int cmdMaxLength = 1024;
	private int unusedLines = 0;
	
	private KeyRepeatTask keyRepeatTask = new KeyRepeatTask(); 
	
	private final OutputStream output = new OutputStream() {
		@Override
		public void write(final int b) throws IOException {
			textArea.setText(textArea.getText() + String.valueOf(b));
			textArea.setCursorPosition(textArea.getText().endsWith("\n") ? textArea.getText().length() - 1 : textArea.getText().length());
		}

		@Override
		public void write(byte[] b, int off, int len) throws IOException {
			textArea.setText(textArea.getText() + new String(b, off, len));
			textArea.setCursorPosition(textArea.getText().endsWith("\n") ? textArea.getText().length() - 1 : textArea.getText().length());
		}

		@Override
		public void write(byte[] b) throws IOException {
			textArea.setText(textArea.getText() + new String(b));
			textArea.setCursorPosition(textArea.getText().endsWith("\n") ? textArea.getText().length() - 1 : textArea.getText().length());
		}
	};
	
	public WarpConsoleGUI() {
		show = false;
		stage = new Stage();
		stage.setViewport(new FitViewport(WarpEngine.VIRTUAL_WIDTH, WarpEngine.VIRTUAL_HEIGHT));
		
		Skin consoleSkin = new Skin(Gdx.files.internal("data/skins/warpconsole.json"), new TextureAtlas(Gdx.files.internal("data/skins/warpconsole.atlas")));
		
		textArea = new TextArea("", consoleSkin, "textarea");
		textArea.setDisabled(true);
		
		textField = new WarpTextField("", consoleSkin, "textfield");
		textField.setTextFieldFilter(this);
		textField.setMaxLength(cmdMaxLength);
				
		Label label = new Label(prompt, consoleSkin);
		
		table = new Table();
		table.setFillParent(true);
		table.add(textArea).prefSize(WarpEngine.VIRTUAL_WIDTH, WarpEngine.VIRTUAL_HEIGHT * screenRatio).colspan(2).fill();
		table.row();
		table.add(label).fillY();
		table.add(textField).prefWidth(WarpEngine.VIRTUAL_WIDTH);
		table.top();
		table.setPosition(0, WarpEngine.VIRTUAL_HEIGHT);
		
		stage.addActor(table);
		
		WarpConsole.out = new PrintStream(output);
		
		// insert newline to give the effect of sliding from the bottom upwards
		stage.draw();
		unusedLines = textArea.getLinesShowing();
		for(int i = 0; i < unusedLines; i++)
			WarpConsole.out.print("\n");
		
		console.addCommand("quit", new WarpCommand() {
			@Override
			public void executeCommand(String[] args) {
				Gdx.app.exit();				
			}
		});
	}
	
	private void toggle() {
		show = !show;
		
		if(show)
			stage.setKeyboardFocus(textField);
		else
			stage.unfocusAll();
	}
	
	public void setInput(InputMultiplexer input) {
		input.addProcessor(0, stage);
	}
	
	private void update(float delta) {
		float posY = 0;
		
		handleInputs();
		
		if(show && table.getY() > 0) {
			posY = table.getY() - delta * speed;
			if(posY >= 0)
				table.setPosition(table.getX(), posY);
			else
				table.setPosition(table.getX(), 0);
		}
		
		if(!show && table.getY() < WarpEngine.VIRTUAL_HEIGHT) {
			posY = table.getY() + delta * speed;
			if(posY <= WarpEngine.VIRTUAL_HEIGHT)
				table.setPosition(table.getX(), posY);
			else
				table.setPosition(table.getX(), WarpEngine.VIRTUAL_HEIGHT);
		}
	}
		
	private void handleInputs() {
		int keypressed = 0;
		int lastTextLenght = 0;
		
		if(Gdx.input.isKeyJustPressed(toggleConsoleKey)) {			
			toggle();
		}
		
		if(!show)
			return;
		
		if(Gdx.input.isKeyJustPressed(Input.Keys.ENTER) && textField.getText().length() > 0) {
			if(cmdHistory.size() == 0 || !textField.getText().trim().equals(cmdHistory.get(cmdHistory.size() - 1)))
				cmdHistory.add(textField.getText().trim());
			
			cmdHistoryIndex = cmdHistory.size();
			
			WarpConsole.out.print(prompt + textField.getText() + "\n");			
			lastTextLenght = textArea.getText().length();
			
			console.executeCommand(textField.getText().trim());
			
			if(lastTextLenght != textArea.getText().length())
				WarpConsole.out.print("\n");
			
			textField.setText("");
		}
		
		if(Gdx.input.isKeyPressed(Input.Keys.UP))
			keypressed = Input.Keys.UP;
		if(Gdx.input.isKeyPressed(Input.Keys.DOWN))
			keypressed = keypressed == 0 ? Input.Keys.DOWN : 0;
		if(Gdx.input.isKeyPressed(Input.Keys.PAGE_UP))
			keypressed = keypressed == 0 ? Input.Keys.PAGE_UP : 0;
		if(Gdx.input.isKeyPressed(Input.Keys.PAGE_DOWN))
			keypressed = keypressed == 0 ? Input.Keys.PAGE_DOWN : 0;
		
		if(keypressed != 0 && (!keyRepeatTask.isScheduled() || keyRepeatTask.keycode != keypressed)) {
			keyDown(keypressed);
			keyRepeatTask.keycode = keypressed;
			keyRepeatTask.cancel();
			Timer.schedule(keyRepeatTask, keyRepeatInitialTime, keyRepeatTime);
		}
		else if(keypressed == 0 && keyRepeatTask.isScheduled())		
			keyRepeatTask.cancel();
	}
	
	private void keyDown(int keycode) {
		int line;
		
		switch(keycode)	{
		case Input.Keys.UP:
			cmdHistoryIndex--;
			
			if(cmdHistoryIndex < 0) {
				cmdHistoryIndex = 0;
			}
			else {
				textField.setText(cmdHistory.get(cmdHistoryIndex));
				textField.setCursorPosition(textField.getText().length());
			}
			break;
			
		case Input.Keys.DOWN:
			cmdHistoryIndex++;
			
			if(cmdHistoryIndex > cmdHistory.size() - 1) {
				cmdHistoryIndex = cmdHistory.size();
				textField.setText("");
			}
			else {
				textField.setText(cmdHistory.get(cmdHistoryIndex));
				textField.setCursorPosition(textField.getText().length());
			}
			break;
			
		case Input.Keys.PAGE_UP:
			line = textArea.getCursorLine() == textArea.getFirstLineShowing() ?
				   textArea.getCursorLine() - textArea.getLinesShowing() :
				   textArea.getCursorLine() - textArea.getLinesShowing() * 2 + 1;
					   
			textArea.moveCursorLine(line < unusedLines ? unusedLines : line);
			break;
			
		case Input.Keys.PAGE_DOWN:
			line = textArea.getCursorLine() == textArea.getFirstLineShowing() ?
				   textArea.getCursorLine() + textArea.getLinesShowing() * 2 - 1:
				   textArea.getCursorLine() + textArea.getLinesShowing();
			
			textArea.moveCursorLine(line >= textArea.getLines() ? textArea.getLines() - 2 : line);
			break;
		}
	}
	
	public void render() {
		stage.act(Gdx.graphics.getDeltaTime());
		update(Gdx.graphics.getDeltaTime());
		stage.draw();
	}
	
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);		
	}
	
	@Override
	public boolean acceptChar(TextField textField, char c) {
		if(c == toggleConsoleChar)
			return false;
		
		return true;
	}
		
	@Override
	public void dispose() {
		stage.dispose();
	}
	
	class KeyRepeatTask extends Task {
		int keycode;

		public void run () {
			keyDown(keycode);
		}
	}	
}
