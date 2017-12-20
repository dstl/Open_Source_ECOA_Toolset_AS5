/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.gui;

import java.awt.Color;
import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import com.iawg.ecoa.ECOA_Monitor;
import com.iawg.ecoa.systemmodel.deployment.logicalsystem.SM_LogicalComputingPlatform;

@SuppressWarnings("unused")
public class LCPPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8162772961125861540L;
	private ECOA_Monitor ecoaMonitor;

	public LCPPanel(ECOA_Monitor ecoaMonitor) {
		this.ecoaMonitor = ecoaMonitor;

		this.setLayout(new FlowLayout());

		this.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black, 1), "Logical Computing Platforms"));

		for (SM_LogicalComputingPlatform lcp : ecoaMonitor.getSystemModel().getLogicalSystem().getLogicalcomputingPlatforms()) {
			this.add(new LCPButton(lcp, ecoaMonitor));
		}

	}

}
