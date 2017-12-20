/*
 * Copyright 2017, BAE Systems Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package tech.ecoa.osets.eclipse.plugin.editors.parts.deploy.figure;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;

import tech.ecoa.osets.eclipse.plugin.common.TooltipFigure;
import tech.ecoa.osets.eclipse.plugin.editors.parts.deploy.model.PlatformConfigurationNode;

public class PlatformConfigurationFigure extends AbstractFigure {
	private Label display = new Label();
	private PlatformConfigurationNode node;

	public PlatformConfigurationFigure() {
		super();
		setLayout(new XYLayout());
		setCornerDimensions(new Dimension(15, 15));
	}

	@Override
	public void rebuildFigure() {
		getDisplay().setForegroundColor(ColorConstants.black);
		add(getDisplay());
		setConstraint(getDisplay(), new Rectangle(10, 10, -1, -1));

		setForegroundColor(PlatformConfigurationNode.FONT_COLOR);
		setBackgroundColor(PlatformConfigurationNode.DEF_COLOR);
		TooltipFigure tooltip = new TooltipFigure();
		tooltip.setMessage("Platform Configuration");
		setToolTip(tooltip);
		setOpaque(true);
		setVisible(true);
	}

	public Label getDisplay() {
		String dis = "Platform : " + node.getCompPlatform() + "\nNotification Max Number : " + node.getNotifMaxNumber() + "\nPlatform Id : " + node.getPlatNum() + "\nMulticast Address : " + node.getMcastAddr() + "\nPort : " + node.getPort();
		display.setText(dis);
		return display;
	}

	public void setDisplay(Label display) {
		this.display = display;
	}

	public PlatformConfigurationNode getNode() {
		return node;
	}

	public void setNode(PlatformConfigurationNode node) {
		this.node = node;
	}

}
