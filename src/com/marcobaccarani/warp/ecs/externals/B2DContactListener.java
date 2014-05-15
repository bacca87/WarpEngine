package com.marcobaccarani.warp.ecs.externals;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.marcobaccarani.warp.ecs.Entity;

public interface B2DContactListener {	
	public void beginContact(Entity e, Contact contact);
	public void endContact(Entity e, Contact contact);
	public void preSolve(Entity e, Contact contact, Manifold oldManifold);
	public void postSolve(Entity e, Contact contact, ContactImpulse impulse);
}
