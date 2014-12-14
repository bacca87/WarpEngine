package com.marcobaccarani.warp;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.marcobaccarani.warp.ecs.System;
import com.marcobaccarani.warp.physics.B2DManager;

public class B2DScene implements GameScene {
	private InputMultiplexer input;
	private OrthographicCamera camera;
	private Viewport viewport;
	
	private System system;
    
	private B2DManager physics;
	private boolean debug;
    
	public B2DScene() {
		input = new InputMultiplexer();
		camera = new OrthographicCamera();
		
		physics = new B2DManager();
		debug = false;

		system = new System();
	}

	@Override
	public void render(float deltaTime) {
		Gdx.gl.glClearColor(0f, 0f, 0f, 0f);
		Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT);
		
		// B2D step physics
		physics.processPhysics(deltaTime);
		
		// update entities
		system.update(deltaTime);
		
		// update camera position
		camera.update();
		
		// render entities
		viewport.apply();
		system.render();
		
		// B2D debug
		if(debug) physics.render(camera.combined);
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
		physics.dispose();
		system.dispose();
	}
	
	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
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

	public B2DManager getPhysics() {
		return physics;
	}

	public Viewport getViewport() {
		return viewport;
	}

	public void setViewport(Viewport viewport) {
		this.viewport = viewport;
	}
}
