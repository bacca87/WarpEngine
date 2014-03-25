package com.marcobaccarani.warp.ecs.externals;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.Array;
import com.marcobaccarani.warp.ecs.Component;
import com.marcobaccarani.warp.ecs.Entity;
import com.marcobaccarani.warp.ecs.components.RigidBodyComponent;
import com.marcobaccarani.warp.ecs.components.TransformComponent;

public class B2DManager implements ContactListener {
	// pixel to meter ratio
	private float box2d_to_world = 1;
	@SuppressWarnings("unused")
	private float world_to_box2d = 1;
	
	// physic simulation
	private World physicsWorld;
	private Array<Body> bodies = new Array<Body>();
	private float box2d_timestep = 1.0f/60.0f;
	private int box2d_velocity_iterations = 8;
	private int box2d_position_iterations = 4;
	private float accumulator;
	private float accumulatorRatio;
	private Vector2 gravity = new Vector2(0, -10);
		
	// debug	
	private Box2DDebugRenderer debugRenderer = new Box2DDebugRenderer();
	
	public B2DManager() {
		physicsWorld = new World(gravity, true);
		physicsWorld.setAutoClearForces(false);
		physicsWorld.setContactListener(this);
	}

	public B2DManager(Vector2 gravity, boolean doSleep) {
		physicsWorld = new World(gravity, doSleep);
		physicsWorld.setAutoClearForces(false);
		physicsWorld.setContactListener(this);
	}
	
	public void setPixelToMeterRatio(float pixels) {
		box2d_to_world = pixels;
		world_to_box2d = 1 / pixels;
	}
	
	public void setTimeStep(float box2d_timestep) {
		this.box2d_timestep = box2d_timestep;
	}
	
	public void setVelocityIterations(int box2d_velocity_iterations) {
		this.box2d_velocity_iterations = box2d_velocity_iterations;
	}
	
	public void setPositionIterations(int box2d_position_iterations) {
		this.box2d_position_iterations = box2d_position_iterations;
	}
	
	public Vector2 getGravity(Vector2 gravity) {
		gravity.x = this.gravity.x;
		gravity.y = this.gravity.y;
		
		return gravity;
	}

	public void setGravity(float x, float y) {
		this.gravity.x = x;
		this.gravity.y = y;		
		physicsWorld.setGravity(this.gravity);
	}
	
	public void setGravity(Vector2 gravity) {
		this.gravity.x = gravity.x;
		this.gravity.y = gravity.y;
		physicsWorld.setGravity(this.gravity);
	}
	
	public void render(OrthographicCamera camera) {
		debugRenderer.render(physicsWorld, camera.combined.cpy().scl(box2d_to_world));
	}
	
	/***********************************/
	/* 		SIMULATION METHODS		   */
	/***********************************/
	
	public void processPhysics(float delta) {
		// fixed timestep for physics
		// http://gafferongames.com/game-physics/fix-your-timestep/
		// http://www.unagames.com/blog/daniele/2010/06/fixed-time-step-implementation-box2d
		accumulator += delta;
		
		int steps = MathUtils.floor(accumulator/box2d_timestep);
		
		if(steps > 0)
			accumulator -= steps * box2d_timestep;
		
		accumulatorRatio = accumulator / box2d_timestep;
		
		int clampedSteps = Math.min(steps, 5);
		
		for(int i = 0; i < clampedSteps; i++) {
			setLastPosition();
			//TODO: fixedUpdate();
			physicsWorld.step(box2d_timestep, box2d_velocity_iterations, box2d_position_iterations);
		}
				
		physicsWorld.clearForces();
		
		smoothStates();
	}
		
	private void setLastPosition() {
		physicsWorld.getBodies(bodies);
		
		for(Body body : bodies) {
			Entity e = (Entity) body.getUserData();
			
			if(e == null || body.getType() == BodyType.StaticBody || body.getType() == BodyType.KinematicBody)
				continue;
			
			RigidBodyComponent rigidbody = e.getComponent(RigidBodyComponent.class);
			
			if(rigidbody == null) 
				continue;
			
			rigidbody.lastPosition.set(body.getPosition());
			rigidbody.lastAngle = body.getAngle();
		}
	}
	
	private void smoothStates() {
		float oneMinusRatio = 1.0f - accumulatorRatio;
		physicsWorld.getBodies(bodies);
		
		for(Body body : bodies) {
			Entity e = (Entity) body.getUserData();
			
			if(e == null || body.getType() == BodyType.StaticBody || body.getType() == BodyType.KinematicBody)
				continue;
			
			RigidBodyComponent rigidbody = e.getComponent(RigidBodyComponent.class);
			TransformComponent transform = e.getComponent(TransformComponent.class);
			
			if(rigidbody == null || transform == null) 
				continue;
			
			transform.setXY(
					(body.getPosition().x * accumulatorRatio + rigidbody.lastPosition.x * oneMinusRatio) * box2d_to_world,
					(body.getPosition().y * accumulatorRatio + rigidbody.lastPosition.y * oneMinusRatio) * box2d_to_world 
			);
			
			transform.setRotation((accumulatorRatio * body.getAngle() + oneMinusRatio * rigidbody.lastAngle) * MathUtils.radDeg);
		}
	}

	/***********************************/
	/* 	  COLLISION EVENTS HANDLING    */
	/***********************************/
	
	@Override
	public void beginContact(Contact contact) {
		Entity a = (Entity) contact.getFixtureA().getBody().getUserData();
		Entity b = (Entity) contact.getFixtureB().getBody().getUserData();
				
		for(Component component : a.getComponents()) {
			if(component instanceof B2DContactListener) {
				((B2DContactListener)component).beginContact(b, contact);
			}
		}
		
		for(Component component : b.getComponents()) {
			if(component instanceof B2DContactListener) {
				((B2DContactListener)component).beginContact(a, contact);
			}
		}
	}

	@Override
	public void endContact(Contact contact) {
		Entity a = (Entity) contact.getFixtureA().getBody().getUserData();
		Entity b = (Entity) contact.getFixtureB().getBody().getUserData();
				
		for(Component component : a.getComponents()) {
			if(component instanceof B2DContactListener) {
				((B2DContactListener)component).endContact(b, contact);
			}
		}
		
		for(Component component : b.getComponents()) {
			if(component instanceof B2DContactListener) {
				((B2DContactListener)component).endContact(a, contact);
			}
		}
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		Entity a = (Entity) contact.getFixtureA().getBody().getUserData();
		Entity b = (Entity) contact.getFixtureB().getBody().getUserData();
				
		for(Component component : a.getComponents()) {
			if(component instanceof B2DContactListener) {
				((B2DContactListener)component).preSolve(b, contact, oldManifold);
			}
		}
		
		for(Component component : b.getComponents()) {
			if(component instanceof B2DContactListener) {
				((B2DContactListener)component).preSolve(a, contact, oldManifold);
			}
		}
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		Entity a = (Entity) contact.getFixtureA().getBody().getUserData();
		Entity b = (Entity) contact.getFixtureB().getBody().getUserData();
				
		for(Component component : a.getComponents()) {
			if(component instanceof B2DContactListener) {
				((B2DContactListener)component).postSolve(b, contact, impulse);
			}
		}
		
		for(Component component : b.getComponents()) {
			if(component instanceof B2DContactListener) {
				((B2DContactListener)component).postSolve(a, contact, impulse);
			}
		}
	}
	
	/***********************************/
	/* 		  WRAPPED METHODS		   */
	/***********************************/
	
	public Body createBody(BodyDef bodyDef) {
		return physicsWorld.createBody(bodyDef);
	}
}
