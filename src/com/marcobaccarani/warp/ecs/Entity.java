package com.marcobaccarani.warp.ecs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Entity {
	private System system;
	
	private Entity parent = null;
	private ArrayList<Entity> childs = new ArrayList<Entity>();
	
	public Transform transform = new Transform();
	
	private String name;
	private int tag;
	private int layerId;
	private boolean active;
	
	private Map<Class<? extends Component>, Component> components = new HashMap<Class<? extends Component>, Component>();
	private Renderer renderer;
	
	public Entity() {
		this("Entity");
	}
	
	public Entity(String name) {
		this.name = name;
		active = true;
	}
	
	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
		
		for(Component component : components.values()) {			
			component.setEnabled(active);
		}
		
		if(renderer != null)
			renderer.setEnabled(active);
		
		for(Entity entity : childs) {
			entity.setActive(active);
		}
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

	public void setParent(Entity parent) {
		this.parent = parent;
		this.transform.setParent(parent.transform);
	}
	
	public Entity getParent() {
		return parent;
	}
	
	public void addChild(Entity child) {
		child.setParent(this);		
		child.transform.addChild(child.transform);
		childs.add(child);
	}
	
	public void removeChild(Entity child) {
		child.setParent(null);
		child.transform.removeChild(child.transform);
		childs.remove(child);
	}	

	public void destroy() {		
		system.removeEntity(this);
		
		for(Entity entity : childs) {
			system.removeEntity(entity);
		}		
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
		renderer.transform = transform;
		this.renderer = renderer;
	}
	
	public void addComponent(Component component) {
		component.entity = this;
		component.transform = transform;
		components.put(component.getClass(), component);
	}
	
	public void removeComponent(Component component) {
		removeComponent(component.getClass());
	}
	
	public void removeComponent(Class<? extends Component> componentClass) {
		Component component = components.remove(componentClass);
		component.removed();
		component.entity = null;
		component.transform = null;
	}
		
	public <T extends Component> T getComponent(Class<T> type) {
		return type.cast(components.get(type));
	}
	
	public Collection<Component> getComponents() {
		return components.values();
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
		components.clear();
		
		if(renderer != null) {
			renderer.removed();
			renderer = null;
		}
		
		if(parent != null) {
			parent.removeChild(this);
			parent = null;
		}
			
		childs.clear();
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
