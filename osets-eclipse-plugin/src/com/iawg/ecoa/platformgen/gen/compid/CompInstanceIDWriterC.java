/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.platformgen.gen.compid;

import java.nio.file.Path;

import com.iawg.ecoa.SourceFileWriter;
import com.iawg.ecoa.platformgen.common.WriterSupport;
import com.iawg.ecoa.systemmodel.SystemModel;
import com.iawg.ecoa.systemmodel.assembly.SM_ComponentInstance;

public class CompInstanceIDWriterC extends SourceFileWriter {

	private SystemModel systemModel;

	public CompInstanceIDWriterC(Path outputDir, SystemModel systemModel) {
		super(outputDir);
		this.systemModel = systemModel;

		setFileStructure();
	}

	@Override
	public void open() {
		super.openFile(outputDir.resolve("Component_Instance_ID.h"));
	}

	@Override
	protected void setFileStructure() {
		String fileStructure = "#PREAMBLE#" + LF + "#COMP_INST_ID_DEFS#" + LF;
		codeStringBuilder.append(fileStructure);
	}

	public void writeCompInstIDDefs() {
		int compInstanceID = 0;

		String compInstIDText = "/* Define an ID for each component instance in the system */" + LF;

		for (SM_ComponentInstance compInst : systemModel.getFinalAssembly().getComponentInstances()) {
			compInstIDText += "#define CI_" + compInst.getName().toUpperCase() + "_ID " + compInstanceID++ + LF;
		}

		// Replace the #COMP_INST_ID_DEFS# tag in string builder.
		WriterSupport.replaceText(codeStringBuilder, "#COMP_INST_ID_DEFS#", compInstIDText);

	}

	public void writePreamble() {
		String preambleText = "/* File Component_Instance_ID.h */" + LF;

		// Replace the #PREAMBLE# tag in string builder.
		WriterSupport.replaceText(codeStringBuilder, "#PREAMBLE#", preambleText);
	}

}
