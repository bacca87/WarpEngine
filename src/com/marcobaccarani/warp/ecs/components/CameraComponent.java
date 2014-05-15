package com.marcobaccarani.warp.ecs.components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.marcobaccarani.warp.ecs.Component;

public class CameraComponent extends Component {
	private OrthographicCamera camera;
	
	public CameraComponent() {
		camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	}
	
	public CameraComponent(int width, int height) {
		camera = new OrthographicCamera(width, height);
	}
	
	public CameraComponent(OrthographicCamera camera) {
		this.camera = camera;
	}
	
	public OrthographicCamera getCamera() {
		return camera;
	}
}
