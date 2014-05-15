package com.marcobaccarani.warp.ecs;

public abstract class Component {
	protected Entity entity;	
	private boolean enabled = true;
	
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
