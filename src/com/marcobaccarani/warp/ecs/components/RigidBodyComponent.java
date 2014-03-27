package com.marcobaccarani.warp.ecs.components;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.marcobaccarani.warp.ecs.Component;

public class RigidBodyComponent extends Component {
	private Body body;
	private World world;
	public Vector2 lastPosition = new Vector2();
	public float lastAngle = 0;
	
	public RigidBodyComponent(Body body) {
		this.body = body;
		//this.body.setUserData(entity);
		this.world = body.getWorld();		
	}

	public Body getBody() {
		return body;
	}
	
	@Override
	protected void initialize() {
		lastPosition.set(body.getPosition());
	}
	
	@Override
	protected void removed() {
		world.destroyBody(body);
	}
}
