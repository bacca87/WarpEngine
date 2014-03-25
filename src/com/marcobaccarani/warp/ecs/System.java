package com.marcobaccarani.warp.ecs;

import java.util.LinkedList;
import java.util.Queue;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class System {
	private int MAX_LAYERS;
	
	private EntityList[] layers;
	private Queue<Entity> added;
	private Queue<Entity> removed;
	
	public System(int MAX_LAYERS) {
		if (MAX_LAYERS <= 0) throw new IllegalArgumentException("MAX_LAYERS cannot be less than 1.");
		
		this.MAX_LAYERS = MAX_LAYERS;
		
		added = new LinkedList<Entity>();
		removed = new LinkedList<Entity>();
		layers = new EntityList[MAX_LAYERS];
		
		for(int i = 0; i < layers.length; i++) {
			layers[i] = new EntityList();
		}
	}
	
	public int getEntitiesCount() {
		int count = 0;
		
		for(EntityList list : layers) {
			count += list.size();
		}
		
		return count;
	}
	
	public void addEntities(EntityList entities) {
		for(Entity entity : entities) {
			addEntity(entity);
		}
	}
	
	public void addEntity(Entity entity) {
		if (entity.getLayerId() < 0 || entity.getLayerId() >= MAX_LAYERS) 
			throw new IllegalArgumentException("entity layer id must be set between 0 and " + Integer.toString(MAX_LAYERS-1));
		
		added.add(entity);
	}
	
	public void removeEntity(Entity entity) {
		removed.add(entity);
	}
	
	public Entity getEntityByName(String name) {
		for(EntityList entities : layers) {			
			Entity e = entities.getEntityByName(name);
			
			if(e != null)
				return e;
		}		
		return null;
	}
	
	public Entity getEntityByName(String name, int layer) {
		return layers[layer].getEntityByName(name);
	}
	
	public EntityList getEntitiesByTag(int tag) {
		EntityList listByTag = new EntityList();
		for(EntityList entities : layers) {			
			EntityList list = entities.getEntitiesByTag(tag);
			
			if(list != null) 
				listByTag.addAll(list);
		}		
		return listByTag;
	}
	
	public EntityList getEntitiesByTag(int tag, int layerId) {
		return layers[layerId].getEntitiesByTag(tag);
	}
		
	public void update(float deltaTime) {
		// remove entities
		while(!removed.isEmpty()) {
			Entity entity = removed.poll();
			layers[entity.getLayerId()].remove(entity);
			entity.removed();
		}
		
		// add entities
		for(Entity entity : added) {
			entity.setSystem(this);
			layers[entity.getLayerId()].add(entity);
			entity.initialize();
		}
		
		// start entities
		while(!added.isEmpty()) {
			added.poll().start();
		}
		
		// update entities
		for(EntityList entities : layers) {
			for(Entity entity : entities) {
				entity.update(deltaTime);
			}
		}
		
		// TODO: considerare di aggiungere anche la chiamata del lateUpdate come in unity
	}
	
	public void render(SpriteBatch batch) {
		for(EntityList entities : layers) {
			for(Entity entity : entities) {
				entity.render(batch);
			}
		}		
	}
	
	public void render(SpriteBatch batch, int[] layersIDs) {
		for(int id : layersIDs) {
			if(layers[id].size() > 0) {
				for(Entity entity : layers[id]) {
					entity.render(batch);
				}
			}
		}		
	}
}
