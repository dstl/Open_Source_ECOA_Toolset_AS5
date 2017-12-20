/*
 * Copyright 2017, BAE Systems Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.commands;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.commands.Command;

import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.ServiceNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.ServiceOperationNode;

public class ServiceOperationCreateCommand extends Command {
	private ServiceOperationNode node;
	private ServiceNode parent;

	public void setLocation(Rectangle rect) {
		this.node.setConstraints(rect);
	}

	@Override
	public void execute() {
		node.setParent(parent);
		parent.getChild().add(node);
		node.refreshAll(node.getRootNode());
	}

	@Override
	public void undo() {
		parent.getChild().remove(node);
		node.refreshAll(node.getRootNode());
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

	public ServiceOperationNode getNode() {
		return node;
	}

	public void setNode(ServiceOperationNode node) {
		this.node = node;
	}

	public ServiceNode getParent() {
		return parent;
	}

	public void setParent(ServiceNode parent) {
		this.parent = parent;
	}
}
