package com.marcobaccarani.warp.ecs.renderingsystems;

import java.util.Collections;
import java.util.Comparator;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import com.marcobaccarani.warp.ecs.Entity;
import com.marcobaccarani.warp.ecs.EntityList;
import com.marcobaccarani.warp.ecs.RenderingSystem;

class OrthographicRenderingSystem extends RenderingSystem {
	private static class PainterSort implements Comparator<Entity> {
		@Override
		public int compare(Entity o1, Entity o2) {
			if(o1.getLayerId() < o2.getLayerId())
				return -1;
			
			if(o1.getLayerId() == o2.getLayerId())
				return 0;
			
			if(o1.getLayerId() > o2.getLayerId())
				return 1;
			
			return 0;
		}
	}
	
	private PainterSort sorting = new PainterSort();
	private SpriteBatch batch = new SpriteBatch();
	private Matrix4 projection;
	
	public OrthographicRenderingSystem(Matrix4 projection) {
		this.projection = projection;
	}
	
	@Override
	public void loop(EntityList entities, boolean newEntities) {
		if(newEntities)
			Collections.sort(entities, sorting);
		
		batch.setProjectionMatrix(projection);
		batch.begin();
		
		for(Entity entity : entities) {
			render(entity, batch);
		}
		
		batch.end();
	}
}
