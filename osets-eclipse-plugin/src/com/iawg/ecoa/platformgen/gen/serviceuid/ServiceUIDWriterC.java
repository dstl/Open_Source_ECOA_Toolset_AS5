/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.platformgen.gen.serviceuid;

import java.nio.file.Path;

import com.iawg.ecoa.SourceFileWriter;
import com.iawg.ecoa.platformgen.common.WriterSupport;
import com.iawg.ecoa.systemmodel.SystemModel;
import com.iawg.ecoa.systemmodel.assembly.SM_ComponentInstance;
import com.iawg.ecoa.systemmodel.uid.SM_UIDServiceInst;

public class ServiceUIDWriterC extends SourceFileWriter {
	private SystemModel systemModel;

	public ServiceUIDWriterC(Path outputDir, SystemModel systemModel) {
		super(outputDir);
		this.systemModel = systemModel;

		setFileStructure();
	}

	@Override
	public void open() {
		super.openFile(outputDir.resolve("Service_UID.h"));

	}

	@Override
	protected void setFileStructure() {
		String fileStructure = "#PREAMBLE#" + LF + "#SERVICE_UID_DEFS#" + LF;
		codeStringBuilder.append(fileStructure);
	}

	public void writePreamble() {
		String preambleText = "/* File Service_UID.h */" + LF;

		// Replace the #PREAMBLE# tag in string builder.
		WriterSupport.replaceText(codeStringBuilder, "#PREAMBLE#", preambleText);
	}

	public void writeServiceUIDDefs() {
		String serviceUIDText = "/* Define an ID for each service UID */" + LF;

		for (SM_ComponentInstance compInst : systemModel.getFinalAssembly().getComponentInstances()) {

			for (SM_UIDServiceInst serviceInstanceUID : compInst.getUIDList()) {
				serviceUIDText += "#define " + compInst.getName().toUpperCase() + "_" + serviceInstanceUID.getServiceInstance().getName().toUpperCase() + "_UID " + serviceInstanceUID.getID() + LF;
			}
		}

		// Replace the #SERVICE_UID_DEFS# tag in string builder.
		WriterSupport.replaceText(codeStringBuilder, "#SERVICE_UID_DEFS#", serviceUIDText);

	}

}
