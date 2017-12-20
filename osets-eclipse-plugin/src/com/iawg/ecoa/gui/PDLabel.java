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

import com.iawg.ecoa.systemmodel.deployment.SM_ProtectionDomain;

public class PDLabel extends JLabel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7652695043711576201L;
	private SM_ProtectionDomain pd;
	private PDState pdState = PDState.DOWN;

	public PDLabel(SM_ProtectionDomain pd) {
		this.pd = pd;

		this.setText(pd.getName());
		this.setToolTipText(pdState.toString());

		this.setOpaque(true);
	}

	public SM_ProtectionDomain getPD() {
		return pd;
	}

	public void setState(String pdState) {
		switch (pdState) {
		case "DOWN":
			this.pdState = PDState.DOWN;
			this.setBackground(Color.RED);
			break;
		case "UP":
			this.pdState = PDState.UP;
			this.setBackground(Color.GREEN);
			break;
		case "RESET":
			this.pdState = PDState.DOWN;
			this.setBackground(UIManager.getColor("Button.background"));
			break;
		default:
			break;
		}

		this.setToolTipText(pdState.toString());

	}

	public boolean isUp() {
		return (this.pdState == PDState.UP);
	}

	public String getState() {
		return this.pd.toString();
	}

	private enum PDState {
		DOWN, UP
	};

}
