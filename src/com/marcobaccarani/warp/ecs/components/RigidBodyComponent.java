package com.marcobaccarani.warp.ecs.components;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.marcobaccarani.warp.ecs.Component;
import com.marcobaccarani.warp.ecs.externals.B2DManager;

//TODO: vedere di implementare un component per le shape/fixture
//		creare il body quando viene inizializzato
//		wrappare le definizioni per la creazione di body shape e fixture dentro i componenti
//
//		non esporre mai direttamente l'oggetto Body
//
public class RigidBodyComponent extends Component {
	private World world;
	private B2DManager b2dManager;
	private Body body;
	private BodyDef bodyDef;
	private FixtureDef[] fixtureDefs;
	public Vector2 lastPosition = new Vector2(); //TODO: proteggere questa proprietà usata per lo smooth del movimento
	public float lastAngle = 0;
	
	public RigidBodyComponent(BodyDef bodyDef, FixtureDef[] fixtureDefs, B2DManager b2dManager) {
		this.bodyDef = bodyDef;
		this.fixtureDefs = fixtureDefs;
		this.b2dManager = b2dManager;
	}
	
	@Override
	protected void initialize() {
		body = b2dManager.createBody(bodyDef);
		body.setUserData(entity);
		lastPosition.set(body.getPosition());
		
		for(FixtureDef def : fixtureDefs) {
			createFixture(def);
			
			// Remember to dispose of any shapes after you're done with them!
			// BodyDef and FixtureDef don't need disposing, but shapes do.
			def.shape.dispose();
		}
	}
	
	@Override
	protected void enabled() {
		if(!world.isLocked())
			body.setActive(true);
	}
	
	@Override
	protected void disabled() {
		if(!world.isLocked())
			body.setActive(false);
	}
	
	@Override
	protected void removed() {
		if(!world.isLocked())
			world.destroyBody(body);
	}
	
	public Fixture createFixture(FixtureDef def) {
		if(body == null)
			return null;
		
		return body.createFixture(def);
	}
	
	public void destroyFixture(Fixture fixture) {
		if(body != null)
			body.destroyFixture(fixture);
	}
}
