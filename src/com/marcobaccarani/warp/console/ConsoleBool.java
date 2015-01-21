
package com.marcobaccarani.warp.console;

public final class ConsoleBool implements Command {
	private boolean value;
	private String name;
	private String description;

	public ConsoleBool (final String name, boolean value, final String description) {
		this.value = value;
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
			Console.out.print(isValue());
			return;
		}

		switch (args[1]) {
		case "1":
		case "true":
			setValue(true);
			return;
		case "0":
		case "false":
			setValue(false);
			return;
		default:
			Console.out.println("Error: Invalid boolean.");
		}
	}

	public boolean isValue () {
		return value;
	}

	public void setValue (boolean value) {
		this.value = value;
	}
}
