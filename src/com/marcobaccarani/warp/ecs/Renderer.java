package com.marcobaccarani.warp.ecs;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public abstract class Renderer {
	protected Entity entity;
	private boolean enabled = true;
		
	public boolean isEnabled() {
		return enabled;
	}
	
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}	
	
	protected void initialize() { }
	protected void start() { }
	protected void removed() { }
	
	protected abstract void render(SpriteBatch batch);
}
