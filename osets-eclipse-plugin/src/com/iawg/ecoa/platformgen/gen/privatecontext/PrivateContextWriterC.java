/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.platformgen.gen.privatecontext;

import java.nio.file.Path;
import java.util.ArrayList;

import com.iawg.ecoa.CLanguageSupport;
import com.iawg.ecoa.SourceFileWriter;
import com.iawg.ecoa.platformgen.common.WriterSupport;

public class PrivateContextWriterC extends SourceFileWriter {

	private ArrayList<String> includeList = new ArrayList<String>();

	public PrivateContextWriterC(Path outputDir) {
		super(outputDir);

		setFileStructure();
	}

	@Override
	public void close() {
		String closeText = "#endif  /* _PRIVATE_CONTEXT_H */" + LF + LF;

		// Replace the #CLOSE# tag in string builder.
		WriterSupport.replaceText(codeStringBuilder, "#CLOSE#", closeText);

		super.close();
	}

	@Override
	public void open() {
		super.openFile(outputDir.resolve("Private_Context.h"));
	}

	@Override
	protected void setFileStructure() {
		String fileStructure = "#PREAMBLE#" + LF + "#INCLUDES#" + LF + "#PRIVATE_CONTEXT#" + LF + "#CLOSE#" + LF;
		codeStringBuilder.append(fileStructure);
	}

	public void writeIncludes() {
		includeList.add("ECOA");

		String includeText = CLanguageSupport.generateIncludes(includeList);

		// Replace the #INCLUDES# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#INCLUDES#", includeText);

	}

	public void writePreamble() {
		String preambleText = "/* File Private_Context.h */" + LF + LF +

				"#if !defined(_PRIVATE_CONTEXT_H)" + LF + "#define _PRIVATE_CONTEXT_H" + LF + LF;

		// Replace the #PREAMBLE# tag in string builder.
		WriterSupport.replaceText(codeStringBuilder, "#PREAMBLE#", preambleText);
	}

	public void writePrivateContext() {

		String privateContextText = "/* Define the private context structure */" + LF + "typedef struct private_context" + LF + "{" + LF + "   int moduleInstanceID;" + LF + "   int componentInstanceID;" + LF + "   ECOA__module_states_type moduleState;" + LF + "} private_context;" + LF;

		// Replace the #MODULE_INSTANCE_ID_DEF# tag in string builder.
		WriterSupport.replaceText(codeStringBuilder, "#PRIVATE_CONTEXT#", privateContextText);

	}
}
