/*
 * Copyright 2017, BAE Systems Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package tech.ecoa.osets.eclipse.plugin.util;

import java.io.PrintWriter;
import java.util.ArrayList;

import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.MessageConsole;

public class EclipseUtil {
	private static PrintWriter writer;
	static {
		MessageConsole console = new MessageConsole("OSETS Console", null);
		console.activate();
		ConsolePlugin.getDefault().getConsoleManager().addConsoles(new IConsole[] { console });
		writer = new PrintWriter(console.newOutputStream());
	}

	public static void writeStactTraceToConsole(Exception e) {
		e.printStackTrace(writer);
		writer.flush();
	}

	public static void writeMessageToConsole(String msg) {
		writer.write(msg);
		writer.flush();
	}

	public static void displayValidations(ArrayList<String> msgs) {
		for (String msg : msgs) {
			writer.write(msg + "\n");
			writer.flush();
		}
	}
}
