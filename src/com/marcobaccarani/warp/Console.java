
package com.marcobaccarani.warp;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics.DisplayMode;

public final class Console {
	private static final OutputStream stdOut = new OutputStream() {
		@Override
		public void write (final int b) throws IOException {
			Gdx.app.log("[WarpConsole]", String.valueOf(b));
		}

		@Override
		public void write (byte[] b, int off, int len) throws IOException {
			Gdx.app.log("[WarpConsole]", new String(b, off, len));
		}

		@Override
		public void write (byte[] b) throws IOException {
			Gdx.app.log("[WarpConsole]", new String(b));
		}
	};

	public static PrintStream out = new PrintStream(stdOut);
	private static Map<String, Command> commands = new HashMap<String, Command>();
	private static Pattern regex = Pattern.compile("([^\"]\\S*|\".+?\")\\s*");
	private final static int commandMaxLenght = 30;

	private Console () {
	}

	public static void addCommand (final String name, Command command) {
		if (name == null) throw new IllegalArgumentException("The command name can't be null!");
		if (name.length() > commandMaxLenght) throw new IllegalArgumentException("The command name must be maximum 30 char long!");
		if (commands.containsKey(name)) throw new IllegalArgumentException("Command \"" + name + "\" already exist!");
		if (command == null) throw new IllegalArgumentException("The command can't be null!");

		commands.put(name, command);
	}

	public static void executeCommand (final String command) {
		ArrayList<String> arglist = new ArrayList<String>();
		String args[];

		Matcher m = regex.matcher(command);

		while (m.find())
			arglist.add(m.group(1).replace("\"", ""));

		args = arglist.toArray(new String[arglist.size()]);

		try {
			if (args.length == 0) return;

			Command cmd = commands.get(args[0]);

			if (cmd != null)
				cmd.execute(args);
			else
				out.print("Error: Unknown command!\nType \"list\" for showing all available commands.");
		} catch (Exception ex) {
			ex.printStackTrace(out);
		}
	}

	static void executeCommand (Command command, String... args) {
		if (command == null) throw new IllegalArgumentException("The command can't be null!");

		try {
			command.execute(args);
		} catch (Exception ex) {
			ex.printStackTrace(out);
		}
	}

	static Command getCommand (String name) {
		return commands.get(name);
	}

