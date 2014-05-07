package com.marcobaccarani.warp.ecs.externals;

import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.Viewport;

public class GUIManager implements Disposable {
	private Stage stage;

	public GUIManager() {		
		stage = new Stage();
	}
	
	public void setViewport(Viewport viewport) {
		stage.setViewport(viewport);
	}
	
	public void attachInputs(InputMultiplexer input) {
		input.addProcessor(0, stage);
	}
	
	public void update(float delta) {
		stage.act(delta);
	}
	
	public void render() {
		stage.draw();
	}
	
	public void resize(int width, int height) {
		resize(width, height, true);
	}
	
	public void resize(int width, int height, boolean centerCamera) {
	    stage.getViewport().update(width, height, centerCamera);
	}
		
	public void addActor(Actor actor) {
		stage.addActor(actor);
	}
	
	@Override
	public void dispose() {
		stage.dispose();
	}
}
