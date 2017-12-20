/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.gui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.UIManager;

import com.iawg.ecoa.ECOA_Monitor;
import com.iawg.ecoa.systemmodel.assembly.SM_ComponentInstance;
import com.iawg.ecoa.systemmodel.componentdefinition.serviceinstance.SM_ServiceInstance;
import com.iawg.ecoa.systemmodel.deployment.SM_ProtectionDomain;
import com.iawg.ecoa.systemmodel.deployment.logicalsystem.SM_LogicalComputingPlatform;

public class LCPButton extends JButton implements ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = -333318764700222170L;
	private SM_LogicalComputingPlatform lcp;
	private ECOA_Monitor ecoaMonitor;
	private PlatformState platformState = PlatformState.DOWN;
	private ArrayList<PDLabel> pdLabelList = new ArrayList<PDLabel>();
	private ArrayList<ServiceAvailLabel> serviceAvailabiltyLabelList = new ArrayList<ServiceAvailLabel>();

	public LCPButton(SM_LogicalComputingPlatform lcp, ECOA_Monitor ecoaMonitor) {
		this.lcp = lcp;
		this.ecoaMonitor = ecoaMonitor;

		this.setText(lcp.getName());
		this.setToolTipText(platformState.toString());

		this.addActionListener(this);

		for (SM_ProtectionDomain pd : lcp.getAllProtectionDomains()) {
			pdLabelList.add(new PDLabel(pd));

			for (SM_ComponentInstance compInst : pd.getComponentInstances()) {
				for (SM_ServiceInstance serviceInst : compInst.getCompType().getServiceInstancesList()) {
					serviceAvailabiltyLabelList.add(new ServiceAvailLabel(compInst, serviceInst));
				}
			}
		}

	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// Add protection domains to panel
		ecoaMonitor.getPDPanel().displayPDs(pdLabelList);

		// Add provided service availabilities to panel
		ecoaMonitor.getServiceAvailPanel().displayServiceAvailButtons(serviceAvailabiltyLabelList);
	}

	public SM_LogicalComputingPlatform getLCP() {
		return lcp;
	}

	public PDLabel getPDLabel(String pdName) {
		for (PDLabel label : pdLabelList) {
			if (label.getPD().getName().equalsIgnoreCase(pdName)) {
				return label;
			}
		}

		// TODO - throw an exception?!
		return null;
	}

	public ServiceAvailLabel getServiceAvailLabel(String serviceInstName) {
		for (ServiceAvailLabel label : serviceAvailabiltyLabelList) {
			if (label.getServiceInstName().equalsIgnoreCase(serviceInstName)) {
				return label;
			}
		}

		// TODO - throw an exception?!
		return null;
	}

	public ArrayList<PDLabel> getPDLabelList() {
		return pdLabelList;
	}

	public ArrayList<ServiceAvailLabel> getServiceAvailabiltyLabelList() {
		return serviceAvailabiltyLabelList;
	}

	public void setState(String lcpState) {
		switch (lcpState) {
		case "DOWN":
			this.platformState = PlatformState.DOWN;
			this.setBackground(Color.RED);
			break;
		case "UP":
			this.platformState = PlatformState.UP;
			this.setBackground(Color.GREEN);
			break;
		case "RESET":
			this.platformState = PlatformState.DOWN;
			this.setBackground(UIManager.getColor("Button.background"));
		default:
			break;
		}

		this.setToolTipText(platformState.toString());
	}

	private enum PlatformState {
		DOWN, UP
	}

}
