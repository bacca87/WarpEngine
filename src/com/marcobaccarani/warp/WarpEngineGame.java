package com.marcobaccarani.warp;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.marcobaccarani.warp.debug.WarpConsoleGUI;

public abstract class WarpEngineGame implements ApplicationListener{
	private WarpConsoleGUI console;
	private GameScene scene;
	
	public abstract void initialize();
	
	@Override
	public final void create() {
		//TODO: procedura inizializzazione warp engine
		console = new WarpConsoleGUI();
		
		initialize();
	}
	
	@Override
	public void dispose () {
		if (scene != null) scene.hide();
		
		console.dispose();
	}

	@Override
	public void pause () {
		if (scene != null) scene.pause();
	}

	@Override
	public void resume () {
		if (scene != null) scene.resume();
	}

	@Override
	public void render () {
		if (scene != null) scene.render(Gdx.graphics.getDeltaTime());
		
		console.render();
	}

	@Override
	public void resize (int width, int height) {
		if (scene != null) scene.resize(width, height);
		
		console.resize(width, height);
	}

	/** Sets the current screen. {@link GameScene#hide()} is called on any old screen, and {@link GameScene#show()} is called on the new
	 * screen, if any.
	 * @param screen may be {@code null} */
	public void setScene (GameScene gameScreen) {
		if (this.scene != null) this.scene.hide();
		this.scene = gameScreen;
		if (this.scene != null) {
			Gdx.input.setInputProcessor(this.scene.getInput());
			console.setInput(this.scene.getInput());
			this.scene.show();
			this.scene.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		}
	}

	/** @return the currently active {@link GameScene}. */
	public GameScene getGameScene () {
		return scene;
	}
}
