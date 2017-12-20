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
import tech.ecoa.osets.eclipse.plugin.editors.parts.deploy.model.DeployedModuleInstanceNode;

public class DeployedModuleInstanceFigure extends AbstractFigure {
	private Label display = new Label();
	private DeployedModuleInstanceNode node;

	public DeployedModuleInstanceFigure() {
		super();
		setLayout(new XYLayout());
		setCornerDimensions(new Dimension(15, 15));
	}

	@Override
	public void rebuildFigure() {
		getDisplay().setForegroundColor(ColorConstants.black);
		add(getDisplay());
		setConstraint(getDisplay(), new Rectangle(10, 10, -1, -1));

		setForegroundColor(DeployedModuleInstanceNode.FONT_COLOR);
		setBackgroundColor(DeployedModuleInstanceNode.DEF_COLOR);
		TooltipFigure tooltip = new TooltipFigure();
		tooltip.setMessage("Deployed Module");
		setToolTip(tooltip);
		setOpaque(true);
		setVisible(true);
	}

	public Label getDisplay() {
		String dis = "Component Instance : " + node.getCompName() + "\nModule Instance : " + node.getModuleName() + "\nPriority : " + node.getPriority();
		display.setText(dis);
		return display;
	}

	public void setDisplay(Label display) {
		this.display = display;
	}

	public DeployedModuleInstanceNode getNode() {
		return node;
	}

	public void setNode(DeployedModuleInstanceNode node) {
		this.node = node;
	}

}
