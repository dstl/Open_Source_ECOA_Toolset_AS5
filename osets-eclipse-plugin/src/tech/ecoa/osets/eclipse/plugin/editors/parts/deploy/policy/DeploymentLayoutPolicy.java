/*
 * Copyright 2017, BAE Systems Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package tech.ecoa.osets.eclipse.plugin.editors.parts.deploy.policy;

import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.Viewport;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.XYLayoutEditPolicy;
import org.eclipse.gef.requests.CreateRequest;

import tech.ecoa.osets.eclipse.plugin.editors.parts.deploy.commands.ChangeConstraintCommand;
import tech.ecoa.osets.eclipse.plugin.editors.parts.deploy.commands.PlatformConfigurationCreateCommand;
import tech.ecoa.osets.eclipse.plugin.editors.parts.deploy.commands.ProtectionDomainCreateCommand;
import tech.ecoa.osets.eclipse.plugin.editors.parts.deploy.model.DeploymentNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.deploy.model.Node;
import tech.ecoa.osets.eclipse.plugin.editors.parts.deploy.model.PlatformConfigurationNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.deploy.model.ProtectionDomainNode;

public class DeploymentLayoutPolicy extends XYLayoutEditPolicy {

	@Override
	protected Command getCreateCommand(CreateRequest request) {
		Command ret = null;
		if (request.getNewObjectType().equals(PlatformConfigurationNode.class) && (getHost().getModel() instanceof DeploymentNode)) {
			Viewport vp = ((FigureCanvas) getHost().getViewer().getControl()).getViewport();
			Point viewSize = vp.getViewLocation();
			Point p = request.getLocation();
			p.setX(p.x + viewSize.x);
			p.setY(p.y + viewSize.y);
			PlatformConfigurationCreateCommand cmd = new PlatformConfigurationCreateCommand();
			cmd.setNode((PlatformConfigurationNode) request.getNewObject());
			cmd.setParent((DeploymentNode) getHost().getModel());
			cmd.setLocation(new Rectangle(p.x, p.y, PlatformConfigurationNode.DEF_WIDTH, PlatformConfigurationNode.DEF_HEIGHT));
			ret = cmd;
		} else if (request.getNewObjectType().equals(ProtectionDomainNode.class) && (getHost().getModel() instanceof DeploymentNode)) {
			Viewport vp = ((FigureCanvas) getHost().getViewer().getControl()).getViewport();
			Point viewSize = vp.getViewLocation();
			Point p = request.getLocation();
			p.setX(p.x + viewSize.x);
			p.setY(p.y + viewSize.y);
			ProtectionDomainCreateCommand cmd = new ProtectionDomainCreateCommand();
			cmd.setNode((ProtectionDomainNode) request.getNewObject());
			cmd.setParent((DeploymentNode) getHost().getModel());
			cmd.setLocation(new Rectangle(p.x, p.y, ProtectionDomainNode.DEF_WIDTH, ProtectionDomainNode.DEF_HEIGHT));
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
