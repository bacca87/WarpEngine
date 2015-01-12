package com.marcobaccarani.warp;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.controllers.ControllerListener;

public interface Input {
	public void addInputProcessor(InputProcessor processor);
	public void removeInputProcessor(InputProcessor processor);
	public void addControllerProcessor(ControllerListener listener);
	public void removeControllerProcessor(ControllerListener listener);
}
