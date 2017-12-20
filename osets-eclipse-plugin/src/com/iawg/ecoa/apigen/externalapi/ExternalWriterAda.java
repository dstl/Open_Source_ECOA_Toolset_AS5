/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.apigen.externalapi;

import java.nio.file.Path;
import java.util.List;

import com.iawg.ecoa.TypesProcessorAda;
import com.iawg.ecoa.systemmodel.SM_OperationParameter;
import com.iawg.ecoa.systemmodel.componentimplementation.SM_ComponentImplementation;

public class ExternalWriterAda extends ExternalWriter {
	private boolean addSemi = false;

	public ExternalWriterAda(Path componentDirName, SM_ComponentImplementation componentImpl) {
		super(componentDirName, componentImpl);
	}

	@Override
	public void close() {
		codeStringBuilder.append(LF + "end " + compImplName + "_External_Interface;" + LF + LF);

		super.close();
	}

	@Override
	public void open() {
		super.openFile(outputDir.resolve(compImplName + "_External_Interface.ads"));
	}

	@Override
	protected void setFileStructure() {
		// Not required for API generation - TODO look at ways to stop this
		// being required.

	}

	public void writeConstParameter(SM_OperationParameter para) {
		// Write to header or body
		String spacer = "          ";

		if (addSemi) {
			codeStringBuilder.append(";" + LF);
		} else {
			addSemi = true;
			spacer = "";
		}

		codeStringBuilder.append(spacer + para.getName() + " : in " + TypesProcessorAda.convertParameterToAda(para.getType()));
	}

	public void writeEndParameters() {
		codeStringBuilder.append(");" + LF + LF);
	}

	@Override
	public void writeExternalInterface(String senderOpName, List<SM_OperationParameter> params) {
		addSemi = false;

		codeStringBuilder.append("   procedure " + senderOpName + "(");

		// Add parameters
		for (SM_OperationParameter opParam : params) {
			writeConstParameter(opParam);
		}
		writeEndParameters();
	}

	@Override
	public void writePreamble() {
		codeStringBuilder.append("-- File " + compImplName + "_External_Interface.ads" + LF + LF +

				"with ECOA;" + LF + LF +

				"package " + compImplName + "_External_Interface is" + LF + LF);
	}
}
