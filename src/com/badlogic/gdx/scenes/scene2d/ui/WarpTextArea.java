package com.badlogic.gdx.scenes.scene2d.ui;

public class WarpTextArea extends TextArea {

	public WarpTextArea (String text, Skin skin) {
		super(text, skin);
	}

	public WarpTextArea (String text, Skin skin, String styleName) {
		super(text, skin, styleName);
	}

	public WarpTextArea (String text, TextFieldStyle style) {
		super(text, style);
	}

	public void appendText (String str) {
		if (str == null) throw new IllegalArgumentException("text cannot be null.");

		clearSelection();
		cursor = text.length();
		paste(str, onlyFontChars);
	}
}
