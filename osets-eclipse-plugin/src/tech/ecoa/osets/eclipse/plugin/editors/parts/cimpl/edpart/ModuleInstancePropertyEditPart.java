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

import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.figure.ModuleInstanceFigure;
import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.figure.ModuleInstancePropertyFigure;
import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.ModuleInstancePropertyNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.policy.ModuleInstancePropertyEditPolicy;

public class ModuleInstancePropertyEditPart extends AppAbstractEditPart {

	public ModuleInstancePropertyEditPart(ModuleInstancePropertyNode model, String containerName) {
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
		ModuleInstancePropertyNode model = (ModuleInstancePropertyNode) getModel();
		ModuleInstancePropertyFigure figure = new ModuleInstancePropertyFigure();
		figure.setNode(model);
		figure.rebuildFigure();
		return figure;
	}

	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new ModuleInstancePropertyEditPolicy());
	}

	@Override
	protected void refreshVisuals() {
		ModuleInstancePropertyNode model = (ModuleInstancePropertyNode) getModel();
		ModuleInstancePropertyFigure figure = (ModuleInstancePropertyFigure) getFigure();
		figure.setNode(model);
		try {
			ModuleInstanceEditPart parent = (ModuleInstanceEditPart) getParent();
			ModuleInstanceFigure pFigure = (ModuleInstanceFigure) parent.getFigure();
			pFigure.setConstraint(figure, model.getConstraints());
		} catch (Exception e) {
		}
		figure.setBounds(model.getConstraints());
		figure.rebuildFigure();
	}

}
