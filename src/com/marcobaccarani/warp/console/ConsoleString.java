package com.marcobaccarani.warp.console;


public class ConsoleString implements Command {
	private String value;
	private String name;
	private String description;

	public ConsoleString (final String name, final String value, final String description) {
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

		setValue(args[1]);
	}
	
	public String getValue () {
		return value;
	}

	public void setValue (String value) {
		this.value = value;
	}
}
