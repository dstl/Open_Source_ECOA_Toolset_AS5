/*
 * Copyright 2017, BAE Systems Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package tech.ecoa.osets.eclipse.plugin.editors.parts.assmbl.figure;

import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.RoundedRectangle;
import org.eclipse.draw2d.RoundedRectangleAnchor;
import org.eclipse.draw2d.XYLayout;

public abstract class AbstractFigure extends RoundedRectangle {
	private XYLayout layout;
	private RoundedRectangleAnchor anchor;

	public abstract void rebuildFigure();

	public XYLayout getLayout() {
		return layout;
	}

	public void setLayout(XYLayout layout) {
		setLayoutManager(layout);
		this.layout = layout;
	}

	public ConnectionAnchor getAnchor() {
		if (anchor == null)
			anchor = new RoundedRectangleAnchor(this);
		return anchor;
	}

	public void setAnchor(RoundedRectangleAnchor anchor) {
		this.anchor = anchor;
	}

}
