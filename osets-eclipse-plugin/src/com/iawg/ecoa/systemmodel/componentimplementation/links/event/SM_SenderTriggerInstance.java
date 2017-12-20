/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.systemmodel.componentimplementation.links.event;

import com.iawg.ecoa.systemmodel.componentimplementation.SM_TriggerInstance;

public class SM_SenderTriggerInstance implements SM_SenderInterface {

	private SM_TriggerInstance sender;
	private double triggerPeriod;
	private Integer triggerPeriodSec;
	private Integer triggerPeriodNano;

	public SM_SenderTriggerInstance(SM_TriggerInstance trigger, double period) {
		this.sender = trigger;
		this.triggerPeriod = period;

		triggerPeriodSec = (int) triggerPeriod;
		triggerPeriodNano = (int) ((triggerPeriod - triggerPeriodSec) * 1000000000);

	}

	public SM_TriggerInstance getSenderInst() {
		return sender;
	}

	public double getTriggerPeriod() {
		return triggerPeriod;
	}

	public String getTriggerPeriodSec() {
		return triggerPeriodSec.toString();
	}

	public String getTriggerPeriodNano() {
		return triggerPeriodNano.toString();
	}

	@Override
	public String getSenderOpName() {
		return "triggerOp";
	}
}
