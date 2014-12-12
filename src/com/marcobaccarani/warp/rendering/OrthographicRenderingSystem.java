package com.marcobaccarani.warp.rendering;

import java.util.Collections;
import java.util.Comparator;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.marcobaccarani.warp.ecs.Entity;
import com.marcobaccarani.warp.ecs.EntityList;
import com.marcobaccarani.warp.ecs.RenderingSystem;

public class OrthographicRenderingSystem extends RenderingSystem {
	private static class PainterSort implements Comparator<Entity> {
		@Override
		public int compare(Entity o1, Entity o2) {
			if(o1.getLayerId() < o2.getLayerId())
				return -1;
			else if(o1.getLayerId() > o2.getLayerId())
				return 1;
			else
				return 0;
		}
	}
	
	private PainterSort sorting = new PainterSort();
	private SpriteBatch batch = new SpriteBatch();
	private Viewport viewport;
	
	public OrthographicRenderingSystem(Viewport viewport) {
		this.viewport = viewport;
	}
	
	@Override
	public void rendering(EntityList entities, boolean newEntities) {
		if(newEntities)
			Collections.sort(entities, sorting);
		
		viewport.apply();
		batch.setProjectionMatrix(viewport.getCamera().combined);
		batch.begin();
		
		for(Entity entity : entities) {
			render(entity, batch);
		}
		
		batch.end();
	}

	@Override
	public void dispose() {
		batch.dispose();
	}
}
