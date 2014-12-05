package com.marcobaccarani.warp.debug;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

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
	
	private Map<String, WarpCommand> commands = new HashMap<String, WarpCommand>();
	
	public void addCommand(String name, WarpCommand command) {
		commands.put(name, command);
	}
	
	public void executeCommand(String command) {
		String args[] = command.split(" ");
		
		if(args.length > 0 && commands.containsKey(args[0]))
			commands.get(args[0]).executeCommand(args);
		else
			out.print("Error: Unknown command!");
	}	
}
