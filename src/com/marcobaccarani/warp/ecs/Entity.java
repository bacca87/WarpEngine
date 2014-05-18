package com.marcobaccarani.warp.ecs;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Entity {
	private System system;
	
	private String name;
	private int tag;
	private int layerId;
	private boolean active;
	
	private Map<Class<? extends Component>, Component> components;	
	
	private Renderer renderer;
	
	public Entity() {
		this("Entity");
	}
	
	public Entity(String name) {
		components = new HashMap<Class<? extends Component>, Component>();
		this.name = name;
		active = true;
	}
	
	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
		
		for(Component component : components.values()) {			
			component.setEnabled(isActive());
		}
		
		if(renderer != null)
			renderer.setEnabled(isActive());
	}

	public System getSystem() {
		return system;
	}

	public void setSystem(System system) {		
		this.system = system;
	}
	
	public int getLayerId() {
		return layerId;
	}

	public void setLayerId(int layerId) {
		this.layerId = layerId;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		if(name == null)
			throw new IllegalArgumentException("the entity name can't be null!");
		
		this.name = name;
	}
	
	public int getTag() {
		return tag;
	}

	public void setTag(int tag) {
		this.tag = tag;
	}

	public <T extends Renderer> T getRenderer(Class<T> type) {
		if(type.isInstance(renderer))
			return type.cast(renderer);
		else
			return null;
	}
	
	public void setRenderer(Renderer renderer) {
		if(this.renderer != null)
			this.renderer.removed();
		
		renderer.entity = this;
		this.renderer = renderer;
	}
	
	public void addComponent(Component component) {
		component.entity = this;		
		components.put(component.getClass(), component);
	}
	
	public void removeComponent(Class<? extends Component> componentClass) {
		Component component = components.remove(componentClass);
		component.removed();
	}
	
	public <T extends Component> T getComponent(Class<T> type) {
		return type.cast(components.get(type));
	}
	
	public Collection<Component> getComponents() {
		return components.values();
	}
	
	public void destroy() {
		system.removeEntity(this);
	}
	
	protected void initialize() {
		for(Component component : components.values()) {
			component.initialize();
		}
		
		if(renderer != null)
			renderer.initialize();
	}
	
	protected void start() {
		for(Component component : components.values()) {
			component.start();
		}
		
		if(renderer != null)
			renderer.start();
	}
	
	protected void removed() {
		for(Component component : components.values()) {
			component.removed();
		}
		
		if(renderer != null)
			renderer.removed();
	}
	
	protected void update(float deltaTime) {
		if(!isActive())
			return;
		
		for(Component component : components.values()) {
			if(component.isEnabled())
				component.update(deltaTime);
		}
	}
	
	protected void render(SpriteBatch batch) {
		if(!isActive())
			return;
		
		if(renderer != null && renderer.isEnabled())
			renderer.render(batch);
	}
}
