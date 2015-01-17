
package com.marcobaccarani.warp;


public class ConsoleCommand {
	private CommandListener listener = null;
	private String description;

	public ConsoleCommand (final String name, final String description) {
		this.description = description;
		
		Console.addCommand(name, new Command() {
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

	public String getDescription() {
		return description;
	}
	
	public CommandListener getListener () {
		return listener;
	}

	public void setListener (CommandListener listener) {
		this.listener = listener;
	}
}
