
package com.marcobaccarani.warp;

import java.util.HashMap;

import com.badlogic.gdx.Application.ApplicationType;
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
	private HashMap<String, Integer> keys = new HashMap<String, Integer>();
	private IntMap<Binding> bindings = new IntMap<Binding>();

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
			event.type = KeyType.KEYBOARD;
			event.state = KeyState.KEY_DOWN;
			event.keyCode = keycode;
			events.add(event);
			return false;
		}

		@Override
		public boolean keyUp (int keycode) {
			InputEvent event = eventsPool.obtain();
			event.type = KeyType.KEYBOARD;
			event.state = KeyState.KEY_UP;
			event.keyCode = keycode;
			events.add(event);
			return false;
		}

		@Override
		public boolean touchDown (int screenX, int screenY, int pointer, int button) {
			InputEvent event = eventsPool.obtain();
			event.type = KeyType.MOUSE;
			event.state = KeyState.KEY_DOWN;
			event.keyCode = button;
			events.add(event);
			return false;
		}

		@Override
		public boolean touchUp (int screenX, int screenY, int pointer, int button) {
			InputEvent event = eventsPool.obtain();
			event.type = KeyType.MOUSE;
			event.state = KeyState.KEY_UP;
			event.keyCode = button;
			events.add(event);
			return false;
		}
	};

	private ControllerListener controllerListener = new ControllerAdapter() {
		@Override
		public boolean buttonDown (Controller controller, int buttonCode) {
			InputEvent event = eventsPool.obtain();
			event.type = KeyType.CONTROLLER;
			event.state = KeyState.KEY_DOWN;
			event.keyCode = buttonCode;
			events.add(event);
			return false;
		}

		@Override
		public boolean buttonUp (Controller controller, int buttonCode) {
			InputEvent event = eventsPool.obtain();
			event.type = KeyType.CONTROLLER;
			event.state = KeyState.KEY_UP;
			event.keyCode = buttonCode;
			events.add(event);
			return false;
		}
	};

	private CommandListener bindListener = new CommandListener() {
		@Override
		public void execute (String[] args) {
			if (args.length < 3) {
				printHelp();
				return;
			}

			Integer keyHash = keys.get(args[1].toUpperCase());

			if (keyHash == null) {
				Console.out.println("Error: Invalid KeyCode.");
				return;
			}

			Command cmd = Console.getCommand(args[2]);

			if (cmd == null) {
				Console.out.println("Error: Command \"" + args[2] + "\" doesn't exist.");
				Console.out.println("Type \"list\" for showing all available commands.");
				return;
			}

			bindings.put(keyHash, new Binding(args[2], cmd));
		}

		private void printHelp () {
			Console.out.println(EngineCommands.bind.getDescription());
			Console.out.println("Usage:");
			Console.out.println("\tbind [keycode] [command]");
			Console.out.println("Example:");
			Console.out.println("\tbind SPACE jump");
		}
	};

	InputManager () {
		initKeys();

		Gdx.input.setInputProcessor(multiplexer);
		Controllers.addListener(controllerListener);
		addInputProcessor(inputListener);

		EngineCommands.bind.setListener(bindListener);
	}

	void update () {
		processEvents();
	}

	private void processEvents () {
		Binding binding = null;

		for (InputEvent event : events) {
			binding = bindings.get(getKeyHash(event.keyCode, event.type));

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

	private int getKeyHash (int keyCode, KeyType type) {
		return keyCode + type.hashCode();
	}

	private void initKeys () {
		keys.put("0", getKeyHash(Keys.NUM_0, KeyType.KEYBOARD));
		keys.put("1", getKeyHash(Keys.NUM_1, KeyType.KEYBOARD));
		keys.put("2", getKeyHash(Keys.NUM_2, KeyType.KEYBOARD));
		keys.put("3", getKeyHash(Keys.NUM_3, KeyType.KEYBOARD));
		keys.put("4", getKeyHash(Keys.NUM_4, KeyType.KEYBOARD));
		keys.put("5", getKeyHash(Keys.NUM_5, KeyType.KEYBOARD));
		keys.put("6", getKeyHash(Keys.NUM_6, KeyType.KEYBOARD));
		keys.put("7", getKeyHash(Keys.NUM_7, KeyType.KEYBOARD));
		keys.put("8", getKeyHash(Keys.NUM_8, KeyType.KEYBOARD));
		keys.put("9", getKeyHash(Keys.NUM_9, KeyType.KEYBOARD));
		keys.put("A", getKeyHash(Keys.A, KeyType.KEYBOARD));
		keys.put("B", getKeyHash(Keys.B, KeyType.KEYBOARD));
		keys.put("C", getKeyHash(Keys.C, KeyType.KEYBOARD));
		keys.put("D", getKeyHash(Keys.D, KeyType.KEYBOARD));
		keys.put("E", getKeyHash(Keys.E, KeyType.KEYBOARD));
		keys.put("F", getKeyHash(Keys.F, KeyType.KEYBOARD));
		keys.put("G", getKeyHash(Keys.G, KeyType.KEYBOARD));
		keys.put("H", getKeyHash(Keys.H, KeyType.KEYBOARD));
		keys.put("I", getKeyHash(Keys.I, KeyType.KEYBOARD));
		keys.put("J", getKeyHash(Keys.J, KeyType.KEYBOARD));
		keys.put("K", getKeyHash(Keys.K, KeyType.KEYBOARD));
		keys.put("L", getKeyHash(Keys.L, KeyType.KEYBOARD));
		keys.put("M", getKeyHash(Keys.M, KeyType.KEYBOARD));
		keys.put("N", getKeyHash(Keys.N, KeyType.KEYBOARD));
		keys.put("O", getKeyHash(Keys.O, KeyType.KEYBOARD));
		keys.put("P", getKeyHash(Keys.P, KeyType.KEYBOARD));
		keys.put("Q", getKeyHash(Keys.Q, KeyType.KEYBOARD));
		keys.put("R", getKeyHash(Keys.R, KeyType.KEYBOARD));
		keys.put("S", getKeyHash(Keys.S, KeyType.KEYBOARD));
		keys.put("T", getKeyHash(Keys.T, KeyType.KEYBOARD));
		keys.put("U", getKeyHash(Keys.U, KeyType.KEYBOARD));
		keys.put("V", getKeyHash(Keys.V, KeyType.KEYBOARD));
		keys.put("W", getKeyHash(Keys.W, KeyType.KEYBOARD));
		keys.put("X", getKeyHash(Keys.X, KeyType.KEYBOARD));
		keys.put("Y", getKeyHash(Keys.Y, KeyType.KEYBOARD));
		keys.put("Z", getKeyHash(Keys.Z, KeyType.KEYBOARD));
		keys.put("TAB", getKeyHash(Keys.TAB, KeyType.KEYBOARD));
		keys.put("SPACE", getKeyHash(Keys.SPACE, KeyType.KEYBOARD));
		keys.put("SUPER", getKeyHash(Keys.SYM, KeyType.KEYBOARD));
		keys.put("ENTER", getKeyHash(Keys.ENTER, KeyType.KEYBOARD));
		keys.put("BACKSPACE", getKeyHash(Keys.DEL, KeyType.KEYBOARD));
		keys.put("`", getKeyHash(Keys.GRAVE, KeyType.KEYBOARD));
		keys.put("+", getKeyHash(Keys.PLUS, KeyType.KEYBOARD));
		keys.put("-", getKeyHash(Keys.MINUS, KeyType.KEYBOARD));
		keys.put("=", getKeyHash(Keys.EQUALS, KeyType.KEYBOARD));
		keys.put("*", getKeyHash(Keys.STAR, KeyType.KEYBOARD));
		keys.put("#", getKeyHash(Keys.POUND, KeyType.KEYBOARD));
		keys.put("[", getKeyHash(Keys.LEFT_BRACKET, KeyType.KEYBOARD));
		keys.put("]", getKeyHash(Keys.RIGHT_BRACKET, KeyType.KEYBOARD));
		keys.put("\\", getKeyHash(Keys.BACKSLASH, KeyType.KEYBOARD));
		keys.put(":", getKeyHash(Keys.COLON, KeyType.KEYBOARD));
		keys.put(",", getKeyHash(Keys.COMMA, KeyType.KEYBOARD));
		keys.put(".", getKeyHash(Keys.PERIOD, KeyType.KEYBOARD));
		keys.put(";", getKeyHash(Keys.SEMICOLON, KeyType.KEYBOARD));
		keys.put("'", getKeyHash(Keys.APOSTROPHE, KeyType.KEYBOARD));
		keys.put("/", getKeyHash(Keys.SLASH, KeyType.KEYBOARD));
		keys.put("@", getKeyHash(Keys.AT, KeyType.KEYBOARD));
		keys.put("ESCAPE", getKeyHash(Keys.ESCAPE, KeyType.KEYBOARD));
		keys.put("CTRL_LEFT", getKeyHash(Keys.CONTROL_LEFT, KeyType.KEYBOARD));
		keys.put("CTRL_RIGHT", getKeyHash(Keys.CONTROL_RIGHT, KeyType.KEYBOARD));
		keys.put("ALT_LEFT", getKeyHash(Keys.ALT_LEFT, KeyType.KEYBOARD));
		keys.put("ALT_RIGHT", getKeyHash(Keys.ALT_RIGHT, KeyType.KEYBOARD));
		keys.put("SHIFT_LEFT", getKeyHash(Keys.SHIFT_LEFT, KeyType.KEYBOARD));
		keys.put("SHIFT_RIGHT", getKeyHash(Keys.SHIFT_RIGHT, KeyType.KEYBOARD));
		keys.put("UP", getKeyHash(Keys.UP, KeyType.KEYBOARD));
		keys.put("DOWN", getKeyHash(Keys.DOWN, KeyType.KEYBOARD));
		keys.put("LEFT", getKeyHash(Keys.LEFT, KeyType.KEYBOARD));
		keys.put("RIGHT", getKeyHash(Keys.RIGHT, KeyType.KEYBOARD));
		keys.put("HOME", getKeyHash(Keys.HOME, KeyType.KEYBOARD));
		keys.put("END", getKeyHash(Keys.END, KeyType.KEYBOARD));
		keys.put("PGUP", getKeyHash(Keys.PAGE_UP, KeyType.KEYBOARD));
		keys.put("PGDN", getKeyHash(Keys.PAGE_DOWN, KeyType.KEYBOARD));
		keys.put("INSERT", getKeyHash(Keys.INSERT, KeyType.KEYBOARD));
		keys.put("DELETE", getKeyHash(Keys.FORWARD_DEL, KeyType.KEYBOARD));
		keys.put("NUM", getKeyHash(Keys.NUM, KeyType.KEYBOARD));
		keys.put("NUM_0", getKeyHash(Keys.NUMPAD_0, KeyType.KEYBOARD));
		keys.put("NUM_1", getKeyHash(Keys.NUMPAD_1, KeyType.KEYBOARD));
		keys.put("NUM_2", getKeyHash(Keys.NUMPAD_2, KeyType.KEYBOARD));
		keys.put("NUM_3", getKeyHash(Keys.NUMPAD_3, KeyType.KEYBOARD));
		keys.put("NUM_4", getKeyHash(Keys.NUMPAD_4, KeyType.KEYBOARD));
		keys.put("NUM_5", getKeyHash(Keys.NUMPAD_5, KeyType.KEYBOARD));
		keys.put("NUM_6", getKeyHash(Keys.NUMPAD_6, KeyType.KEYBOARD));
		keys.put("NUM_7", getKeyHash(Keys.NUMPAD_7, KeyType.KEYBOARD));
		keys.put("NUM_8", getKeyHash(Keys.NUMPAD_8, KeyType.KEYBOARD));
		keys.put("NUM_9", getKeyHash(Keys.NUMPAD_9, KeyType.KEYBOARD));
		keys.put("F1", getKeyHash(Keys.F1, KeyType.KEYBOARD));
		keys.put("F2", getKeyHash(Keys.F2, KeyType.KEYBOARD));
		keys.put("F3", getKeyHash(Keys.F3, KeyType.KEYBOARD));
		keys.put("F4", getKeyHash(Keys.F4, KeyType.KEYBOARD));
		keys.put("F5", getKeyHash(Keys.F5, KeyType.KEYBOARD));
		keys.put("F6", getKeyHash(Keys.F6, KeyType.KEYBOARD));
		keys.put("F7", getKeyHash(Keys.F7, KeyType.KEYBOARD));
		keys.put("F8", getKeyHash(Keys.F8, KeyType.KEYBOARD));
		keys.put("F9", getKeyHash(Keys.F9, KeyType.KEYBOARD));
		keys.put("F10", getKeyHash(Keys.F10, KeyType.KEYBOARD));
		keys.put("F11", getKeyHash(Keys.F11, KeyType.KEYBOARD));
		keys.put("F12", getKeyHash(Keys.F12, KeyType.KEYBOARD));

		if (Gdx.app.getType() == ApplicationType.Android) {
			keys.put("SOFTLEFT", getKeyHash(Keys.SOFT_LEFT, KeyType.KEYBOARD));
			keys.put("SOFTRIGHT", getKeyHash(Keys.SOFT_RIGHT, KeyType.KEYBOARD));
			keys.put("BACK", getKeyHash(Keys.BACK, KeyType.KEYBOARD));
			keys.put("CALL", getKeyHash(Keys.CALL, KeyType.KEYBOARD));
			keys.put("ENDCALL", getKeyHash(Keys.ENDCALL, KeyType.KEYBOARD));
			keys.put("VOLUMEUP", getKeyHash(Keys.VOLUME_UP, KeyType.KEYBOARD));
			keys.put("VOLUMEDOWN", getKeyHash(Keys.VOLUME_DOWN, KeyType.KEYBOARD));
			keys.put("EXPLORER", getKeyHash(Keys.EXPLORER, KeyType.KEYBOARD));
			keys.put("ENVELOPE", getKeyHash(Keys.ENVELOPE, KeyType.KEYBOARD));
			keys.put("HEADSETHOOK", getKeyHash(Keys.HEADSETHOOK, KeyType.KEYBOARD));
			keys.put("CENTER", getKeyHash(Keys.CENTER, KeyType.KEYBOARD));
			keys.put("POWER", getKeyHash(Keys.POWER, KeyType.KEYBOARD));
			keys.put("CAMERA", getKeyHash(Keys.CAMERA, KeyType.KEYBOARD));
			keys.put("CLEAR", getKeyHash(Keys.CLEAR, KeyType.KEYBOARD));
			keys.put("MENU", getKeyHash(Keys.MENU, KeyType.KEYBOARD));
			keys.put("FOCUS", getKeyHash(Keys.FOCUS, KeyType.KEYBOARD));
			keys.put("NOTIFICATION", getKeyHash(Keys.NOTIFICATION, KeyType.KEYBOARD));
			keys.put("SEARCH", getKeyHash(Keys.SEARCH, KeyType.KEYBOARD));
			keys.put("MEDIAPLAYPAUSE", getKeyHash(Keys.MEDIA_PLAY_PAUSE, KeyType.KEYBOARD));
			keys.put("MEDIASTOP", getKeyHash(Keys.MEDIA_STOP, KeyType.KEYBOARD));
			keys.put("MEDIANEXT", getKeyHash(Keys.MEDIA_NEXT, KeyType.KEYBOARD));
			keys.put("MEDIAPREVIOUS", getKeyHash(Keys.MEDIA_PREVIOUS, KeyType.KEYBOARD));
			keys.put("MEDIAREWIND", getKeyHash(Keys.MEDIA_REWIND, KeyType.KEYBOARD));
			keys.put("MEDIAFASTFORWARD", getKeyHash(Keys.MEDIA_FAST_FORWARD, KeyType.KEYBOARD));
			keys.put("MUTE", getKeyHash(Keys.MUTE, KeyType.KEYBOARD));
			keys.put("PICTSYMBOLS", getKeyHash(Keys.PICTSYMBOLS, KeyType.KEYBOARD));
			keys.put("SWITCHCHARSET", getKeyHash(Keys.SWITCH_CHARSET, KeyType.KEYBOARD));
			keys.put("BUTTON_A", getKeyHash(Keys.BUTTON_A, KeyType.KEYBOARD));
			keys.put("BUTTON_B", getKeyHash(Keys.BUTTON_B, KeyType.KEYBOARD));
			keys.put("BUTTON_C", getKeyHash(Keys.BUTTON_C, KeyType.KEYBOARD));
			keys.put("BUTTON_X", getKeyHash(Keys.BUTTON_X, KeyType.KEYBOARD));
			keys.put("BUTTON_Y", getKeyHash(Keys.BUTTON_Y, KeyType.KEYBOARD));
			keys.put("BUTTON_Z", getKeyHash(Keys.BUTTON_Z, KeyType.KEYBOARD));
			keys.put("BUTTON_L1", getKeyHash(Keys.BUTTON_L1, KeyType.KEYBOARD));
			keys.put("BUTTON_R1", getKeyHash(Keys.BUTTON_R1, KeyType.KEYBOARD));
			keys.put("BUTTON_L2", getKeyHash(Keys.BUTTON_L2, KeyType.KEYBOARD));
			keys.put("BUTTON_R2", getKeyHash(Keys.BUTTON_R2, KeyType.KEYBOARD));
			keys.put("BUTTON_THUMBL", getKeyHash(Keys.BUTTON_THUMBL, KeyType.KEYBOARD));
			keys.put("BUTTON_THUMBR", getKeyHash(Keys.BUTTON_THUMBR, KeyType.KEYBOARD));
			keys.put("BUTTON_START", getKeyHash(Keys.BUTTON_START, KeyType.KEYBOARD));
			keys.put("BUTTON_SELECT", getKeyHash(Keys.BUTTON_SELECT, KeyType.KEYBOARD));
			keys.put("BUTTON_MODE", getKeyHash(Keys.BUTTON_MODE, KeyType.KEYBOARD));
		}
	}

	public enum KeyType {
		KEYBOARD(1000), MOUSE(2000), CONTROLLER(3000);

		private int hash;

		KeyType (int hash) {
			this.hash = hash;
		}

		public int getHash () {
			return hash;
		}
	}

	public enum KeyState {
		KEY_DOWN, KEY_UP
	}

	private class InputEvent {
		public KeyType type;
		public KeyState state;
		int keyCode;
	}

	private class Binding {
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
