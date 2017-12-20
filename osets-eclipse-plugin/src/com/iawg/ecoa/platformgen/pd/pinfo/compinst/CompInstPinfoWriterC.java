/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.platformgen.pd.pinfo.compinst;

import java.nio.file.Path;
import java.util.ArrayList;

import com.iawg.ecoa.CLanguageSupport;
import com.iawg.ecoa.SourceFileWriter;
import com.iawg.ecoa.platformgen.common.WriterSupport;
import com.iawg.ecoa.systemmodel.assembly.SM_ComponentInstance;
import com.iawg.ecoa.systemmodel.componentimplementation.SM_PinfoValue;
import com.iawg.ecoa.systemmodel.deployment.SM_DeployedModInst;

public class CompInstPinfoWriterC extends SourceFileWriter {
	private SM_ComponentInstance compInst;
	private boolean isHeader;

	private ArrayList<String> includeList = new ArrayList<String>();

	public CompInstPinfoWriterC(Path outputDir, boolean isHeader, SM_ComponentInstance compInst) {
		super(outputDir);
		this.compInst = compInst;
		this.isHeader = isHeader;

		setFileStructure();
	}

	@Override
	public void open() {
		if (isHeader) {
			super.openFile(outputDir.resolve(compInst.getName() + "_PINFO_Sizes.h"));
		} else {
			super.openFile(outputDir.resolve(compInst.getName() + "_PINFO_Sizes.c"));
		}
	}

	@Override
	protected void setFileStructure() {
		String fileStructure = "#PREAMBLE#" + LF + "#INCLUDES#" + LF + "#CURRENT_SIZE_DEFS#" + LF;
		codeStringBuilder.append(fileStructure);
	}

	public void writeCurrentSize() {
		String currentSizeText = "/* Define the current size for each private PINFO (private PINFO can be read-only or read-write) */" + LF;

		for (SM_DeployedModInst depModInst : compInst.getDeployedModInsts()) {
			for (SM_PinfoValue pinfo : depModInst.getModInstance().getPrivatePinfoValues()) {
				if (isHeader) {
					currentSizeText += "extern ECOA__uint32 " + pinfo.getPinfoFile().getName().replaceAll("\\.", "_").toUpperCase() + "_CURRENT_SIZE; " + LF;
				} else {
					currentSizeText += "ECOA__uint32 " + pinfo.getPinfoFile().getName().replaceAll("\\.", "_").toUpperCase() + "_CURRENT_SIZE; " + LF;
				}

			}
		}

		// Replace the #CURRENT_SIZE_DEFS# tag in string builder.
		WriterSupport.replaceText(codeStringBuilder, "#CURRENT_SIZE_DEFS#", currentSizeText);

	}

	public void writeIncludes() {
		if (isHeader) {
			includeList.add("ECOA");
		} else {
			includeList.add(compInst.getName() + "_PINFO_Sizes");
		}

		String includeText = CLanguageSupport.generateIncludes(includeList);

		// Replace the #INCLUDES# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#INCLUDES#", includeText);
	}

	public void writePreamble() {
		String preambleText;

		if (isHeader) {
			preambleText = "/* File " + compInst.getName() + "_PINFO_Sizes.h */" + LF;
		} else {
			preambleText = "/* File " + compInst.getName() + "_PINFO_Sizes.h */" + LF;
		}

		// Replace the #PREAMBLE# tag in string builder.
		WriterSupport.replaceText(codeStringBuilder, "#PREAMBLE#", preambleText);
	}

}
