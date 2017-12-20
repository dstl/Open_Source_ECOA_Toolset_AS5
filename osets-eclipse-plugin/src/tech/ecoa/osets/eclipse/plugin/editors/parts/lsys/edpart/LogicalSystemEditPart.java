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
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPolicy;

import tech.ecoa.osets.eclipse.plugin.editors.parts.lsys.figure.LogicalSystemFigure;
import tech.ecoa.osets.eclipse.plugin.editors.parts.lsys.model.LogicalComputingPlatformNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.lsys.model.LogicalSystemNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.lsys.model.Node;
import tech.ecoa.osets.eclipse.plugin.editors.parts.lsys.policy.LogicalSystemLayoutPolicy;

@SuppressWarnings("rawtypes")
public class LogicalSystemEditPart extends AppAbstractEditPart {

	public LogicalSystemEditPart(LogicalSystemNode model, String containerName) {
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
		LogicalSystemNode model = (LogicalSystemNode) getModel();
		LogicalSystemFigure figure = new LogicalSystemFigure();
		figure.setNode(model);
		Dimension dim = getDimension(model);
		model.setConstraints(new Rectangle(new Point(0, 0), (dim != null) ? dim : new Dimension(1000, 1000)));
		figure.setBounds(model.getConstraints());
		figure.rebuildFigure();
		return figure;
	}

	private Dimension getDimension(LogicalSystemNode node) {
		Dimension ret = null;
		int h = 0, w = 0;
		for (Node nd : node.getChild()) {
			if (nd instanceof LogicalComputingPlatformNode) {
				Rectangle bounds = nd.getConstraints();
				int bottom = bounds.y + bounds.height;
				int right = bounds.x + bounds.width;
				if (h < bottom)
					h = bottom;
				if (w < right)
					w = right;
			}
		}
		h = (h == 0) ? 50 : h + 50;
		w = (w == 0) ? 250 : w + 50;
		ret = new Dimension(w, h);
		return ret;
	}

	@Override
	protected List getModelChildren() {
		return ((LogicalSystemNode) getModel()).getChild();
	}

	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.LAYOUT_ROLE, new LogicalSystemLayoutPolicy());
	}

	@Override
	protected void refreshVisuals() {
		LogicalSystemNode model = (LogicalSystemNode) getModel();
		LogicalSystemFigure figure = (LogicalSystemFigure) getFigure();
		figure.setNode(model);
		Dimension dim = getDimension(model);
		model.setConstraints(new Rectangle(new Point(0, 0), (dim != null) ? dim : new Dimension(1000, 1000)));
		figure.setBounds(model.getConstraints());
		figure.rebuildFigure();
	}

}
