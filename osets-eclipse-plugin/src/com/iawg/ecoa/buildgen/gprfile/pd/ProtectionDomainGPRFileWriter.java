/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.buildgen.gprfile.pd;

import java.nio.file.Path;
import java.util.ArrayList;

import com.iawg.ecoa.SourceFileWriter;
import com.iawg.ecoa.platformgen.common.WriterSupport;
import com.iawg.ecoa.systemmodel.assembly.SM_ComponentInstance;
import com.iawg.ecoa.systemmodel.componentimplementation.SM_ComponentImplementation;
import com.iawg.ecoa.systemmodel.componentimplementation.SM_ModuleImpl;
import com.iawg.ecoa.systemmodel.deployment.SM_ProtectionDomain;

public class ProtectionDomainGPRFileWriter extends SourceFileWriter {
	private static final String SEP_PATTERN = "                            \"";
	private SM_ProtectionDomain pd;
	private String stepsDirString;

	public ProtectionDomainGPRFileWriter(Path outputDir, SM_ProtectionDomain pd, Path stepsDir) {
		super(outputDir);
		this.stepsDirString = outputDir.normalize().relativize(stepsDir).toString().replace("\\", "/");
		this.pd = pd;

		setFileStructure();
	}

	@Override
	public void open() {
		super.openFile(outputDir.resolve(pd.getName() + ".gpr"));

		// Create a directory for the build (OBJ_DIR)
		super.createDirectory(outputDir.resolve("build_" + pd.getName()));
	}

	@Override
	protected void setFileStructure() {
		// Set the file structure
		String fileStructure = "#LIBRARY_WITH#" + LF + "#PREAMBLE#" + LF + "#SOURCE_DIRS#" + LF + "#GENERIC_CONTENT#" + LF;

		codeStringBuilder.append(fileStructure);
	}

	public void writeGenericContent() {
		String genericText = "   for Languages use (\"C\");" + LF + "   for Object_Dir use \"build_" + pd.getName() + "\";" + LF + "   for Main use (\"main.c\");" + LF + LF +

				"   package Naming is" + LF + "      for Specification_Suffix (\"c\") use \".h\";" + LF + "      for Implementation_Suffix (\"c\") use \".c\";" + LF + "   end Naming;" + LF + LF +

				"   package Compiler is" + LF;

		if (pd.getLogicalComputingNode().isLittleEndian()) {
			genericText += "      for Default_Switches (\"c\") use (\"-g\", \"-DECOA_64BIT_SUPPORT\", \"-DLITTLE_ENDIAN\"";
		} else {
			genericText += "      for Default_Switches (\"c\") use (\"-g\", \"-DECOA_64BIT_SUPPORT\"";
		}
		// If the module is prebuilt, add an include to the module directory so
		// we can reference the container header and user context.
		ArrayList<SM_ComponentImplementation> processedCompImplList = new ArrayList<SM_ComponentImplementation>();

		for (SM_ComponentInstance compInst : pd.getComponentInstances()) {
			SM_ComponentImplementation compImpl = compInst.getImplementation();

			// Only generate a with if not already generated for this
			// implementation.
			if (!processedCompImplList.contains(compImpl)) {
				for (SM_ModuleImpl modImpl : compImpl.getModuleImplementations().values()) {
					// Only add include if the module is prebuilt
					if (modImpl.isPrebuilt()) {
						// TODO - note for some reason this appears to be from
						// the "build" directory, so have to go up an extra
						// directory (hence the extra "../")
						genericText += "," + LF + "         \"-I../" + stepsDirString + "/4-ComponentImplementations/" + compImpl.getContainingDir().getName(compImpl.getContainingDir().getNameCount() - 1).toString() + "/" + modImpl.getName() + "/\"";
					}
				}
			}
		}

		genericText += ");" + LF + "   end Compiler;" + LF + LF +

				"   package Linker is" + LF + "      case Build is" + LF + "         when \"Standalone\" =>" + LF + "            for Default_Switches (\"c\") use (\"-lpthread\", \"-lrt\", \"-lm\"";

		// Add any libraries/objects that need to be linked (if prebuilt module)
		processedCompImplList.clear();
		for (SM_ComponentInstance compInst : pd.getComponentInstances()) {
			SM_ComponentImplementation compImpl = compInst.getImplementation();

			// Only generate a with if not already generated for this
			// implementation.
			if (!processedCompImplList.contains(compImpl)) {
				for (SM_ModuleImpl modImpl : compImpl.getModuleImplementations().values()) {
					// Only add libraries/objects if module is prebuilt.
					if (modImpl.isPrebuilt()) {
						for (String objectFile : modImpl.getObjectFileList()) {
							genericText += "," + LF + SEP_PATTERN + stepsDirString + "/" + modImpl.getPrebuiltObjLocation() + "/" + objectFile + "\"";
						}
					}
				}
			}
		}

		genericText += "," + LF + "                            \"./build_" + pd.getName() + "/lib" + pd.getName().toLowerCase() + ".a\");" + LF + "         when \"Aggregate\" =>" + LF + "            for Default_Switches (\"c\") use (\"-lpthread\", \"-lrt\", \"-lm\"";

		// Add any libraries/objects that need to be linked (if prebuilt module)
		processedCompImplList.clear();
		for (SM_ComponentInstance compInst : pd.getComponentInstances()) {
			SM_ComponentImplementation compImpl = compInst.getImplementation();

			// Only generate a with if not already generated for this
			// implementation.
			if (!processedCompImplList.contains(compImpl)) {
				for (SM_ModuleImpl modImpl : compImpl.getModuleImplementations().values()) {
					// Only add libraries/objects if module is prebuilt.
					if (modImpl.isPrebuilt()) {
						for (String objectFile : modImpl.getObjectFileList()) {
							genericText += "," + LF + SEP_PATTERN + stepsDirString + "/" + modImpl.getPrebuiltObjLocation() + "/" + objectFile + "\"";
						}
					}
				}
			}
		}

		genericText += "," + LF + SEP_PATTERN + pd.getLogicalComputingNode().getLogicalComputingPlatform().getName() + "/" + pd.getLogicalComputingNode().getName() + "/" + pd.getName() + "/build_" + pd.getName() + "/lib" + pd.getName().toLowerCase() + ".a\");" + LF + "      end case;" + LF + "   end Linker;" + LF + LF +

				"end " + pd.getName() + ";" + LF;

		// Replace the #GENERIC_CONTENT# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#GENERIC_CONTENT#", genericText);
	}

