package com.marcobaccarani.warp.rendering;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.marcobaccarani.warp.ecs.Component;

public class CameraComponent extends Component {
	private Viewport viewport;
	
	public CameraComponent(Viewport viewport) {
		this.viewport = viewport;
	}
	
	public OrthographicCamera getCamera() {
		return (OrthographicCamera) viewport.getCamera();
	}
	
	public Viewport getViewport() {
		return viewport;
	}
}
