/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.buildgen.protectiondomainmakefile.pf;

import java.nio.file.Path;

import com.iawg.ecoa.SourceFileWriter;
import com.iawg.ecoa.platformgen.common.WriterSupport;
import com.iawg.ecoa.systemmodel.deployment.logicalsystem.SM_LogicalComputingPlatform;

public class PlatformManagerMakefileWriter extends SourceFileWriter {
	private static final String SEP_PATTERN_51 = "src-gen";
	private static final String SEP_PATTERN_A = "../src-gen";
	private static final String SEP_PATTERN_B = "../inc-gen";
	private static final String SEP_PATTERN_C = "../../../include";
	private static final String SEP_PATTERN_D = "../../../src";
	private static final String SEP_PATTERN_E = "inc-gen";

	private SM_LogicalComputingPlatform lcp;

	public PlatformManagerMakefileWriter(Path outputDir, SM_LogicalComputingPlatform lcp) {
		super(outputDir);
		this.lcp = lcp;

		setFileStructure();
	}

	private String generateDepText(String name, String srcDir, String incDir) {
		String outputString = "";

		outputString = "obj/" + name + ".o : " + srcDir + "/" + name + ".c ";
		if (incDir != null) {
			outputString += incDir + "/" + name + ".h";
		}
		outputString += LF + "\t$(CC) $(CFLAGS) -c $< -o $@" + LF + LF;

		return outputString;
	}

	@Override
	public void open() {
		super.openFile((outputDir.resolve("Makefile")));
	}

	@Override
	protected void setFileStructure() {
		// Set the file structure
		String fileStructure;
		fileStructure = "#PREAMBLE#" + LF + LF + "OBJS=#OBJECTS#" + LF + "TARGET=#TARGET#" + LF + "CC=gcc $(CPPFLAGS)" + LF + "LD=$(CC) -lrt -lpthread -lm -lstdc++" + LF;

		// TODO - need a way to determine which node we are running on!
		if (lcp.getLogicalcomputingNodes().get(0).isLittleEndian()) {
			fileStructure += "CFLAGS=-Iinc-gen -I../inc-gen -I../../include -I../../../include -DLITTLE_ENDIAN -DECOA_64BIT_SUPPORT" + LF + LF;
		} else {
			fileStructure += "CFLAGS=-Iinc-gen -I../inc-gen -I../../include -I../../../include -DECOA_64BIT_SUPPORT" + LF + LF;
		}

		fileStructure += "default: $(TARGET)" + LF + LF + "clean:" + LF + "\trm -f obj/*.o" + LF + LF + "#OBJDEPS#" + LF + LF + "dirCheck:" + LF + "\tmkdir -p obj" + LF + LF + "$(TARGET) : dirCheck $(OBJS)" + LF + "\t$(LD) $(OBJS) -o $@ $(LDLIBS) $(LDMAP)" + LF;

		codeStringBuilder.append(fileStructure);
	}

	public void writeObjectDeps() {
		String objectText = "";

		// ************************************************
		// Dependencies for generic things

		objectText += generateDepText("ECOA_time_utils", SEP_PATTERN_A, SEP_PATTERN_B);

		objectText += generateDepText("ecoaLog", SEP_PATTERN_A, SEP_PATTERN_B);

		objectText += generateDepText("ELI_In__deserialiser", SEP_PATTERN_D, SEP_PATTERN_C);

		objectText += generateDepText("ELI_Out__serialiser", SEP_PATTERN_D, SEP_PATTERN_C);

		objectText += generateDepText("message_queue", SEP_PATTERN_A, SEP_PATTERN_B);

		objectText += generateDepText("fragment", SEP_PATTERN_D, SEP_PATTERN_C);

		objectText += generateDepText("reassemble", SEP_PATTERN_D, SEP_PATTERN_C);

		// ************************************************
		// dependencies for Platform Manager

		objectText += generateDepText(lcp.getName() + "_ELI_In", SEP_PATTERN_51, SEP_PATTERN_E);

		objectText += generateDepText(lcp.getName() + "_ELI_Support", SEP_PATTERN_51, SEP_PATTERN_E);

		objectText += generateDepText(lcp.getName() + "_PF_Controller", SEP_PATTERN_51, SEP_PATTERN_E);

		objectText += generateDepText(lcp.getName() + "_PF_Service_Manager", SEP_PATTERN_51, SEP_PATTERN_E);

		objectText += generateDepText(lcp.getName() + "_PFtoPD_Manager", SEP_PATTERN_51, SEP_PATTERN_E);

		objectText += generateDepText(lcp.getName() + "_PFtoPF_Manager", SEP_PATTERN_51, SEP_PATTERN_E);

		objectText += generateDepText(lcp.getName() + "_VC_IDS", SEP_PATTERN_51, SEP_PATTERN_E);

		objectText += generateDepText("posix_apos_binding", SEP_PATTERN_51, SEP_PATTERN_E);

		objectText += generateDepText("main", SEP_PATTERN_51, null);

		WriterSupport.replaceText(codeStringBuilder, "#OBJDEPS#", objectText);

	}

	public void writeObjects() {
		String objectText = "";

		// ************************************************
		// Objects for generic things
		objectText += "obj/ECOA_time_utils.o " + "obj/ecoaLog.o " + "obj/main.o " + "obj/message_queue.o ";

		objectText += "obj/ELI_In__deserialiser.o " + "obj/ELI_Out__serialiser.o " + "obj/fragment.o " + "obj/reassemble.o ";

		objectText += "\\" + LF;

		// ************************************************
		// Objects for Platform Manager
		objectText += "\t\t";

		objectText += "obj/" + lcp.getName() + "_ELI_In.o ";

		objectText += "obj/" + lcp.getName() + "_PF_Controller.o " + "obj/" + lcp.getName() + "_PF_Service_Manager.o ";
		objectText += " \\\n\t\t";

		// ************************************************

		objectText += "obj/posix_apos_binding.o ";

		objectText += "obj/" + lcp.getName() + "_ELI_Support.o " + "obj/" + lcp.getName() + "_PFtoPD_Manager.o " + "obj/" + lcp.getName() + "_PFtoPF_Manager.o " + "obj/" + lcp.getName() + "_VC_IDS.o ";

		objectText += LF;

		WriterSupport.replaceText(codeStringBuilder, "#OBJECTS#", objectText);
	}

	public void writePreamble() {
		String preambleText = "";

		preambleText += "# Generated Makefile for " + lcp.getName() + "_PF_Manager";

		// Replace the #PREAMBLE# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#PREAMBLE#", preambleText);
	}

	public void writeTarget() {
		String targetText = "";

		targetText += lcp.getName();

		// Replace the #TARGET# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#TARGET#", targetText);
	}

}
