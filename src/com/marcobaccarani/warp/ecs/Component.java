package com.marcobaccarani.warp.ecs;

public abstract class Component {
	private boolean enabled = true;
	
	protected Entity entity;
	protected Transform transform;	
	
	public final boolean isEnabled() {
		return enabled;
	}
	
	public final void setEnabled(boolean enabled) {
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
	
	protected void update(float deltaTime) { }
}
