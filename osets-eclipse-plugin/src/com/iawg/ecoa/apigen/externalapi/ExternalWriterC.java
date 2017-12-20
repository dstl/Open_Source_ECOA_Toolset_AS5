/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.apigen.externalapi;

import java.nio.file.Path;
import java.util.List;

import com.iawg.ecoa.TypesProcessorC;
import com.iawg.ecoa.systemmodel.SM_OperationParameter;
import com.iawg.ecoa.systemmodel.componentimplementation.SM_ComponentImplementation;
import com.iawg.ecoa.systemmodel.types.SM_Namespace;

public class ExternalWriterC extends ExternalWriter {

	private boolean addComma = false;

	public ExternalWriterC(Path outputDir, SM_ComponentImplementation compImpl) {
		super(outputDir, compImpl);
	}

	@Override
	public void close() {
		// Header
		codeStringBuilder.append(LF + "#if defined(__cplusplus)" + LF + "}" + LF + "#endif /* __cplusplus */" + LF + LF +

				"#endif  /* _" + compImplName.toUpperCase() + "_EXTERNAL_INTERFACE_H */" + LF);

		super.close();
	}

	@Override
	public void open() {
		super.openFile(outputDir.resolve(compImplName + "_External_Interface.h"));
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

		codeStringBuilder.append(spacer + "const " + TypesProcessorC.convertParameterToC(para.getType()));
		if (para.getType().isSimple()) {
			codeStringBuilder.append(" ");
		} else {
			codeStringBuilder.append("* ");
		}
		codeStringBuilder.append(para.getName());
	}

	public void writeEndParameters() {
		codeStringBuilder.append(");" + LF + LF);
	}

	@Override
	public void writeExternalInterface(String senderOpName, List<SM_OperationParameter> params) {
		addComma = false;

		codeStringBuilder.append("   void " + compImplName + "__" + senderOpName + "(");

		// Add parameters
		for (SM_OperationParameter opParam : params) {
			writeConstParameter(opParam);
		}
		writeEndParameters();
	}

	@Override
	public void writePreamble() {
		codeStringBuilder.append("/* File " + compImplName + "_External_Interface.h */" + LF + LF +

				"#if !defined(_" + compImplName.toUpperCase() + "_EXTERNAL_INTERFACE_H)" + LF + "#define _" + compImplName.toUpperCase() + "_EXTERNAL_INTERFACE_H" + LF + LF);

		for (SM_Namespace use : componentImplementation.getUses()) {
			codeStringBuilder.append("#include \"" + use.getName().replaceAll("\\.", "__") + ".h" + "\"" + LF + LF);
		}

		codeStringBuilder.append(LF + "#if defined(__cplusplus)" + LF + "extern \"C\" {" + LF + "#endif /* __cplusplus */" + LF + LF);
	}
}
