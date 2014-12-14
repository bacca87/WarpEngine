package com.marcobaccarani.warp.debug;

public interface WarpCommand {
	public String getDescription();
	public void executeCommand(String[] args);
}
