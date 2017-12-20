/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.platformgen.pd.externalinterface;

import java.nio.file.Path;
import java.util.ArrayList;

import com.iawg.ecoa.CLanguageSupport;
import com.iawg.ecoa.SourceFileWriter;
import com.iawg.ecoa.platformgen.PlatformGenerator;
import com.iawg.ecoa.platformgen.common.WriterSupport;
import com.iawg.ecoa.systemmodel.SM_OperationParameter;
import com.iawg.ecoa.systemmodel.componentimplementation.SM_ComponentImplementation;
import com.iawg.ecoa.systemmodel.componentimplementation.SM_ModuleInstance;
import com.iawg.ecoa.systemmodel.componentimplementation.links.event.SM_EventLink;
import com.iawg.ecoa.systemmodel.componentimplementation.links.event.SM_ReceiverInterface;
import com.iawg.ecoa.systemmodel.componentimplementation.links.event.SM_ReceiverService;
import com.iawg.ecoa.systemmodel.componentimplementation.links.event.SM_SenderExternal;
import com.iawg.ecoa.systemmodel.componentimplementation.links.event.SM_SenderInterface;
import com.iawg.ecoa.systemmodel.componentimplementation.modoperation.event.SM_EventReceivedOp;
import com.iawg.ecoa.systemmodel.deployment.SM_DeployedModInst;
import com.iawg.ecoa.systemmodel.servicedefinition.serviceoperation.SM_EventServiceOp;

public class ExternalInterfaceBodyWriterC extends SourceFileWriter {
	private ArrayList<String> includeList = new ArrayList<String>();

	private SM_ComponentImplementation compImpl;
	private SM_DeployedModInst depModInst;

	public ExternalInterfaceBodyWriterC(PlatformGenerator platformGenerator, SM_ComponentImplementation compImpl, SM_DeployedModInst depModInst) {
		super(platformGenerator.getPdOutputDir());
		this.compImpl = compImpl;
		this.depModInst = depModInst;

		setFileStructure();
	}

	@Override
	public void open() {
		Path directory = outputDir.resolve("src-gen/");

		super.openFile(directory.resolve(compImpl.getName() + "_External_Interface.c"));
	}

	@Override
	protected void setFileStructure() {
		// Set the file structure
		String fileStructure = "#PREAMBLE#" + LF + "#INCLUDES#" + LF + "#EXTERNAL_INTERFACES#" + LF;
		codeStringBuilder.append(fileStructure);
	}

	public void writeExternalInterfaces() {
		String externalInterfaceString = "";

		for (SM_EventLink evLink : compImpl.getEventLinks()) {
			for (SM_SenderInterface sender : evLink.getSenders()) {
				if (sender instanceof SM_SenderExternal) {
					externalInterfaceString += "void " + compImpl.getName() + "__" + sender.getSenderOpName() + "(";

					// Get the first receiver to work out parameters.
					boolean generateComma = false;
					for (SM_OperationParameter opParam : evLink.getReceivers().get(0).getInputs()) {
						externalInterfaceString += CLanguageSupport.writeConstParam(opParam, generateComma);
						generateComma = true;
					}

					externalInterfaceString += ")" + LF + "{" + LF +

							"   ECOA__timestamp timestamp;" + LF + "   ECOA__uint32 modUID = 0;" + LF + LF +

							"   /* Timestamp point */" + LF + "   ECOA_setTimestamp(&timestamp);" + LF + LF;

					for (SM_ReceiverInterface receiver : evLink.getReceivers()) {
						if (receiver instanceof SM_ReceiverService) {
							SM_EventServiceOp recvOp = (SM_EventServiceOp) receiver.getReceiverOp();

							externalInterfaceString += "   /* Call the Service API */" + LF + "   " + depModInst.getCompInstance().getName() + "_" + receiver.getReceiverInst().getName() + "_" + recvOp.getName() + "__event_send(&timestamp";

							for (SM_OperationParameter opParam : recvOp.getInputs()) {
								externalInterfaceString += ", " + opParam.getName();
							}
							externalInterfaceString += ");" + LF;
						} else if (receiver.getReceiverInst() instanceof SM_ModuleInstance) {
							SM_EventReceivedOp recvOp = (SM_EventReceivedOp) receiver.getReceiverOp();

							includeList.add(compImpl.getName() + "_Module_Instance_Operation_UID");
							String modUID = compImpl.getName().toUpperCase() + "_EXTERNAL_" + sender.getSenderOpName().toUpperCase() + "_UID";

							externalInterfaceString += "   modUID = " + modUID + ";" + LF + "   /* Call the Module Instance Queue Operation */" + LF + "   " + depModInst.getCompInstance().getName() + "_" + receiver.getReceiverInst().getName() + "_Controller__" + recvOp.getName() + "__event_received(&timestamp," + modUID;

							for (SM_OperationParameter opParam : recvOp.getInputs()) {
								externalInterfaceString += ", " + opParam.getName();
							}
							externalInterfaceString += ");" + LF;
						}
					}

					externalInterfaceString += "}" + LF + LF;
				}
			}
		}

		// Replace the #EVENT_SENT# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#EXTERNAL_INTERFACES#", externalInterfaceString);
	}

	public void writeIncludes() {
		includeList.add(compImpl.getName() + "_External_Interface");
		includeList.add("ECOA_time_utils");

		String includeText = CLanguageSupport.generateIncludes(includeList);

		// Replace the #INCLUDES# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#INCLUDES#", includeText);
	}

	public void writePreamble() {
		String preambleText = "/* File " + compImpl.getName() + "_External_Interface.c */" + LF;

		// Replace the #PREAMBLE# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#PREAMBLE#", preambleText);
	}

}
