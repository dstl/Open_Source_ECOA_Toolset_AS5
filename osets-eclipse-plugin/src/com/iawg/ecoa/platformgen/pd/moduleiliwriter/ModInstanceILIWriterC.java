/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.platformgen.pd.moduleiliwriter;

import java.nio.file.Path;
import java.util.ArrayList;

import com.iawg.ecoa.CLanguageSupport;
import com.iawg.ecoa.SourceFileWriter;
import com.iawg.ecoa.TypesProcessorC;
import com.iawg.ecoa.platformgen.common.WriterSupport;
import com.iawg.ecoa.platformgen.pd.moduleiliwriter.ilimessage.DataILIMessage;
import com.iawg.ecoa.platformgen.pd.moduleiliwriter.ilimessage.ErrorNotificationILIMessage;
import com.iawg.ecoa.platformgen.pd.moduleiliwriter.ilimessage.EventILIMessage;
import com.iawg.ecoa.platformgen.pd.moduleiliwriter.ilimessage.FaultNotificationILIMessage;
import com.iawg.ecoa.platformgen.pd.moduleiliwriter.ilimessage.ILIMessage;
import com.iawg.ecoa.platformgen.pd.moduleiliwriter.ilimessage.LifecycleILIMessage;
import com.iawg.ecoa.platformgen.pd.moduleiliwriter.ilimessage.RequestILIMessage;
import com.iawg.ecoa.platformgen.pd.moduleiliwriter.ilimessage.ResponseILIMessage;
import com.iawg.ecoa.platformgen.pd.moduleiliwriter.ilimessage.ServiceAvailNotificationILIMessage;
import com.iawg.ecoa.platformgen.pd.moduleiliwriter.ilimessage.ServiceProviderNotificationILIMessage;
import com.iawg.ecoa.systemmodel.SM_OperationParameter;
import com.iawg.ecoa.systemmodel.componentimplementation.SM_ModuleInstance;
import com.iawg.ecoa.systemmodel.componentimplementation.SM_TriggerInstance;
import com.iawg.ecoa.systemmodel.componentimplementation.links.data.SM_ReaderModuleInstance;
import com.iawg.ecoa.systemmodel.componentimplementation.links.event.SM_ReceiverInterface;
import com.iawg.ecoa.systemmodel.componentimplementation.links.event.SM_SenderExternal;
import com.iawg.ecoa.systemmodel.componentimplementation.links.event.SM_SenderInterface;
import com.iawg.ecoa.systemmodel.componentimplementation.links.request.SM_ClientInterface;
import com.iawg.ecoa.systemmodel.componentimplementation.modoperation.data.SM_DataReadOp;

public class ModInstanceILIWriterC extends SourceFileWriter {
	private static final String SEP_PATTERN_31 = "_ILI_";
	private ArrayList<ILIMessage> iliMessageList = new ArrayList<ILIMessage>();
	private SM_ModuleInstance modInst;

	private ArrayList<String> includeList = new ArrayList<String>();

	public ModInstanceILIWriterC(Path outputDir, SM_ModuleInstance modInst) {
		super(outputDir);
		this.modInst = modInst;

		setFileStructure();
	}

	@Override
	public void close() {
		String closeText = "#endif  /* _" + modInst.getName().toUpperCase() + "ILI_H */" + LF + LF;

		// Replace the #CLOSE# tag in string builder.
		WriterSupport.replaceText(codeStringBuilder, "#CLOSE#", closeText);

		super.close();
	}

	@Override
	public void open() {
		super.openFile(outputDir.resolve(modInst.getName() + "_ILI.h"));
	}

	@Override
	protected void setFileStructure() {
		String fileStructure = "#PREAMBLE#" + LF + "#INCLUDES#" + LF + "#MESSAGE_DEF#" + LF + "#MESSAGE_STRUCT#" + LF + "#CLOSE#" + LF;

		codeStringBuilder.append(fileStructure);
	}

	public void setILIMessages(ArrayList<ILIMessage> iliMessageList) {
		this.iliMessageList = iliMessageList;
	}

	public void writeIncludes() {
		String includeText = CLanguageSupport.generateIncludes(includeList);

		// Replace the #INCLUDES# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#INCLUDES#", includeText);
	}

