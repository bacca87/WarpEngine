package com.marcobaccarani.warp;

public class ConsoleFloat implements ConsoleVariable {
	private float value;

	public ConsoleFloat (final String name, float value, final String description) {
		this.value = value;

		Console.addCommand(name, new Command() {
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
				} 
				catch (Exception ex) {
					Console.out.println("Error: Invalid float.");
				}
			}
		});
	}

	public float getValue () {
		return value;
	}

	public void setValue (float value) {
		this.value = value;
	}
}
