/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.systemmodel.componentimplementation.modoperation.request;

import java.math.BigInteger;
import java.util.List;

import com.iawg.ecoa.systemmodel.SM_OperationParameter;

public class SM_RequestSentOp extends SM_RequestOp {

	private Boolean isSynchronous;
	private double timeout;
	private Integer timeoutSec;
	private Integer timeoutNano;
	private BigInteger maxConcurrentRequests;

	public SM_RequestSentOp(String name, List<SM_OperationParameter> inList, List<SM_OperationParameter> outList, boolean isSync, double timeout, BigInteger maxConcurrentRequests) {
		super(name, inList, outList);
		this.isSynchronous = isSync;
		this.timeout = timeout;
		this.maxConcurrentRequests = maxConcurrentRequests;

		timeoutSec = (int) timeout;
		timeoutNano = (int) ((timeout - timeoutSec) * 1000000000);
	}

	public double getTimeout() {
		return timeout;
	}

	public String getTimeoutSec() {
		return timeoutSec.toString();
	}

	public String getTimeoutNano() {
		return timeoutNano.toString();
	}

	public Boolean getIsSynchronous() {
		return isSynchronous;
	}

	public BigInteger getMaxConcurrentRequests() {
		return maxConcurrentRequests;
	}
}
