package com.marcobaccarani.warp;

import com.badlogic.gdx.InputMultiplexer;

public interface Scene {
	public InputMultiplexer getInput();
	
	/** Called when the scene should render itself.
	 * @param delta The time in seconds since the last render. */
	public void render (float delta);

	/** @see ApplicationListener#resize(int, int) */
	public void resize (int width, int height);

	/** @see ApplicationListener#pause() */
	public void pause ();

	/** @see ApplicationListener#resume() */
	public void resume ();

	/** Called when this scene becomes the current screen for a {@link Game}. */
	public void show ();
	
	/** Called when this scene is no longer the current screen for a {@link Game}. */
	public void hide ();

	/** Called when this scene should release all resources. */
	public void dispose ();
}
