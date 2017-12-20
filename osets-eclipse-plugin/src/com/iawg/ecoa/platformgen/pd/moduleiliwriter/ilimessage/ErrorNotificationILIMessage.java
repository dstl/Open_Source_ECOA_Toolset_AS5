/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.platformgen.pd.moduleiliwriter.ilimessage;

import com.iawg.ecoa.systemmodel.componentimplementation.SM_ModuleInstance;

public class ErrorNotificationILIMessage extends ILIMessage {
	private SM_ModuleInstance modInstance;

	public ErrorNotificationILIMessage(int messageID, SM_ModuleInstance modInstance) {
		super(messageID);
		this.modInstance = modInstance;
	}

	public SM_ModuleInstance getModuleInstance() {
		return modInstance;
	}

	public void setModuleInstance(SM_ModuleInstance modInstance) {
		this.modInstance = modInstance;
	}
}
