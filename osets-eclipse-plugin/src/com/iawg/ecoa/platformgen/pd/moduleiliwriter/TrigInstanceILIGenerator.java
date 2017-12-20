/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.platformgen.pd.moduleiliwriter;

import java.nio.file.Path;
import java.util.ArrayList;

import com.iawg.ecoa.platformgen.PlatformGenerator;
import com.iawg.ecoa.platformgen.pd.moduleiliwriter.ilimessage.ILIMessage;
import com.iawg.ecoa.platformgen.pd.moduleiliwriter.ilimessage.LifecycleILIMessage;
import com.iawg.ecoa.platformgen.pd.moduleiliwriter.ilimessage.LifecycleILIMessage.ILIMessageType;
import com.iawg.ecoa.platformgen.pd.moduleiliwriter.ilimessage.TriggerInstanceILI;
import com.iawg.ecoa.systemmodel.componentimplementation.SM_TriggerInstance;

public class TrigInstanceILIGenerator {

	private PlatformGenerator platformGenerator;
	private TriggerInstanceILI trigInstILI;
	private ArrayList<ILIMessage> iliMessageList = new ArrayList<ILIMessage>();
	private SM_TriggerInstance trigInst;

	public TrigInstanceILIGenerator(PlatformGenerator platformGenerator, SM_TriggerInstance trigInst) {
		this.platformGenerator = platformGenerator;
		this.trigInstILI = new TriggerInstanceILI(trigInst);
		this.trigInst = trigInst;
	}

	private int addModuleLifecycleMessages(int msgNum) {
		// Add 5 messages for module state
		// 1 - START_module
		// 2 - STOP_module
		// 3 - INITIALIZE_module
		// 4 - SHUTDOWN_module
		// 5 - LIFECYCLE_NOTIFICATION
		iliMessageList.add(new LifecycleILIMessage(ILIMessageType.INITIALIZE_MODULE, msgNum++));
		iliMessageList.add(new LifecycleILIMessage(ILIMessageType.START_MODULE, msgNum++));
		iliMessageList.add(new LifecycleILIMessage(ILIMessageType.STOP_MODULE, msgNum++));
		iliMessageList.add(new LifecycleILIMessage(ILIMessageType.SHUTDOWN_MODULE, msgNum++));

		return msgNum;
	}

	public void generate() {
		// Get a list of all ILI messages required for this trigger
		getILIMessages();

		Path directory = platformGenerator.getPdOutputDir().resolve("src-gen/" + trigInst.getComponentImplementation().getName() + "/" + trigInst.getName());

		TrigInstanceILIWriterC iliWriter = new TrigInstanceILIWriterC(directory, trigInstILI.getTrigInst());

		iliWriter.open();
		iliWriter.writePreamble();
		iliWriter.setILIMessages(iliMessageList);
		iliWriter.writeMessageDefinition();
		iliWriter.writeMessageStructure();
		iliWriter.writeIncludes();
		iliWriter.close();

		// Set the ILI Message list in the module type
		trigInstILI.setILIMessageList(iliMessageList);

	}

	public void getILIMessages() {
		// Offset other ILI messages by the number of module lifecycle messages
		int msgNum = 1;

		// Add module lifecycle ILI messages (these will always be the first 5
		// messages)
		msgNum = addModuleLifecycleMessages(msgNum);
	}

	public TriggerInstanceILI getTrigInstILI() {
		return trigInstILI;
	}
}
