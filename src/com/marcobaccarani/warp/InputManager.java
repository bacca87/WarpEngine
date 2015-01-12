
package com.marcobaccarani.warp;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerAdapter;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.Pool;

public final class InputManager implements Input {
	static private IntMap<Binding> bindings = new IntMap<Binding>();

	private InputMultiplexer multiplexer = new InputMultiplexer();
	private Array<InputEvent> events = new Array<InputEvent>();

	private Pool<InputEvent> eventsPool = new Pool<InputEvent>(16, 1000) {
		@Override
		protected InputEvent newObject () {
			return new InputEvent();
		}
	};

	private InputProcessor inputListener = new InputAdapter() {
		@Override
		public boolean keyDown (int keycode) {
			InputEvent event = eventsPool.obtain();
			event.type = Type.KEYBOARD;
			event.state = State.KEY_DOWN;
			event.keyCode = keycode;
			events.add(event);
			return false;
		}

		@Override
		public boolean keyUp (int keycode) {
			InputEvent event = eventsPool.obtain();
			event.type = Type.KEYBOARD;
			event.state = State.KEY_UP;
			event.keyCode = keycode;
			events.add(event);
			return false;
		}

		@Override
		public boolean touchDown (int screenX, int screenY, int pointer, int button) {
			InputEvent event = eventsPool.obtain();
			event.type = Type.MOUSE;
			event.state = State.KEY_DOWN;
			event.keyCode = button;
			events.add(event);
			return false;
		}

		@Override
		public boolean touchUp (int screenX, int screenY, int pointer, int button) {
			InputEvent event = eventsPool.obtain();
			event.type = Type.MOUSE;
			event.state = State.KEY_UP;
			event.keyCode = button;
			events.add(event);
			return false;
		}
	};

	ControllerListener controllerListener = new ControllerAdapter() {
		@Override
		public boolean buttonDown (Controller controller, int buttonCode) {
			InputEvent event = eventsPool.obtain();
			event.type = Type.CONTROLLER;
			event.state = State.KEY_DOWN;
			event.keyCode = buttonCode;
			events.add(event);
			return false;
		}

		@Override
		public boolean buttonUp (Controller controller, int buttonCode) {
			InputEvent event = eventsPool.obtain();
			event.type = Type.CONTROLLER;
			event.state = State.KEY_UP;
			event.keyCode = buttonCode;
			events.add(event);
			return false;
		}
	};

	InputManager () {
		Gdx.input.setInputProcessor(multiplexer);
		Controllers.addListener(controllerListener);
		addInputProcessor(inputListener);
	}

	void update () {
		processEvents();
	}

	private void processEvents () {
		Binding binding = null;

		for (InputEvent event : events) {
			binding = bindings.get(event.keyCode);

			if (binding != null) {
				switch (event.state) {
				case KEY_DOWN:
					if (binding.isAction)
						Console.executeCommand(binding.command, binding.name, ConsoleAction.KEYDOWN);
					else
						Console.executeCommand(binding.command, binding.name);
					break;
				case KEY_UP:
					if (binding.isAction) Console.executeCommand(binding.command, binding.name, ConsoleAction.KEYUP);
					break;
				}
			}

			eventsPool.free(event);
		}

		events.clear();
	}

	@Override
	public void addInputProcessor (InputProcessor processor) {
		multiplexer.addProcessor(processor);
	}

	@Override
	public void removeInputProcessor (InputProcessor processor) {
		multiplexer.removeProcessor(processor);
	}

	@Override
	public void addControllerProcessor (ControllerListener listener) {
		Controllers.addListener(listener);
	}

	@Override
	public void removeControllerProcessor (ControllerListener listener) {
		Controllers.removeListener(listener);
	}

	static {
		Console.addCommand("bind", new Command() {

			@Override
			public String getDescription () {
				return "Bind a Key to specific command";
			}

			@Override
			public void execute (String[] args) {
				bindings.put(Keys.valueOf(args[1]), new Binding(args[2], Console.getCommand(args[2])));
			}
		});
	}

	public enum Type {
		KEYBOARD, MOUSE, CONTROLLER
	}

	public enum State {
		KEY_DOWN, KEY_UP
	}

	private class InputEvent {
		public Type type;
		public State state;
		int keyCode;
	}

	private static class Binding {
		public Command command;
		public String name;
		public boolean isAction;

		public Binding (String name, Command command) {
			this.command = command;
			this.name = name;
			this.isAction = command instanceof Action;
		}
	}
}
