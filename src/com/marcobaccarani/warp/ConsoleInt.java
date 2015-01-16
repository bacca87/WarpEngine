
package com.marcobaccarani.warp;

public final class ConsoleInt implements ConsoleVariable {
	private int value;

	public ConsoleInt (final String name, int value, final String description) {
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
					setValue(Integer.parseInt(args[1]));
				} catch (Exception ex) {
					Console.out.println("Error: Invalid integer.");
				}
			}
		});
	}

	public int getValue () {
		return value;
	}

	public void setValue (int value) {
		this.value = value;
	}
}
