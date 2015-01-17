
package com.marcobaccarani.warp;


public final class ConsoleBool implements ConsoleVariable {
	private boolean value;

	public ConsoleBool (final String name, boolean value, final String description) {
		this.value = value;

		Console.addCommand(name, new Command() {
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
		});
	}

	public boolean isValue () {
		return value;
	}

	public void setValue (boolean value) {
		this.value = value;
	}
}
