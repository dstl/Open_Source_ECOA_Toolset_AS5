/*
 * Copyright 2017, BAE Systems Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.edpart;

import java.util.Observable;

import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPolicy;

import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.figure.ModuleOperationFigure;
import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.figure.ModuleOperationParameterFigure;
import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.ModuleOperationParameterNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.policy.ModuleOperationParameterEditPolicy;

public class ModuleOperationParameterEditPart extends AppAbstractEditPart {

	public ModuleOperationParameterEditPart(ModuleOperationParameterNode model, String containerName) {
		super();
		model.addObserver(this);
		model.setContainerName(containerName);
		setModel(model);
	}

	@Override
	public void update(Observable o, Object arg) {
		refresh();
	}

	@Override
	protected IFigure createFigure() {
		ModuleOperationParameterNode model = (ModuleOperationParameterNode) getModel();
		ModuleOperationParameterFigure figure = new ModuleOperationParameterFigure();
		figure.setNode(model);
		figure.rebuildFigure();
		return figure;
	}

	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new ModuleOperationParameterEditPolicy());
	}

	@Override
	protected void refreshVisuals() {
		ModuleOperationParameterNode model = (ModuleOperationParameterNode) getModel();
		ModuleOperationParameterFigure figure = (ModuleOperationParameterFigure) getFigure();
		figure.setNode(model);
		try {
			ModuleOperationEditPart parent = (ModuleOperationEditPart) getParent();
			ModuleOperationFigure pFigure = (ModuleOperationFigure) parent.getFigure();
			pFigure.setConstraint(figure, model.getConstraints());
		} catch (Exception e) {
		}
		figure.setBounds(model.getConstraints());
		figure.rebuildFigure();
	}

}