	public void writePreamble() {
		String preambleText = "-- Generated GPR Build file for " + pd.getName() + LF + LF +

				"project " + pd.getName() + " is" + LF + LF +

				"   type Build_Type is (\"Aggregate\", \"Standalone\");" + LF + "   Build : Build_Type := external (\"Build_Type\", \"Standalone\");" + LF;

		// Replace the #PREAMBLE# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#PREAMBLE#", preambleText);
	}

	public void writeSourceDirs() {
		String sourceDirsText = "   for Source_Dirs use (\"./**\"," + LF + "      \"../inc-gen/**\"," + LF + "      \"../src-gen/**\"," + LF + "      \"../../include/**\"," + LF + "      \"../../../include/**\"," + LF + "      \"../../../src/**\");" + LF;

		// Replace the #SOURCE_DIRS# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#SOURCE_DIRS#", sourceDirsText);
	}

	public void writeModuleLibraryWiths() {
		String libraryWithText = "";

		// Add source directories for component code
		ArrayList<SM_ComponentImplementation> processedCompImplList = new ArrayList<SM_ComponentImplementation>();

		for (SM_ComponentInstance compInst : pd.getComponentInstances()) {
			SM_ComponentImplementation compImpl = compInst.getImplementation();

			// Only generate a with if not already generated for this
			// implementation.
			if (!processedCompImplList.contains(compImpl)) {
				for (SM_ModuleImpl modImpl : compImpl.getModuleImplementations().values()) {
					// Only include if the module is NOT prebuilt
					if (!modImpl.isPrebuilt()) {
						libraryWithText += "with \"" + stepsDirString + "/4-ComponentImplementations/" + compImpl.getContainingDir().getName(compImpl.getContainingDir().getNameCount() - 1).toString() + "/" + modImpl.getName() + "/" + modImpl.getName() + ".gpr\";" + LF;
					}
				}
				processedCompImplList.add(compImpl);
			}
		}

		// Replace the #LIBRARY_WITH# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#LIBRARY_WITH#", libraryWithText);
	}

}
