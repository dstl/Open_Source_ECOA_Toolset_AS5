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

public class UserContextWriterC extends UserContextWriter {
	private static final String SEP_PATTERN_01 = "_user_context.h";

	public UserContextWriterC(SystemModel systemModel, ToolConfig toolConfig, Path outputDir, SM_ModuleImpl moduleImpl) {
		super(systemModel, toolConfig, outputDir, moduleImpl);
	}

	@Override
	public void close() {
		codeStringBuilder.append("#if defined(__cplusplus)" + LF + "}" + LF + "#endif /* __cplusplus */" + LF + LF +

				"#endif  /* _" + moduleImplName.toUpperCase() + "_USER_CONTEXT_H */");
		super.close();
	}

	@Override
	public void open() {
		if (!Files.exists(outputDir.resolve(moduleImplName + SEP_PATTERN_01)) || toolConfig.isOverwriteFiles()) {
			super.openFile(outputDir.resolve(moduleImplName + SEP_PATTERN_01));
		} else {
			super.openFile(outputDir.resolve(moduleImplName + "_user_context.h.new"));
		}
	}

	@Override
	protected void setFileStructure() {
		// Not required for API generation - TODO look at ways to stop this
		// being required.
	}

	@Override
	public void writePreamble() {
		codeStringBuilder.append("/* @file " + moduleImplName + SEP_PATTERN_01 + LF + " * This is an example of a user defined User Module context" + LF + " */" + LF + LF +

				"#if !defined(_" + moduleImplName.toUpperCase() + "_USER_CONTEXT_H)" + LF + "#define _" + moduleImplName.toUpperCase() + "_USER_CONTEXT_H" + LF + LF +

				"#if defined(__cplusplus)" + LF + "extern \"C\" {" + LF + "#endif /* __cplusplus */" + LF + LF +

				"/* Container Types */" + LF + "#include \"" + moduleImplName + "_container_types.h\"" + LF + LF);
	}

	@Override
	public void writeUserContext() {
		codeStringBuilder.append("/* User Module Context structure example */" + LF + "typedef struct" + LF + "{" + LF + "   /* declare the User Module Context \"local\" data here */" + LF + "   int dmy;" + LF + "} " + moduleImplName + "_user_context;" + LF + LF +

				"/* Warm Start Module Context structure example */" + LF + "typedef struct" + LF + "{" + LF + "   /* declare the Warm Start Module Context data here */" + LF + "   int dmy;" + LF + "} " + moduleImplName + "_warm_start_context;" + LF + LF);

	}
}
