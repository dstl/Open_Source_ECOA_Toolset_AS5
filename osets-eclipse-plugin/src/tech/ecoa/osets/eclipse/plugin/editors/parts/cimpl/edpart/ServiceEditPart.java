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
import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.figure.ServiceFigure;
import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.ServiceNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.policy.ServiceLayoutPolicy;

@SuppressWarnings("rawtypes")
public class ServiceEditPart extends AppAbstractEditPart {

	public ServiceEditPart(ServiceNode model, String containerName) {
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
		ServiceNode model = (ServiceNode) getModel();
		ServiceFigure figure = new ServiceFigure();
		figure.setNode(model);
		figure.rebuildFigure();
		return figure;
	}

	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.LAYOUT_ROLE, new ServiceLayoutPolicy());
	}

	@Override
	protected List getModelChildren() {
		return ((ServiceNode) getModel()).getChild();
	}

	@Override
	protected void refreshVisuals() {
		ServiceNode model = (ServiceNode) getModel();
		ServiceFigure figure = (ServiceFigure) getFigure();
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
