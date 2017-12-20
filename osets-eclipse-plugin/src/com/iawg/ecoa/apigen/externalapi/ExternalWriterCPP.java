/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.apigen.externalapi;

import java.nio.file.Path;
import java.util.List;

import com.iawg.ecoa.TypesProcessorCPP;
import com.iawg.ecoa.systemmodel.SM_OperationParameter;
import com.iawg.ecoa.systemmodel.componentimplementation.SM_ComponentImplementation;

public class ExternalWriterCPP extends ExternalWriter {
	private boolean addComma = false;

	public ExternalWriterCPP(Path componentDirName, SM_ComponentImplementation componentImpl) {
		super(componentDirName, componentImpl);
	}

	@Override
	public void close() {
		codeStringBuilder.append("}" + LF + LF +

				"#endif  /* _" + compImplName.toUpperCase() + "_EXTERNAL_INTERFACE_HH */");

		super.close();
	}

	@Override
	public void open() {
		super.openFile(outputDir.resolve(compImplName + "_External_Interface.hpp"));
	}

	@Override
	protected void setFileStructure() {
		// Not required for API generation - TODO look at ways to stop this
		// being required.

	}

	public void writeConstParameter(SM_OperationParameter para) {
		// Write to header or body
		String spacer = "          ";

		if (addComma) {
			codeStringBuilder.append("," + LF);
		} else {
			addComma = true;
			spacer = "";
		}

		codeStringBuilder.append(spacer + "const " + TypesProcessorCPP.convertParameterToCPP(para.getType()));
		if (para.getType().isSimple()) {
			codeStringBuilder.append(" ");
		} else {
			codeStringBuilder.append(" &");
		}
		codeStringBuilder.append(para.getName());
	}

	public void writeEndParameters() {
		codeStringBuilder.append(");" + LF + LF);
	}

	@Override
	public void writeExternalInterface(String senderOpName, List<SM_OperationParameter> params) {
		addComma = false;

		codeStringBuilder.append("   void " + senderOpName + "(");

		// Add parameters
		for (SM_OperationParameter opParam : params) {
			writeConstParameter(opParam);
		}
		writeEndParameters();
	}

	@Override
	public void writePreamble() {
		// Header file
		codeStringBuilder.append("/* File " + compImplName + "_External_Interface.hpp */" + LF + LF +

				"#if !defined(_" + compImplName.toUpperCase() + "_EXTERNAL_INTERFACE_HH)" + LF + "#define _" + compImplName.toUpperCase() + "_EXTERNAL_INTERFACE_HH" + LF + LF +

				"#include \"ECOA.hpp\"" + LF + LF +

				"namespace " + compImplName + "_External_Interface" + LF + "{" + LF + LF);

	}

}
