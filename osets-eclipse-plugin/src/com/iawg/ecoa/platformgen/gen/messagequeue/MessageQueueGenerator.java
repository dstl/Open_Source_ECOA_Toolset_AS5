/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.platformgen.gen.messagequeue;

import java.nio.file.Path;

import com.iawg.ecoa.WriterSupport;
import com.iawg.ecoa.platformgen.PlatformGenerator;
import com.iawg.ecoa.systemmodel.deployment.logicalsystem.SM_LogicalComputingNode;

public class MessageQueueGenerator {

	private PlatformGenerator platformGenerator;
	private SM_LogicalComputingNode lcn;

	public MessageQueueGenerator(PlatformGenerator platformGenerator, SM_LogicalComputingNode lcn) {
		this.platformGenerator = platformGenerator;
		this.lcn = lcn;
	}

	public void generate() {
		Path directory = platformGenerator.getOutputDir().resolve(lcn.getLogicalComputingPlatform().getName() + "/" + lcn.getName());

		// Copy the header
		WriterSupport.copyResource(directory.resolve("inc-gen/message_queue.h"), platformGenerator.getunderlyingPlatformInstantiation().getMessageQueueTemplate(true));

		// Copy the body
		WriterSupport.copyResource(directory.resolve("src-gen/message_queue.c"), platformGenerator.getunderlyingPlatformInstantiation().getMessageQueueTemplate(false));
	}

}
