
package com.marcobaccarani.warp.console;


public final class ConsoleInt implements Command {
	private int value;
	private String name;
	private String description;

	public ConsoleInt (final String name, int value, final String description) {
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
			Console.out.print(getValue());
			return;
		}

		try {
			setValue(Integer.parseInt(args[1]));
		} catch (Exception ex) {
			Console.out.println("Error: Invalid integer.");
		}
	}
	
	public int getValue () {
		return value;
	}

	public void setValue (int value) {
		this.value = value;
	}
}
