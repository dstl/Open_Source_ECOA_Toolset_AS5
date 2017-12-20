/*
 * Copyright 2017, BAE Systems Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package tech.ecoa.osets.eclipse.plugin.common;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class CompoundWrapperCommand extends CompoundCommand {

	@Override
	public void execute() {
		Shell shell = Display.getDefault().getActiveShell();
		boolean confirm = MessageDialog.openQuestion(shell, "Confirm Delete", "Cannot UNDO / REDO. Are you sure?");
		if (confirm) {
			for (int i = 0; i < getCommands().size(); i++) {
				Command cmd = (Command) getCommands().get(i);
				cmd.execute();
			}
		}
	}
}
