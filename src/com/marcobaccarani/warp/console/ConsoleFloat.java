
package com.marcobaccarani.warp.console;

public class ConsoleFloat implements Command {
	private float value;
	private String name;
	private String description;

	public ConsoleFloat (final String name, float value, final String description) {
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
			setValue(Float.parseFloat(args[1].replace(',', '.')));
		} catch (Exception ex) {
			Console.out.println("Error: Invalid float.");
		}
	}

	public float getValue () {
		return value;
	}

	public void setValue (float value) {
		this.value = value;
	}
}
