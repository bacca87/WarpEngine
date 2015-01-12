
package com.marcobaccarani.warp;

public abstract class ConsoleAction {
	public static final String KEYDOWN = "down";
	public static final String KEYUP = "up";

	private boolean keyDown = false;

	public ConsoleAction (final String name, final String description) {
		Console.addCommand(name, new Action() {
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
					keyDown();
					break;
				case KEYUP:
					keyUp();
					break;
				default:
					printError();
				}
			}

			private void printError () {
				Console.out.println("Arguments:\n\t" + KEYDOWN + ": Key down\n\t" + KEYUP + ": Key up");
			}
		});
	}

	public boolean isKeyDown () {
		return keyDown;
	}

	public abstract void keyDown ();

	public abstract void keyUp ();
}
