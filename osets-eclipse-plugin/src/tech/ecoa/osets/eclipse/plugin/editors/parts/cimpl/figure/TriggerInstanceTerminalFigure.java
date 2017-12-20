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
import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.TriggerInstanceTerminalNode;

public class TriggerInstanceTerminalFigure extends AbstractFigure {

	private Label display = new Label();
	private TriggerInstanceTerminalNode node;

	public TriggerInstanceTerminalFigure() {
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
		setForegroundColor(TriggerInstanceTerminalNode.FONT_COLOR);
		setBackgroundColor(TriggerInstanceTerminalNode.DEF_COLOR);
		TooltipFigure tooltip = new TooltipFigure();
		tooltip.setMessage("Trigger Terminal");
		setToolTip(tooltip);
	}

	public Label getDisplay() {
		String dis = "OUT";
		display.setText(dis);
		return display;
	}

	public void setDisplay(Label display) {
		this.display = display;
	}

	public TriggerInstanceTerminalNode getNode() {
		return node;
	}

	public void setNode(TriggerInstanceTerminalNode node) {
		this.node = node;
	}

}
