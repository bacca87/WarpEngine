package com.marcobaccarani.warp.ecs.components;

import java.util.ArrayList;

import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Vector2;
import com.marcobaccarani.warp.ecs.Component;

// TODO: vedere se ottimizzare il componente in modo che se non ha figli o padri non faccia i calcoli sulle matrici ma utilizzi direttamente le posizioni.
// 		 Questo perche normalmente la maggior parte delle entità non saranno annidate ma elementi singoli.

public class TransformComponent extends Component {
	private Vector2 position = new Vector2();
	private Vector2 scale = new Vector2(1,1);
	private float rotation = 0.0f;
	
	private Vector2 localPosition = new Vector2();
	private Vector2 localScale = new Vector2(1,1);
	private float localRotation = 0.0f;
	
	private Matrix3 localTransform = new Matrix3();
	private Matrix3 worldTransform = new Matrix3();
	private Matrix3 invWorldTransform = new Matrix3();
	
	private TransformComponent parent;

	private ArrayList<TransformComponent> childs = new ArrayList<TransformComponent>();

	public void addChild(TransformComponent transform) {
		transform.setParent(this);
		childs.add(transform);
	}
	
	public void removeChild(TransformComponent transform) {
		transform.setParent(null);		
		childs.remove(transform);
	}
	
	protected void calculateWorldTransform() {
        localTransform.idt();
        
        if (localRotation != 0) localTransform.rotate(localRotation);
        if (localScale.x != 1 || localScale.y != 1) localTransform.scale(localScale.x, localScale.y);
        localTransform.trn(localPosition.x, localPosition.y);

        if(parent != null)
        	worldTransform.set(parent.worldTransform).mul(localTransform);
        else
        	worldTransform.set(localTransform);
        
        invWorldTransform.set(worldTransform).inv();
        
        worldTransform.getTranslation(position);
        worldTransform.getScale(scale);
        rotation = worldTransform.getRotation();
        
        calculateChildsWorldTransform();
	}
		
	protected void calculateLocalTransform() {
		worldTransform.idt();
        
        if (rotation != 0) worldTransform.rotate(rotation);
        if (scale.x != 1 || scale.y != 1) worldTransform.scale(scale.x, scale.y);
        worldTransform.trn(position.x, position.y);
        invWorldTransform.set(worldTransform).inv();

        if(parent != null)
        	localTransform.set(parent.invWorldTransform).mul(worldTransform);
        else
        	localTransform.set(worldTransform);        	

        localTransform.getTranslation(localPosition);
        localTransform.getScale(localScale);
        localRotation = localTransform.getRotation();
        
        calculateChildsWorldTransform();
	}
	
	protected void calculateChildsWorldTransform() {
		for(TransformComponent transform : childs) {
			transform.calculateWorldTransform();			
			transform.calculateChildsWorldTransform();
		}
	}
		
	public TransformComponent getParent() {
		return parent;
	}
	
	private void setParent(TransformComponent parent) {
		this.parent = parent;
		calculateLocalTransform();
	}
		
	public float getX() {
		return position.x;
	}
	
	public float getY() {
		return position.y;
	}
	
	public float getLocalX() {
		return localPosition.x;
	}
	
	public float getLocalY() {
		return localPosition.y;
	}
		
	public float getScaleX() {
		return scale.x;
	}
	
	public float getScaleY() {
		return scale.y;
	}
	
	public float getRotation() {
		return rotation;
	}
	
	public float getLocalScaleX() {
		return localScale.x;
	}
	
	public float getLocalScaleY() {
		return localScale.y;
	}
	
	public float getLocalRotation() {
		return localRotation;
	}
	
	public TransformComponent setX(float x) {
		position.x = x;
		calculateLocalTransform();
		return this;
	}
	
	public TransformComponent setY(float y) {
		position.y = y;
		calculateLocalTransform();
		return this;
	}
	
	public TransformComponent setXY(float x, float y) {
		position.x = x;
		position.y = y;
		calculateLocalTransform();
		return this;
	}
	
	public TransformComponent setXY(Vector2 position) {
		this.position.x = position.x;
		this.position.y = position.y;
		calculateLocalTransform();
		return this;
	}
	
	public TransformComponent setLocalX(float x) {
		localPosition.x = x;
		calculateWorldTransform();
		return this;
	}
	
	public TransformComponent setLocalY(float y) {
		localPosition.y = y;
		calculateWorldTransform();
		return this;
	}
	
	public TransformComponent setLocalXY(float x, float y) {
		localPosition.x = x;
		localPosition.y = y;
		calculateWorldTransform();
		return this;
	}
	
	public TransformComponent setLocalXY(Vector2 localPosition) {
		this.localPosition.x = localPosition.x;
		this.localPosition.y = localPosition.y;
		calculateWorldTransform();
		return this;
	}
	
	public TransformComponent setScaleX(float x) {
		scale.x = x;
		calculateLocalTransform();
		return this;
	}
	
	public TransformComponent setScaleY(float y) {
		scale.y = y;
		calculateLocalTransform();
		return this;
	}
	
	public TransformComponent setScaleXY(float x, float y) {
		scale.x = x;
		scale.y = y;
		calculateLocalTransform();
		return this;
	}
	
	public TransformComponent setScaleXY(Vector2 scale) {
		this.scale.x = scale.x;
		this.scale.y = scale.y;
		calculateLocalTransform();
		return this;
	}
	
	public TransformComponent setRotation(float degrees) {
		rotation = degrees;
		calculateLocalTransform();
		return this;
	}
	
	public TransformComponent setLocalScaleX(float x) {
		localScale.x = x;
		calculateWorldTransform();
		return this;
	}
	
	public TransformComponent setLocalScaleY(float y) {
		localScale.y = y;
		calculateWorldTransform();
		return this;
	}
	
	public TransformComponent setLocalScaleXY(float x, float y) {
		localScale.x = x;
		localScale.y = y;
		calculateWorldTransform();
		return this;
	}
	
	public TransformComponent setLocalScaleXY(Vector2 localScale) {
		this.localScale.x = localScale.x;
		this.localScale.y = localScale.y;
		calculateWorldTransform();
		return this;
	}
	
	public TransformComponent setLocalRotation(float degrees) {
		localRotation = degrees;
		calculateWorldTransform();
		return this;
	}
}
