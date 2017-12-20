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
import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.figure.DynamicTriggerInstanceFigure;
import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.DynamicTriggerInstanceNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.policy.DynamicTriggerInstanceEditPolicy;
import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.policy.DynamicTriggerInstanceLayoutPolicy;

@SuppressWarnings("rawtypes")
public class DynamicTriggerInstanceEditPart extends AppAbstractEditPart {

	public DynamicTriggerInstanceEditPart(DynamicTriggerInstanceNode model, String containerName) {
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
	protected List getModelChildren() {
		return ((DynamicTriggerInstanceNode) getModel()).getChild();
	}

	@Override
	protected IFigure createFigure() {
		DynamicTriggerInstanceNode model = (DynamicTriggerInstanceNode) getModel();
		DynamicTriggerInstanceFigure figure = new DynamicTriggerInstanceFigure();
		figure.setNode(model);
		figure.rebuildFigure();
		return figure;
	}

	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.LAYOUT_ROLE, new DynamicTriggerInstanceLayoutPolicy());
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new DynamicTriggerInstanceEditPolicy());
	}

	@Override
	protected void refreshVisuals() {
		DynamicTriggerInstanceNode model = (DynamicTriggerInstanceNode) getModel();
		DynamicTriggerInstanceFigure figure = (DynamicTriggerInstanceFigure) getFigure();
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
