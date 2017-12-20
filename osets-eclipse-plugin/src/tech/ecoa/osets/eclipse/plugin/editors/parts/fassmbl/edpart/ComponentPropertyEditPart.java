/*
 * Copyright 2017, BAE Systems Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.edpart;

import java.util.Observable;

import org.eclipse.draw2d.IFigure;

import tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.figure.ComponentFigure;
import tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.figure.ComponentPropertyFigure;
import tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.model.ComponentPropertyNode;

public class ComponentPropertyEditPart extends AppAbstractEditPart {

	public ComponentPropertyEditPart(ComponentPropertyNode node, String containerName) {
		super();
		node.addObserver(this);
		node.setContainerName(containerName);
		setModel(node);
	}

	@Override
	protected IFigure createFigure() {
		ComponentPropertyNode model = (ComponentPropertyNode) getModel();
		ComponentPropertyFigure figure = new ComponentPropertyFigure();
		figure.setNode(model);
		figure.rebuildFigure();
		return figure;
	}

	@Override
	protected void createEditPolicies() {
		// installEditPolicy(EditPolicy.COMPONENT_ROLE, new
		// ComponentPropertyNodeEditPolicy());
	}

	@Override
	protected void refreshVisuals() {
		ComponentPropertyNode model = (ComponentPropertyNode) getModel();
		ComponentPropertyFigure figure = (ComponentPropertyFigure) getFigure();
		figure.setNode(model);
		try {
			ComponentEditPart parent = (ComponentEditPart) getParent();
			ComponentFigure pFigure = (ComponentFigure) parent.getFigure();
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