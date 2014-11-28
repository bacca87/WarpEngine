package com.marcobaccarani.warp.debug;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldFilter;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldListener;
import com.marcobaccarani.warp.gui.GUIManager;

public class WarpConsoleGUI extends InputAdapter implements TextFieldFilter, TextFieldListener {
	private GUIManager gui;
	
	private boolean show;	
	private boolean enabled = true;
	
	private Table table; 
	private TextArea textArea;
	private TextField textField;
	
	private ArrayList<String> cmdHistory = new ArrayList<String>();
	private int cmdHistoryIndex = 0;
	
	private int toggleConsoleKey = Input.Keys.BACKSLASH;
	private String prompt = "] ";
	
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
		
		Skin consoleSkin = new Skin(Gdx.files.internal("com/marcobaccarani/warp/assets/skins/warpconsole.json"), new TextureAtlas(Gdx.files.internal("com/marcobaccarani/warp/assets/skins/warpconsole.atlas")));
		
		textArea = new TextArea("", consoleSkin);
		textArea.setDisabled(true);
				
		textField = new TextField("", consoleSkin);		
		textField.setTextFieldFilter(this);
		textField.setTextFieldListener(this);
		
		table = new Table();
		table.setHeight(Gdx.graphics.getHeight() * 0.5f);
		table.setWidth(Gdx.graphics.getWidth());
		table.setPosition(0, Gdx.graphics.getHeight());
		table.add(textArea).expand().fill();
		table.row();
		table.add(textField).fillX();
		
		gui.addActor(table);
		
		WarpConsole.out = new PrintStream(output);
	}
	
	private void toggle() {
		show = !show;
		
		if(show) {
			gui.setFocus(textField);
		}
		else {
			gui.unfocusAll();
		}	
	}
	
	public void setInput(InputMultiplexer input) {
		gui.attachInputs(input);
		input.addProcessor(this);
	}
	
	private void update(float delta) {
		float posY = 0;
		
		if(show && table.getY() > Gdx.graphics.getHeight() / 2) {
			posY = table.getY() - delta * 1000;
			if(posY >= Gdx.graphics.getHeight() / 2)
				table.setPosition(table.getX(), posY);
			else
				table.setPosition(table.getX(), Gdx.graphics.getHeight() / 2);
		}
		
		if(!show && table.getY() < Gdx.graphics.getHeight()) {
			posY = table.getY() + delta * 1000;
			if(posY <= Gdx.graphics.getHeight())
				table.setPosition(table.getX(), posY);
			else
				table.setPosition(table.getX(), Gdx.graphics.getHeight());
		}
	}
	
	public void render() {
		update(Gdx.graphics.getDeltaTime());
		gui.render();
	}
	
	@Override
	public boolean keyTyped (char character) {
		if(enabled && Gdx.input.isKeyJustPressed(toggleConsoleKey))
			toggle();
		
		return false;
	}

	@Override
	public boolean acceptChar(TextField textField, char c) {
		if(Gdx.input.isKeyJustPressed(toggleConsoleKey))
		{
			toggle();
			return false;
		}
		
		return true;
	}

	@Override
	public void keyTyped(TextField textField, char c) {
		if(Gdx.input.isKeyJustPressed(Input.Keys.ENTER) && textField.getText().length() > 0) {
			if(!cmdHistory.contains(textField.getText().trim()))
				cmdHistory.add(0, textField.getText().trim());
			
			WarpConsole.out.print(prompt + textField.getText() + "\n");
			console.executeCommand(textField.getText().trim());
			textField.setText("");
		}
		
		if(Gdx.input.isKeyPressed(Input.Keys.UP)) {
			cmdHistoryIndex++;
			if(cmdHistoryIndex >= cmdHistory.size())
				cmdHistoryIndex = 0;
			textField.setText(cmdHistory.get(cmdHistoryIndex));
			textField.setCursorPosition(textField.getText().length());
		} else if(Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
			cmdHistoryIndex--;
			if(cmdHistoryIndex < 0)
				cmdHistoryIndex = cmdHistory.size() - 1;
			
			textField.setText(cmdHistory.get(cmdHistoryIndex));
			textField.setCursorPosition(textField.getText().length());
		}
		else
			cmdHistoryIndex = -1;
		
//		if(Gdx.input.isKeyPressed(Input.Keys.PAGE_UP)) {
//			textArea.setCursorPosition(textArea.getCursorLine() - textArea.getLinesShowing());			
//		} else if(Gdx.input.isKeyPressed(Input.Keys.PAGE_DOWN)) {
//			textArea.setCursorPosition(textArea.getCursorLine() + textArea.getLinesShowing());
//		}
	}
}
