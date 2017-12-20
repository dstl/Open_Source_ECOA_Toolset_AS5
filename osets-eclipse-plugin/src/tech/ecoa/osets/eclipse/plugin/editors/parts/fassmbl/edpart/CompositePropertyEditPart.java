/*
 * Copyright 2017, BAE Systems Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.edpart;

import java.util.Observable;

import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPolicy;

import tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.figure.CompositeFigure;
import tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.figure.CompositePropertyFigure;
import tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.model.CompositePropertyNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.policy.CompositePropertyNodeEditPolicy;

public class CompositePropertyEditPart extends AppAbstractEditPart {

	public CompositePropertyEditPart(CompositePropertyNode node, String containerName) {
		super();
		node.addObserver(this);
		node.setContainerName(containerName);
		setModel(node);
	}

	@Override
	protected IFigure createFigure() {
		CompositePropertyNode model = (CompositePropertyNode) getModel();
		CompositePropertyFigure figure = new CompositePropertyFigure();
		figure.setNode(model);
		figure.rebuildFigure();
		return figure;
	}

	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new CompositePropertyNodeEditPolicy());
	}

	@Override
	protected void refreshVisuals() {
		CompositePropertyNode model = (CompositePropertyNode) getModel();
		CompositePropertyFigure figure = (CompositePropertyFigure) getFigure();
		figure.setNode(model);
		try {
			CompositeEditPart parent = (CompositeEditPart) getParent();
			CompositeFigure pFigure = (CompositeFigure) parent.getFigure();
			pFigure.setConstraint(figure, model.getConstraints());
		} catch (Exception e) {
		}
		figure.rebuildFigure();
	}

	@Override
	public void update(Observable o, Object arg) {
		refresh();
	}
}