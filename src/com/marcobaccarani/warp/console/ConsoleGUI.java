
package com.marcobaccarani.warp.console;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldFilter;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.marcobaccarani.warp.EngineCommands;
import com.marcobaccarani.warp.WarpEngine;

public class ConsoleGUI implements TextFieldFilter, Disposable {
	private Stage stage;

	private boolean show;
	private float screenRatio = 0.5f;
	private int screenWidth = 0;
	private int screenHeight = 0;

	private Table table;
	private TextArea textArea;
	private TextField textField;

	private ArrayList<String> cmdHistory = new ArrayList<String>();
	private int cmdHistoryIndex;

	private int toggleConsoleKey = Input.Keys.BACKSLASH;
	private char toggleConsoleChar = '\\';
	private String prompt = "] ";
	private int tabSpaces = 4;
	private float keyRepeatInitialTime = 0.4f;
	private float keyRepeatTime = 0.08f;
	private int speed = 2000;
	private int cmdMaxLength = 1024;
	private int unusedLines = 0;

	private KeyRepeatTask keyRepeatTask = new KeyRepeatTask();

	private final OutputStream output = new OutputStream() {
		@Override
		public void write (final int b) throws IOException {
			textArea.appendText(String.valueOf(b));
			setCursorPosition();
		}

		@Override
		public void write (byte[] b, int off, int len) throws IOException {
			textArea.appendText(formatInputString(new String(b, off, len)));
			setCursorPosition();
		}

		@Override
		public void write (byte[] b) throws IOException {
			textArea.appendText(formatInputString(new String(b)));
			setCursorPosition();
		}

		private String formatInputString (String string) {
			string = string.replace("\t", String.format("%" + tabSpaces + "s", " "));
			string = string.replace("\r\n", "\n");
			return string;
		}

		private void setCursorPosition () {
			textArea.setCursorPosition(textArea.getText().endsWith("\n") ? textArea.getText().length() - 1 : textArea.getText()
				.length());
		}
	};

	private CommandListener clearListener = new CommandListener() {
		@Override
		public void execute (String[] args) {
			if (textArea != null) {
				textArea.moveCursorLine(0);
				textArea.setText("");
				insertNewlines();
			}
		}
	};
	
	public ConsoleGUI () {
		show = false;
		stage = new Stage(new ScreenViewport());
		WarpEngine.input.addInputProcessor(stage);

		Skin consoleSkin = new Skin(Gdx.files.internal("data/skins/warpconsole.json"), new TextureAtlas(
			Gdx.files.internal("data/skins/warpconsole.atlas")));

		textArea = new TextArea("", consoleSkin, "textarea");
		textArea.setDisabled(true);

		textField = new TextField("", consoleSkin, "textfield");
		textField.setTextFieldFilter(this);
		textField.setMaxLength(cmdMaxLength);

		Label label = new Label(prompt, consoleSkin);

		table = new Table();
		table.setFillParent(true);
		table.add(textArea).colspan(2).expandX().height(Gdx.graphics.getHeight() * screenRatio).fill();
		table.row();
		table.add(label).fill();
		table.add(textField).expandX().fill();
		table.top();

		stage.addActor(table);

		Console.out = new PrintStream(output);
		EngineCommands.clear.setListener(clearListener);

		// insert newline to give the effect of sliding from the bottom upwards
		stage.draw();
		unusedLines = textArea.getLinesShowing();
		insertNewlines();		
	}

	private void insertNewlines () {
		if (textArea == null) return;

		for (int i = 0; i < unusedLines; i++)
			Console.out.print("\n");
	}

	private void toggle () {
		show = !show;

		if (show)
			stage.setKeyboardFocus(textField);
		else
			stage.unfocusAll();
	}

	private void update (float delta) {
		float posY = 0;

		handleInputs();

		if (show && table.getY() > 0) {
			posY = table.getY() - delta * speed;
			if (posY >= 0)
				table.setPosition(table.getX(), posY);
			else
				table.setPosition(table.getX(), 0);
		}

		if (!show && table.getY() < screenHeight) {
			posY = table.getY() + delta * speed;
			if (posY <= screenHeight)
				table.setPosition(table.getX(), posY);
			else
				table.setPosition(table.getX(), screenHeight);
		}
	}

