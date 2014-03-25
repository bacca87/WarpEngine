package com.marcobaccarani.warp.ecs;

public abstract class Component {
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
	
	protected void update(float deltaTime) { }
}
