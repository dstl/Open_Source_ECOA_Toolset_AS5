/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.platformgen.pd.clienttype.moduleopuid;

import java.nio.file.Path;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.iawg.ecoa.CLanguageSupport;
import com.iawg.ecoa.SourceFileWriter;
import com.iawg.ecoa.platformgen.common.WriterSupport;
import com.iawg.ecoa.systemmodel.componentimplementation.SM_ComponentImplementation;
import com.iawg.ecoa.systemmodel.componentimplementation.SM_DynamicTriggerInstance;
import com.iawg.ecoa.systemmodel.componentimplementation.SM_ModuleInstance;
import com.iawg.ecoa.systemmodel.componentimplementation.SM_TriggerInstance;
import com.iawg.ecoa.systemmodel.componentimplementation.links.event.SM_EventLink;
import com.iawg.ecoa.systemmodel.componentimplementation.links.event.SM_SenderExternal;
import com.iawg.ecoa.systemmodel.componentimplementation.links.event.SM_SenderInterface;
import com.iawg.ecoa.systemmodel.componentimplementation.modoperation.event.SM_EventSentOp;
import com.iawg.ecoa.systemmodel.componentimplementation.modoperation.request.SM_RequestSentOp;

public class ModInstanceOpUIDWriterC extends SourceFileWriter {
	private static final Logger LOGGER = LogManager.getLogger(ModInstanceOpUIDWriterC.class);
	private SM_ComponentImplementation compImpl;
	private ArrayList<String> includeList = new ArrayList<String>();

	public ModInstanceOpUIDWriterC(Path outputDir, SM_ComponentImplementation compImpl) {
		super(outputDir);
		this.compImpl = compImpl;

		setFileStructure();
	}

	@Override
	public void close() {
		String closeText = "#endif  /* _" + compImpl.getName().toUpperCase() + "_MODULE_INSTANCE_OPERATION_UID */" + LF + LF;

		// Replace the #CLOSE# tag in string builder.
		WriterSupport.replaceText(codeStringBuilder, "#CLOSE#", closeText);

		super.close();
	}

	private String generateUID(ArrayList<Integer> UIDList, String senderName, String operationName) {
		// UID string built up as "moduleInstanceName:operationName"
		String uidHashName = senderName + ":" + operationName;
		Integer uid = Math.abs(uidHashName.hashCode());

		boolean added = false;
		while (!added) {
			if (UIDList.contains(uid)) {
				LOGGER.info("Warning - Duplicate Module Instance Operation UID has been found - adding 1 to hash value");
				uid++;
			} else {
				UIDList.add(uid);
				added = true;
			}
		}

		return "#define " + compImpl.getName().toUpperCase() + "_" + senderName.toUpperCase() + "_" + operationName.toUpperCase() + "_UID " + uid + LF;
	}

	@Override
	public void open() {
		super.openFile(outputDir.resolve(compImpl.getName() + "_Module_Instance_Operation_UID.h"));
	}

	@Override
	protected void setFileStructure() {
		String fileStructure = "#PREAMBLE#" + LF + "#INCLUDES#" + LF + "#UIDS#" + LF + "#CLOSE#" + LF;

		codeStringBuilder.append(fileStructure);
	}

	public void writeIncludes() {
		String includeText = CLanguageSupport.generateIncludes(includeList);

		// Replace the #INCLUDES# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#INCLUDES#", includeText);
	}

	public void writePreamble() {
		String preambleText = "/* File " + compImpl.getName() + "_Module_Instance_Operation_UID.h */" + LF + LF +

				"#if !defined(_" + compImpl.getName().toUpperCase() + "_MODULE_INSTANCE_OPERATION_UID)" + LF + "#define _" + compImpl.getName().toUpperCase() + "_MODULE_INSTANCE_OPERATION_UID" + LF + LF;

		// Replace the #PREAMBLE# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#PREAMBLE#", preambleText);
	}

	public void writeUIDs() {
		ArrayList<Integer> UIDList = new ArrayList<Integer>();

		StringBuilder uidText = new StringBuilder();
		uidText.append("/* Define the Module Instance Operation UIDs */" + LF + LF);

		for (SM_ModuleInstance modInst : compImpl.getModuleInstances().values()) {
			uidText.append("/* Module Instance Operation UIDs for : " + modInst.getName() + " */" + LF);

			for (SM_RequestSentOp requestSentOp : modInst.getModuleType().getRequestSentOps()) {
				uidText.append(generateUID(UIDList, modInst.getName(), requestSentOp.getName()));
			}
			for (SM_EventSentOp eventSentOp : modInst.getModuleType().getEventSentOps()) {
				uidText.append(generateUID(UIDList, modInst.getName(), eventSentOp.getName()));
			}
		}

		for (SM_TriggerInstance trigInst : compImpl.getTriggerInstances().values()) {
			uidText.append("/* Trigger Instance Operation UIDs for : " + trigInst.getName() + " */" + LF);

			// UID string built up as "triggerInstanceName_triggerOp" - as don't
			// have defined name for trigger operation!
			uidText.append(generateUID(UIDList, trigInst.getName(), "triggerOp"));

		}

		for (SM_DynamicTriggerInstance dynTrigInst : compImpl.getDynamicTriggerInstances().values()) {
			uidText.append("/* Dynamic Trigger Instance Operation UIDs for : " + dynTrigInst.getName() + " */" + LF);

			for (SM_EventSentOp eventSentOp : dynTrigInst.getModuleType().getEventSentOps()) {
				uidText.append(generateUID(UIDList, dynTrigInst.getName(), eventSentOp.getName()));
			}
		}

		for (SM_ModuleInstance modInst : compImpl.getModuleInstances().values()) {
			uidText.append("/* External Operation UIDs for : " + compImpl.getName() + " */" + LF);
			for (SM_EventLink evLink : modInst.getEventLinks()) {
				for (SM_SenderInterface sender : evLink.getSenders()) {
					if (sender instanceof SM_SenderExternal) {
						// UID string built up as "external_operationName" - as
						// don't have defined name for Sender!
						uidText.append(generateUID(UIDList, "external", sender.getSenderOpName()));

					}
				}
			}

		}

		// Replace the #UIDS# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#UIDS#", uidText.toString());
	}

}
