package com.marcobaccarani.warp;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.marcobaccarani.warp.ecs.System;

public class SimpleScene implements GameScene {
	private InputMultiplexer input;
	private OrthographicCamera camera;
	private Viewport viewport;
	
	private System system;
    
	public SimpleScene() {
		input = new InputMultiplexer();
		camera = new OrthographicCamera();
		
		system = new System();		
	}

	@Override
	public void render(float deltaTime) {
		Gdx.gl.glClearColor(0f, 0f, 0f, 0f);
		Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT);
		
		// update entities
		system.update(deltaTime);
		
		// update camera position
		camera.update();
		
		// render entities
		if(viewport != null) viewport.apply();
		system.render();
	}
	
	@Override
	public void resize(int width, int height) {
		if(viewport != null) viewport.update(width, height);
	}

	@Override
	public void show() {
	}
	
	@Override
	public void hide() {
	}
	
	@Override
	public void pause() {	
	}

	@Override
	public void resume() {
	}
	
	@Override
	public void dispose() {
		system.dispose();
	}	

	@Override
	public InputMultiplexer getInput() {
		return input;
	}
	
	public OrthographicCamera getCamera() {
		return camera;
	}

	public System getSystem() {
		return system;
	}

	public Viewport getViewport() {
		return viewport;
	}

	public void setViewport(Viewport viewport) {
		this.viewport = viewport;
	}
}
