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

import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.figure.ModuleImplementationFigure;
import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.figure.ModuleTypeFigure;
import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.ModuleImplementationNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.policy.ModuleImplementationEditPolicy;

public class ModuleImplementationEditPart extends AppAbstractEditPart {

	public ModuleImplementationEditPart(ModuleImplementationNode model, String containerName) {
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
		ModuleImplementationNode model = (ModuleImplementationNode) getModel();
		ModuleImplementationFigure figure = new ModuleImplementationFigure();
		figure.setNode(model);
		figure.rebuildFigure();
		return figure;
	}

	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new ModuleImplementationEditPolicy());
	}

	@Override
	protected void refreshVisuals() {
		ModuleImplementationNode model = (ModuleImplementationNode) getModel();
		ModuleImplementationFigure figure = (ModuleImplementationFigure) getFigure();
		figure.setNode(model);
		try {
			ModuleTypeEditPart parent = (ModuleTypeEditPart) getParent();
			ModuleTypeFigure pFigure = (ModuleTypeFigure) parent.getFigure();
			pFigure.setConstraint(figure, model.getConstraints());
		} catch (Exception e) {
		}
		figure.setBounds(model.getConstraints());
		figure.rebuildFigure();
	}

}
