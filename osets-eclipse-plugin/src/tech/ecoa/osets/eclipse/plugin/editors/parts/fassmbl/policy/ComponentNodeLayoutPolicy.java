/*
 * Copyright 2017, BAE Systems Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.policy;

import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.Viewport;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.XYLayoutEditPolicy;
import org.eclipse.gef.requests.CreateRequest;

import tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.commands.ChangeConstraintCommand;
import tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.commands.ComponentPropertyNodeCreateCommand;
import tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.model.ComponentNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.model.ComponentPropertyNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.model.Node;

public class ComponentNodeLayoutPolicy extends XYLayoutEditPolicy {

	@Override
	protected Command getCreateCommand(CreateRequest request) {
		Command ret = null;
		if (request.getNewObjectType().equals(ComponentPropertyNode.class) && (getHost().getModel() instanceof ComponentNode)) {
			ComponentPropertyNodeCreateCommand cmd = new ComponentPropertyNodeCreateCommand();
			Node node = (Node) request.getNewObject();
			node.setParent((Node) getHost().getModel());
			Viewport vp = ((FigureCanvas) getHost().getViewer().getControl()).getViewport();
			Point viewSize = vp.getViewLocation();
			Point p = request.getLocation();
			p.setX(p.x + viewSize.x);
			p.setY(p.y + viewSize.y);
			p = node.getAnchor(p, 1);
			cmd.setNode((ComponentPropertyNode) node);
			cmd.setParent((ComponentNode) node.getParent());
			cmd.setLocation(new Rectangle(p.x, p.y, ComponentPropertyNode.DEF_WIDTH, ComponentPropertyNode.DEF_HEIGHT));
			ret = cmd;
		}
		return ret;
	}

	@Override
	protected Command createChangeConstraintCommand(EditPart child, Object constraint) {
		ChangeConstraintCommand ret = new ChangeConstraintCommand();
		ret.setModel((Node) child.getModel());
		ret.setNewConstraint((Rectangle) constraint);
		return ret;
	}
}
