package com.marcobaccarani.warp.ecs;

import java.util.ArrayList;

public class EntityList extends ArrayList<Entity> {
	private static final long serialVersionUID = 1L;
	
	public Entity getEntityByName(String name) {			
		for(Entity entity : this) {
			if(entity.getName().equals(name))
				return entity;
		}
		
		return null;
	}
	
	public EntityList getEntitiesByTag(int tag) {
		EntityList listByTag = new EntityList();
		
		for(Entity entity : this) {
			if(entity.getTag() == tag)
				listByTag.add(entity);
		}
		
		return listByTag;
	}
	
	public void addAllToSystem() {
		for(Entity entity : this) {
			entity.addToSystem();
		}
	}
}
