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
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPolicy;

import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.figure.ComponentImplementationFigure;
import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.ComponentImplementationNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.DynamicTriggerInstanceNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.ModuleTypeNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.Node;
import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.ServiceNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.TriggerInstanceNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.policy.ComponentImplementationLayoutPolicy;

@SuppressWarnings("rawtypes")
public class ComponentImplementationEditPart extends AppAbstractEditPart {

	public ComponentImplementationEditPart(ComponentImplementationNode model, String containerName) {
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
		ComponentImplementationNode model = (ComponentImplementationNode) getModel();
		ComponentImplementationFigure figure = new ComponentImplementationFigure();
		figure.setNode(model);
		Dimension dim = getDimension(model);
		model.setConstraints(new Rectangle(new Point(0, 0), (dim != null) ? dim : new Dimension(1000, 1000)));
		figure.setBounds(model.getConstraints());
		figure.rebuildFigure();
		return figure;
	}

	private Dimension getDimension(ComponentImplementationNode model) {
		Dimension ret = null;
		int h = 0, w = 0;
		for (Node nd : model.getChild()) {
			if (nd instanceof TriggerInstanceNode || nd instanceof ServiceNode || nd instanceof DynamicTriggerInstanceNode || nd instanceof ModuleTypeNode) {
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
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.LAYOUT_ROLE, new ComponentImplementationLayoutPolicy());
	}

	@Override
	protected List getModelChildren() {
		return ((ComponentImplementationNode) getModel()).getChild();
	}

	@Override
	protected void refreshVisuals() {
		ComponentImplementationNode model = (ComponentImplementationNode) getModel();
		ComponentImplementationFigure figure = (ComponentImplementationFigure) getFigure();
		figure.setNode(model);
		figure.setBounds(model.getConstraints());
		Dimension dim = getDimension(model);
		model.setConstraints(new Rectangle(new Point(0, 0), (dim != null) ? dim : new Dimension(1000, 1000)));
		figure.setBounds(model.getConstraints());
		figure.rebuildFigure();
	}

}
