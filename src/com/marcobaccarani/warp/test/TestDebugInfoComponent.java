package com.marcobaccarani.warp.test;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.marcobaccarani.warp.ecs.Component;
import com.marcobaccarani.warp.gui.GUIComponent;

public class TestDebugInfoComponent extends Component {
	private GUIComponent gui;
	private Label fpsLabel;
	private Label entitiesLabel;
	private BitmapFont font;
	
	@Override
	protected void initialize() {
		gui = entity.getComponent(GUIComponent.class);
		
		font = new BitmapFont();		
		LabelStyle style = new LabelStyle(font, Color.WHITE);
		
		fpsLabel = new Label("FPS: 0", style);
		fpsLabel.setColor(Color.MAGENTA);
		
		entitiesLabel = new Label("Entities: 0", style);
		entitiesLabel.setColor(Color.MAGENTA);
		
		Table table = new Table();		
		table.setPosition(Gdx.graphics.getWidth() - 75, Gdx.graphics.getHeight() - 45);		
		table.debug();
		table.add(fpsLabel).align(Align.right);
		table.row();
		table.add(entitiesLabel).align(Align.right);
		table.pack();
		
		gui.addActor(table);
	}
	
	@Override 
	protected void update(float deltaTime) {
		entitiesLabel.setText("Entities: " + entity.getSystem().getEntitiesCount());
		fpsLabel.setText("FPS: " + Gdx.graphics.getFramesPerSecond());
	}
	
	@Override
	protected void removed() {
		font.dispose();
	}
}
