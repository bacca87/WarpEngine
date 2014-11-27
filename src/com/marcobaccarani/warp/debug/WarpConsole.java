package com.marcobaccarani.warp.debug;

import java.util.HashMap;
import java.util.Map;

public final class WarpConsole {
	private Map<String, WarpCommand> commands = new HashMap<String, WarpCommand>();

	public void addCommand(String name, WarpCommand command) {
		commands.put(name, command);
	}
	
	public WarpCommandResult executeCommand(String command) {
		String args[] = command.split(" ");
		
		if(args.length > 0 && commands.containsKey(args[0]))
			return commands.get(args[0]).executeCommand(args);
		
		return WarpCommandResult.UNKNOWN_COMMAND;
	}	
}
