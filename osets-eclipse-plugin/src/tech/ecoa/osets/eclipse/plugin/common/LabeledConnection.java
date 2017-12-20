/*
 * Copyright 2017, BAE Systems Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package tech.ecoa.osets.eclipse.plugin.common;

import org.eclipse.draw2d.ConnectionEndpointLocator;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.PolylineConnection;

public class LabeledConnection extends PolylineConnection {
	private Label label = new Label();
	private String display;

	public LabeledConnection(String display) {
		this.display = display;
		redraw();
	}

	public void redraw() {
		ConnectionEndpointLocator teLoc = new ConnectionEndpointLocator(this, true);
		teLoc.setVDistance(15);
		label.setText(display);
		label.setOpaque(true);
		setLineWidth(2);
		add(label, teLoc);
	}

	public Label getLabel() {
		return label;
	}

	public void setLabel(Label label) {
		this.label = label;
	}

	public String getDisplay() {
		return display;
	}

	public void setDisplay(String display) {
		this.display = display;
	}

}
