/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.systemmodel.componentimplementation.links;

import java.math.BigInteger;

public class SM_ActivatingFIFO {
	private BigInteger fifoSize;

	public SM_ActivatingFIFO(BigInteger bigInteger) {
		this.fifoSize = bigInteger;
	}

	public BigInteger getFifoSize() {
		return fifoSize;
	}

}
