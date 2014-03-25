package com.marcobaccarani.src.warp.ecs.components;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.marcobaccarani.src.warp.ecs.Component;
import com.marcobaccarani.src.warp.ecs.externals.GUIManager;

public class GUIComponent extends Component {
	private GUIManager gui;
	
	public GUIComponent(GUIManager gui) {
		this.gui = gui;
	}
	
	public void addActor(Actor actor) {
		gui.addActor(actor);
	}
}
