/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.platformgen.pd.vcid;

import java.nio.file.Path;
import java.util.ArrayList;

import com.iawg.ecoa.CLanguageSupport;
import com.iawg.ecoa.SourceFileWriter;
import com.iawg.ecoa.platformgen.common.WriterSupport;
import com.iawg.ecoa.systemmodel.deployment.SM_ProtectionDomain;

public class VCIDWriterC extends SourceFileWriter {
	private static final String SEP_PATTERN_51 = "#PREAMBLE#";
	private SM_ProtectionDomain pd;
	private ArrayList<String> includeList = new ArrayList<String>();
	private boolean isHeader;

	public VCIDWriterC(Path outputDir, SM_ProtectionDomain pd, boolean isHeader) {
		super(outputDir);
		this.pd = pd;
		this.isHeader = isHeader;

		setFileStructure();
	}

	@Override
	public void open() {
		if (isHeader) {
			super.openFile(outputDir.resolve(pd.getName() + "_VC_IDS.h"));
		} else {
			super.openFile(outputDir.resolve(pd.getName() + "_VC_IDS.c"));
		}
	}

	@Override
	protected void setFileStructure() {
		String fileStructure;

		if (isHeader) {
			fileStructure = "#INCLUDES#" + LF + SEP_PATTERN_51 + LF + "#VC_IDS#" + LF + "#GET_SEND_VC_ID#" + LF + "#GET_RECEIVE_VC_ID#" + LF;
		} else {
			fileStructure = "#INCLUDES#" + LF + SEP_PATTERN_51 + LF + "#GET_SEND_VC_ID#" + LF + "#GET_RECEIVE_VC_ID#" + LF;
		}

		codeStringBuilder.append(fileStructure);
	}

	public void writePreamble() {
		String preambleText;

		if (isHeader) {
			preambleText = "/* File " + pd.getName() + "_VC_IDs.h */" + LF;
		} else {
			preambleText = "/* File " + pd.getName() + "_VC_IDs.c */" + LF;
		}

		// Replace the #PREAMBLE# tag in string builder.
		WriterSupport.replaceText(codeStringBuilder, SEP_PATTERN_51, preambleText);
	}

	public void writeIncludes() {
		if (isHeader) {
			includeList.add("ECOA");
		} else {
			includeList.add("PD_IDS");
			includeList.add(pd.getName() + "_VC_IDS");
		}

		String includeText = CLanguageSupport.generateIncludes(includeList);

		// Replace the #INCLUDES# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#INCLUDES#", includeText);
	}

	public void writeVCIDs() {
		String vcIDText = "/* Define two VC IDs for Platform Manager comms (1 send, 1 receive)*/" + LF + "#define VC_IDS__PLATFORM_MANAGER_SEND 1" + LF + "#define VC_IDS__PLATFORM_MANAGER_RECEIVE 2" + LF + LF +

				"/* Define two VC IDs for each protection domain we communicate with (1 send, 1 receive)*/" + LF;

		int vcNum = 3;
		for (SM_ProtectionDomain remotePD : pd.getListOfPDsCommunicateWith()) {
			vcIDText += "#define VC_IDS__" + remotePD.getName().toUpperCase() + "_SEND " + vcNum++ + LF + "#define VC_IDS__" + remotePD.getName().toUpperCase() + "_RECEIVE " + vcNum++ + LF;
		}

		// Replace the #VC_IDS# tag in string builder.
		WriterSupport.replaceText(codeStringBuilder, "#VC_IDS#", vcIDText);
	}

	public void writeGetSendVCID() {
		String getVCText;

		if (isHeader) {
			getVCText = "ECOA__uint32 Get_Send_VC_ID(ECOA__uint32 pdID);" + LF;
		} else {
			getVCText = "ECOA__uint32 Get_Send_VC_ID(ECOA__uint32 pdID)" + LF + "{" + LF + "   switch (pdID)" + LF + "   {" + LF;

			for (SM_ProtectionDomain remotePD : pd.getListOfPDsCommunicateWith()) {
				getVCText += "      case PD_IDS__" + remotePD.getName().toUpperCase() + ":" + LF + "         return VC_IDS__" + remotePD.getName().toUpperCase() + "_SEND;" + LF + "         break;" + LF;
			}
			// Always generate a case for sending to the Platform Manager.
			getVCText += "      case PD_IDS__" + pd.getLogicalComputingNode().getLogicalComputingPlatform().getName().toUpperCase() + ":" + LF + "         return VC_IDS__PLATFORM_MANAGER_SEND;" + LF + "         break;" + LF;

			getVCText += "   }" + LF + "}" + LF + LF;
		}

		// Replace the #GET_SEND_VC_ID# tag in string builder.
		WriterSupport.replaceText(codeStringBuilder, "#GET_SEND_VC_ID#", getVCText);
	}

	public void writeGetReceiveVCID() {
		String getVCText;

		if (isHeader) {
			getVCText = "ECOA__uint32 Get_Receive_VC_ID(ECOA__uint32 pdID);" + LF;
		} else {
			getVCText = "ECOA__uint32 Get_Receive_VC_ID(ECOA__uint32 pdID)" + LF + "{" + LF + "   switch (pdID)" + LF + "   {" + LF;

			for (SM_ProtectionDomain remotePD : pd.getListOfPDsCommunicateWith()) {
				getVCText += "      case PD_IDS__" + remotePD.getName().toUpperCase() + ":" + LF + "         return VC_IDS__" + remotePD.getName().toUpperCase() + "_RECEIVE;" + LF + "         break;" + LF;
			}

			getVCText += "   }" + LF + "}" + LF + LF;
		}

		// Replace the #GET_RECEIVE_VC_ID# tag in string builder.
		WriterSupport.replaceText(codeStringBuilder, "#GET_RECEIVE_VC_ID#", getVCText);
	}

}
