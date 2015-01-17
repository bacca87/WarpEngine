
package com.marcobaccarani.warp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics.DisplayMode;

class EngineCommands {
	static final ConsoleCommand list = new ConsoleCommand("list", "Show all available commands");
	static final ConsoleCommand quit = new ConsoleCommand("quit", "Exit the game");
	static final ConsoleCommand echo = new ConsoleCommand("echo", "Echo text to console");
	static final ConsoleCommand r_vsync = new ConsoleCommand("r_vsync", "Enable/Disable vertical sync");
	static final ConsoleCommand r_displaymode = new ConsoleCommand("r_displaymode", "Set the display mode");
	static final ConsoleCommand bind = new ConsoleCommand("bind", "Bind a Key to specific command");
	static final ConsoleCommand clear = new ConsoleCommand("clear", "Clear the console output");

	public static void init () {
		quit.setListener(new CommandListener() {
			@Override
			public void execute (String[] args) {
				Console.out.print("Bye :'(");
				Gdx.app.exit();
			}
		});

		echo.setListener(new CommandListener() {
			@Override
			public void execute (String[] args) {
				for (int i = 1; i < args.length; i++)
					Console.out.print(args[i] + " ");
			}
		});

		r_vsync.setListener(new CommandListener() {
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

			private void displayHelp () {
				Console.out.print("Arguments:\n\t0: vsync OFF\n\t1: vsync ON");
			}
		});

		r_displaymode.setListener(new CommandListener() {
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
		});

		// TODO: togliere i bordi alla finestra si puo fare solo prima di lanciare l'applicazione desktop, vedere come fare
	}
}
