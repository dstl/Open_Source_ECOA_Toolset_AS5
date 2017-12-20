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
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.XYLayoutEditPolicy;
import org.eclipse.gef.requests.CreateRequest;

import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.commands.ChangeConstraintCommand;
import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.commands.DynamicTriggerInstanceCreateCommand;
import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.commands.ModuleTypeCreateCommand;
import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.commands.TriggerInstanceCreateCommand;
import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.ComponentImplementationNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.DynamicTriggerInstanceNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.ModuleTypeNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.Node;
import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.TriggerInstanceNode;

public class ComponentImplementationLayoutPolicy extends XYLayoutEditPolicy {

	@Override
	protected Command getCreateCommand(CreateRequest request) {
		Command ret = null;
		if (request.getNewObjectType().equals(DynamicTriggerInstanceNode.class) && (getHost().getModel() instanceof ComponentImplementationNode)) {
			DynamicTriggerInstanceCreateCommand cmd = new DynamicTriggerInstanceCreateCommand();
			Node node = (Node) request.getNewObject();
			node.setParent((Node) getHost().getModel());
			Viewport vp = ((FigureCanvas) getHost().getViewer().getControl()).getViewport();
			Point viewSize = vp.getViewLocation();
			Point p = request.getLocation();
			p.setX(p.x + viewSize.x);
			p.setY(p.y + viewSize.y);
			cmd.setNode((DynamicTriggerInstanceNode) node);
			cmd.setParent((ComponentImplementationNode) node.getParent());
			cmd.setLocation(new Rectangle(p.x, p.y, DynamicTriggerInstanceNode.DEF_WIDTH, DynamicTriggerInstanceNode.DEF_HEIGHT));
			ret = cmd;
		} else if (request.getNewObjectType().equals(TriggerInstanceNode.class) && (getHost().getModel() instanceof ComponentImplementationNode)) {
			TriggerInstanceCreateCommand cmd = new TriggerInstanceCreateCommand();
			Node node = (Node) request.getNewObject();
			node.setParent((Node) getHost().getModel());
			Viewport vp = ((FigureCanvas) getHost().getViewer().getControl()).getViewport();
			Point viewSize = vp.getViewLocation();
			Point p = request.getLocation();
			p.setX(p.x + viewSize.x);
			p.setY(p.y + viewSize.y);
			cmd.setNode((TriggerInstanceNode) node);
			cmd.setParent((ComponentImplementationNode) node.getParent());
			cmd.setLocation(new Rectangle(p.x, p.y, TriggerInstanceNode.DEF_WIDTH, TriggerInstanceNode.DEF_HEIGHT));
			ret = cmd;
		} else if (request.getNewObjectType().equals(ModuleTypeNode.class) && (getHost().getModel() instanceof ComponentImplementationNode)) {
			ModuleTypeCreateCommand cmd = new ModuleTypeCreateCommand();
			Node node = (Node) request.getNewObject();
			node.setParent((Node) getHost().getModel());
			Viewport vp = ((FigureCanvas) getHost().getViewer().getControl()).getViewport();
			Point viewSize = vp.getViewLocation();
			Point p = request.getLocation();
			p.setX(p.x + viewSize.x);
			p.setY(p.y + viewSize.y);
			cmd.setNode((ModuleTypeNode) node);
			cmd.setParent((ComponentImplementationNode) node.getParent());
			cmd.setLocation(new Rectangle(p.x, p.y, ModuleTypeNode.DEF_WIDTH, ModuleTypeNode.DEF_HEIGHT));
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