	private void handleInputs () {
		int keypressed = 0;

		if (Gdx.input.isKeyJustPressed(toggleConsoleKey)) {
			toggle();
		}

		if (!show) return;

		if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) && textField.getText().length() > 0) {
			if (cmdHistory.size() == 0 || !textField.getText().trim().equals(cmdHistory.get(cmdHistory.size() - 1)))
				cmdHistory.add(textField.getText().trim());

			cmdHistoryIndex = cmdHistory.size();

			if (!textArea.getText().endsWith("\n")) Console.out.print("\n");
			Console.out.print(prompt + textField.getText() + "\n");
			Console.executeCommand(textField.getText().trim());
			textField.setText("");
		}

		if (Gdx.input.isKeyPressed(Input.Keys.UP)) keypressed = Input.Keys.UP;
		if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) keypressed = keypressed == 0 ? Input.Keys.DOWN : 0;
		if (Gdx.input.isKeyPressed(Input.Keys.PAGE_UP)) keypressed = keypressed == 0 ? Input.Keys.PAGE_UP : 0;
		if (Gdx.input.isKeyPressed(Input.Keys.PAGE_DOWN)) keypressed = keypressed == 0 ? Input.Keys.PAGE_DOWN : 0;

		if (keypressed != 0 && (!keyRepeatTask.isScheduled() || keyRepeatTask.keycode != keypressed)) {
			keyDown(keypressed);
			keyRepeatTask.keycode = keypressed;
			keyRepeatTask.cancel();
			Timer.schedule(keyRepeatTask, keyRepeatInitialTime, keyRepeatTime);
		} else if (keypressed == 0 && keyRepeatTask.isScheduled()) keyRepeatTask.cancel();
	}

	private void keyDown (int keycode) {
		int line;

		switch (keycode) {
		case Input.Keys.UP:
			cmdHistoryIndex--;

			if (cmdHistoryIndex < 0) {
				cmdHistoryIndex = 0;
			} else {
				textField.setText(cmdHistory.get(cmdHistoryIndex));
				textField.setCursorPosition(textField.getText().length());
			}
			break;

		case Input.Keys.DOWN:
			cmdHistoryIndex++;

			if (cmdHistoryIndex > cmdHistory.size() - 1) {
				cmdHistoryIndex = cmdHistory.size();
				textField.setText("");
			} else {
				textField.setText(cmdHistory.get(cmdHistoryIndex));
				textField.setCursorPosition(textField.getText().length());
			}
			break;

		case Input.Keys.PAGE_UP:
			line = textArea.getCursorLine() == textArea.getFirstLineShowing() ? textArea.getCursorLine()
				- textArea.getLinesShowing() : textArea.getCursorLine() - textArea.getLinesShowing() * 2 + 1;

			textArea.moveCursorLine(line < unusedLines ? unusedLines : line);
			break;

		case Input.Keys.PAGE_DOWN:
			line = textArea.getCursorLine() == textArea.getFirstLineShowing() ? textArea.getCursorLine()
				+ textArea.getLinesShowing() * 2 - 1 : textArea.getCursorLine() + textArea.getLinesShowing();

			textArea.moveCursorLine(line >= textArea.getLines() ? textArea.getLines() - 2 : line);
			break;
		}
	}

	public void render () {
		update(Gdx.graphics.getDeltaTime());

		stage.getViewport().apply();
		stage.act(Gdx.graphics.getDeltaTime());
		stage.draw();
	}

	public void resize (int width, int height) {
		screenWidth = width;
		screenHeight = height;

		stage.getViewport().update(screenWidth, screenHeight, true);

		table.getCell(textArea).height(screenHeight * screenRatio);

		if (show)
			table.setPosition(0, 0);
		else
			table.setPosition(0, screenHeight);
	}

	@Override
	public boolean acceptChar (TextField textField, char c) {
		if (c == toggleConsoleChar) return false;

		return true;
	}

	@Override
	public void dispose () {
		stage.dispose();
	}

	class KeyRepeatTask extends Task {
		int keycode;

		public void run () {
			keyDown(keycode);
		}
	}
}
