/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.systemmodel.componentimplementation.links.data;

import com.iawg.ecoa.systemmodel.SM_Object;

public interface SM_WriterInterface {
	public SM_Object getWriterInst();

	public SM_Object getWriterOp();
}
