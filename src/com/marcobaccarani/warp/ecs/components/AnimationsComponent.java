package com.marcobaccarani.warp.ecs.components;

import java.util.HashMap;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.marcobaccarani.warp.ecs.Component;
import com.marcobaccarani.warp.ecs.renderers.SpriteRenderer;

public class AnimationsComponent extends Component {
	private SpriteRenderer spriteRenderer;
	private HashMap<String, Animation> animations;
	private Animation activeAnimation;
	private String activeAnimationName;
	public float stateTime;
	
	public AnimationsComponent() {
		stateTime = 0;
		animations = new HashMap<String, Animation>();		
	}
	
	public void addAnimation(String name, Animation animation) {
		animations.put(name, animation);
	}
	
	public void removeAnimation(String name) {
		animations.remove(name);
	}
	
	public Animation getAnimation(String name) {
		return animations.get(name);
	}
	
	public void playAnimation(String name) {
		if(activeAnimationName == name)
			return;
			
		stateTime = 0;
		activeAnimationName = name;
		activeAnimation = animations.get(name);
	}
	
	public void stopAnimation() {
		activeAnimationName = null;
		activeAnimation = null;
	}
	
	public Animation getActiveAnimation() {
		return activeAnimation;
	}
	
	public String getActiveAnimationName() {
		return activeAnimationName;
	}
	
	@Override
	protected void initialize() {
		spriteRenderer = entity.getRenderer(SpriteRenderer.class);
	}
	
	@Override	
	protected void update(float deltaTime) {
		if(spriteRenderer == null || activeAnimation == null || spriteRenderer.getSprite() == null)
			return;
	
		spriteRenderer.getSprite().setRegion(activeAnimation.getKeyFrame(stateTime += deltaTime));
	}
}
