
package com.marcobaccarani.warp.console;

public interface Command {
	public String getName ();

	public String getDescription ();

	public void execute (String[] args);
}
