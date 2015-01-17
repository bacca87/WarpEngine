
package com.marcobaccarani.warp;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.badlogic.gdx.Gdx;

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
	private static HashMap<String, Command> commands = new HashMap<String, Command>();
	private static Pattern regex = Pattern.compile("([^\"]\\S*|\".+?\")\\s*");
	private static final int COMMAND_MAX_LENGHT = 30;

	static {
		EngineCommands.list.setListener(new CommandListener() {
			@Override
			public void execute (String[] args) {
				Map<String, Command> orderedCommands = new TreeMap<String, Command>();
				orderedCommands.putAll(commands);
	
				out.print("All available commands listed below:\n\n");
	
				for (Entry<String, Command> entry : orderedCommands.entrySet()) {
					out.print(String.format("%1$-" + Console.COMMAND_MAX_LENGHT + "s", entry.getKey())
						+ (entry.getValue().getDescription() == null ? "\n" : " - "
							+ entry.getValue().getDescription().replace("\n", "").replace("\r", "") + "\n"));
				}
			}
		});
	}
	
	Console () {
	}

	static void addCommand (final String name, Command command) {
		if (name == null) throw new IllegalArgumentException("The command name can't be null!");
		if (name.length() > COMMAND_MAX_LENGHT) throw new IllegalArgumentException("The command name must be maximum 30 char long!");
		if (commands.containsKey(name)) throw new IllegalArgumentException("Command \"" + name + "\" already exist!");
		if (command == null) throw new IllegalArgumentException("The command can't be null!");

		commands.put(name, command);
	}

	static void executeCommand (final String command) {
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
}
