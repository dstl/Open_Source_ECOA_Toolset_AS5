/*
 * Copyright 2017, BAE Systems Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.edpart;

import java.util.List;
import java.util.Observable;

import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.NodeEditPart;
import org.eclipse.gef.Request;

import tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.figure.ComponentFigure;
import tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.figure.ServiceFigure;
import tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.model.ServiceNode;

@SuppressWarnings("rawtypes")
public class ServiceEditPart extends AppAbstractEditPart implements NodeEditPart {

	public ServiceEditPart(ServiceNode node, String containerName) {
		super();
		node.addObserver(this);
		node.setContainerName(containerName);
		setModel(node);
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
		// installEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE, new
		// LinkCreatePolicy());
	}

	@Override
	protected void refreshVisuals() {
		ServiceNode model = (ServiceNode) getModel();
		ServiceFigure figure = (ServiceFigure) getFigure();
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

	@Override
	protected List getModelSourceConnections() {
		return ((ServiceNode) getModel()).getOutLinks();
	}

	@Override
	protected List getModelTargetConnections() {
		return ((ServiceNode) getModel()).getInLinks();
	}

	@Override
	public ConnectionAnchor getSourceConnectionAnchor(ConnectionEditPart connection) {
		return ((ServiceFigure) getFigure()).getAnchor();
	}

	@Override
	public ConnectionAnchor getTargetConnectionAnchor(ConnectionEditPart connection) {
		return ((ServiceFigure) getFigure()).getAnchor();
	}

	@Override
	public ConnectionAnchor getSourceConnectionAnchor(Request request) {
		return ((ServiceFigure) getFigure()).getAnchor();
	}

	@Override
	public ConnectionAnchor getTargetConnectionAnchor(Request request) {
		return ((ServiceFigure) getFigure()).getAnchor();
	}

}