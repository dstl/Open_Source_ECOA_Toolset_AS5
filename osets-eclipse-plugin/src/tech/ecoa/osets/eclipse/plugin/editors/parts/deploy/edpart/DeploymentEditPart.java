/*
 * Copyright 2017, BAE Systems Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package tech.ecoa.osets.eclipse.plugin.editors.parts.deploy.edpart;

import java.util.List;
import java.util.Observable;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPolicy;

import tech.ecoa.osets.eclipse.plugin.editors.parts.deploy.figure.DeploymentFigure;
import tech.ecoa.osets.eclipse.plugin.editors.parts.deploy.model.DeploymentNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.deploy.model.Node;
import tech.ecoa.osets.eclipse.plugin.editors.parts.deploy.model.PlatformConfigurationNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.deploy.model.ProtectionDomainNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.deploy.policy.DeploymentLayoutPolicy;

@SuppressWarnings("rawtypes")
public class DeploymentEditPart extends AppAbstractEditPart {

	public DeploymentEditPart(DeploymentNode model, String containerName) {
		super();
		model.addObserver(this);
		model.setContainerName(containerName);
		setModel(model);
	}

	@Override
	protected IFigure createFigure() {
		DeploymentNode model = (DeploymentNode) getModel();
		DeploymentFigure figure = new DeploymentFigure();
		figure.setNode(model);
		Dimension dim = getDimension(model);
		model.setConstraints(new Rectangle(new Point(0, 0), (dim != null) ? dim : new Dimension(1000, 1000)));
		figure.setBounds(model.getConstraints());
		figure.rebuildFigure();
		return figure;
	}

	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.LAYOUT_ROLE, new DeploymentLayoutPolicy());
	}

	@Override
	protected void refreshVisuals() {
		DeploymentNode model = (DeploymentNode) getModel();
		DeploymentFigure figure = (DeploymentFigure) getFigure();
		figure.setNode(model);
		Dimension dim = getDimension(model);
		model.setConstraints(new Rectangle(new Point(0, 0), (dim != null) ? dim : new Dimension(1000, 1000)));
		figure.setBounds(model.getConstraints());
		figure.rebuildFigure();
	}

	private Dimension getDimension(DeploymentNode node) {
		Dimension ret = null;
		int h = 0, w = 0;
		for (Node nd : node.getChild()) {
			if (nd instanceof PlatformConfigurationNode || nd instanceof ProtectionDomainNode) {
				Rectangle bounds = nd.getConstraints();
				int bottom = bounds.y + bounds.height;
				int right = bounds.x + bounds.width;
				if (h < bottom)
					h = bottom;
				if (w < right)
					w = right;
			}
		}
		h = (h == 0) ? 50 : h + 50;
		w = (w == 0) ? 250 : w + 50;
		ret = new Dimension(w, h);
		return ret;
	}

	@Override
	protected List getModelChildren() {
		return ((DeploymentNode) getModel()).getChild();
	}

	@Override
	public void update(Observable o, Object arg) {
		refresh();
	}

}
