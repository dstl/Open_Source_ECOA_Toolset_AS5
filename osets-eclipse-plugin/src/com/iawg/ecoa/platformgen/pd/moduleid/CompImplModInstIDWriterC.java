/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.platformgen.pd.moduleid;

import java.nio.file.Path;

import com.iawg.ecoa.SourceFileWriter;
import com.iawg.ecoa.platformgen.common.WriterSupport;
import com.iawg.ecoa.systemmodel.SystemModel;
import com.iawg.ecoa.systemmodel.componentimplementation.SM_ComponentImplementation;
import com.iawg.ecoa.systemmodel.componentimplementation.SM_DynamicTriggerInstance;
import com.iawg.ecoa.systemmodel.componentimplementation.SM_ModuleInstance;
import com.iawg.ecoa.systemmodel.componentimplementation.SM_TriggerInstance;

public class CompImplModInstIDWriterC extends SourceFileWriter {
	private static final String SEP_PATTERN_01 = "#define ";
	private SM_ComponentImplementation compImpl;

	public CompImplModInstIDWriterC(Path outputDir, SystemModel systemModel, SM_ComponentImplementation compImpl) {
		super(outputDir);
		this.compImpl = compImpl;

		setFileStructure();
	}

	@Override
	public void open() {
		super.openFile(outputDir.resolve(compImpl.getName() + "_Module_Instance_ID.h"));
	}

	@Override
	protected void setFileStructure() {
		String fileStructure = "#PREAMBLE#" + LF + "#MOD_INST_ID_DEFS#" + LF;
		codeStringBuilder.append(fileStructure);
	}

	public void writeModInstIDDefs() {
		int instanceID = 0;

		String modInstIDText = "/* Define an ID for each module/trigger instance in the component implementation */" + LF;

		for (SM_ModuleInstance modInst : compImpl.getModuleInstances().values()) {
			modInstIDText += SEP_PATTERN_01 + modInst.getComponentImplementation().getName().toUpperCase() + "_" + modInst.getName().toUpperCase() + "_ID " + instanceID++ + LF;
		}

		for (SM_TriggerInstance trigInst : compImpl.getTriggerInstances().values()) {
			modInstIDText += SEP_PATTERN_01 + trigInst.getComponentImplementation().getName().toUpperCase() + "_" + trigInst.getName().toUpperCase() + "_ID " + instanceID++ + LF;
		}

		for (SM_DynamicTriggerInstance dynTrigInst : compImpl.getDynamicTriggerInstances().values()) {
			modInstIDText += SEP_PATTERN_01 + dynTrigInst.getComponentImplementation().getName().toUpperCase() + "_" + dynTrigInst.getName().toUpperCase() + "_ID " + instanceID++ + LF;
		}

		// Replace the #MOD_INST_ID_DEFS# tag in string builder.
		WriterSupport.replaceText(codeStringBuilder, "#MOD_INST_ID_DEFS#", modInstIDText);

	}

	public void writePreamble() {
		String preambleText = "/* File " + compImpl.getName() + "_Module_Instance_ID.h */" + LF;

		// Replace the #PREAMBLE# tag in string builder.
		WriterSupport.replaceText(codeStringBuilder, "#PREAMBLE#", preambleText);
	}

}
