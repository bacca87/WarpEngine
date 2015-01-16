
package com.marcobaccarani.warp;

public class ConsoleCommand {
	private CommandListener listener = null;

	public ConsoleCommand (final String name, final String description) {
		Console.addCommand(name, new Action() {
			@Override
			public String getDescription () {
				return description;
			}

			@Override
			public void execute (String[] args) {
				if (listener != null) listener.execute(args);
			}
		});
	}

	public CommandListener getListener () {
		return listener;
	}

	public void setListener (CommandListener listener) {
		this.listener = listener;
	}
}
