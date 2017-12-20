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

import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.figure.ModuleOperationFigure;
import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.figure.ModuleTypeFigure;
import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.ModuleOperationNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.policy.LinkCreatePolicy;
import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.policy.ModuleOperationEditPolicy;
import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.policy.ModuleOperationLayoutPolicy;

@SuppressWarnings("rawtypes")
public class ModuleOperationEditPart extends AppAbstractEditPart implements NodeEditPart {

	public ModuleOperationEditPart(ModuleOperationNode model, String containerName) {
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
		ModuleOperationNode model = (ModuleOperationNode) getModel();
		ModuleOperationFigure figure = new ModuleOperationFigure();
		figure.setNode(model);
		figure.rebuildFigure();
		return figure;
	}

	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.LAYOUT_ROLE, new ModuleOperationLayoutPolicy());
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new ModuleOperationEditPolicy());
		installEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE, new LinkCreatePolicy());
	}

	@Override
	protected List getModelChildren() {
		return ((ModuleOperationNode) getModel()).getChild();
	}

	@Override
	protected void refreshVisuals() {
		ModuleOperationNode model = (ModuleOperationNode) getModel();
		ModuleOperationFigure figure = (ModuleOperationFigure) getFigure();
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

	@Override
	protected List getModelSourceConnections() {
		return ((ModuleOperationNode) getModel()).getOutLinks();
	}

	@Override
	protected List getModelTargetConnections() {
		return ((ModuleOperationNode) getModel()).getInLinks();
	}

	@Override
	public ConnectionAnchor getSourceConnectionAnchor(ConnectionEditPart connection) {
		return ((ModuleOperationFigure) getFigure()).getAnchor();
	}

	@Override
	public ConnectionAnchor getTargetConnectionAnchor(ConnectionEditPart connection) {
		return ((ModuleOperationFigure) getFigure()).getAnchor();
	}

	@Override
	public ConnectionAnchor getSourceConnectionAnchor(Request request) {
		return ((ModuleOperationFigure) getFigure()).getAnchor();
	}

	@Override
	public ConnectionAnchor getTargetConnectionAnchor(Request request) {
		return ((ModuleOperationFigure) getFigure()).getAnchor();
	}

}
