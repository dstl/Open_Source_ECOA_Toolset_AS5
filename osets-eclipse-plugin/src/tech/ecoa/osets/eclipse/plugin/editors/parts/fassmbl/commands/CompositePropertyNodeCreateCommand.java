/*
 * Copyright 2017, BAE Systems Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.commands;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.commands.Command;

import tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.model.CompositeNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.model.CompositePropertyNode;

public class CompositePropertyNodeCreateCommand extends Command {
	private CompositePropertyNode node;
	private CompositeNode parent;

	public CompositePropertyNode getNode() {
		return node;
	}

	public void setNode(CompositePropertyNode node) {
		this.node = node;
	}

	public CompositeNode getParent() {
		return parent;
	}

	public void setParent(CompositeNode parent) {
		this.parent = parent;
	}

	public void setLocation(Rectangle rect) {
		this.node.setConstraints(rect);
	}

	@Override
	public void execute() {
		node.setParent(parent);
		parent.getChild().add(node);
		node.refreshAll(node.getCompositeNode());
	}

	@Override
	public void undo() {
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