	public void writeMessageDefinition() {
		String messageDefText = "/* Define the message IDs */" + LF + LF;

		for (ILIMessage iliMessage : iliMessageList) {
			if (iliMessage instanceof EventILIMessage) {
				EventILIMessage eventILI = (EventILIMessage) iliMessage;

				messageDefText += "/* Sources: ";
				for (SM_SenderInterface senderInterface : eventILI.getEventLink().getSenders()) {
					if (!(senderInterface instanceof SM_SenderExternal)) {
						if (senderInterface.getSenderInst() instanceof SM_TriggerInstance) {
							messageDefText += senderInterface.getSenderInst().getName() + "   ";
						} else {
							messageDefText += senderInterface.getSenderInst().getName() + "_" + senderInterface.getSenderOpName() + "   ";
						}
					} else {
						messageDefText += "(External) " + modInst.getImplementation().getComponentImplementation().getName() + "__" + senderInterface.getSenderOpName() + "   ";
					}
				}
				messageDefText += "*/" + LF + "/* Destinations: ";
				for (SM_ReceiverInterface receiver : eventILI.getEventLink().getReceivers()) {
					messageDefText += receiver.getReceiverInst().getName() + "_" + receiver.getReceiverOp().getName() + "   ";
				}
				messageDefText += "*/" + LF;
			} else if (iliMessage instanceof RequestILIMessage) {
				RequestILIMessage requestILI = (RequestILIMessage) iliMessage;

				messageDefText += "/* Sources: ";

				for (SM_ClientInterface clientInterface : requestILI.getRequestLink().getClients()) {
					messageDefText += clientInterface.getClientInst().getName() + "_" + clientInterface.getClientOp().getName() + "   ";
				}

				messageDefText += "*/" + LF + "/* Destinations: " + requestILI.getRequestLink().getServer().getServerInst().getName() + "_" + requestILI.getRequestLink().getServer().getServerOp().getName() + " */" + LF;
			} else if (iliMessage instanceof ResponseILIMessage) {
				ResponseILIMessage responseILI = (ResponseILIMessage) iliMessage;

				messageDefText += "/* Sources: " + responseILI.getRequestLink().getServer().getServerInst().getName() + "_" + responseILI.getRequestLink().getServer().getServerOp().getName() + " */" + LF + "/* Destinations: ";

				for (SM_ClientInterface clientInterface : responseILI.getRequestLink().getClients()) {
					messageDefText += clientInterface.getClientInst().getName() + "_" + clientInterface.getClientOp().getName() + "   ";
				}
				messageDefText += "*/" + LF;
			} else if (iliMessage instanceof DataILIMessage) {
				DataILIMessage dataILI = (DataILIMessage) iliMessage;

				messageDefText += "/* Sources: Container (VD updated notification message) */" + LF + "/* Destinations: ";
				for (SM_ReaderModuleInstance reader : dataILI.getDataLink().getLocalReaders()) {
					if (((SM_DataReadOp) reader.getReaderOp()).getIsNotifying()) {
						messageDefText += reader.getReaderInst().getName() + "_" + reader.getReaderOp().getName() + "   ";
					}
				}
				messageDefText += "*/" + LF;
			} else if (iliMessage instanceof LifecycleILIMessage) {
				messageDefText += "/* Sources: N/A (lifecycle message) */" + LF + "/* Destinations: N/A (lifecycle message) */" + LF;
			} else if (iliMessage instanceof ServiceAvailNotificationILIMessage) {
				messageDefText += "/* Sources: Container (required service " + ((ServiceAvailNotificationILIMessage) iliMessage).getServiceInstance().getName() + " availability notification) */" + LF + "/* Destinations: supervisor module */" + LF;
			} else if (iliMessage instanceof ServiceProviderNotificationILIMessage) {
				messageDefText += "/* Sources: Container (required service " + ((ServiceProviderNotificationILIMessage) iliMessage).getServiceInstance().getName() + " provider changed notification) */" + LF + "/* Destinations: supervisor module */" + LF;
			} else if (iliMessage instanceof ErrorNotificationILIMessage) {
				messageDefText += "/* Sources: " + ((ErrorNotificationILIMessage) iliMessage).getModuleInstance().getName() + " error notification */" + LF + "/* Destinations: supervisor module */" + LF;
			} else if (iliMessage instanceof FaultNotificationILIMessage) {
				messageDefText += "/* Sources: fault notification */" + LF + "/* Destinations: supervisor module */" + LF;
			}

			messageDefText += "#define " + modInst.getName() + SEP_PATTERN_31 + iliMessage.getMessageID() + " " + iliMessage.getMessageID() + LF + LF;
		}

		// Replace the #MESSAGE_DEF# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#MESSAGE_DEF#", messageDefText);
	}

	public void writeMessageStructure() {
		String messageStructText = "/* Define the payload structure for each ILI message */" + LF + LF;

		// Add all the payloads
		for (ILIMessage iliMessage : iliMessageList) {
			if (iliMessage instanceof DataILIMessage) {
				messageStructText += "/* " + modInst.getName() + SEP_PATTERN_31 + iliMessage.getMessageID() + " has no parameters */" + LF;
			} else {
				if (iliMessage.getParams().size() > 0) {
					messageStructText += "typedef struct {" + LF;
					for (SM_OperationParameter param : iliMessage.getParams()) {
						messageStructText += "   " + TypesProcessorC.convertParameterToC(param.getType()) + " " + param.getName() + ";" + LF;

						// Add types library to include list
						includeList.add(param.getType().getNamespace().getName());
					}
					messageStructText += "} " + modInst.getName() + SEP_PATTERN_31 + iliMessage.getMessageID() + "_params;" + LF;

				} else {
					messageStructText += "/* " + modInst.getName() + SEP_PATTERN_31 + iliMessage.getMessageID() + " has no parameters */" + LF;
				}
			}
		}

		// Replace the #MESSAGE_STRUCT# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#MESSAGE_STRUCT#", messageStructText);
	}

	public void writePreamble() {
		String preambleText = "/* File " + modInst.getName() + "_ILI.h */" + LF + LF +

				"#if !defined(_" + modInst.getName().toUpperCase() + "ILI_H)" + LF + "#define _" + modInst.getName().toUpperCase() + "ILI_H" + LF + LF;

		// Replace the #PREAMBLE# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#PREAMBLE#", preambleText);
	}

}
