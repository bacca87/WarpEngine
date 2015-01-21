
package com.marcobaccarani.warp;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.controllers.ControllerListener;
import com.marcobaccarani.warp.console.Command;

public interface Input {
	public void addInputProcessor (InputProcessor processor);

	public void removeInputProcessor (InputProcessor processor);

	public void addControllerProcessor (ControllerListener listener);

	public void removeControllerProcessor (ControllerListener listener);

	public void bind (String keyName, Command command);

	public void unbindKey (String keyName);

	public void unbindCommand (Command command);

	public void unbindAll ();
}
