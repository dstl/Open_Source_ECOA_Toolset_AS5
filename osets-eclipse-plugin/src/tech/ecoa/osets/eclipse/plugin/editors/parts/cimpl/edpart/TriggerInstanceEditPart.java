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
import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.figure.TriggerInstanceFigure;
import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.TriggerInstanceNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.policy.TriggerInstanceEditPolicy;
import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.policy.TriggerInstanceLayoutPolicy;

@SuppressWarnings("rawtypes")
public class TriggerInstanceEditPart extends AppAbstractEditPart {

	public TriggerInstanceEditPart(TriggerInstanceNode model, String containerName) {
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
		TriggerInstanceNode model = (TriggerInstanceNode) getModel();
		TriggerInstanceFigure figure = new TriggerInstanceFigure();
		figure.setNode(model);
		figure.rebuildFigure();
		return figure;
	}

	@Override
	protected List getModelChildren() {
		return ((TriggerInstanceNode) getModel()).getChild();
	}

	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.LAYOUT_ROLE, new TriggerInstanceLayoutPolicy());
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new TriggerInstanceEditPolicy());
	}

	@Override
	protected void refreshVisuals() {
		TriggerInstanceNode model = (TriggerInstanceNode) getModel();
		TriggerInstanceFigure figure = (TriggerInstanceFigure) getFigure();
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
