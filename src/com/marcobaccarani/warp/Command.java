
package com.marcobaccarani.warp;

public interface Command {
	public String getDescription ();

	public void execute (String[] args);
}
