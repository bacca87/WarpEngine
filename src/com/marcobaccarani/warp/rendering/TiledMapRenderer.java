package com.marcobaccarani.warp.rendering;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.marcobaccarani.warp.ecs.Renderer;

public class TiledMapRenderer extends Renderer {
	private CameraComponent mapCamera;
	private OrthogonalTiledMapRenderer mapRenderer;
	private int[] layers;
	
	public TiledMapRenderer(OrthogonalTiledMapRenderer mapRenderer) {
		this.mapRenderer = mapRenderer;
	}
	
	public TiledMapRenderer(OrthogonalTiledMapRenderer mapRenderer, int[] layers) {
		this.mapRenderer = mapRenderer;
		this.layers = layers;
	}
	
	@Override
	protected void start() {
		mapCamera = entity.getSystem().getEntityByName("MainCamera").getComponent(CameraComponent.class);
	}
	
	@Override
	protected void render(SpriteBatch batch) {
		batch.end();
		
		mapRenderer.setView(mapCamera.getCamera());
		
		if(layers != null)
			mapRenderer.render(layers);
		else
			mapRenderer.render();
		
		batch.begin();
	}
}
