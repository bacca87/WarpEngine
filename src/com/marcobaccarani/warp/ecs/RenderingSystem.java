package com.marcobaccarani.warp.ecs;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;

public abstract class RenderingSystem implements Disposable {
	protected abstract void rendering(EntityList entities, boolean newEntities);
	
	protected void render(Entity entity, SpriteBatch batch) {
		entity.render(batch);
	}	
}
