package com.marcobaccarani.warp.ecs;

import java.util.LinkedList;
import java.util.Queue;

import com.badlogic.gdx.utils.Disposable;

public class System implements Disposable {
	private boolean newEntities = false;
	private final EntityList entities = new EntityList();
	private Queue<Entity> added = new LinkedList<Entity>();
	private Queue<Entity> removed = new LinkedList<Entity>();
	
	private RenderingSystem renderer;
	
	public System() {
	}
	
	public RenderingSystem getRenderer() {
		return renderer;
	}

	public void setRenderer(RenderingSystem renderer) {
		this.renderer = renderer;
	}
	
	public int getEntitiesCount() {		
		return entities.size();
	}
	
	public void addEntities(EntityList entities) {
		for(Entity entity : entities) {
			addEntity(entity);
		}
	}
	
	public void addEntity(Entity entity) {
		added.add(entity);
	}
	
	public void removeEntity(Entity entity) {
		removed.add(entity);
	}
	
	public Entity getEntityByName(String name) {
		return entities.getEntityByName(name);
	}
	
	public EntityList getEntitiesByTag(int tag) {
		return entities.getEntitiesByTag(tag);
	}
		
	public void update(float deltaTime) {
		newEntities = false;
		
		// remove entities
		while(!removed.isEmpty()) {
			Entity entity = removed.poll();
			entities.remove(entity);
			entity.removed();
		}
		
		// add entities
		for(Entity entity : added) {
			entity.setSystem(this);
			entities.add(entity);
			entity.initialize();
			newEntities = true;
		}
		
		// start entities
		while(!added.isEmpty()) {
			added.poll().start();
		}
		
		// update entities		
		for(Entity entity : entities) {
			if(entity.isActive())
				entity.update(deltaTime);
		}
		
		// TODO: considerare di aggiungere anche la chiamata del lateUpdate come in unity
	}
	
	public void render() {		
		renderer.loop(entities, newEntities);
	}
	
	@Override
	public void dispose() {
		// remove all entities		
		for(Entity entity : entities) {
			entity.removed();
		}
		
		entities.clear();
	}	
}
