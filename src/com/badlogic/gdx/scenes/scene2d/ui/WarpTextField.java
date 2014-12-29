package com.badlogic.gdx.scenes.scene2d.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;

public class WarpTextField extends TextField {
	public WarpTextField (String text, Skin skin) {
		super(text, skin.get(TextFieldStyle.class));
	}

	public WarpTextField(String text, Skin skin, String styleName) {
		super(text, skin, styleName);
	}

	public WarpTextField (String text, TextFieldStyle style) {
		super(text, style);
	}
	
	public void appendText (String str) {
		if (str == null) throw new IllegalArgumentException("text cannot be null.");

		clearSelection();
		cursor = text.length();
		paste(str, onlyFontChars);
	}
}
