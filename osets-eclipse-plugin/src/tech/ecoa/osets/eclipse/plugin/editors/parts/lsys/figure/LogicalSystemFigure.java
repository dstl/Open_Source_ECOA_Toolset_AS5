/*
 * Copyright 2017, BAE Systems Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package tech.ecoa.osets.eclipse.plugin.editors.parts.lsys.figure;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.RoundedRectangleAnchor;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Rectangle;

import tech.ecoa.osets.eclipse.plugin.common.TooltipFigure;
import tech.ecoa.osets.eclipse.plugin.editors.parts.lsys.model.LogicalSystemNode;

public class LogicalSystemFigure extends Figure {
	private Label display = new Label();
	private LogicalSystemNode node;
	private XYLayout layout;
	private RoundedRectangleAnchor anchor;

	public LogicalSystemFigure() {
		super();
		setLayoutManager(new XYLayout());
	}

	public void rebuildFigure() {
		getDisplay().setForegroundColor(ColorConstants.black);
		add(getDisplay());
		setConstraint(getDisplay(), new Rectangle(10, 10, -1, -1));
		setOpaque(true);
		setVisible(true);
		setForegroundColor(ColorConstants.black);
		TooltipFigure tooltip = new TooltipFigure();
		tooltip.setMessage("Logical System");
		setToolTip(tooltip);
	}

	public Label getDisplay() {
		String dis = "Id : " + node.getName();
		display.setText(dis);
		return display;
	}

	public void setDisplay(Label display) {
		this.display = display;
	}

	public LogicalSystemNode getNode() {
		return node;
	}

	public void setNode(LogicalSystemNode node) {
		this.node = node;
	}

	public XYLayout getLayout() {
		return layout;
	}

	public void setLayout(XYLayout layout) {
		this.layout = layout;
	}

	public ConnectionAnchor getAnchor() {
		return anchor;
	}

	public void setAnchor(RoundedRectangleAnchor anchor) {
		this.anchor = anchor;
	}
}
