/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.platformgen.pf.pfcontrol;

import java.nio.file.Path;
import java.util.ArrayList;

import com.iawg.ecoa.CLanguageSupport;
import com.iawg.ecoa.SourceFileWriter;
import com.iawg.ecoa.platformgen.common.WriterSupport;
import com.iawg.ecoa.platformgen.common.underlyingplatform.Generic_Platform;
import com.iawg.ecoa.platformgen.pf.PlatformManagerGenerator;
import com.iawg.ecoa.systemmodel.deployment.SM_ProtectionDomain;
import com.iawg.ecoa.systemmodel.deployment.logicalsystem.SM_LogicalComputingPlatform;

public class PFControllerWriterC extends SourceFileWriter {
	private static final String SEP_PATTERN_11 = "void ";
	private boolean isHeader;
	private SM_LogicalComputingPlatform lcp;
	private PlatformManagerGenerator pfManagerGenerator;

	private ArrayList<String> includeList = new ArrayList<String>();

	public PFControllerWriterC(PlatformManagerGenerator pfManagerGenerator, boolean isHeader, Path outputDir, SM_LogicalComputingPlatform lcp) {
		super(outputDir);
		this.isHeader = isHeader;
		this.lcp = lcp;
		this.pfManagerGenerator = pfManagerGenerator;

		setFileStructure();
	}

	@Override
	public void open() {
		if (isHeader) {
			super.openFile(outputDir.resolve(lcp.getName() + "_PF_Controller.h"));
		} else {
			super.openFile(outputDir.resolve(lcp.getName() + "_PF_Controller.c"));
		}
	}

	@Override
	protected void setFileStructure() {
		// Set the file structure
		String fileStructure = "#PREAMBLE#" + LF + "#INCLUDES#" + LF + "#INITIALISE#" + LF;

		codeStringBuilder.append(fileStructure);
	}

	public void writeIncludes() {
		if (isHeader) {
			includeList.addAll(pfManagerGenerator.getUnderlyingPlatformInstantiation().addIncludesPFControllerHeader());
		} else {
			includeList.addAll(pfManagerGenerator.getUnderlyingPlatformInstantiation().addIncludesPFControllerBody());
		}

		String includeText = CLanguageSupport.generateIncludes(includeList);

		// Replace the #INCLUDES# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#INCLUDES#", includeText);
	}

