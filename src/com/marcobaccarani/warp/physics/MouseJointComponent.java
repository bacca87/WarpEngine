package com.marcobaccarani.warp.physics;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.physics.box2d.joints.MouseJoint;
import com.badlogic.gdx.physics.box2d.joints.MouseJointDef;
import com.marcobaccarani.warp.ecs.Component;
import com.marcobaccarani.warp.ecs.Entity;
import com.marcobaccarani.warp.rendering.CameraComponent;
import com.marcobaccarani.warp.physics.B2DManager;

public class MouseJointComponent extends Component {
	private B2DManager b2dManager;
	
	private MouseJointDef jointDef;
	private MouseJoint mouseJoint;
	private CameraComponent camera;
	
	private Vector3 mousePosition = new Vector3();
	private Vector2 targetPosition = new Vector2();
	
	private float maxForce = 100;
	
	public MouseJointComponent(B2DManager b2dManager){
		this.b2dManager = b2dManager;
	}
	
	public float getMaxForce() {
		return maxForce;
	}

	public void setMaxForce(float maxForce) {
		this.maxForce = maxForce;
	}
	
	@Override
	protected void start() {
		Entity cameraEntity = system.getEntityByName("MainCamera");
		
		if(cameraEntity != null)
			camera = cameraEntity.getComponent(CameraComponent.class);
		
		// mouse joint
		jointDef = new MouseJointDef();
		jointDef.collideConnected = true;
		jointDef.maxForce = maxForce;
		jointDef.bodyA = b2dManager.createBody(new BodyDef()); //bodyA inutilizzato ma necessario
	};
	
	
	private QueryCallback jointCallback = new QueryCallback() {
		@Override
		public boolean reportFixture(Fixture fixture) {
			if(!fixture.testPoint(mousePosition.x, mousePosition.y))
				return true;
			
			jointDef.bodyB = fixture.getBody();
			jointDef.target.set(mousePosition.x, mousePosition.y);		
			mouseJoint = (MouseJoint) b2dManager.createJoint(jointDef);
			return false;
		}
	};
	
	private QueryCallback pushCallback = new QueryCallback() {
		public boolean reportFixture(Fixture fixture) {
			if(!fixture.testPoint(mousePosition.x, mousePosition.y))
				return true;
			
			fixture.getBody().applyLinearImpulse(30, 0, mousePosition.x, mousePosition.y, true);
			return false;
		}
	};
	
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if(camera == null)
			return false;
		
		camera.getViewport().unproject(mousePosition.set(screenX, screenY, 0));
		mousePosition.scl(b2dManager.getWorldToBox2DRatio());
		
		if (button == Input.Buttons.LEFT)
			b2dManager.queryAABB(jointCallback, mousePosition.x, mousePosition.y, mousePosition.x, mousePosition.y);            
		else
			b2dManager.queryAABB(pushCallback, mousePosition.x, mousePosition.y, mousePosition.x, mousePosition.y);
		
		return true;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if(mouseJoint == null)
			return false;
		
		b2dManager.destroyJoint(mouseJoint);
		mouseJoint = null;		
		return true;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		if(mouseJoint == null || camera == null)
			return false;
		
		camera.getViewport().unproject(mousePosition.set(screenX, screenY, 0));
		mouseJoint.setTarget(targetPosition.set(mousePosition.x, mousePosition.y).scl(b2dManager.getWorldToBox2DRatio()));
		return true;
	}
}
