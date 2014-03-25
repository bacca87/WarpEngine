package com.marcobaccarani.src.warp.ecs.renderers;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.marcobaccarani.src.warp.ecs.Renderer;
import com.marcobaccarani.src.warp.ecs.components.TransformComponent;

public class SpriteRenderer extends Renderer {
	private Sprite sprite;
	private TransformComponent transform;
	
	public SpriteRenderer() {		
	}
	
	public SpriteRenderer(Sprite sprite) {
		this.sprite = sprite;
	}
	
	public Sprite getSprite() {
		return sprite;
	}

	public void setSprite(Sprite sprite) {
		this.sprite = sprite;
	}

	@Override
	protected void initialize() {
		transform = entity.getComponent(TransformComponent.class);
	}

	@Override
	protected void render(SpriteBatch batch) {
		if(transform == null || sprite == null || sprite.getTexture() == null)
			return;
		
		//sprite.setOrigin(transform.origin.x, transform.origin.y);
		sprite.setRotation(transform.getRotation());
		sprite.setScale(transform.getScaleX(), transform.getScaleY());
		sprite.setPosition(transform.getX(), transform.getY());
		sprite.draw(batch);
	}
}
