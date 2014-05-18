package com.marcobaccarani.warp.ecs;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public abstract class RenderingSystem {
	protected abstract void loop(EntityList entities, boolean newEntities);
	
	protected void render(Entity entity, SpriteBatch batch) {
		entity.render(batch);
	}	
}
