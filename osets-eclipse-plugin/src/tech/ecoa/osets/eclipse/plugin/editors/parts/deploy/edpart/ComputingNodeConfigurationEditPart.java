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

import tech.ecoa.osets.eclipse.plugin.editors.parts.deploy.figure.ComputingNodeConfigurationFigure;
import tech.ecoa.osets.eclipse.plugin.editors.parts.deploy.figure.PlatformConfigurationFigure;
import tech.ecoa.osets.eclipse.plugin.editors.parts.deploy.model.ComputingNodeConfigurationNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.deploy.policy.ComputingNodeConfigurationEditPolicy;

public class ComputingNodeConfigurationEditPart extends AppAbstractEditPart {

	public ComputingNodeConfigurationEditPart(ComputingNodeConfigurationNode model, String containerName) {
		super();
		model.addObserver(this);
		model.setContainerName(containerName);
		setModel(model);
	}

	@Override
	protected IFigure createFigure() {
		ComputingNodeConfigurationNode model = (ComputingNodeConfigurationNode) getModel();
		ComputingNodeConfigurationFigure figure = new ComputingNodeConfigurationFigure();
		figure.setNode(model);
		figure.rebuildFigure();
		return figure;
	}

	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new ComputingNodeConfigurationEditPolicy());
	}

	@Override
	protected void refreshVisuals() {
		ComputingNodeConfigurationNode model = (ComputingNodeConfigurationNode) getModel();
		ComputingNodeConfigurationFigure figure = (ComputingNodeConfigurationFigure) getFigure();
		figure.setNode(model);
		try {
			PlatformConfigurationEditPart parent = (PlatformConfigurationEditPart) getParent();
			PlatformConfigurationFigure pFigure = (PlatformConfigurationFigure) parent.getFigure();
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
