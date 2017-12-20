/*
 * Copyright 2017, BAE Systems Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.policy;

import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.Viewport;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.BendpointEditPolicy;
import org.eclipse.gef.requests.BendpointRequest;

import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.commands.BendpointCreateCommand;
import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.commands.BendpointDeleteCommand;
import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.commands.BendpointMoveCommand;
import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.Link;

public class BendpointPolicy extends BendpointEditPolicy {

	@Override
	protected Command getCreateBendpointCommand(BendpointRequest request) {
		Viewport vp = ((FigureCanvas) getHost().getViewer().getControl()).getViewport();
		Point viewSize = vp.getViewLocation();
		Point p = request.getLocation();
		p.setX(p.x + viewSize.x);
		p.setY(p.y + viewSize.y);
		BendpointCreateCommand cmd = new BendpointCreateCommand();
		cmd.setLink((Link) request.getSource().getModel());
		cmd.setLocation(p);
		cmd.setIndex(request.getIndex());
		return cmd;
	}

	@Override
	protected Command getDeleteBendpointCommand(BendpointRequest request) {
		Viewport vp = ((FigureCanvas) getHost().getViewer().getControl()).getViewport();
		Point viewSize = vp.getViewLocation();
		Point p = request.getLocation();
		p.setX(p.x + viewSize.x);
		p.setY(p.y + viewSize.y);
		BendpointDeleteCommand cmd = new BendpointDeleteCommand();
		cmd.setLink((Link) request.getSource().getModel());
		cmd.setLocation(p);
		cmd.setIndex(request.getIndex());
		return cmd;
	}

	@Override
	protected Command getMoveBendpointCommand(BendpointRequest request) {
		Viewport vp = ((FigureCanvas) getHost().getViewer().getControl()).getViewport();
		Point viewSize = vp.getViewLocation();
		Point p = request.getLocation();
		p.setX(p.x + viewSize.x);
		p.setY(p.y + viewSize.y);
		BendpointMoveCommand cmd = new BendpointMoveCommand();
		cmd.setLink((Link) request.getSource().getModel());
		cmd.setIndex(request.getIndex());
		cmd.setNewLocation(p);
		return cmd;
	}

}
