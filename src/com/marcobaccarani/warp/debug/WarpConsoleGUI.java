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
import com.badlogic.gdx.utils.TimeUtils;
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
	
	private long UP_DelayStartTime = 0;
	private long DOWN_DelayStartTime = 0;
	private long PGUP_DelayStartTime = 0;
	private long PGDOWN_DelayStartTime = 0;
	private long inputRepeatStartTime = 0;
	private long inputDelay = 500;
	private long inputRepeatTime = 50;
	
	private int speed = 2000;
	
	private WarpConsole console = new WarpConsole();
	
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
		table.setFillParent(true);
		table.add(textArea).prefSize(WarpEngine.VIRTUAL_WIDTH, WarpEngine.VIRTUAL_HEIGHT * screenRatio).colspan(2).fill();
		table.row();
		table.add(label);
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
		int line;
		
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
				
		if(TimeUtils.millis() - inputRepeatStartTime < inputRepeatTime) 
			return;
		else
			inputRepeatStartTime = TimeUtils.millis();
		
		if(Gdx.input.isKeyPressed(Input.Keys.UP)) {
			if(UP_DelayStartTime != 0 && TimeUtils.millis() - UP_DelayStartTime < inputDelay)
				return;
			
			cmdHistoryIndex--;
			
			if(cmdHistoryIndex < 0) {
				cmdHistoryIndex = 0;
			}
			else {
				textField.setText(cmdHistory.get(cmdHistoryIndex));
				textField.setCursorPosition(textField.getText().length());
			}
			
			if(UP_DelayStartTime == 0)
				UP_DelayStartTime = TimeUtils.millis();
		}
		else
			UP_DelayStartTime = 0;
			
		if(Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
			if(DOWN_DelayStartTime != 0 && TimeUtils.millis() - DOWN_DelayStartTime < inputDelay)
				return;
			
			cmdHistoryIndex++;
			
			if(cmdHistoryIndex > cmdHistory.size() - 1) {
				cmdHistoryIndex = cmdHistory.size();
				textField.setText("");
			}
			else {
				textField.setText(cmdHistory.get(cmdHistoryIndex));
				textField.setCursorPosition(textField.getText().length());
			}
			
			if(DOWN_DelayStartTime == 0)
				DOWN_DelayStartTime = TimeUtils.millis();
		}
		else 
			DOWN_DelayStartTime = 0;
		
		if(Gdx.input.isKeyPressed(Input.Keys.PAGE_UP)) {
			if(PGUP_DelayStartTime != 0 && TimeUtils.millis() - PGUP_DelayStartTime < inputDelay)
				return;
			
			line = textArea.getCursorLine() == textArea.getFirstLineShowing() ?
				   textArea.getCursorLine() - textArea.getLinesShowing() :
				   textArea.getCursorLine() - textArea.getLinesShowing() * 2 + 1;
				   
			textArea.moveCursorLine(line < 0 ? 0 : line);
			
			if(PGUP_DelayStartTime == 0)
				PGUP_DelayStartTime = TimeUtils.millis();
		}
		else 
			PGUP_DelayStartTime = 0;
		
		if(Gdx.input.isKeyPressed(Input.Keys.PAGE_DOWN)) {
			if(PGDOWN_DelayStartTime != 0 && TimeUtils.millis() - PGDOWN_DelayStartTime < inputDelay)
				return;
			
			line = textArea.getCursorLine() == textArea.getFirstLineShowing() ?
				   textArea.getCursorLine() + textArea.getLinesShowing() * 2 - 1:
				   textArea.getCursorLine() + textArea.getLinesShowing();
				   
			textArea.moveCursorLine(line >= textArea.getLines() ? textArea.getLines() - 1 : line);
			
			if(PGDOWN_DelayStartTime == 0)
				PGDOWN_DelayStartTime = TimeUtils.millis();
		}
		else
			PGDOWN_DelayStartTime = 0;
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
}
