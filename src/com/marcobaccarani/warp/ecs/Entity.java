package com.marcobaccarani.warp.ecs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public final class Entity {
	private final System system;
	
	private Entity parent = null;
	private ArrayList<Entity> childs = new ArrayList<Entity>();
	
	public final Transform transform = new Transform();
	
	private boolean added;
	private int id;
	private String name;
	private int tag;
	private int layerId;
	private boolean active;
	
	private Map<Class<? extends Component>, Component> components = new HashMap<Class<? extends Component>, Component>();
	private Renderer renderer;
	
	protected Entity(int id, System system) {
		this.system = system;
		added = false;
		setActive(true);		
		setName("Entity_" + Integer.toString(id));
	}
	
	public Entity addToSystem() {
		//add the entity to the system only once
		if(!added) {
			system.addEntity(this);
			added = true;
		}
		return this;
	}
	
	public int getId() {
		return id;
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
			throw new IllegalArgumentException("The entity name can't be null!");
		
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
		
		if(parent != null)
			this.transform.setParent(parent.transform);
		else
			this.transform.setParent(null);
	}
	
	public Entity getParent() {
		return parent;
	}
	
	public void addChild(Entity child) {
		child.setParent(this);		
		transform.addChild(child.transform);
		childs.add(child);
	}
	
	public void removeChild(Entity child) {
		child.setParent(null);
		transform.removeChild(child.transform);
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
		{
			this.renderer.removed();
			renderer.system = null;
			renderer.entity = null;
			renderer.transform = null;
		}
		
		this.renderer = renderer;
		renderer.system = system;
		renderer.entity = this;
		renderer.transform = transform;
	}
	
	public void addComponent(Component component) {
		component.system = system;
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
		component.system = null;
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
