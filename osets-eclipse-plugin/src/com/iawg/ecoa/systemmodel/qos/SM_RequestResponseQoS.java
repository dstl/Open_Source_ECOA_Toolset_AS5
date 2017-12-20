/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.systemmodel.qos;

import com.iawg.ecoa.systemmodel.SM_Object;
import com.iawg.ecoa.systemmodel.servicedefinition.serviceoperation.SM_RRServiceOp;

public class SM_RequestResponseQoS extends SM_Object {
	private double maxResponseTime;
	private Integer maxResponseSec;
	private Integer maxResponseNano;

	private SM_RRServiceOp referenceRRServiceOp;

	public SM_RequestResponseQoS(String name, double maxResponse, SM_RRServiceOp rrServiceOp) {
		super(name);
		this.maxResponseTime = maxResponse;
		this.referenceRRServiceOp = rrServiceOp;

		maxResponseSec = (int) maxResponse;
		maxResponseNano = (int) ((maxResponse - maxResponseSec) * 1000000000);
	}

	public double getMaxResponseTime() {
		return maxResponseTime;
	}

	public String getMaxResponseTimeSec() {
		return maxResponseSec.toString();
	}

	public String getMaxResponseTimeNano() {
		return maxResponseNano.toString();
	}

	public SM_RRServiceOp getReferenceRRServiceOp() {
		return referenceRRServiceOp;
	}

}
