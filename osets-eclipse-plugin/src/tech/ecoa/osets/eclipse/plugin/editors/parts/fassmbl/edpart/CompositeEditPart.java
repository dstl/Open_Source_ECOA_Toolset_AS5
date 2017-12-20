/*
 * Copyright 2017, BAE Systems Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.edpart;

import java.util.List;
import java.util.Observable;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPolicy;

import tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.figure.CompositeFigure;
import tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.model.ComponentNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.model.CompositeNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.model.CompositePropertyNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.model.Node;
import tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.policy.CompositeNodeLayoutPolicy;

@SuppressWarnings("rawtypes")
public class CompositeEditPart extends AppAbstractEditPart {

	public CompositeEditPart(CompositeNode node, String containerName) {
		super();
		node.addObserver(this);
		node.setContainerName(containerName);
		setModel(node);
	}

	@Override
	protected IFigure createFigure() {
		CompositeNode model = (CompositeNode) getModel();
		CompositeFigure figure = new CompositeFigure();
		figure.setNode(model);
		Dimension dim = getDimension(model);
		model.setConstraints(new Rectangle(new Point(0, 0), (dim != null) ? dim : new Dimension(1000, 1000)));
		figure.setBounds(model.getConstraints());
		figure.rebuildFigure();
		return figure;
	}

	private Dimension getDimension(CompositeNode node) {
		Dimension ret = null;
		int h = 0, w = 0;
		for (Node nd : node.getChild()) {
			if (nd instanceof ComponentNode || nd instanceof CompositePropertyNode) {
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
		installEditPolicy(EditPolicy.LAYOUT_ROLE, new CompositeNodeLayoutPolicy());
	}

	@Override
	protected void refreshVisuals() {
		CompositeNode model = (CompositeNode) getModel();
		CompositeFigure figure = (CompositeFigure) getFigure();
		figure.setNode(model);
		Dimension dim = getDimension(model);
		model.setConstraints(new Rectangle(new Point(0, 0), (dim != null) ? dim : new Dimension(1000, 1000)));
		figure.setBounds(model.getConstraints());
		figure.rebuildFigure();
	}

	@Override
	protected List getModelChildren() {
		return ((CompositeNode) getModel()).getChild();
	}

	@Override
	public void update(Observable o, Object arg) {
		refresh();
	}

}
