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
import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.commands.ModuleOperationParameterCreateCommand;
import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.Enums;
import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.ModuleOperationNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.ModuleOperationParameterNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.Node;

public class ModuleOperationLayoutPolicy extends XYLayoutEditPolicy {

	@Override
	protected Command getCreateCommand(CreateRequest request) {
		Command ret = null;
		if (request.getNewObjectType().equals(ModuleOperationParameterNode.class) && (getHost().getModel() instanceof ModuleOperationNode)) {
			ModuleOperationNode par = (ModuleOperationNode) getHost().getModel();
			if (!(par.getType().equalsIgnoreCase(Enums.ModuleOperationTypes.DATA_READ.name()) || par.getType().equalsIgnoreCase(Enums.ModuleOperationTypes.DATA_WRITE.name()))) {
				ModuleOperationParameterCreateCommand cmd = new ModuleOperationParameterCreateCommand();
				Node node = (Node) request.getNewObject();
				node.setParent((Node) getHost().getModel());
				Viewport vp = ((FigureCanvas) getHost().getViewer().getControl()).getViewport();
				Point viewSize = vp.getViewLocation();
				Point p = request.getLocation();
				p.setX(p.x + viewSize.x);
				p.setY(p.y + viewSize.y);
				p = node.getAnchor(p, 2);
				cmd.setNode((ModuleOperationParameterNode) node);
				cmd.setParent((ModuleOperationNode) node.getParent());
				cmd.setLocation(new Rectangle(p.x, p.y, ModuleOperationParameterNode.DEF_WIDTH, ModuleOperationParameterNode.DEF_HEIGHT));
				ret = cmd;
			}
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
