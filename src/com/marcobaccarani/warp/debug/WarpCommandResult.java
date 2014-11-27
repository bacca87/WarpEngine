package com.marcobaccarani.warp.debug;

public enum WarpCommandResult {
	SUCCESSFUL(""),
	ERROR("Error"),
	UNKNOWN_COMMAND("Unknown command.");
	
	WarpCommandResult(String output) {
		this.output = output;
	}
	
	public String output;
}
