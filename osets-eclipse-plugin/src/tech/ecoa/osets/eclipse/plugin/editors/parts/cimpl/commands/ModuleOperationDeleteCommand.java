/*
 * Copyright 2017, BAE Systems Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.commands;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.commands.Command;

import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.Link;
import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.ModuleOperationNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.ModuleTypeNode;

public class ModuleOperationDeleteCommand extends Command {
	private ModuleTypeNode parent;
	private ModuleOperationNode node;
	private List<LinkDeleteCommand> delCmd;

	public void initLinks() {
		for (Link link : node.getOutLinks()) {
			LinkDeleteCommand cmd = new LinkDeleteCommand();
			cmd.setLink(link);
			cmd.setSource(link.getSource());
			cmd.setTarget(link.getTarget());
			getDelCmd().add(cmd);
		}
		for (Link link : node.getInLinks()) {
			LinkDeleteCommand cmd = new LinkDeleteCommand();
			cmd.setLink(link);
			cmd.setSource(link.getSource());
			cmd.setTarget(link.getTarget());
			getDelCmd().add(cmd);
		}
	}

	public void setLocation(Rectangle rect) {
		this.node.setConstraints(rect);
	}

	@Override
	public void undo() {
		node.setParent(parent);
		parent.getChild().add(node);
		for (LinkDeleteCommand cmd : getDelCmd())
			cmd.undo();
		node.refreshAll(node.getRootNode());
	}

	@Override
	public void execute() {
		parent.getChild().remove(node);
		for (LinkDeleteCommand cmd : getDelCmd())
			cmd.execute();
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

	public ModuleTypeNode getParent() {
		return parent;
	}

	public void setParent(ModuleTypeNode parent) {
		this.parent = parent;
	}

	public ModuleOperationNode getNode() {
		return node;
	}

	public void setNode(ModuleOperationNode node) {
		this.node = node;
	}

	public List<LinkDeleteCommand> getDelCmd() {
		if (delCmd == null)
			delCmd = new ArrayList<LinkDeleteCommand>();
		return delCmd;
	}

	public void setDelCmd(List<LinkDeleteCommand> delCmd) {
		this.delCmd = delCmd;
	}
}
