/*
 * Copyright 2017, BAE Systems Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.commands;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.commands.Command;

import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.ComponentImplementationNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.ModuleTypeNode;

public class ModuleTypeDeleteCommand extends Command {
	private ModuleTypeNode node;
	private ComponentImplementationNode parent;

	public void setLocation(Rectangle rect) {
		this.node.setConstraints(rect);
	}

	@Override
	public void undo() {
		node.setParent(parent);
		parent.getChild().add(node);
		node.refreshAll(node.getRootNode());
	}

	@Override
	public void execute() {
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

	public ModuleTypeNode getNode() {
		return node;
	}

	public void setNode(ModuleTypeNode node) {
		this.node = node;
	}

	public ComponentImplementationNode getParent() {
		return parent;
	}

	public void setParent(ComponentImplementationNode parent) {
		this.parent = parent;
	}
}
