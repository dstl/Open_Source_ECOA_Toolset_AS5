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

import com.iawg.ecoa.CLanguageSupport;
import com.iawg.ecoa.TypesProcessorCPP;
import com.iawg.ecoa.WriterSupport;
import com.iawg.ecoa.systemmodel.SystemModel;
import com.iawg.ecoa.systemmodel.componentdefinition.serviceinstance.SM_ServiceInstance;
import com.iawg.ecoa.systemmodel.componentimplementation.SM_ComponentImplementation;
import com.iawg.ecoa.systemmodel.componentimplementation.SM_ModuleImpl;
import com.iawg.ecoa.systemmodel.types.SM_Namespace;
import com.iawg.ecoa.systemmodel.types.SM_Type;

public class ContainerTypesWriterCPP extends ContainerTypesWriter {

	public ContainerTypesWriterCPP(SystemModel systemModel, Path outputDir, SM_ComponentImplementation compImpl, SM_ModuleImpl moduleImpl) {
		super(systemModel, outputDir, compImpl, moduleImpl);
	}

	@Override
	public void close() {
		// Close the header
		codeStringBuilder.append("}" + LF + LF +

				"#endif  /* _" + moduleImplName.toUpperCase() + "_CONTAINER_TYPES_HPP */" + LF);

		super.close();
	}

	@Override
	public void open() {
		super.openFile(outputDir.resolve(moduleImplName + "_container_types.hpp"));
	}

	@Override
	protected void setFileStructure() {
		// Not required for API generation - TODO look at ways to stop this
		// being required.
	}

	@Override
	public void writePreamble() {
		codeStringBuilder.append("/*" + LF + " * @file " + moduleImplName + "_container_types.hpp" + LF + " * Container Types for Module " + moduleImplName + LF + " * Generated automatically from specification; do not modify here" + LF + " */" + LF + LF +

				"#if !defined(_" + moduleImplName.toUpperCase() + "_CONTAINER_TYPES_HPP)" + LF + "#define _" + moduleImplName.toUpperCase() + "_CONTAINER_TYPES_HPP" + LF + LF +

				"#include \"ECOA.hpp\"" + LF + LF +

				"/* Include the types from the XML types files */" + LF);
		for (SM_Namespace use : componentImplementation.getUses()) {
			codeStringBuilder.append("#include \"" + use.getName().replaceAll("\\.", "__") + ".hpp" + "\"" + LF + LF);
		}

		codeStringBuilder.append("namespace " + moduleImplName + LF + "{" + LF + LF +

				"#define ECOA_VERSIONED_DATA_HANDLE_PRIVATE_SIZE 32" + LF + LF);
	}

	@Override
	public void writeVDHandle(String opName, SM_Type opType) {
		includeList.add(opType.getNamespace().getName());

		codeStringBuilder.append("   typedef struct" + LF + "   {" + LF + "      " + TypesProcessorCPP.convertParameterToCPP(opType) + "* data;" + LF + "      ECOA::timestamp timestamp;" + LF + "      ECOA::byte platform_hook[ECOA_VERSIONED_DATA_HANDLE_PRIVATE_SIZE];" + LF + "   } " + opName + "_handle;" + LF + LF);
	}

	@Override
	public void writeSupervisionTypes() {
		writeProvidedServiceIDs();
		writeRequiredServiceIDs();
	}

	private void writeProvidedServiceIDs() {
		// Only generate if there is at least one provided service
		if (componentImplementation.getCompType().getServiceInstancesList().size() > 0) {
			codeStringBuilder.append("   /* Provided Service IDs */" + LF + "   struct service_id {" + LF + "      ECOA::uint32 value;" + LF + "      enum  EnumValues {" + LF);

			Iterator<Entry<String, SM_ServiceInstance>> it = componentImplementation.getCompType().getServiceInstances().entrySet().iterator();
			Integer serviceID = 0;

			while (it.hasNext()) {
				Map.Entry<String, SM_ServiceInstance> mapEntry = it.next();

				codeStringBuilder.append("         " + mapEntry.getKey() + " = " + serviceID + "," + LF);
				serviceID++;
			}

			codeStringBuilder.append("      };" + LF + "      inline void operator = (ECOA::uint32 i) { value = i; }" + LF + "      inline operator ECOA::uint32 () const { return value; }" + LF + "   };" + LF + LF);
		}
	}

	private void writeRequiredServiceIDs() {
		// Only generate if there is at least one required service
		if (componentImplementation.getCompType().getReferenceInstancesList().size() > 0) {
			codeStringBuilder.append("   /* Required Service IDs */" + LF + "   struct reference_id {" + LF + "      ECOA::uint32 value;" + LF + "      enum  EnumValues {" + LF);

			Iterator<Entry<String, SM_ServiceInstance>> it = componentImplementation.getCompType().getReferenceInstances().entrySet().iterator();
			Integer serviceID = 0;

			while (it.hasNext()) {
				Map.Entry<String, SM_ServiceInstance> mapEntry = it.next();

				codeStringBuilder.append("         " + mapEntry.getKey() + " = " + serviceID + "," + LF);
				serviceID++;
			}

			codeStringBuilder.append("      };" + LF + "      inline void operator = (ECOA::uint32 i) { value = i; }" + LF + "      inline operator ECOA::uint32 () const { return value; }" + LF + "   };" + LF + LF);
		}
	}

	@Override
	public void writeIncludes() {
		String includeText = CLanguageSupport.generateIncludes(includeList);

		// Replace the #INCLUDES# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#INCLUDES#", includeText);
	}

}
