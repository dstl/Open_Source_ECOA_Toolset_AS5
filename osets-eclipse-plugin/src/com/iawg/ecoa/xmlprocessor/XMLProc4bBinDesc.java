/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.xmlprocessor;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.iawg.ecoa.jaxbclasses.step4bBinDesc.BinDesc;
import com.iawg.ecoa.jaxbclasses.step4bBinDesc.BinaryModule;
import com.iawg.ecoa.systemmodel.SystemModel;
import com.iawg.ecoa.systemmodel.componentimplementation.SM_ComponentImplementation;
import com.iawg.ecoa.systemmodel.componentimplementation.SM_ModuleImpl;

/**
 * 
 * @author Daniel.Clarke
 *
 */
public class XMLProc4bBinDesc {
	private static final Logger LOGGER = LogManager.getLogger(XMLProc4bBinDesc.class);
	private static List<ECOAFile> binDescFiles = new ArrayList<ECOAFile>();

	public void parseFile(Path binDescFile) {
		XMLFileProcessor pxfp = new XMLFileProcessor("ecoa-bin-desc-1.0.xsd", "com.iawg.ecoa.jaxbclasses.step4bBinDesc");

		BinDesc binDesc = (BinDesc) pxfp.parseFile(binDescFile);
		binDescFiles.add(new ECOAFile(binDescFile, binDesc));
	}

	public void updateSystemModel(SystemModel systemModel) {
		for (ECOAFile bdFile : binDescFiles) {
			BinDesc bd = (BinDesc) bdFile.getObject();

			SM_ComponentImplementation compImpl = systemModel.getComponentImplementations().getImplementationByName(bd.getComponentImplementation());

			for (BinaryModule binModule : bd.getBinaryModules()) {
				SM_ModuleImpl modImpl = compImpl.getModuleImplementationByName(binModule.getReference());

				if (modImpl == null) {
					LOGGER.info("ERROR: " + compImpl.getName() + " bin-desc Module reference not found - " + binModule.getReference().toString());
					
				} else {
					modImpl.addObjectFile(binModule.getObject());
				}
			}
		}
	}

}
