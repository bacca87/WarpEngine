package com.marcobaccarani.warp.ecs;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public abstract class Renderer {
	private boolean enabled = true;
	
	protected System system;
	protected Entity entity;
	protected Transform transform;	
		
	public final boolean isEnabled() {
		return enabled;
	}
	
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
		
		if(enabled)
			enabled();
		else
			disabled();
	}	
	
	protected void initialize() { }
	protected void start() { }
	protected void enabled() { }
	protected void disabled() { }
	protected void removed() { }
	
	protected abstract void render(SpriteBatch batch);
}
