/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.platformgen.gen.clienttype;

import java.nio.file.Path;
import java.util.ArrayList;

import com.iawg.ecoa.CLanguageSupport;
import com.iawg.ecoa.SourceFileWriter;
import com.iawg.ecoa.platformgen.common.WriterSupport;

public class ClientInfoTypeWriterC extends SourceFileWriter {
	private ArrayList<String> includeList = new ArrayList<String>();

	public ClientInfoTypeWriterC(Path outputDir) {
		super(outputDir);

		setFileStructure();
	}

	@Override
	public void close() {
		String closeText = "#endif  /* __CLIENT_INFO_TYPE */" + LF + LF;

		// Replace the #CLOSE# tag in string builder.
		WriterSupport.replaceText(codeStringBuilder, "#CLOSE#", closeText);

		super.close();
	}

	@Override
	public void open() {
		super.openFile(outputDir.resolve("Client_Info_Type.h"));
	}

	@Override
	protected void setFileStructure() {
		String fileStructure = "#PREAMBLE#" + LF + "#INCLUDES#" + LF + "#CLIENT_INFO_TYPE#" + LF + "#CLOSE#" + LF;

		codeStringBuilder.append(fileStructure);
	}

	public void writeClientInfoType() {
		String clientInfoText = "/* Define the Client Info Type */" + LF + LF +

				"typedef ECOA__uint32 Client_Type;" + LF + "#define Client_Type__MODULE_OPERATION 0" + LF + "#define Client_Type__SERVICE_OPERATION 1" + LF + LF +

				"typedef struct " + LF + "{" + LF + "   Client_Type type;" + LF + "   ECOA__uint32 ID;" + LF + "   ECOA__uint32 serviceUID;" + LF + "   ECOA__uint32 localSeqNum;" + LF + "   ECOA__uint32 globalSeqNum;" + LF + "} Client_Info_Type;" + LF;

		// Replace the #CLIENT_INFO_TYPE# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#CLIENT_INFO_TYPE#", clientInfoText);
	}

	public void writeIncludes() {
		includeList.add("ECOA");

		String includeText = CLanguageSupport.generateIncludes(includeList);

		// Replace the #INCLUDES# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#INCLUDES#", includeText);
	}

	public void writePreamble() {
		String preambleText = "/* File Client_Info_Type.h */" + LF + LF +

				"#if !defined(_CLIENT_INFO_TYPE)" + LF + "#define _CLIENT_INFO_TYPE" + LF + LF;

		// Replace the #PREAMBLE# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#PREAMBLE#", preambleText);
	}

}
