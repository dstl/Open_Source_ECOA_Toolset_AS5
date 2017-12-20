/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.platformgen.gen.assetids;

import com.iawg.ecoa.platformgen.PlatformGenerator;
import com.iawg.ecoa.platformgen.common.WriterSupport;
import com.iawg.ecoa.systemmodel.assembly.SM_ComponentInstance;
import com.iawg.ecoa.systemmodel.assembly.SM_Wire;
import com.iawg.ecoa.systemmodel.deployment.SM_ProtectionDomain;
import com.iawg.ecoa.systemmodel.deployment.logicalsystem.SM_LogicalComputingNode;
import com.iawg.ecoa.systemmodel.deployment.logicalsystem.SM_LogicalComputingPlatform;
import com.iawg.ecoa.systemmodel.uid.SM_UIDServiceOp;

public class AssetIDWriterAda extends AssetIDWriter {
	private static final String SEP_PATTERN_01 = "  : constant IDs_Type := ";

	public AssetIDWriterAda(PlatformGenerator platformGenerator) {
		super(platformGenerator);

		setFileStructure();
	}

	@Override
	public void close() {
		String endText = "";

		endText += "end ECOA.FaultHandler;" + LF;

		// Replace the #END_FILE# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#END_FILE#", endText);

		super.close();
	}

	@Override
	public void open() {
		super.openFile(outputDir.resolve("include/ECOA-FaultHandler.ads"));
	}

	@Override
	protected void setFileStructure() {
		// Set the file structure
		String fileStructure = "#PREAMBLE#" + LF + "#INCLUDES#" + LF + "#START_FILE#" + LF + "#CMP_INST_IDS#" + LF + "#PD_IDS#" + LF + "#CN_IDS#" + LF + "#PF_IDS#" + LF + "#OP_IDS#" + LF + "#DEP_IDS#" + LF + "#END_FILE#" + LF;
		codeStringBuilder.append(fileStructure);
	}

	public void writeCNs() {
		String cnText = "";
		int count = 0;

		for (SM_LogicalComputingPlatform pld : systemModel.getLogicalSystem().getLogicalcomputingPlatforms()) {

			for (SM_LogicalComputingNode lcn : pld.getLogicalcomputingNodes()) {
				cnText += "   IDs_NOD_" + lcn.getName().replaceAll("-", "_") + SEP_PATTERN_01 + count + ";" + LF;
				count = count + 1;
			}
		}

		// Replace the #CN_IDS# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#CN_IDS#", cnText);

	}

	public void writeCompInstIDs() {
		String compInstIDText = "";
		int count = 0;

		for (SM_ProtectionDomain pd : systemModel.getDeployment().getProtectionDomains()) {
			for (SM_ComponentInstance ci : pd.getComponentInstances()) {
				compInstIDText += "   IDs_CMP_" + ci.getName() + " : constant IDs_Type := " + count + ";" + LF;
				count = count + 1;
			}
		}

		// Replace the #CMP_INST_IDS# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#CMP_INST_IDS#", compInstIDText);

	}

	public void writeDeps() {
		String depText = "";
		int count = 0;

		depText += "   IDs_DEP_" + systemModel.getDeployment().getAssembly().getName() + SEP_PATTERN_01 + count + ";" + LF;
		count = count + 1;

		// Replace the #DEP_IDS# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#DEP_IDS#", depText);

	}

	public void writeIncludes() {
		String includeText = "";

		// Replace the #INCLUDES# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#INCLUDES#", includeText);
	}

	public void writeOPs() {
		String opText = "";
		int count = 0;

		for (SM_Wire wire : systemModel.getFinalAssembly().getWires()) {
			for (SM_UIDServiceOp op : wire.getUIDList()) {
				String UIDName = op.getUIDString().replaceAll(":", "_").replaceAll("/", "_");
				opText += "   IDs_SOP_" + UIDName + SEP_PATTERN_01 + op.getID() + ";" + LF;
				count = count + 1;
			}
		}

		// Replace the #OP_IDS# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#OP_IDS#", opText);

	}

	public void writePDIDs() {
		String pdIDText = "";
		int count = 0;

		for (SM_ProtectionDomain pd : systemModel.getDeployment().getProtectionDomains()) {
			pdIDText += "   IDs_PD_" + pd.getName() + " : constant IDs_Type := " + count + ";" + LF;
			count = count + 1;
		}

		// Replace the #PD_IDS# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#PD_IDS#", pdIDText);

	}

	public void writePFs() {
		String pfText = "";
		int count = 0;

		for (SM_LogicalComputingPlatform pld : systemModel.getLogicalSystem().getLogicalcomputingPlatforms()) {

			pfText += "   IDs_PF_" + pld.getName() + SEP_PATTERN_01 + count + ";" + LF;
			count = count + 1;
		}

		// Replace the #PF_IDS# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#PF_IDS#", pfText);

	}

	public void writePreamble() {
		String preambleText = "-- File ECOA-FaultHandler.ads" + LF;

		// Replace the #PREAMBLE# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#PREAMBLE#", preambleText);

		String startText = "package ECOA.FaultHandler is" + LF + LF +

				"   type IDs_Type is new ECOA.Unsigned_32_Type;" + LF + LF;

		// Replace the #START_FILE# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#START_FILE#", startText);
	}

}
