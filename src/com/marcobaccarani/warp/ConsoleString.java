package com.marcobaccarani.warp;

public class ConsoleString implements ConsoleVariable {
	private String value;

	public ConsoleString (final String name, final String value, final String description) {
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

				setValue(args[1]);
			}
		});
	}

	public String getValue () {
		return value;
	}

	public void setValue (String value) {
		this.value = value;
	}
}
