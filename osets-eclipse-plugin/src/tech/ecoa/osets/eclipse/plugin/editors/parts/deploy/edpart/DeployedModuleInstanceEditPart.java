/*
 * Copyright 2017, BAE Systems Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package tech.ecoa.osets.eclipse.plugin.editors.parts.deploy.edpart;

import java.util.Observable;

import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPolicy;

import tech.ecoa.osets.eclipse.plugin.editors.parts.deploy.figure.DeployedModuleInstanceFigure;
import tech.ecoa.osets.eclipse.plugin.editors.parts.deploy.figure.ProtectionDomainFigure;
import tech.ecoa.osets.eclipse.plugin.editors.parts.deploy.model.DeployedModuleInstanceNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.deploy.policy.DeployedModuleInstanceEditPolicy;

public class DeployedModuleInstanceEditPart extends AppAbstractEditPart {

	public DeployedModuleInstanceEditPart(DeployedModuleInstanceNode model, String containerName) {
		super();
		model.addObserver(this);
		model.setContainerName(containerName);
		setModel(model);
	}

	@Override
	protected IFigure createFigure() {
		DeployedModuleInstanceNode model = (DeployedModuleInstanceNode) getModel();
		DeployedModuleInstanceFigure figure = new DeployedModuleInstanceFigure();
		figure.setNode(model);
		figure.rebuildFigure();
		return figure;
	}

	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new DeployedModuleInstanceEditPolicy());
	}

	@Override
	protected void refreshVisuals() {
		try {
			DeployedModuleInstanceNode model = (DeployedModuleInstanceNode) getModel();
			DeployedModuleInstanceFigure figure = (DeployedModuleInstanceFigure) getFigure();
			figure.setNode(model);
			try {
				ProtectionDomainEditPart parent = (ProtectionDomainEditPart) getParent();
				ProtectionDomainFigure pFigure = (ProtectionDomainFigure) parent.getFigure();
				pFigure.setConstraint(figure, model.getConstraints());
			} catch (Exception e) {
			}
			figure.setBounds(model.getConstraints());
			figure.rebuildFigure();
		} catch (Exception e) {
		}
	}

	@Override
	public void update(Observable o, Object arg) {
		refresh();
	}

}
