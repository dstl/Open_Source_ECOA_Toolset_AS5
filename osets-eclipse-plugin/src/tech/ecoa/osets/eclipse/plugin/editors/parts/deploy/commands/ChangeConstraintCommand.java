/*
 * Copyright 2017, BAE Systems Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package tech.ecoa.osets.eclipse.plugin.editors.parts.deploy.commands;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.commands.Command;

import tech.ecoa.osets.eclipse.plugin.editors.parts.deploy.model.Node;

public class ChangeConstraintCommand extends Command {

	private Rectangle oldConstraint;
	private Rectangle newConstraint;
	private Node model;

	@Override
	public void execute() {
		if (oldConstraint == null) {
			oldConstraint = model.getConstraints();
		}
		model.setConstraints(newConstraint);
		model.refreshAll(model.getRootNode());
	}

	@Override
	public void undo() {
		model.setConstraints(oldConstraint);
		model.refreshAll(model.getRootNode());
	}

	public void setModel(Node model) {
		this.model = model;
	}

	public void setNewConstraint(Rectangle newConstraint) {
		this.newConstraint = newConstraint;
	}

	@Override
	public boolean canRedo() {
		return true;
	}

	@Override
	public boolean canExecute() {
		return true;
	}

	@Override
	public boolean canUndo() {
		return true;
	}
}
