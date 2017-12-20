/*
 * Copyright 2017, BAE Systems Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package tech.ecoa.osets.eclipse.plugin.editors.parts.lsys.figure;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;

import tech.ecoa.osets.eclipse.plugin.common.TooltipFigure;
import tech.ecoa.osets.eclipse.plugin.editors.parts.lsys.model.LogicalComputingNode;

public class LogicalComputingFigure extends AbstractFigure {
	private Label display = new Label();
	private LogicalComputingNode node;

	public LogicalComputingFigure() {
		super();
		setLayout(new XYLayout());
		setCornerDimensions(new Dimension(15, 15));
	}

	@Override
	public void rebuildFigure() {
		getDisplay().setForegroundColor(ColorConstants.black);
		add(getDisplay());
		setConstraint(getDisplay(), new Rectangle(10, 10, -1, -1));
		setOpaque(true);
		setVisible(true);

		setForegroundColor(LogicalComputingNode.FONT_COLOR);
		setBackgroundColor(LogicalComputingNode.DEF_COLOR);
		TooltipFigure tooltip = new TooltipFigure();
		tooltip.setMessage("Computing Node");
		setToolTip(tooltip);
	}

	public Label getDisplay() {
		String dis = "Id : " + node.getName() + "\nEndianess : " + node.getEndianess() + "\nOS Name : " + node.getOsName() + "\nOS Version : " + node.getOsVer() + "\nAvailable Memory (GB) : " + node.getAvailMem() + "\nSwitch Time (µs) : " + node.getMst();
		display.setText(dis);
		return display;
	}

	public void setDisplay(Label display) {
		this.display = display;
	}

	public LogicalComputingNode getNode() {
		return node;
	}

	public void setNode(LogicalComputingNode node) {
		this.node = node;
	}
}
