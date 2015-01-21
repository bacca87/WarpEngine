
package com.marcobaccarani.warp.console;

public class ConsoleCommand implements Command {
	private CommandListener listener = null;
	private String name;
	private String description;

	public ConsoleCommand (final String name, final String description) {
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
		if (listener != null) listener.execute(args);
	}

	public CommandListener getListener () {
		return listener;
	}

	public void setListener (CommandListener listener) {
		this.listener = listener;
	}
}