	static {
		Console.addCommand("list", new Command() {
			@Override
			public void execute (String[] args) {
				Map<String, Command> orderedCommands = new TreeMap<String, Command>();
				orderedCommands.putAll(commands);

				Console.out.print("All available commands listed below:\n\n");

				for (Entry<String, Command> entry : orderedCommands.entrySet()) {
					Console.out.print(String.format("%1$-" + commandMaxLenght + "s", entry.getKey())
						+ (entry.getValue().getDescription() == null ? "\n" : " - "
							+ entry.getValue().getDescription().replace("\n", "").replace("\r", "") + "\n"));
				}
			}

			@Override
			public String getDescription () {
				return "Show all available commands";
			}
		});

		Console.addCommand("quit", new Command() {
			@Override
			public void execute (String[] args) {
				Console.out.print("Bye :'(");
				Gdx.app.exit();
			}

			@Override
			public String getDescription () {
				return "Exit the game";
			}
		});

		Console.addCommand("echo", new Command() {
			@Override
			public void execute (String[] args) {
				for (int i = 1; i < args.length; i++)
					Console.out.print(args[i] + " ");
			}

			@Override
			public String getDescription () {
				return "Echo text to console";
			}
		});

		Console.addCommand("r_vsync", new Command() {
			@Override
			public void execute (String[] args) {
				if (args.length < 2) {
					displayHelp();
					return;
				}

				switch (args[1]) {
				case "1":
					Gdx.graphics.setVSync(true);
					break;
				case "0":
					Gdx.graphics.setVSync(false);
					break;
				default:
					displayHelp();
				}
			}

			@Override
			public String getDescription () {
				return "Enable/Disable vertical sync";
			}

			private void displayHelp () {
				Console.out.print("Arguments:\n\t0: vsync OFF\n\t1: vsync ON");
			}
		});

		Console.addCommand("r_displaymode", new Command() {
			@Override
			public void execute (String[] args) {
				int width = 0;
				int height = 0;
				boolean fullscreen = false;

				if (args.length == 1) {
					displayHelp();
					return;
				}

				switch (args[1]) {
				case "list":
					DisplayMode modes[] = Gdx.graphics.getDisplayModes();
					Arrays.sort(modes, new Comparator<DisplayMode>() {
						@Override
						public int compare (DisplayMode arg0, DisplayMode arg1) {
							if (arg0.width < arg1.width)
								return -1;
							else if (arg0.width > arg1.width)
								return 1;
							else if (arg0.height < arg1.height)
								return -1;
							else if (arg0.height > arg1.height)
								return 1;
							else
								return 0;
						}
					});

					ArrayList<String> displayModes = new ArrayList<String>();

					Console.out.println("Available fullscreen display modes:");

					for (DisplayMode mode : modes) {
						String strMode = mode.width + " x " + mode.height;

						if (mode.bitsPerPixel == Gdx.graphics.getDesktopDisplayMode().bitsPerPixel
							&& mode.refreshRate == Gdx.graphics.getDesktopDisplayMode().refreshRate && !displayModes.contains(strMode)) {
							displayModes.add(strMode);
							Console.out.println("\t" + strMode);
						}
					}

					Console.out.print("\nType \"r_displaymode [width] [height]\" for set the display mode.");
					break;

				case "fullscreen":
					if (!Gdx.graphics.setDisplayMode(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true))
						Console.out
							.print("Error: The current display mode is not valid for fullscreen.\nType \"r_displaymode list\" for showing all the supported modes.");
					break;

				case "windowed":
					Gdx.graphics.setDisplayMode(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
					break;

				case "default":
					Gdx.graphics.setDisplayMode(Gdx.graphics.getDesktopDisplayMode());
					break;

				case "current":
					Console.out.print(Gdx.graphics.getWidth() + "x" + Gdx.graphics.getHeight() + " "
						+ (Gdx.graphics.isFullscreen() ? "fullscreen" : "windowed"));
					break;

				default:
					try {
						if (args.length < 3) {
							displayError();
							return;
						}

						width = Integer.parseUnsignedInt(args[1]);
						height = Integer.parseUnsignedInt(args[2]);

						if (args.length == 3)
							fullscreen = Gdx.graphics.isFullscreen();
						else if (args[3].equals("fullscreen"))
							fullscreen = true;
						else if (args[3].equals("windowed"))
							fullscreen = false;
						else {
							displayError();
							return;
						}

						if (!Gdx.graphics.setDisplayMode(width, height, fullscreen))
							Console.out
								.print("Error: invalid display mode.\nType \"r_displaymode list\" for showing all the supported modes.");
					} catch (Exception ex) {
						displayError();
					}
					break;
				}
			}

			void displayError () {
				Console.out.print("Error: invalid arguments");
				displayHelp();
			}

			void displayHelp () {
				Console.out
					.print("Usage:\n"
						+ "\tr_displaymode [width] [height] {optional [fullscreen/windowed]}   - Set the display mode.\n"
						+ "\tr_displaymode [fullscreen/windowed]                               - Set the current display mode [fullscreen/windowed].\n"
						+ "\tr_displaymode default                                             - Set the default display mode.\n"
						+ "\tr_displaymode current                                             - Show the current display mode.\n"
						+ "\tr_displaymode list                                                - Show all available fullscreen display modes.\n"
						+ "\nExample:\n\tr_displaymode 1920 1080 fullscreen");
			}

			@Override
			public String getDescription () {
				return "Set the display mode";
			}
		});

		// TODO: togliere i bordi alla finestra si puo fare solo prima di lanciare l'applicazione desktop, vedere come fare
	}
}
