/*
 * Copyright 2017, BAE Systems Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.commands;

import org.eclipse.gef.commands.Command;

import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.Link;
import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.Node;

public class LinkCreateCommand extends Command {
	private Node source;
	private Node target;
	private Link link;

	@Override
	public void execute() {
		link.setSource(source);
		link.setTarget(target);
		source.getOutLinks().add(link);
		target.getInLinks().add(link);
		link.setId(source.getId() + ":" + target.getId());
		target.getRootNode().getLinks().add(link);
		link.refresh();
		source.refresh();
		target.refresh();
	}

	@Override
	public void undo() {
		target.getRootNode().getLinks().remove(link);
		source.getOutLinks().remove(link);
		target.getInLinks().remove(link);
		link.setSource(null);
		link.setTarget(null);
		link.setId(null);
		link.refresh();
		source.refresh();
		target.refresh();
	}

	public Node getSource() {
		return source;
	}

	public void setSource(Node source) {
		this.source = source;
	}

	public Node getTarget() {
		return target;
	}

	public void setTarget(Node target) {
		this.target = target;
	}

	public Link getLink() {
		return link;
	}

	public void setLink(Link link) {
		this.link = link;
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
