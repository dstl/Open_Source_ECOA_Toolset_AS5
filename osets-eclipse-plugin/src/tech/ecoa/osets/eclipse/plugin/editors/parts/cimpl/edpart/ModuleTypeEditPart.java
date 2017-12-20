/*
 * Copyright 2017, BAE Systems Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.edpart;

import java.util.List;
import java.util.Observable;

import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPolicy;

import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.figure.ComponentImplementationFigure;
import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.figure.ModuleTypeFigure;
import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.ModuleTypeNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.policy.ModuleTypeEditPolicy;
import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.policy.ModuleTypeLayoutPolicy;

@SuppressWarnings("rawtypes")
public class ModuleTypeEditPart extends AppAbstractEditPart {

	public ModuleTypeEditPart(ModuleTypeNode model, String containerName) {
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
		ModuleTypeNode model = (ModuleTypeNode) getModel();
		ModuleTypeFigure figure = new ModuleTypeFigure();
		figure.setNode(model);
		figure.rebuildFigure();
		return figure;
	}

	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.LAYOUT_ROLE, new ModuleTypeLayoutPolicy());
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new ModuleTypeEditPolicy());
	}

	@Override
	protected List getModelChildren() {
		return ((ModuleTypeNode) getModel()).getChild();
	}

	@Override
	protected void refreshVisuals() {
		ModuleTypeNode model = (ModuleTypeNode) getModel();
		ModuleTypeFigure figure = (ModuleTypeFigure) getFigure();
		figure.setNode(model);
		try {
			ComponentImplementationEditPart parent = (ComponentImplementationEditPart) getParent();
			ComponentImplementationFigure pFigure = (ComponentImplementationFigure) parent.getFigure();
			pFigure.setConstraint(figure, model.getConstraints());
		} catch (Exception e) {
		}
		figure.setBounds(model.getConstraints());
		figure.rebuildFigure();
	}

}
