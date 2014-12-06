package com.badlogic.gdx.scenes.scene2d.ui;

import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.Timer;

public class WarpTextField extends TextField {
	public static float keyRepeatInitialTime = 0.4f;
	public static float keyRepeatTime = 0.05f;

	public WarpTextField (String text, Skin skin) {
		super(text, skin.get(TextFieldStyle.class));
	}

	public WarpTextField(String text, Skin skin, String styleName) {
		super(text, skin, styleName);
	}

	public WarpTextField (String text, TextFieldStyle style) {
		super(text, style);
	}
	
	protected InputListener createInputListener () {
		return new WarpConsoleTextFieldClickListener();
	}
	
	public class WarpConsoleTextFieldClickListener extends TextFieldClickListener {
		protected void scheduleKeyRepeatTask (int keycode) {
			if (!keyRepeatTask.isScheduled() || keyRepeatTask.keycode != keycode) {
				keyRepeatTask.keycode = keycode;
				keyRepeatTask.cancel();
				Timer.schedule(keyRepeatTask, keyRepeatInitialTime, keyRepeatTime);
			}
		}
	}
}
