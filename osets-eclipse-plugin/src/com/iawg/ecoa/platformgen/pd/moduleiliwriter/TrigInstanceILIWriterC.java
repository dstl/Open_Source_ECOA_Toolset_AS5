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
import com.iawg.ecoa.platformgen.pd.moduleiliwriter.ilimessage.ILIMessage;
import com.iawg.ecoa.platformgen.pd.moduleiliwriter.ilimessage.LifecycleILIMessage;
import com.iawg.ecoa.systemmodel.SM_OperationParameter;
import com.iawg.ecoa.systemmodel.componentimplementation.SM_TriggerInstance;

public class TrigInstanceILIWriterC extends SourceFileWriter {
	private static final String SEP_PATTERN_01 = "_ILI_";
	private ArrayList<ILIMessage> iliMessageList = new ArrayList<ILIMessage>();
	private SM_TriggerInstance trigInst;

	private ArrayList<String> includeList = new ArrayList<String>();

	public TrigInstanceILIWriterC(Path outputDir, SM_TriggerInstance trigInst) {
		super(outputDir);
		this.trigInst = trigInst;

		setFileStructure();
	}

	@Override
	public void close() {
		String closeText = "#endif  /* _" + trigInst.getComponentImplementation().getName().toUpperCase() + "_" + trigInst.getName().toUpperCase() + "ILI_H */" + LF + LF;

		// Replace the #CLOSE# tag in string builder.
		WriterSupport.replaceText(codeStringBuilder, "#CLOSE#", closeText);

		super.close();
	}

	@Override
	public void open() {
		super.openFile(outputDir.resolve(trigInst.getComponentImplementation().getName() + "_" + trigInst.getName() + "_ILI.h"));
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
			if (iliMessage instanceof LifecycleILIMessage) {
				messageDefText += "/* Sources: N/A (lifecycle message) */" + LF + "/* Destinations: N/A (lifecycle message) */" + LF;
			}

			messageDefText += "#define " + trigInst.getComponentImplementation().getName() + "_" + trigInst.getName() + SEP_PATTERN_01 + iliMessage.getMessageID() + " " + iliMessage.getMessageID() + LF + LF;
		}

		// Replace the #MESSAGE_DEF# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#MESSAGE_DEF#", messageDefText);
	}

	public void writeMessageStructure() {
		String messageStructText = "/* Define the payload structure for each ILI message */" + LF + LF;

		// Add all the payloads
		for (ILIMessage iliMessage : iliMessageList) {
			if (iliMessage instanceof DataILIMessage) {
				messageStructText += "/* " + trigInst.getComponentImplementation().getName() + "_" + trigInst.getName() + SEP_PATTERN_01 + iliMessage.getMessageID() + " has no parameters */" + LF;
			} else {
				if (iliMessage.getParams().size() > 0) {
					messageStructText += "typedef struct {" + LF;
					for (SM_OperationParameter param : iliMessage.getParams()) {
						messageStructText += "   " + TypesProcessorC.convertParameterToC(param.getType()) + " " + param.getName() + ";" + LF;

						// Add types library to include list
						includeList.add(param.getType().getNamespace().getName());
					}
					messageStructText += "} " + trigInst.getComponentImplementation().getName() + "_" + trigInst.getName() + SEP_PATTERN_01 + iliMessage.getMessageID() + "_params;" + LF;

				} else {
					messageStructText += "/* " + trigInst.getComponentImplementation().getName() + "_" + trigInst.getName() + SEP_PATTERN_01 + iliMessage.getMessageID() + " has no parameters */" + LF;
				}
			}
		}

		// Replace the #MESSAGE_STRUCT# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#MESSAGE_STRUCT#", messageStructText);
	}

	public void writePreamble() {
		String preambleText = "/* File " + trigInst.getComponentImplementation().getName() + "_" + trigInst.getName() + "_ILI.h */" + LF + LF +

				"#if !defined(_" + trigInst.getComponentImplementation().getName().toUpperCase() + "_" + trigInst.getName().toUpperCase() + "ILI_H)" + LF + "#define _" + trigInst.getComponentImplementation().getName().toUpperCase() + "_" + trigInst.getName().toUpperCase() + "ILI_H" + LF + LF;

		// Replace the #PREAMBLE# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#PREAMBLE#", preambleText);
	}

}
