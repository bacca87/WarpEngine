package com.marcobaccarani.warp.ecs.renderers;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.marcobaccarani.warp.ecs.Renderer;

public class SpriteRenderer extends Renderer {
	private Sprite sprite;
	private Vector2 offset = new Vector2();
	
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
	
	public void setPositionOffset(float x, float y) {
		offset.x = x;
		offset.y = y;
	}
	
	public void setPositionOffset(Vector2 offset) {
		this.offset.x = offset.x;
		this.offset.y = offset.y;
	}
	
	@Override
	protected void initialize() {		
	}

	@Override
	protected void render(SpriteBatch batch) {
		if(sprite == null || sprite.getTexture() == null)
			return;
		
		//sprite.setOrigin(transform.origin.x, transform.origin.y);
		sprite.setRotation(transform.getRotation());
		sprite.setScale(transform.getScaleX(), transform.getScaleY());
		sprite.setPosition(transform.getX() + offset.x, transform.getY() + offset.y);
		sprite.draw(batch);
	}
}
