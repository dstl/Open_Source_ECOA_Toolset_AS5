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
import com.iawg.ecoa.platformgen.pd.moduleiliwriter.ilimessage.DynamicTriggerInstanceILI;
import com.iawg.ecoa.platformgen.pd.moduleiliwriter.ilimessage.EventILIMessage;
import com.iawg.ecoa.platformgen.pd.moduleiliwriter.ilimessage.ILIMessage;
import com.iawg.ecoa.platformgen.pd.moduleiliwriter.ilimessage.LifecycleILIMessage;
import com.iawg.ecoa.platformgen.pd.moduleiliwriter.ilimessage.LifecycleILIMessage.ILIMessageType;
import com.iawg.ecoa.systemmodel.SM_OperationParameter;
import com.iawg.ecoa.systemmodel.componentimplementation.SM_DynamicTriggerInstance;
import com.iawg.ecoa.systemmodel.componentimplementation.links.event.SM_EventLink;
import com.iawg.ecoa.systemmodel.componentimplementation.links.event.SM_ReceiverDynamicTriggerInstance;
import com.iawg.ecoa.systemmodel.componentimplementation.links.event.SM_ReceiverInterface;
import com.iawg.ecoa.systemmodel.componentimplementation.modoperation.event.SM_EventOp;

public class DynTrigInstanceILIGenerator {

	private PlatformGenerator platformGenerator;
	private DynamicTriggerInstanceILI trigInstILI;
	private ArrayList<ILIMessage> iliMessageList = new ArrayList<ILIMessage>();
	private SM_DynamicTriggerInstance trigInst;

	public DynTrigInstanceILIGenerator(PlatformGenerator platformGenerator, SM_DynamicTriggerInstance trigInst) {
		this.platformGenerator = platformGenerator;
		this.trigInstILI = new DynamicTriggerInstanceILI(trigInst);
		this.trigInst = trigInst;
	}

	private int addModuleLifecycleMessages(int msgNum) {
		// Add 4 messages for module state
		// 1 - START_module
		// 2 - STOP_module
		// 3 - INITIALIZE_module
		// 4 - SHUTDOWN_module
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

		DynTrigInstanceILIWriterC iliWriter = new DynTrigInstanceILIWriterC(directory, trigInstILI.getTrigInst());

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

		// Add dynamic trigger event ILI messages
		msgNum = processEventLinks(msgNum);
	}

	public DynamicTriggerInstanceILI getTrigInstILI() {
		return trigInstILI;
	}

	private int processEventLinks(int msgNum) {
		// If there are any local receivers of this event link, create an ILI
		for (SM_EventLink eventLink : trigInst.getComponentImplementation().getEventLinks()) {
			for (SM_ReceiverInterface receiver : eventLink.getReceivers()) {
				if (receiver instanceof SM_ReceiverDynamicTriggerInstance) {
					if (((SM_ReceiverDynamicTriggerInstance) receiver).getReceiverInst() == trigInst) {
						// Create a new ILI Message
						ILIMessage ili = new EventILIMessage(msgNum++, eventLink);

						// Add any parameters (inputs)
						if (((SM_EventOp) receiver.getReceiverOp()).getInputs() != null) {
							for (SM_OperationParameter param : ((SM_EventOp) receiver.getReceiverOp()).getInputs()) {
								ili.addParam(param);
							}
						}

						iliMessageList.add(ili);
						break;
					}
				}
			}
		}
		return msgNum;
	}
}
