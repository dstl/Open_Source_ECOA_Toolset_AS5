/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.gui;

import java.awt.Color;

import javax.swing.JLabel;
import javax.swing.UIManager;

import com.iawg.ecoa.systemmodel.assembly.SM_ComponentInstance;
import com.iawg.ecoa.systemmodel.componentdefinition.serviceinstance.SM_ServiceInstance;

@SuppressWarnings("unused")
public class ServiceAvailLabel extends JLabel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4663919586283008878L;
	private SM_ComponentInstance compInst;
	private SM_ServiceInstance serviceInst;
	private boolean isAvailable = false;

	public ServiceAvailLabel(SM_ComponentInstance compInst, SM_ServiceInstance serviceInst) {
		this.compInst = compInst;
		this.serviceInst = serviceInst;

		this.setText(compInst.getName() + "/" + serviceInst.getName());
		this.setToolTipText("UNAVAILABLE");
		this.setOpaque(true);
	}

	public String getServiceInstName() {
		return compInst.getName() + "/" + serviceInst.getName();
	}

	public void setAvailability(String serviceAvailability) {
		switch (serviceAvailability) {
		case "AVAILABLE":
			this.isAvailable = true;
			this.setBackground(Color.GREEN);
			break;
		case "UNAVAILABLE":
			this.isAvailable = false;
			this.setBackground(Color.RED);
			break;
		case "RESET":
			this.isAvailable = false;
			this.setBackground(UIManager.getColor("Button.background"));
		default:
			break;
		}

		this.setToolTipText(serviceAvailability);

	}

}
