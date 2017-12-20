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
import org.eclipse.gef.EditPolicy;

import tech.ecoa.osets.eclipse.plugin.editors.parts.deploy.figure.DeploymentFigure;
import tech.ecoa.osets.eclipse.plugin.editors.parts.deploy.figure.ProtectionDomainFigure;
import tech.ecoa.osets.eclipse.plugin.editors.parts.deploy.model.ProtectionDomainNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.deploy.policy.ProtectionDomainEditPolicy;
import tech.ecoa.osets.eclipse.plugin.editors.parts.deploy.policy.ProtectionDomainLayoutPolicy;

@SuppressWarnings("rawtypes")
public class ProtectionDomainEditPart extends AppAbstractEditPart {

	public ProtectionDomainEditPart(ProtectionDomainNode model, String containerName) {
		super();
		model.addObserver(this);
		model.setContainerName(containerName);
		setModel(model);
	}

	@Override
	protected IFigure createFigure() {
		ProtectionDomainNode model = (ProtectionDomainNode) getModel();
		ProtectionDomainFigure figure = new ProtectionDomainFigure();
		figure.setNode(model);
		figure.rebuildFigure();
		return figure;
	}

	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.LAYOUT_ROLE, new ProtectionDomainLayoutPolicy());
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new ProtectionDomainEditPolicy());
	}

	@Override
	protected List getModelChildren() {
		return ((ProtectionDomainNode) getModel()).getChild();
	}

	@Override
	protected void refreshVisuals() {
		ProtectionDomainNode model = (ProtectionDomainNode) getModel();
		ProtectionDomainFigure figure = (ProtectionDomainFigure) getFigure();
		figure.setNode(model);
		try {
			DeploymentEditPart parent = (DeploymentEditPart) getParent();
			DeploymentFigure pFigure = (DeploymentFigure) parent.getFigure();
			pFigure.setConstraint(figure, model.getConstraints());
		} catch (Exception e) {
		}
		figure.setBounds(model.getConstraints());
		figure.rebuildFigure();

	}

	@Override
	public void update(Observable o, Object arg) {
		refresh();
	}

}
