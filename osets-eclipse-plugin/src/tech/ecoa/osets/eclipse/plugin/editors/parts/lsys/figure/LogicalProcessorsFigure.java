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
import tech.ecoa.osets.eclipse.plugin.editors.parts.lsys.model.LogicalProcessorsNode;

public class LogicalProcessorsFigure extends AbstractFigure {
	private Label display = new Label();
	private LogicalProcessorsNode node;

	public LogicalProcessorsFigure() {
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

		setForegroundColor(LogicalProcessorsNode.FONT_COLOR);
		setBackgroundColor(LogicalProcessorsNode.DEF_COLOR);
		TooltipFigure tooltip = new TooltipFigure();
		tooltip.setMessage("Logical Processor");
		setToolTip(tooltip);
	}

	public Label getDisplay() {
		String dis = "Number : " + node.getNum() + "\nType : " + node.getType() + "\nStep Duration (ns) : " + node.getStepDur();
		display.setText(dis);
		return display;
	}

	public void setDisplay(Label display) {
		this.display = display;
	}

	public LogicalProcessorsNode getNode() {
		return node;
	}

	public void setNode(LogicalProcessorsNode node) {
		this.node = node;
	}
}
