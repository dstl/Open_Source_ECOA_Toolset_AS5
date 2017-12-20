/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.platformgen.pf.vcid;

import java.nio.file.Path;
import java.util.ArrayList;

import com.iawg.ecoa.CLanguageSupport;
import com.iawg.ecoa.SourceFileWriter;
import com.iawg.ecoa.platformgen.common.WriterSupport;
import com.iawg.ecoa.platformgen.pf.PlatformManagerGenerator;
import com.iawg.ecoa.systemmodel.deployment.SM_ProtectionDomain;
import com.iawg.ecoa.systemmodel.deployment.logicalsystem.SM_LogicalComputingPlatform;

public class VCIDWriterC extends SourceFileWriter {
	private static final String SEP_PATTERN_71 = "#PREAMBLE#";
	private SM_LogicalComputingPlatform lcp;
	private ArrayList<String> includeList = new ArrayList<String>();
	private boolean isHeader;
	private PlatformManagerGenerator pfManagerGenerator;

	public VCIDWriterC(Path outputDir, SM_LogicalComputingPlatform lcp, boolean isHeader, PlatformManagerGenerator pfManagerGenerator) {
		super(outputDir);
		this.lcp = lcp;
		this.isHeader = isHeader;
		this.pfManagerGenerator = pfManagerGenerator;

		setFileStructure();
	}

	@Override
	public void open() {
		if (isHeader) {
			super.openFile(outputDir.resolve(lcp.getName() + "_VC_IDS.h"));
		} else {
			super.openFile(outputDir.resolve(lcp.getName() + "_VC_IDS.c"));
		}
	}

	@Override
	protected void setFileStructure() {
		String fileStructure;

		if (isHeader) {
			fileStructure = "#INCLUDES#" + LF + SEP_PATTERN_71 + LF + "#VC_IDS#" + LF + "#GET_SEND_PLATFORM_VC_ID#" + LF + "#GET_SEND_PD_VC_ID#" + LF + "#GET_RECEIVE_PD_VC_ID#" + LF;
		} else {
			fileStructure = "#INCLUDES#" + LF + SEP_PATTERN_71 + LF + "#GET_SEND_PLATFORM_VC_ID#" + LF + "#GET_SEND_PD_VC_ID#" + LF + "#GET_RECEIVE_PD_VC_ID#" + LF;
		}

		codeStringBuilder.append(fileStructure);
	}

	public void writePreamble() {
		String preambleText;

		if (isHeader) {
			preambleText = "/* File " + lcp.getName() + "_VC_IDs.h */" + LF;
		} else {
			preambleText = "/* File " + lcp.getName() + "_VC_IDs.c */" + LF;
		}

		// Replace the #PREAMBLE# tag in string builder.
		WriterSupport.replaceText(codeStringBuilder, SEP_PATTERN_71, preambleText);
	}

	public void writeIncludes() {
		if (isHeader) {
			includeList.add("ECOA");
		} else {
			includeList.add("PD_IDS");
			includeList.add(lcp.getName() + "_VC_IDS");
		}

		String includeText = CLanguageSupport.generateIncludes(includeList);

		// Replace the #INCLUDES# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#INCLUDES#", includeText);
	}

	public void writeVCIDs() {
		int vcNum = 2;
		String vcIDText = "/* Define VC IDs for Platform level comms (1 receive and a send per remote platform)*/" + LF + "#define VC_IDS__PLATFORM_LEVEL_RECEIVE 1" + LF;
		for (SM_LogicalComputingPlatform remoteLCP : pfManagerGenerator.getSystemModel().getLogicalSystem().getLogicalcomputingPlatforms()) {
			if (remoteLCP != lcp) {
				vcIDText += "#define VC_IDS__" + remoteLCP.getName().toUpperCase() + "_SEND " + vcNum++ + LF;
			}
		}

		vcIDText += LF + "/* Define two VC IDs for each protection domain we communicate with (1 send, 1 receive)*/" + LF;

		for (SM_ProtectionDomain remotePD : lcp.getAllProtectionDomains()) {
			vcIDText += "#define VC_IDS__" + remotePD.getName().toUpperCase() + "_SEND " + vcNum++ + LF + "#define VC_IDS__" + remotePD.getName().toUpperCase() + "_RECEIVE " + vcNum++ + LF;
		}

		// Replace the #VC_IDS# tag in string builder.
		WriterSupport.replaceText(codeStringBuilder, "#VC_IDS#", vcIDText);
	}

