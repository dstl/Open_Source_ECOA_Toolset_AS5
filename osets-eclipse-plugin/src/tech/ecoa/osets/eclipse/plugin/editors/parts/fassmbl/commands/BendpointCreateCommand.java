/*
 * Copyright 2017, BAE Systems Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.commands;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.commands.Command;

import tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.model.Link;

public class BendpointCreateCommand extends Command {
	private int index;
	private Point location;
	private Link link;

	public int getIndex() {
		return index;
	}

	@Override
	public void execute() {
		link.getbPoints().add(index, location);
		link.refresh();
	}

	@Override
	public void undo() {
		link.getbPoints().remove(index);
		link.refresh();
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public Point getLocation() {
		return location;
	}

	public void setLocation(Point location) {
		this.location = location;
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
