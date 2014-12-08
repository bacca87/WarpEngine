package com.marcobaccarani.warp.debug;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import com.badlogic.gdx.Gdx;

public final class WarpConsole {	
	private static final OutputStream stdOut = new OutputStream() {
		@Override
		public void write(final int b) throws IOException {
			Gdx.app.log("[WarpConsole]", String.valueOf(b));
		}
		
		@Override
		public void write(byte[] b, int off, int len) throws IOException {
			Gdx.app.log("[WarpConsole]", new String(b, off, len, "UTF-8"));
		}
		
		@Override
		public void write(byte[] b) throws IOException {
			Gdx.app.log("[WarpConsole]", new String(b, "UTF-8"));
		}
	};
	
	public static PrintStream out = new PrintStream(stdOut);	
	private static Map<String, WarpCommand> commands = new HashMap<String, WarpCommand>();
	
	private WarpConsole() {
	}
	
	public static void addCommand(String name, WarpCommand command) {
		if(!commands.containsKey(command))
			commands.put(name, command);
		else
			 throw new IllegalArgumentException("Command already exist!!!");
	}
	
	public static void executeCommand(String command) {
		String args[] = command.split(" ");
		
		if(args.length > 0 && commands.containsKey(args[0]))
			commands.get(args[0]).executeCommand(args);
		else
			out.print("Error: Unknown command!");
	}
	
	static {
		WarpConsole.addCommand("list", new WarpCommand() {
			@Override
			public void executeCommand(String[] args) {
				Map<String, WarpCommand> treeMap = new TreeMap<String, WarpCommand>(commands);
				
				for(String key : treeMap.keySet()) {
					WarpConsole.out.print(key + "\n");
				}
			}
		});
		
		WarpConsole.addCommand("quit", new WarpCommand() {
			@Override
			public void executeCommand(String[] args) {				
				Gdx.app.exit();
			}
		});
		
		WarpConsole.addCommand("r_vsync", new WarpCommand() {
			@Override
			public void executeCommand(String[] args) {
				if(args[1] == "1")
					Gdx.graphics.setVSync(true);
				if(args[1] == "0")
					Gdx.graphics.setVSync(false);
			}
		});
		
		WarpConsole.addCommand("r_displaymode", new WarpCommand() {
			@Override
			public void executeCommand(String[] args) {
				Gdx.graphics.setDisplayMode(Integer.parseInt(args[1]), Integer.parseInt(args[2]), Gdx.graphics.isFullscreen());
			}
		});
		
		WarpConsole.addCommand("r_fullscreen", new WarpCommand() {
			@Override
			public void executeCommand(String[] args) {
				Gdx.graphics.setDisplayMode(Integer.parseInt(args[1]), Integer.parseInt(args[2]), Gdx.graphics.isFullscreen());
			}
		});
	}
}
