/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.apigen.containertypes;

import java.nio.file.Path;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.iawg.ecoa.AdaLanguageSupport;
import com.iawg.ecoa.TypesProcessorAda;
import com.iawg.ecoa.WriterSupport;
import com.iawg.ecoa.systemmodel.SystemModel;
import com.iawg.ecoa.systemmodel.componentdefinition.serviceinstance.SM_ServiceInstance;
import com.iawg.ecoa.systemmodel.componentimplementation.SM_ComponentImplementation;
import com.iawg.ecoa.systemmodel.componentimplementation.SM_ModuleImpl;
import com.iawg.ecoa.systemmodel.types.SM_Type;

public class ContainerTypesWriterAda extends ContainerTypesWriter {
	private static final String SEP_PATTERN_01 = "   type ";

	public ContainerTypesWriterAda(SystemModel systemModel, Path outputDir, SM_ComponentImplementation compImpl, SM_ModuleImpl moduleImpl) {
		super(systemModel, outputDir, compImpl, moduleImpl);
	}

	@Override
	public void close() {
		// Close the header
		codeStringBuilder.append("end " + moduleImplName + "_Container_Types;" + LF);

		super.close();
	}

	@Override
	public void open() {
		super.openFile(outputDir.resolve(moduleImplName + "_Container_Types.ads"));
	}

	@Override
	protected void setFileStructure() {
		// Not required for API generation - TODO look at ways to stop this
		// being required.
	}

	@Override
	public void writePreamble() {
		codeStringBuilder.append("-------------------------------------------------------------------" + LF + "-- @file " + moduleImplName + "_Container_Types.ads" + LF + "-- Container Types package specification for Module " + moduleImplName + LF + "-- Generated automatically from specification; do not modify here" + LF + "-------------------------------------------------------------------" + LF + LF +

				"-- Standard ECOA Types" + LF + "with ECOA;" + LF + LF +

				"package " + moduleImplName + "_Container_Types is" + LF + LF);
	}

	@Override
	public void writeVDHandle(String opName, SM_Type opType) {
		includeList.add(opType.getNamespace().getName());

		codeStringBuilder.append("   " + opName + "_Handle_Platform_Hook_Size : constant := 32;" + LF + SEP_PATTERN_01 + opName + "_Handle_Platform_Hook_Type is array" + LF + "      (0.." + opName + "_Handle_Platform_Hook_Size-1) of ECOA.Byte_Type;" + LF + SEP_PATTERN_01 + opName + "_Data_Access_Type is access all " + TypesProcessorAda.convertParameterToAda(opType) + ";" + LF + LF +

				SEP_PATTERN_01 + opName + "_Handle_Type is" + LF + "      record" + LF + "         Data_Access     : " + opName + "_Data_Access_Type;" + LF + "         Timestamp       : " + "ECOA.Timestamp_Type;" + LF + "         Platform_Hook : " + opName + "_Handle_Platform_Hook_Type;" + LF + "      end record;" + LF + LF);
	}

	@Override
	public void writeSupervisionTypes() {
		writeProvidedServiceIDs();
		writeRequiredServiceIDs();
	}

	private void writeProvidedServiceIDs() {
		// Only generate if there is at least one provided service
		if (componentImplementation.getCompType().getServiceInstancesList().size() > 0) {
			codeStringBuilder.append("   -- Provided Service IDs;" + LF + "   type Service_ID_Type is new ECOA.Unsigned_32_Type;" + LF);

			Iterator<Entry<String, SM_ServiceInstance>> it = componentImplementation.getCompType().getServiceInstances().entrySet().iterator();
			Integer serviceID = 0;

			while (it.hasNext()) {
				Map.Entry<String, SM_ServiceInstance> mapEntry = it.next();

				codeStringBuilder.append("      Service_ID_Type_" + mapEntry.getKey() + " : constant Service_ID_Type := " + serviceID + ";" + LF);
				serviceID++;
			}
			codeStringBuilder.append(LF);
		}
	}

	private void writeRequiredServiceIDs() {
		// Only generate if there is at least one required service
		if (componentImplementation.getCompType().getReferenceInstancesList().size() > 0) {
			codeStringBuilder.append("   -- Required Service IDs;" + LF + "   type Reference_ID_Type is new ECOA.Unsigned_32_Type;" + LF);

			Iterator<Entry<String, SM_ServiceInstance>> it = componentImplementation.getCompType().getReferenceInstances().entrySet().iterator();
			Integer serviceID = 0;

			while (it.hasNext()) {
				Map.Entry<String, SM_ServiceInstance> mapEntry = it.next();

				codeStringBuilder.append("      Reference_ID_Type_" + mapEntry.getKey() + " : constant Reference_ID_Type := " + serviceID + ";" + LF);
				serviceID++;
			}
			codeStringBuilder.append(LF);
		}
	}

	@Override
	public void writeIncludes() {
		String includeText = AdaLanguageSupport.generateIncludes(includeList);

		// Replace the #INCLUDES# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#INCLUDES#", includeText);
	}

}
