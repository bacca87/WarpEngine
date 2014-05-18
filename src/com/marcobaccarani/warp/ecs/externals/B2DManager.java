package com.marcobaccarani.warp.ecs.externals;

import java.util.Iterator;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Polyline;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.marcobaccarani.warp.ecs.Component;
import com.marcobaccarani.warp.ecs.Entity;
import com.marcobaccarani.warp.ecs.EntityList;
import com.marcobaccarani.warp.ecs.components.RigidBodyComponent;
import com.marcobaccarani.warp.ecs.components.TransformComponent;
import com.marcobaccarani.warp.utils.Utility;

public class B2DManager implements ContactListener, Disposable {
	// pixel to meter ratio
	private float box2d_to_world = 1;
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
	
	public float getPixelToMeter() {
		return box2d_to_world;
	}
	
	public void setPixelToMeter(float pixels) {
		box2d_to_world = pixels;
		world_to_box2d = 1 / pixels;
	}
	
	public float getBox2DToWorldRatio() {
		return box2d_to_world;
	}
	
	public float getWorldToBox2DRatio() {
		return world_to_box2d;
	}
		
	public float getTimeStep() {
		return box2d_timestep;
	}
	
	public void setTimeStep(float box2d_timestep) {
		this.box2d_timestep = box2d_timestep;
	}
	
	public int getVelocityIterations() {
		return box2d_velocity_iterations;
	}
	
	public void setVelocityIterations(int box2d_velocity_iterations) {
		this.box2d_velocity_iterations = box2d_velocity_iterations;
	}
	
	public int getPositionIterations() {
		return box2d_position_iterations;
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
	
	public void render(Matrix4 projection) {
		debugRenderer.render(physicsWorld, projection.cpy().scl(box2d_to_world));
	}
	
	@Override
	public void dispose() {
		physicsWorld.dispose();
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
		Entity a = null; 
		Entity b = null; 
		
		// When a body is destroyed endContact is called and the contact fixture could be null
		if(contact.getFixtureA() != null) 
			a = (Entity) contact.getFixtureA().getBody().getUserData();	
		
		if(contact.getFixtureB() != null)
			b = (Entity) contact.getFixtureB().getBody().getUserData();
		
		if(a != null) {
			for(Component component : a.getComponents()) {
				if(component instanceof B2DContactListener) {
					((B2DContactListener)component).endContact(b, contact);
				}
			}
		}
		
		if(b != null) {
			for(Component component : b.getComponents()) {
				if(component instanceof B2DContactListener) {
					((B2DContactListener)component).endContact(a, contact);
				}
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
	
	/***********************************/
	/* 		  		UTILS			   */
	/***********************************/
	
	public EntityList createStaticMapObjects(MapObjects objects, boolean sensors) {
		EntityList list = new EntityList();
		Iterator<MapObject> mapObjectIterator = objects.iterator();
		
		while(mapObjectIterator.hasNext()) {
			MapObject obj = mapObjectIterator.next();
			Entity e = new Entity();
			
			if(obj.getName() != null)
				e.setName(obj.getName());
			
			if(obj instanceof RectangleMapObject) {
				RectangleMapObject rectObj = (RectangleMapObject)obj;
				Rectangle rect = rectObj.getRectangle();
				
				// First we create a body definition
				BodyDef bodyDef = new BodyDef();
				bodyDef.type = BodyType.StaticBody;
				// Set our body's starting position in the world
				rect.getCenter(bodyDef.position).scl(world_to_box2d);
				
				// Create our body in the world using our body definition
				Body body = createBody(bodyDef);
				body.setUserData(e);
				
				// Create a rectangle shape
				PolygonShape b2dRect = new PolygonShape();
				b2dRect.setAsBox((rect.width/2) * world_to_box2d, (rect.height/2) * world_to_box2d);

				// Create our fixture and attach it to the body
				if(sensors)
					body.createFixture(b2dRect, 0.0f).setSensor(true);
				else
					body.createFixture(b2dRect, 0.0f).setSensor(false);

				// Remember to dispose of any shapes after you're done with them!
				// BodyDef and FixtureDef don't need disposing, but shapes do.
				b2dRect.dispose();
				
				e.addComponent(new RigidBodyComponent(body));
				
				list.add(e);
			}
			else if(obj instanceof PolygonMapObject) {
				PolygonMapObject polygonObj = (PolygonMapObject)obj;
				Polygon polygon = polygonObj.getPolygon();
								
				// First we create a body definition
				BodyDef bodyDef = new BodyDef();
				bodyDef.type = BodyType.StaticBody;
				// Set our body's starting position in the world
				bodyDef.position.set(polygon.getOriginX(), polygon.getOriginY()).scl(world_to_box2d);
				
				// Create our body in the world using our body definition
				Body body = createBody(bodyDef);
				body.setUserData(e);
				
				// Create a polygon shape						
				ChainShape b2dChain = new ChainShape();
				b2dChain.createLoop(Utility.mulFloatArray(polygon.getTransformedVertices(), world_to_box2d));
				
				// Create our fixture and attach it to the body
				if(sensors)
					body.createFixture(b2dChain, 0.0f).setSensor(true);
				else
					body.createFixture(b2dChain, 0.0f).setSensor(false);
				
				// Remember to dispose of any shapes after you're done with them!
				// BodyDef and FixtureDef don't need disposing, but shapes do.
				b2dChain.dispose();
				
				e.addComponent(new RigidBodyComponent(body));				
								
				list.add(e);
			}
			else if(obj instanceof PolylineMapObject) {
				PolylineMapObject lineObj = (PolylineMapObject)obj;
				Polyline line = lineObj.getPolyline();
				
				// First we create a body definition
				BodyDef bodyDef = new BodyDef();
				bodyDef.type = BodyType.StaticBody;
				// Set our body's starting position in the world
				bodyDef.position.set(line.getOriginX(), line.getOriginY()).scl(world_to_box2d);
				
				// Create our body in the world using our body definition
				Body body = createBody(bodyDef);
				body.setUserData(e);
				
				// Create a polygon shape						
				ChainShape b2dChain = new ChainShape();
				b2dChain.createChain(Utility.mulFloatArray(line.getTransformedVertices(), world_to_box2d));
				
				// Create our fixture and attach it to the body
				if(sensors)
					body.createFixture(b2dChain, 0.0f).setSensor(true);
				else
					body.createFixture(b2dChain, 0.0f).setSensor(false);

				// Remember to dispose of any shapes after you're done with them!
				// BodyDef and FixtureDef don't need disposing, but shapes do.
				b2dChain.dispose();
				
				e.addComponent(new RigidBodyComponent(body));	
				
				list.add(e);
			}
		}
		
		return list;
	}
}