	public void writeInitialise(Generic_Platform underlyingPlatformInstantiation) {

		String initialiseText = "";

		includeList.add(lcp.getName() + "_PF_Service_Manager");

		if (isHeader) {
			initialiseText += SEP_PATTERN_11 + lcp.getName() + "_PF_Controller__Initialise();" + LF + LF +

					SEP_PATTERN_11 + lcp.getName() + "_startThread(void (*moduleInstance)(), int priority, char *threadName, void *arg);" + LF + LF +

					SEP_PATTERN_11 + lcp.getName() + "_delay(unsigned int seconds, unsigned int nanoseconds);" + LF;

		} else {
			initialiseText +=

					SEP_PATTERN_11 + lcp.getName() + "_delay(unsigned int seconds, unsigned int nanoseconds)" + LF + "{" + LF + underlyingPlatformInstantiation.generateSleepVariableDecls() + LF + underlyingPlatformInstantiation.generateSleepAttributes("seconds", "nanoseconds") + LF + underlyingPlatformInstantiation.generateSleepCall() + LF + "}" + LF + LF +

							SEP_PATTERN_11 + lcp.getName() + "_startThread(void (*moduleInstance)(), int priority, char *threadName, void *arg)" + LF + "{" + LF + LF + "   /* Create the Process Queue Thread */" + LF + underlyingPlatformInstantiation.generateThreadVariableDecls("   ") + LF + underlyingPlatformInstantiation.generateThreadAttributes("   ", "priority") + LF + underlyingPlatformInstantiation.generateThreadCreate("   ", "moduleInstance") + LF + "}" + LF + LF;

			// Generate the Initialise function.
			initialiseText += SEP_PATTERN_11 + lcp.getName() + "_PF_Controller__Initialise() {" + LF + LF +

					underlyingPlatformInstantiation.generateSetOwnPriority("", "49") + LF + LF +

					"   /* PFtoPD Manager initialisation */" + LF + "   " + lcp.getName() + "_PFtoPD_Manager__Initialise();" + LF + LF +

					"   /* PFtoPF Manager initialisation */" + LF + "   " + lcp.getName() + "_PFtoPF_Manager__Initialise();" + LF + LF +

					// Initialise the service manager
					"   /* Service Manager initialisation */" + LF + "   " + lcp.getName() + "_PF_Service_Manager__Initialise();" + LF + LF;

			// Start the ELI receiver thread
			includeList.add(lcp.getName() + "_ELI_Support");
			includeList.add("PD_IDS");

			initialiseText += "   /* Initialise ELI Support */" + LF + "   " + lcp.getName() + "_ELI_Support__Initialise();" + LF + LF;

			// Start the platform level ELI receiver thread if more than one
			// platform.
			if (pfManagerGenerator.getSystemModel().getLogicalSystem().getLogicalcomputingPlatforms().size() > 1) {
				includeList.add(lcp.getName() + "_ELI_Support");

				initialiseText += "   /*  Start the Platform-level ELI receiver */" + LF + "   vcInfoType *vcInfo_PF = (vcInfoType *)malloc(sizeof(vcInfoType));" + LF + "   vcInfo_PF->vcID = VC_IDS__PLATFORM_LEVEL_RECEIVE;" + LF + "   vcInfo_PF->isPlatformVC = ECOA__TRUE;" + LF + "   " + lcp.getName() + "_startThread(" + lcp.getName() + "_ELI_Support__ReceiveELIMessage, " + underlyingPlatformInstantiation.getELIReceiverPriority() + ", \"ELI_Rx\", (void *)vcInfo_PF);" + LF + LF;
			}

			for (SM_ProtectionDomain remotePD : lcp.getAllProtectionDomains()) {
				includeList.add(lcp.getName() + "_VC_IDS");
				initialiseText += "   /*  Start the ELI receiver - passing in the VC ID to receive from */" + LF + "   vcInfoType *vcInfo_" + remotePD.getName() + " = (vcInfoType *)malloc(sizeof(vcInfoType));" + LF + "   vcInfo_" + remotePD.getName() + "->vcID = VC_IDS__" + remotePD.getName().toUpperCase() + "_RECEIVE;" + LF + "   vcInfo_" + remotePD.getName() + "->isPlatformVC = ECOA__FALSE;" + LF + "   " + lcp.getName() + "_startThread(" + lcp.getName() + "_ELI_Support__ReceiveELIMessage, " + underlyingPlatformInstantiation.getELIReceiverPriority() + ", \"ELI_Rx\", (void *)vcInfo_" + remotePD.getName() + ");" + LF + LF;
			}

			// Declare the PD as UP
			includeList.add(lcp.getName() + "_PFtoPD_Manager");
			includeList.add(lcp.getName() + "_PFtoPF_Manager");
			includeList.add("ecoaLog");

			initialiseText += "   /* Send \"DOWN\" status to all protection domains*/" + LF + LF +

					generateSendPDStatusCalls("   ") + LF +

					"   /* Declare the protection domain as \"UP\" */" + LF + "   " + lcp.getName() + "_PFtoPD_Manager__Set_PD_Status(PD_IDS__" + lcp.getName().toUpperCase() + ", ELI_Message__PlatformStatus_UP);" + LF + LF +

					"   unsigned char buffer[255];" + LF + "   int size;" + LF + "   size = sprintf((char *)buffer, \"alive - sent PD/PF status\\n\");" + LF + LF +

					"   /* Enter an infinite loop periodically sending PD status */" + LF + "   while (1)" + LF + "   {" + LF + LF +

					generateSendPDStatusCalls("      ") + LF + generateSendPlatformStatusCalls("      ") + LF +

					"      ecoaLog(buffer, size, LOG_LEVEL_CONTAINER_LEVEL_1, 0);" + LF + "      " + lcp.getName() + "_delay(5, 0);" + LF + "   }" + LF;

			initialiseText += "}" + LF;

		}

		// Replace the #INITIALISE# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#INITIALISE#", initialiseText);
	}

	private String generateSendPDStatusCalls(String indent) {
		String pdStatusCallText = "";

		for (SM_ProtectionDomain pd : lcp.getAllProtectionDomains()) {
			pdStatusCallText += indent + lcp.getName() + "_PFtoPD_Manager__Send_PD_Status(PD_IDS__" + pd.getName().toUpperCase() + ");" + LF;
		}
		return pdStatusCallText;
	}

	private String generateSendPlatformStatusCalls(String indent) {
		String pfStatusCallText = "";

		for (SM_LogicalComputingPlatform remoteLCP : pfManagerGenerator.getSystemModel().getLogicalSystem().getLogicalcomputingPlatforms()) {
			if (remoteLCP != lcp) {
				pfStatusCallText += indent + lcp.getName() + "_PFtoPF_Manager__Send_Platform_Status(" + remoteLCP.getRelatedUDPBinding().getPlatformID() + ");" + LF;
			}
		}
		return pfStatusCallText;
	}

	public void writePreamble() {
		String preambleText = "";

		if (isHeader) {
			preambleText += "/* File " + lcp.getName() + "_PF_Controller.h */" + LF + "/* This is the main entry point for the Platform Manager application */" + LF;
		} else {
			preambleText += "/* File " + lcp.getName() + "_PF_Controller.c */" + LF;
		}

		// Replace the #PREAMBLE# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#PREAMBLE#", preambleText);
	}
}