	public void writeGetSendPDVCID() {
		String getVCText;

		if (isHeader) {
			getVCText = "ECOA__uint32 Get_Send_PD_VC_ID(ECOA__uint32 pdID);" + LF;
		} else {
			getVCText = "ECOA__uint32 Get_Send_PD_VC_ID(ECOA__uint32 pdID)" + LF + "{" + LF + "   switch (pdID)" + LF + "   {" + LF;

			for (SM_ProtectionDomain remotePD : lcp.getAllProtectionDomains()) {
				getVCText += "      case PD_IDS__" + remotePD.getName().toUpperCase() + ":" + LF + "         return VC_IDS__" + remotePD.getName().toUpperCase() + "_SEND;" + LF + "         break;" + LF;
			}

			getVCText += "   }" + LF + "}" + LF + LF;
		}

		// Replace the #GET_SEND_PD_VC_ID# tag in string builder.
		WriterSupport.replaceText(codeStringBuilder, "#GET_SEND_PD_VC_ID#", getVCText);
	}

	public void writeGetReceivePDVCID() {
		String getVCText;

		if (isHeader) {
			getVCText = "ECOA__uint32 Get_Receive_PD_VC_ID(ECOA__uint32 pdID);" + LF;
		} else {
			getVCText = "ECOA__uint32 Get_Receive_PD_VC_ID(ECOA__uint32 pdID)" + LF + "{" + LF + "   switch (pdID)" + LF + "   {" + LF;

			for (SM_ProtectionDomain remotePD : lcp.getAllProtectionDomains()) {
				getVCText += "      case PD_IDS__" + remotePD.getName().toUpperCase() + ":" + LF + "         return VC_IDS__" + remotePD.getName().toUpperCase() + "_RECEIVE;" + LF + "         break;" + LF;
			}

			getVCText += "   }" + LF + "}" + LF + LF;
		}

		// Replace the #GET_RECEIVE_PD_VC_ID# tag in string builder.
		WriterSupport.replaceText(codeStringBuilder, "#GET_RECEIVE_PD_VC_ID#", getVCText);
	}

	public void writeGetSendPlatformVCID() {
		String getVCText;

		if (isHeader) {
			getVCText = "ECOA__uint32 Get_Send_Platform_VC_ID(ECOA__uint32 platformID);" + LF;
		} else {
			getVCText = "ECOA__uint32 Get_Send_Platform_VC_ID(ECOA__uint32 platformID)" + LF + "{" + LF + "   switch (platformID)" + LF + "   {" + LF;

			for (SM_LogicalComputingPlatform remoteLCP : pfManagerGenerator.getSystemModel().getLogicalSystem().getLogicalcomputingPlatforms()) {
				if (remoteLCP != lcp) {
					getVCText += "      case " + remoteLCP.getRelatedUDPBinding().getPlatformID() + ":" + LF + "         return VC_IDS__" + remoteLCP.getName().toUpperCase() + "_SEND;" + LF + "         break;" + LF;
				}
			}

			getVCText += "   }" + LF + "}" + LF + LF;
		}

		// Replace the #GET_SEND_PLATFORM_VC_ID# tag in string builder.
		WriterSupport.replaceText(codeStringBuilder, "#GET_SEND_PLATFORM_VC_ID#", getVCText);
	}
}
