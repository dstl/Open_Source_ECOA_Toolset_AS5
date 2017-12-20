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

import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.figure.ModuleInstanceFigure;
import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.figure.ModuleTypeFigure;
import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.ModuleInstanceNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.policy.ModuleInstanceEditPolicy;
import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.policy.ModuleInstanceLayoutPolicy;

@SuppressWarnings("rawtypes")
public class ModuleInstanceEditPart extends AppAbstractEditPart {

	public ModuleInstanceEditPart(ModuleInstanceNode model, String containerName) {
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
		ModuleInstanceNode model = (ModuleInstanceNode) getModel();
		ModuleInstanceFigure figure = new ModuleInstanceFigure();
		figure.setNode(model);
		figure.rebuildFigure();
		return figure;
	}

	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new ModuleInstanceEditPolicy());
		installEditPolicy(EditPolicy.LAYOUT_ROLE, new ModuleInstanceLayoutPolicy());
	}

	@Override
	protected List getModelChildren() {
		return ((ModuleInstanceNode) getModel()).getChild();
	}

	@Override
	protected void refreshVisuals() {
		ModuleInstanceNode model = (ModuleInstanceNode) getModel();
		ModuleInstanceFigure figure = (ModuleInstanceFigure) getFigure();
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
