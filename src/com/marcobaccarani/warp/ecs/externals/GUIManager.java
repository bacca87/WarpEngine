package com.marcobaccarani.warp.ecs.externals;

import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;

public class GUIManager {
	private Stage stage;
	
	public GUIManager(InputMultiplexer input) {
		stage = new Stage();
		input.addProcessor(0, stage);
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
	
	public void addActor(Actor actor) {
		stage.addActor(actor);
	}
	
	public void dispose() {
		stage.dispose();
	}
}
