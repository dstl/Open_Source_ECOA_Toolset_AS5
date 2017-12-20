/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.platformgen.gen.posixaposbind.portno;

import java.nio.file.Path;

import com.iawg.ecoa.SourceFileWriter;
import com.iawg.ecoa.platformgen.common.WriterSupport;
import com.iawg.ecoa.systemmodel.SystemModel;
import com.iawg.ecoa.systemmodel.deployment.SM_ProtectionDomain;
import com.iawg.ecoa.systemmodel.deployment.logicalsystem.SM_LogicalComputingNode;
import com.iawg.ecoa.systemmodel.deployment.logicalsystem.SM_LogicalComputingPlatform;

public class PortNoWriterC extends SourceFileWriter {
	private static final String SEP_PATTERN_01 = "#define PORT_NO__";
	private SystemModel systemModel;

	public PortNoWriterC(Path outputDir, SystemModel systemModel) {
		super(outputDir);
		this.systemModel = systemModel;
		setFileStructure();
	}

	@Override
	public void open() {
		super.openFile(outputDir.resolve("PORT_NO.h"));
	}

	@Override
	protected void setFileStructure() {
		String fileStructure = "#PREAMBLE#" + LF + "#PORT_NO#" + LF;
		codeStringBuilder.append(fileStructure);
	}

	public void writePreamble() {
		String preambleText = "/* File PORT_NO.h */" + LF;

		// Replace the #PREAMBLE# tag in string builder.
		WriterSupport.replaceText(codeStringBuilder, "#PREAMBLE#", preambleText);
	}

	public void writePorts() {
		String portNoText = "/* Define a port number offset for each connection in the system */" + LF;

		int portNoOffset = 0;
		for (SM_LogicalComputingPlatform lcp : systemModel.getLogicalSystem().getLogicalcomputingPlatforms()) {
			for (SM_LogicalComputingNode node : lcp.getLogicalcomputingNodes()) {
				for (SM_ProtectionDomain pd : node.getProtectionDomains()) {
					// For each protection domain, define a port number for
					// every other protection domain in the platform which we
					// communicate with
					for (SM_ProtectionDomain remotePD : pd.getListOfPDsCommunicateWith()) {
						portNoText += SEP_PATTERN_01 + pd.getName().toUpperCase() + "__" + remotePD.getName().toUpperCase() + " " + (portNoOffset++) + LF;
					}

					// Also define connections to the Platform Manager
					// application.
					portNoText += SEP_PATTERN_01 + pd.getName().toUpperCase() + "__" + lcp.getName().toUpperCase() + " " + (portNoOffset++) + LF;
					portNoText += SEP_PATTERN_01 + lcp.getName().toUpperCase() + "__" + pd.getName().toUpperCase() + " " + (portNoOffset++) + LF;
				}
			}
		}

		// Replace the #PORT_NO# tag in string builder.
		WriterSupport.replaceText(codeStringBuilder, "#PORT_NO#", portNoText);
	}

}
