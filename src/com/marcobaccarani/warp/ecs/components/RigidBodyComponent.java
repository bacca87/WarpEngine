package com.marcobaccarani.warp.ecs.components;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.marcobaccarani.warp.ecs.Component;

public class RigidBodyComponent extends Component {
	private Body body;
	public Vector2 lastPosition = new Vector2();
	public float lastAngle = 0;
	
	public RigidBodyComponent(Body body) {
		this.body = body;
	}

	public Body getBody() {
		return body;
	}
	
	@Override
	protected void initialize() {
		lastPosition.set(body.getPosition());
	}
}
