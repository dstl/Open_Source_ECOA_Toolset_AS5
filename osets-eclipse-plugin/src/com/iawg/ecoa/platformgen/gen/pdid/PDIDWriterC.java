/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.platformgen.gen.pdid;

import java.nio.file.Path;

import com.iawg.ecoa.SourceFileWriter;
import com.iawg.ecoa.platformgen.common.WriterSupport;
import com.iawg.ecoa.systemmodel.deployment.SM_ProtectionDomain;
import com.iawg.ecoa.systemmodel.deployment.logicalsystem.SM_LogicalComputingNode;
import com.iawg.ecoa.systemmodel.deployment.logicalsystem.SM_LogicalComputingPlatform;

public class PDIDWriterC extends SourceFileWriter {
	private SM_LogicalComputingPlatform lcp;

	public PDIDWriterC(Path outputDir, SM_LogicalComputingPlatform lcp) {
		super(outputDir);
		this.lcp = lcp;

		setFileStructure();
	}

	@Override
	public void open() {
		super.openFile(outputDir.resolve("PD_IDS.h"));
	}

	@Override
	protected void setFileStructure() {
		String fileStructure = "#PREAMBLE#" + LF + "#PD_IDS#" + LF;
		codeStringBuilder.append(fileStructure);
	}

	public void writePreamble() {
		String preambleText = "/* File PD_IDS.h */" + LF;

		// Replace the #PREAMBLE# tag in string builder.
		WriterSupport.replaceText(codeStringBuilder, "#PREAMBLE#", preambleText);
	}

	public void writePDIDs() {
		String pdIDText = "/* Define an ID for each protection domain */" + LF + "#define PD_IDS__" + lcp.getName().toUpperCase() + " 0" + LF;

		// Start at 1 as 0 is reserved for the platform manager application.
		int pdNum = 1;
		for (SM_LogicalComputingNode node : lcp.getLogicalcomputingNodes()) {
			for (SM_ProtectionDomain pd : node.getProtectionDomains()) {
				pdIDText += "#define PD_IDS__" + pd.getName().toUpperCase() + " " + pdNum + LF;
				pdNum++;
			}
		}

		// Replace the #PD_IDS# tag in string builder.
		WriterSupport.replaceText(codeStringBuilder, "#PD_IDS#", pdIDText);

	}

}
