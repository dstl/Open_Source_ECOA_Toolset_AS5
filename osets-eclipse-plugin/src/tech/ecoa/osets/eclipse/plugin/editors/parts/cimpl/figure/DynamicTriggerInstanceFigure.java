/*
 * Copyright 2017, BAE Systems Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.figure;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;

import tech.ecoa.osets.eclipse.plugin.common.TooltipFigure;
import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.DynamicTriggerInstanceNode;

public class DynamicTriggerInstanceFigure extends AbstractFigure {
	private Label display = new Label();
	private DynamicTriggerInstanceNode node;

	public DynamicTriggerInstanceFigure() {
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
		setForegroundColor(DynamicTriggerInstanceNode.FONT_COLOR);
		setBackgroundColor(DynamicTriggerInstanceNode.DEF_COLOR);
		TooltipFigure tooltip = new TooltipFigure();
		tooltip.setMessage("DynamicTriggerInstance");
		setToolTip(tooltip);
	}

	public Label getDisplay() {
		String dis = "Name : " + node.getName() + "\nPriority : " + node.getPriority();
		display.setText(dis);
		return display;
	}

	public void setDisplay(Label display) {
		this.display = display;
	}

	public DynamicTriggerInstanceNode getNode() {
		return node;
	}

	public void setNode(DynamicTriggerInstanceNode node) {
		this.node = node;
	}
}
