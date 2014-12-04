package com.marcobaccarani.warp.debug;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldFilter;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.marcobaccarani.warp.WarpEngine;
import com.marcobaccarani.warp.gui.GUIManager;

public class WarpConsoleGUI implements TextFieldFilter {
	private GUIManager gui;
	
	private boolean show;
	private float screenRatio = 0.5f;
	
	private Table table; 
	private TextArea textArea;
	private TextField textField;
	
	private ArrayList<String> cmdHistory = new ArrayList<String>();
	private int cmdHistoryIndex;
	
	private int toggleConsoleKey = Input.Keys.BACKSLASH;
	private char toggleConsoleChar = '\\';
	private String prompt = "] ";
	
	private float keyRepeatInitialTime = 0.4f;
	private float keyRepeatTime = 0.08f;
	
	private int speed = 2000;
	
	private WarpConsole console = new WarpConsole();
	
	private KeyRepeatTask keyRepeatTask = new KeyRepeatTask(); 
	
	private final OutputStream output = new OutputStream() {
		@Override
		public void write(final int b) throws IOException {
			textArea.setText(textArea.getText() + String.valueOf(b));
			textArea.setCursorPosition(textArea.getText().length());
		}

		@Override
		public void write(byte[] b, int off, int len) throws IOException {
			textArea.setText(textArea.getText() + new String(b, off, len));
			textArea.setCursorPosition(textArea.getText().length());
		}

		@Override
		public void write(byte[] b) throws IOException {
			textArea.setText(textArea.getText() + new String(b));
			textArea.setCursorPosition(textArea.getText().length());
		}
	};
	
	public WarpConsoleGUI() {
		show = false;
		gui = new GUIManager();
		gui.setViewport(new FitViewport(WarpEngine.VIRTUAL_WIDTH, WarpEngine.VIRTUAL_HEIGHT));
		
		Skin consoleSkin = new Skin(Gdx.files.internal("com/marcobaccarani/warp/assets/skins/warpconsole.json"), new TextureAtlas(Gdx.files.internal("com/marcobaccarani/warp/assets/skins/warpconsole.atlas")));
		
		textArea = new TextArea("", consoleSkin);
		textArea.setDisabled(true);
		
		textField = new TextField("", consoleSkin);
		textField.setTextFieldFilter(this);
		
		Label label = new Label(prompt, consoleSkin);
		
		table = new Table();
		//table.setDebug(true);
		table.setFillParent(true);
		table.add(textArea).prefSize(WarpEngine.VIRTUAL_WIDTH, WarpEngine.VIRTUAL_HEIGHT * screenRatio).colspan(2).fill();
		table.row();
		table.add(label).fillY();
		table.add(textField).prefWidth(WarpEngine.VIRTUAL_WIDTH);
		table.top();
		table.setPosition(0, WarpEngine.VIRTUAL_HEIGHT);
		
		gui.addActor(table);
		
		WarpConsole.out = new PrintStream(output);
	}
	
	private void toggle() {
		show = !show;
		
		if(show)
			gui.setFocus(textField);
		else
			gui.unfocusAll();
	}
	
	public void setInput(InputMultiplexer input) {
		gui.attachInputs(input);
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
			console.executeCommand(textField.getText().trim());
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
					   
			textArea.moveCursorLine(line < 0 ? 0 : line);
			break;
			
		case Input.Keys.PAGE_DOWN:
			line = textArea.getCursorLine() == textArea.getFirstLineShowing() ?
				   textArea.getCursorLine() + textArea.getLinesShowing() * 2 - 1:
				   textArea.getCursorLine() + textArea.getLinesShowing();
					   
			textArea.moveCursorLine(line >= textArea.getLines() ? textArea.getLines() - 1 : line);
			break;
		}
	}
	
	public void render() {
		update(Gdx.graphics.getDeltaTime());		
		gui.render();
	}
	
	public void resize(int width, int height) {
		gui.resize(width, height);		
	}
	
	@Override
	public boolean acceptChar(TextField textField, char c) {
		if(c == toggleConsoleChar)
			return false;
		
		return true;
	}
		
	class KeyRepeatTask extends Task {
		int keycode;

		public void run () {
			keyDown(keycode);
		}
	}
}
