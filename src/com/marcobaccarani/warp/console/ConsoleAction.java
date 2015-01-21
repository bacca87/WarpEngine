
package com.marcobaccarani.warp.console;


public class ConsoleAction implements Command {
	public static final String KEYDOWN = "1";
	public static final String KEYUP = "0";

	private boolean keyDown = false;
	private ActionListener listener = null;
	private String name;
	private String description;

	public ConsoleAction (final String name, final String description) {
		this.name = name;
		this.description = description;
		Console.addCommand(this);
	}

	@Override
	public String getName () {
		return name;
	}
	
	@Override
	public String getDescription () {
		return description;
	}

	@Override
	public void execute (String[] args) {
		if (args.length < 2) {
			printError();
			return;
		}

		switch (args[1]) {
		case KEYDOWN:
			if (!isKeyDown()) {
				keyDown = true;
				if (listener != null) listener.keyDown();
			}
			break;

		case KEYUP:
			if (isKeyDown()) {
				keyDown = false;
				if (listener != null) listener.keyUp();
			}
			break;
		default:
			printError();
		}
	}

	private void printError () {
		Console.out.println("Arguments:\n\t" + KEYDOWN + ": Start\n\t" + KEYUP + ": Stop");
	}
	
	public boolean isKeyDown () {
		return keyDown;
	}

	public ActionListener getListener () {
		return listener;
	}

	public void setListener (ActionListener listener) {
		this.listener = listener;
	}
}
