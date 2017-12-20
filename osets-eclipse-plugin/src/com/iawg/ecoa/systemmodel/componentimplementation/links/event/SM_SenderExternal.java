/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.systemmodel.componentimplementation.links.event;

import com.iawg.ecoa.systemmodel.SM_Object;

public class SM_SenderExternal implements SM_SenderInterface {
	private String externalOpName;
	private String language;
	private SM_EventLink parentLink;

	public SM_SenderExternal(SM_EventLink parentLink, String externalOpName, String language) {
		this.externalOpName = externalOpName;
		this.language = language;
		this.parentLink = parentLink;
	}

	@Override
	public SM_Object getSenderInst() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getSenderOpName() {
		return externalOpName;
	}

	public String getLanguage() {
		return language;
	}

	public SM_EventLink getParentLink() {
		return parentLink;
	}

}
