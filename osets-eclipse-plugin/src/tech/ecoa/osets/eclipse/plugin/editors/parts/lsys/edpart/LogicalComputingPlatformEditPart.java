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

import tech.ecoa.osets.eclipse.plugin.editors.parts.lsys.figure.LogicalComputingPlatformFigure;
import tech.ecoa.osets.eclipse.plugin.editors.parts.lsys.figure.LogicalSystemFigure;
import tech.ecoa.osets.eclipse.plugin.editors.parts.lsys.model.LogicalComputingPlatformNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.lsys.policy.LogicalComputingPlatformEditPolicy;
import tech.ecoa.osets.eclipse.plugin.editors.parts.lsys.policy.LogicalComputingPlatformLayoutPolicy;

@SuppressWarnings("rawtypes")
public class LogicalComputingPlatformEditPart extends AppAbstractEditPart {

	public LogicalComputingPlatformEditPart(LogicalComputingPlatformNode model, String containerName) {
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
		LogicalComputingPlatformNode model = (LogicalComputingPlatformNode) getModel();
		LogicalComputingPlatformFigure figure = new LogicalComputingPlatformFigure();
		figure.setNode(model);
		figure.rebuildFigure();
		return figure;
	}

	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.LAYOUT_ROLE, new LogicalComputingPlatformLayoutPolicy());
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new LogicalComputingPlatformEditPolicy());
	}

	@Override
	protected void refreshVisuals() {
		LogicalComputingPlatformNode model = (LogicalComputingPlatformNode) getModel();
		LogicalComputingPlatformFigure figure = (LogicalComputingPlatformFigure) getFigure();
		figure.setNode(model);
		try {
			LogicalSystemEditPart parent = (LogicalSystemEditPart) getParent();
			LogicalSystemFigure pFigure = (LogicalSystemFigure) parent.getFigure();
			pFigure.setConstraint(figure, model.getConstraints());
		} catch (Exception e) {
		}
		figure.setBounds(model.getConstraints());
		figure.rebuildFigure();
	}

	@Override
	protected List getModelChildren() {
		return ((LogicalComputingPlatformNode) getModel()).getChild();
	}

}
