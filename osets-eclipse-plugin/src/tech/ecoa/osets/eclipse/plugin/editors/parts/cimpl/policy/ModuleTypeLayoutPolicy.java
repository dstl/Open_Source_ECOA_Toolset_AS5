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
import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.commands.ModuleImplementationCreateCommand;
import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.commands.ModuleInstanceCreateCommand;
import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.commands.ModuleOperationCreateCommand;
import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.ModuleImplementationNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.ModuleInstanceNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.ModuleOperationNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.ModuleTypeNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.Node;

public class ModuleTypeLayoutPolicy extends XYLayoutEditPolicy {

	@Override
	protected Command getCreateCommand(CreateRequest request) {
		Command ret = null;
		if (request.getNewObjectType().equals(ModuleOperationNode.class) && (getHost().getModel() instanceof ModuleTypeNode)) {
			ModuleOperationCreateCommand cmd = new ModuleOperationCreateCommand();
			Node node = (Node) request.getNewObject();
			node.setParent((Node) getHost().getModel());
			Viewport vp = ((FigureCanvas) getHost().getViewer().getControl()).getViewport();
			Point viewSize = vp.getViewLocation();
			Point p = request.getLocation();
			p.setX(p.x + viewSize.x);
			p.setY(p.y + viewSize.y);
			p = node.getAnchor(p, 1);
			cmd.setNode((ModuleOperationNode) node);
			cmd.setParent((ModuleTypeNode) node.getParent());
			cmd.setLocation(new Rectangle(p.x, p.y, ModuleOperationNode.DEF_WIDTH, ModuleOperationNode.DEF_HEIGHT));
			ret = cmd;
		} else if (request.getNewObjectType().equals(ModuleImplementationNode.class) && (getHost().getModel() instanceof ModuleTypeNode)) {
			ModuleImplementationCreateCommand cmd = new ModuleImplementationCreateCommand();
			Node node = (Node) request.getNewObject();
			ModuleImplementationNode mNode = (ModuleImplementationNode) node;
			ModuleTypeNode pNode = (ModuleTypeNode) getHost().getModel();
			mNode.setParent(pNode);
			mNode.setType(pNode.getName());
			Viewport vp = ((FigureCanvas) getHost().getViewer().getControl()).getViewport();
			Point viewSize = vp.getViewLocation();
			Point p = request.getLocation();
			p.setX(p.x + viewSize.x);
			p.setY(p.y + viewSize.y);
			p = node.getAnchor(p, 1);
			cmd.setNode(mNode);
			cmd.setParent(pNode);
			cmd.setLocation(new Rectangle(p.x, p.y, ModuleImplementationNode.DEF_WIDTH, ModuleImplementationNode.DEF_HEIGHT));
			ret = cmd;
		} else if (request.getNewObjectType().equals(ModuleInstanceNode.class) && (getHost().getModel() instanceof ModuleTypeNode)) {
			ModuleInstanceCreateCommand cmd = new ModuleInstanceCreateCommand();
			Node node = (Node) request.getNewObject();
			node.setParent((Node) getHost().getModel());
			Viewport vp = ((FigureCanvas) getHost().getViewer().getControl()).getViewport();
			Point viewSize = vp.getViewLocation();
			Point p = request.getLocation();
			p.setX(p.x + viewSize.x);
			p.setY(p.y + viewSize.y);
			p = node.getAnchor(p, 1);
			cmd.setNode((ModuleInstanceNode) node);
			cmd.setParent((ModuleTypeNode) node.getParent());
			cmd.setLocation(new Rectangle(p.x, p.y, ModuleInstanceNode.DEF_WIDTH, ModuleInstanceNode.DEF_HEIGHT));
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
