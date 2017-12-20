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

public class BendpointMoveCommand extends Command {
	private Point oldLocation;
	private Point newLocation;
	private int index;
	private Link link;

	public Point getOldLocation() {
		return oldLocation;
	}

	public void execute() {
		if (oldLocation == null) {
			oldLocation = link.getbPoints().get(index);
		}
		link.getbPoints().set(index, newLocation);
		link.refresh();
	}

	@Override
	public void undo() {
		link.getbPoints().set(index, oldLocation);
		link.refresh();
	}

	public void setOldLocation(Point oldLocation) {
		this.oldLocation = oldLocation;
	}

	public Point getNewLocation() {
		return newLocation;
	}

	public void setNewLocation(Point newLocation) {
		this.newLocation = newLocation;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
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
