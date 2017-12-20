/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.buildgen.runsystem;

import java.nio.file.Path;

import com.iawg.ecoa.SourceFileWriter;
import com.iawg.ecoa.platformgen.common.WriterSupport;
import com.iawg.ecoa.systemmodel.SystemModel;
import com.iawg.ecoa.systemmodel.deployment.SM_ProtectionDomain;
import com.iawg.ecoa.systemmodel.deployment.logicalsystem.SM_LogicalComputingPlatform;

public class RunSystemFileWriter extends SourceFileWriter {
	private SystemModel sysModel;

	public RunSystemFileWriter(Path outputDir, SystemModel sysModel) {
		super(outputDir);
		this.sysModel = sysModel;

		setFileStructure();
	}

	@Override
	public void open() {
		super.openFile(outputDir.resolve("run_system.sh"));
	}

	@Override
	protected void setFileStructure() {
		// Set the file structure
		String fileStructure = "#PREAMBLE#" + LF + "#GENERIC_CONTENT#" + LF;

		codeStringBuilder.append(fileStructure);
	}

	public void writeGenericContent() {
		String genericText = "";

		for (SM_LogicalComputingPlatform lcp : sysModel.getLogicalSystem().getLogicalcomputingPlatforms()) {
			genericText += "gnome-terminal -e " + lcp.getName() + "/" + lcp.getLogicalcomputingNodes().get(0).getName() + "/" + lcp.getName() + "_PF_Manager/build_" + lcp.getName() + "/main &" + LF;

			for (SM_ProtectionDomain pd : lcp.getAllProtectionDomains()) {
				genericText += "gnome-terminal -e " + lcp.getName() + "/" + pd.getLogicalComputingNode().getName() + "/" + pd.getName() + "/build_" + pd.getName() + "/main &" + LF;
			}
		}

		// Replace the #GENERIC_CONTENT# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#GENERIC_CONTENT#", genericText);
	}

	public void writePreamble() {
		String preambleText = "#!/bin/sh" + LF + "# Script to run all the Protection Domain/ PF_Manager executables in the system." + LF + LF;

		// Replace the #PREAMBLE# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#PREAMBLE#", preambleText);
	}

}
