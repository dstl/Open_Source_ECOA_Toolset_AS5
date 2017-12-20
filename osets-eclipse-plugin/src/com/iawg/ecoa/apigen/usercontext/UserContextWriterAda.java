/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.apigen.usercontext;

import java.nio.file.Files;
import java.nio.file.Path;

import com.iawg.ecoa.ToolConfig;
import com.iawg.ecoa.systemmodel.SystemModel;
import com.iawg.ecoa.systemmodel.componentimplementation.SM_ModuleImpl;

public class UserContextWriterAda extends UserContextWriter {
	private static final String SEP_PATTERN_01 = "_User_Context.ads";

	public UserContextWriterAda(SystemModel systemModel, ToolConfig toolConfig, Path outputDir, SM_ModuleImpl moduleImpl) {
		super(systemModel, toolConfig, outputDir, moduleImpl);
	}

	@Override
	public void close() {
		codeStringBuilder.append("end " + moduleImplName + "_User_Context;" + LF);
		super.close();
	}

	@Override
	public void open() {
		if (!Files.exists(outputDir.resolve(moduleImplName + SEP_PATTERN_01)) || toolConfig.isOverwriteFiles()) {
			super.openFile(outputDir.resolve(moduleImplName + SEP_PATTERN_01));
		} else {
			super.openFile(outputDir.resolve(moduleImplName + "_User_Context.ads.new"));
		}
	}

	@Override
	protected void setFileStructure() {
		// Not required for API generation - TODO look at ways to stop this
		// being required.
	}

	@Override
	public void writePreamble() {
		codeStringBuilder.append("-------------------------------------------------------------------" + LF + "-- @file " + moduleImplName + SEP_PATTERN_01 + LF + "-- This is the module implementation private user context data type" + LF + "-- that is included in the module context." + LF + "-------------------------------------------------------------------" + LF +

				"with ECOA;" + LF + LF +

				"-- Include Container Types" + LF + "with " + moduleImplName + "_Container_Types;" + LF + LF +

				"package " + moduleImplName + "_User_Context is" + LF + LF);
	}

	@Override
	public void writeUserContext() {
		codeStringBuilder.append("   -- User Module Context structure example" + LF + "   type User_Context_Type is record" + LF + "      -- Example user context" + LF + "      My_Counter   : ECOA.Unsigned_8_Type;" + LF + "   end record;" + LF + LF +

				"   type Warm_Start_Context_Type is record" + LF + "      -- Example warm start context" + LF + "      My_Warm_Start_Data   : ECOA.Unsigned_32_Type;" + LF + "   end record;" + LF + LF);

	}
}
