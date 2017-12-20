/*
 * Copyright 2017, BAE Systems Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package tech.ecoa.osets.eclipse.plugin.editors.parts.deploy.commands;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.commands.Command;

import tech.ecoa.osets.eclipse.plugin.editors.parts.deploy.model.ComputingNodeConfigurationNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.deploy.model.PlatformConfigurationNode;

public class ComputingNodeConfigurationCreateCommand extends Command {
	private ComputingNodeConfigurationNode node;
	private PlatformConfigurationNode parent;

	public ComputingNodeConfigurationNode getNode() {
		return node;
	}

	public void setNode(ComputingNodeConfigurationNode node) {
		this.node = node;
	}

	public PlatformConfigurationNode getParent() {
		return parent;
	}

	public void setParent(PlatformConfigurationNode parent) {
		this.parent = parent;
	}

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
}
