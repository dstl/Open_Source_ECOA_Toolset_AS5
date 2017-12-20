/*
 * Copyright 2017, BAE Systems Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package tech.ecoa.osets.eclipse.plugin.editors.parts.lsys.edpart;

import java.util.List;
import java.util.Observable;

import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPolicy;

import tech.ecoa.osets.eclipse.plugin.editors.parts.lsys.figure.LogicalComputingFigure;
import tech.ecoa.osets.eclipse.plugin.editors.parts.lsys.figure.LogicalComputingPlatformFigure;
import tech.ecoa.osets.eclipse.plugin.editors.parts.lsys.model.LogicalComputingNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.lsys.policy.LogicalComputingEditPolicy;
import tech.ecoa.osets.eclipse.plugin.editors.parts.lsys.policy.LogicalComputingLayoutPolicy;

@SuppressWarnings("rawtypes")
public class LogicalComputingEditPart extends AppAbstractEditPart {

	public LogicalComputingEditPart(LogicalComputingNode model, String containerName) {
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
		LogicalComputingNode model = (LogicalComputingNode) getModel();
		LogicalComputingFigure figure = new LogicalComputingFigure();
		figure.setNode(model);
		figure.rebuildFigure();
		return figure;
	}

	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.LAYOUT_ROLE, new LogicalComputingLayoutPolicy());
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new LogicalComputingEditPolicy());
	}

	@Override
	protected void refreshVisuals() {
		LogicalComputingNode model = (LogicalComputingNode) getModel();
		LogicalComputingFigure figure = (LogicalComputingFigure) getFigure();
		figure.setNode(model);
		try {
			LogicalComputingPlatformEditPart parent = (LogicalComputingPlatformEditPart) getParent();
			LogicalComputingPlatformFigure pFigure = (LogicalComputingPlatformFigure) parent.getFigure();
			pFigure.setConstraint(figure, model.getConstraints());
		} catch (Exception e) {
		}
		figure.setBounds(model.getConstraints());
		figure.rebuildFigure();
	}

	@Override
	protected List getModelChildren() {
		return ((LogicalComputingNode) getModel()).getChild();
	}

}
