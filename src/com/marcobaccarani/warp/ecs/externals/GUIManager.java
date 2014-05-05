package com.marcobaccarani.warp.ecs.externals;

import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.ScalingViewport;

public class GUIManager implements Disposable {
	private Stage stage;
	private InputMultiplexer input;

	public GUIManager() {
		stage = new Stage();
	}
	
	public void setInputMultiplexer(InputMultiplexer input) {
		this.input = input;
	}
	
	public void attachInputs() {
		input.addProcessor(0, stage);
	}
	
	public void detachInputs() {
		input.removeProcessor(stage);
	}
	
	public void update(float delta) {
		stage.act(delta);
	}
	
	public void render() {
		stage.draw();
	}
	
	public void resize(int width, int height) {
	    stage.getViewport().update(width, height, true);
	}
	
	public void setViewport(ScalingViewport viewport) {
		stage.setViewport(viewport);
	}
	
	public void addActor(Actor actor) {
		stage.addActor(actor);
	}
	
	@Override
	public void dispose() {
		stage.dispose();
	}
}
