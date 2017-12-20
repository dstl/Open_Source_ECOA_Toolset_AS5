/*
 * Copyright 2017, BAE Systems Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package tech.ecoa.osets.eclipse.plugin.editors.parts.lsys.edpart;

import java.util.Observable;

import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPolicy;

import tech.ecoa.osets.eclipse.plugin.editors.parts.lsys.figure.LogicalComputingFigure;
import tech.ecoa.osets.eclipse.plugin.editors.parts.lsys.figure.LogicalProcessorsFigure;
import tech.ecoa.osets.eclipse.plugin.editors.parts.lsys.model.LogicalProcessorsNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.lsys.policy.LogicalProcessorsEditPolicy;

public class LogicalProcessorsEditPart extends AppAbstractEditPart {

	public LogicalProcessorsEditPart(LogicalProcessorsNode model, String containerName) {
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
		LogicalProcessorsNode model = (LogicalProcessorsNode) getModel();
		LogicalProcessorsFigure figure = new LogicalProcessorsFigure();
		figure.setNode(model);
		figure.rebuildFigure();
		return figure;
	}

	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new LogicalProcessorsEditPolicy());
	}

	@Override
	protected void refreshVisuals() {
		LogicalProcessorsNode model = (LogicalProcessorsNode) getModel();
		LogicalProcessorsFigure figure = (LogicalProcessorsFigure) getFigure();
		figure.setNode(model);
		try {
			LogicalComputingEditPart parent = (LogicalComputingEditPart) getParent();
			LogicalComputingFigure pFigure = (LogicalComputingFigure) parent.getFigure();
			pFigure.setConstraint(figure, model.getConstraints());
		} catch (Exception e) {
		}
		figure.setBounds(model.getConstraints());
		figure.rebuildFigure();
	}

}
