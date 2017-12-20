/*
 * Copyright 2017, BAE Systems Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package tech.ecoa.osets.eclipse.plugin.editors.parts.assmbl.commands;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.commands.Command;

import tech.ecoa.osets.eclipse.plugin.editors.parts.assmbl.model.ComponentNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.assmbl.model.ComponentPropertyNode;

public class ComponentPropertyNodeDeleteCommand extends Command {

	private ComponentPropertyNode node;
	private ComponentNode parent;

	public ComponentPropertyNode getNode() {
		return node;
	}

	public void setNode(ComponentPropertyNode node) {
		this.node = node;
	}

	public ComponentNode getParent() {
		return parent;
	}

	public void setParent(ComponentNode parent) {
		this.parent = parent;
	}

	public void setLocation(Rectangle rect) {
		this.node.setConstraints(rect);
	}

	@Override
	public void undo() {
		node.setParent(parent);
		parent.getChild().add(node);
		node.refreshAll(node.getCompositeNode());
	}

	@Override
	public void execute() {
		parent.getChild().remove(node);
		node.refreshAll(node.getCompositeNode());
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
