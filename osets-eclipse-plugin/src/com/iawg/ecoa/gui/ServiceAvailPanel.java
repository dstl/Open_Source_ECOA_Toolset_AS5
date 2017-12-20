/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.gui;

import java.awt.Color;
import java.awt.GridLayout;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import com.iawg.ecoa.ECOA_Monitor;

@SuppressWarnings("unused")
public class ServiceAvailPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6416986262469278604L;
	private ECOA_Monitor ecoaMonitor;

	public ServiceAvailPanel(ECOA_Monitor ecoaMonitor) {
		this.ecoaMonitor = ecoaMonitor;

		this.setLayout(new GridLayout(20, 1, 10, 10));

		this.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black, 1), "Provided Service Availability"));
	}

	public void displayServiceAvailButtons(ArrayList<ServiceAvailLabel> serviceAvailabiltyLabelList) {
		// Clear the service availability panel.
		if (this.getComponentCount() != 0) {
			this.removeAll();
		}

		for (ServiceAvailLabel label : serviceAvailabiltyLabelList) {
			this.add(label);
		}

		// Redraw the panel container.
		this.updateUI();

	}

}
