/*
 * Copyright 2017, BAE Systems Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.edpart;

import java.util.List;
import java.util.Observable;

import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.NodeEditPart;
import org.eclipse.gef.Request;

import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.figure.TriggerInstanceFigure;
import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.figure.TriggerInstanceTerminalFigure;
import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.TriggerInstanceTerminalNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.policy.LinkCreatePolicy;

@SuppressWarnings("rawtypes")
public class TriggerInstanceTerminalEditPart extends AppAbstractEditPart implements NodeEditPart {
	public TriggerInstanceTerminalEditPart(TriggerInstanceTerminalNode model, String containerName) {
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
		TriggerInstanceTerminalNode model = (TriggerInstanceTerminalNode) getModel();
		TriggerInstanceTerminalFigure figure = new TriggerInstanceTerminalFigure();
		figure.setNode(model);
		figure.rebuildFigure();
		return figure;
	}

	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE, new LinkCreatePolicy());
	}

	@Override
	protected void refreshVisuals() {
		TriggerInstanceTerminalNode model = (TriggerInstanceTerminalNode) getModel();
		TriggerInstanceTerminalFigure figure = (TriggerInstanceTerminalFigure) getFigure();
		figure.setNode(model);
		try {
			TriggerInstanceEditPart parent = (TriggerInstanceEditPart) getParent();
			TriggerInstanceFigure pFigure = (TriggerInstanceFigure) parent.getFigure();
			pFigure.setConstraint(figure, model.getConstraints());
		} catch (Exception e) {
		}
		figure.setBounds(model.getConstraints());
		figure.rebuildFigure();
	}

	@Override
	protected List getModelSourceConnections() {
		return ((TriggerInstanceTerminalNode) getModel()).getOutLinks();
	}

	@Override
	protected List getModelTargetConnections() {
		return ((TriggerInstanceTerminalNode) getModel()).getInLinks();
	}

	@Override
	public ConnectionAnchor getSourceConnectionAnchor(ConnectionEditPart connection) {
		return ((TriggerInstanceTerminalFigure) getFigure()).getAnchor();
	}

	@Override
	public ConnectionAnchor getTargetConnectionAnchor(ConnectionEditPart connection) {
		return ((TriggerInstanceTerminalFigure) getFigure()).getAnchor();
	}

	@Override
	public ConnectionAnchor getSourceConnectionAnchor(Request request) {
		return ((TriggerInstanceTerminalFigure) getFigure()).getAnchor();
	}

	@Override
	public ConnectionAnchor getTargetConnectionAnchor(Request request) {
		return ((TriggerInstanceTerminalFigure) getFigure()).getAnchor();
	}

}
