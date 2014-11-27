package com.marcobaccarani.warp.debug;

import com.badlogic.gdx.Gdx;
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
	private int toggleConsoleKey = '\\';
	
	private WarpConsole console = new WarpConsole();
		
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
		if(enabled && character == toggleConsoleKey)
			toggle();
		
		return false;
	}

	@Override
	public boolean acceptChar(TextField textField, char c) {
		if(c == toggleConsoleKey)
		{
			toggle();
			return false;
		}
		
		return true;
	}

	@Override
	public void keyTyped(TextField textField, char c) {		
		if(c == '\n' || c == '\r') {
			textArea.setText(textArea.getText() + " > " + textField.getText() + "\n");
			textArea.setText(textArea.getText() + console.executeCommand(textField.getText()).output + "\n");
			textField.setText("");
		}
	}
}
