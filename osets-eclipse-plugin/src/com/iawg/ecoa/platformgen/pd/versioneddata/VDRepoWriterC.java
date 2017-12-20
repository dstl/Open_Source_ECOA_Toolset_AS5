/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.platformgen.pd.versioneddata;

import java.nio.file.Path;
import java.util.ArrayList;

import com.iawg.ecoa.CLanguageSupport;
import com.iawg.ecoa.SourceFileWriter;
import com.iawg.ecoa.platformgen.common.WriterSupport;
import com.iawg.ecoa.platformgen.common.underlyingplatform.Generic_Platform;
import com.iawg.ecoa.systemmodel.assembly.SM_ComponentInstance;
import com.iawg.ecoa.systemmodel.componentimplementation.SM_ModuleInstance;
import com.iawg.ecoa.systemmodel.componentimplementation.SM_VDRepository;
import com.iawg.ecoa.systemmodel.componentimplementation.links.data.SM_ReaderInterface;
import com.iawg.ecoa.systemmodel.componentimplementation.links.data.SM_ReaderModuleInstance;
import com.iawg.ecoa.systemmodel.componentimplementation.links.data.SM_WriterInterface;
import com.iawg.ecoa.systemmodel.componentimplementation.modoperation.data.SM_DataReadOp;
import com.iawg.ecoa.systemmodel.deployment.SM_ProtectionDomain;

public class VDRepoWriterC extends SourceFileWriter {
	private static final String SEP_PATTERN_81 = "ECOA__return_status ";
	private SM_VDRepository vdRepo;
	private boolean isHeader;
	private Generic_Platform underlyingPlatform;
	private SM_ComponentInstance compInst;
	private String vdRepoName = "";
	private ArrayList<String> includeList = new ArrayList<String>();

	public VDRepoWriterC(boolean isHeader, Path outputDir, SM_VDRepository vdRepo, Generic_Platform underlyingPlatform, SM_ProtectionDomain protectionDomain, SM_ComponentInstance compInst) {
		super(outputDir);
		this.isHeader = isHeader;
		this.vdRepo = vdRepo;
		this.underlyingPlatform = underlyingPlatform;
		this.compInst = compInst;

		this.vdRepoName = protectionDomain.getName() + "_" + compInst.getName() + "_VD" + vdRepo.getName();

		setFileStructure();
	}

	private String generateReaderWriterFileHeaderComment() {
		String headerCommentString = "/* Readers: */" + LF;
		for (SM_ReaderInterface reader : vdRepo.getReaders()) {
			headerCommentString += "/* " + reader.getReaderInst().getName() + " " + reader.getReaderOp().getName() + " */" + LF;
		}
		headerCommentString += LF + "/* Writers: */" + LF;
		for (SM_WriterInterface writer : vdRepo.getWriters()) {
			headerCommentString += "/* " + writer.getWriterInst().getName() + "_" + writer.getWriterOp().getName() + " */" + LF;
		}
		return headerCommentString;
	}

	@Override
	public void open() {
		if (isHeader) {
			super.openFile(outputDir.resolve("versioned_data/inc/" + vdRepoName + ".h"));
		} else {
			super.openFile(outputDir.resolve("versioned_data/" + vdRepoName + ".c"));
		}
	}

	@Override
	protected void setFileStructure() {
		String fileStructure = "";

		if (isHeader) {
			fileStructure = "#PREAMBLE#" + LF + "#INCLUDES#" + LF + "#DATA_TYPE_DECL#" + LF + "#READ_DATA#" + LF + "#WRITE_DATA#" + LF + "#INITIALISE#" + LF;
		} else {
			fileStructure = "#PREAMBLE#" + LF + "#INCLUDES#" + LF + "#DATA_TYPE_DECL#" + LF + "#SEMAPHORE_DECL#" + LF + "#FIRST_TIME_FLAG#" + LF + "#READ_DATA#" + LF + "#WRITE_DATA#" + LF + "#INITIALISE#" + LF;
		}

		codeStringBuilder.append(fileStructure);

	}

	public void writeDataTypeDecl() {
		String typeDeclText = "";

		if (isHeader) {
			typeDeclText += "typedef struct {" + LF + "ECOA__timestamp dataTimestamp;" + LF + vdRepo.getDataTypeName() + " data;" + LF + "}" + vdRepoName + "_DataType;" + LF;

			// Include the type file.
			includeList.add(vdRepo.getDataType().getNamespace().getName());

		} else {
			typeDeclText += vdRepoName + "_DataType " + vdRepoName + "_Data;" + LF;
		}

		// Replace the #DATA_TYPE_DECL# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#DATA_TYPE_DECL#", typeDeclText);
	}

	public void writeFirstTimeDecl() {
		String firstTimeFlagText = "static ECOA__boolean8 " + vdRepoName + "_FirstWrite = ECOA__TRUE;" + LF;

		// Replace the #FIRST_TIME_FLAG# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#FIRST_TIME_FLAG#", firstTimeFlagText);
	}

	public void writeIncludes() {
		includeList.addAll(underlyingPlatform.addIncludesVDRepo());

		String includeText = CLanguageSupport.generateIncludes(includeList);

		// Replace the #INCLUDES# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#INCLUDES#", includeText);
	}

	public void writeInitialise() {
		String initialiseText = "";

		String semaphoreID = vdRepoName + "_SemaphoreID";

		if (isHeader) {
			initialiseText += "void " + vdRepoName + "__Initialise();" + LF + LF;
		} else {
			includeList.add("Defaulter");

			initialiseText += "void " + vdRepoName + "__Initialise()" + LF + "{" + LF + underlyingPlatform.generateCreateSemaphoreAttributes() + underlyingPlatform.generateCreateSemphore(1, 1, semaphoreID) + underlyingPlatform.checkCreateSemphoreStatus(semaphoreID) + LF + "   default_ECOA__timestamp(&" + vdRepoName + "_Data.dataTimestamp);" + LF + "   default_" + vdRepo.getDataTypeName() + "(&" + vdRepoName + "_Data.data);" + LF + "}" + LF + LF;
		}

		// Replace the #INITIALISE# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#INITIALISE#", initialiseText);

	}

	public void writePreamble() {
		String preambleText = "";

		if (isHeader) {
			preambleText += "/* File " + vdRepoName + ".h */" + LF + "/* This is the versioned data interface for " + vdRepoName + " */" + LF + LF + generateReaderWriterFileHeaderComment();
		} else {
			preambleText += "/* File " + vdRepoName + ".c */" + LF + "/* This is the versioned data implementation for " + vdRepoName + " */" + LF + LF + generateReaderWriterFileHeaderComment();

			// Add include
			includeList.add(vdRepoName);
		}

		// Replace the #PREAMBLE# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#PREAMBLE#", preambleText);
	}

	public void writeReadFunction() {
		String readFunctionText = "";

		if (isHeader) {
			readFunctionText += SEP_PATTERN_81 + vdRepoName + "__Read(" + vdRepo.getDataTypeName() + " *data, ECOA__timestamp *timestamp);" + LF + LF;
		} else {
			readFunctionText += SEP_PATTERN_81 + vdRepoName + "__Read(" + vdRepo.getDataTypeName() + " *data, ECOA__timestamp *timestamp)" + LF + "{" + LF + underlyingPlatform.generateWaitForSemaphoreAttributes() + underlyingPlatform.generatePostSemaphoreAttributes() + underlyingPlatform.generateWaitForSemaphore(vdRepoName + "_SemaphoreID") + underlyingPlatform.checkWaitForSemaphoreStatusCALL_ONLY() + "   {" + LF + LF + "      *timestamp = " + vdRepoName + "_Data.dataTimestamp;" + LF + "      memcpy(data, &" + vdRepoName + "_Data.data, sizeof(" + vdRepo.getDataTypeName() + "));" + LF + LF +

					"   " + underlyingPlatform.generatePostSemaphore(vdRepoName + "_SemaphoreID") + "   " + underlyingPlatform.checkPostSemaphoreStatus(vdRepoName + "_SemaphoreID") + "   }" + LF + "   else" + LF + "   {" + LF + "      printf(\"ERROR - getting " + vdRepoName + "_SemaphoreID\\n\");" + LF + "   }" + LF + LF +

					"   if (" + vdRepoName + "_FirstWrite == ECOA__TRUE)" + LF + "   {" + LF + "      return ECOA__return_status_DATA_NOT_INITIALIZED;" + LF + "   }" + LF + "   else" + LF + "   {" + LF + "      return ECOA__return_status_OK;" + LF + "   }" + LF +

					"}" + LF + LF;
		}

		// Replace the #READ_DATA# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#READ_DATA#", readFunctionText);
	}

	public void writeSemaphoreIDDecl() {
		String semaphoreIDDeclText = "static int " + vdRepoName + "_SemaphoreID;" + LF;

		// Replace the #SEMAPHORE_DECL# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#SEMAPHORE_DECL#", semaphoreIDDeclText);
	}

	public void writeWriteFunction() {
		String writeFunctionText = "";

		if (isHeader) {
			writeFunctionText += SEP_PATTERN_81 + vdRepoName + "__Write(" + vdRepo.getDataTypeName() + " *data, ECOA__timestamp *timestamp);" + LF + LF;
		} else {
			writeFunctionText += SEP_PATTERN_81 + vdRepoName + "__Write(" + vdRepo.getDataTypeName() + " *data, ECOA__timestamp *timestamp)" + LF + "{" + LF + underlyingPlatform.generateWaitForSemaphoreAttributes() + underlyingPlatform.generatePostSemaphoreAttributes() + underlyingPlatform.generateWaitForSemaphore(vdRepoName + "_SemaphoreID") + underlyingPlatform.checkWaitForSemaphoreStatusCALL_ONLY() + "   {" + LF + LF +

					"      " + vdRepoName + "_Data.dataTimestamp = *timestamp;" + LF + "      memcpy( &" + vdRepoName + "_Data.data, data, sizeof(" + vdRepo.getDataTypeName() + "));" + LF + LF +

					"   " + underlyingPlatform.generatePostSemaphore(vdRepoName + "_SemaphoreID") + "   " + underlyingPlatform.checkPostSemaphoreStatus(vdRepoName + "_SemaphoreID") + "   }" + LF + "   else" + LF + "   {" + LF + "      printf(\"ERROR - getting " + vdRepoName + "_SemaphoreID\\n\");" + LF + "   }" + LF + LF +

					"   if (" + vdRepoName + "_FirstWrite == ECOA__TRUE)" + LF + "   {" + LF + "      // Set first write flag to false" + LF + "      " + vdRepoName + "_FirstWrite = ECOA__FALSE;" + LF + "   }" + LF + LF;

			for (SM_ReaderInterface reader : vdRepo.getReaders()) {
				if (reader instanceof SM_ReaderModuleInstance) {
					SM_ModuleInstance readerModInst = (SM_ModuleInstance) reader.getReaderInst();

					// Determine if it's a notifying operation..
					for (SM_DataReadOp dataRead : readerModInst.getModuleType().getDataReadOps()) {
						if (dataRead.getName().equals(reader.getReaderOp().getName())) {
							if (dataRead.getIsNotifying()) {
								// TODO - should we be passing the data in?!
								writeFunctionText += "   /* Call the Module Instance Queue Operation for notification */" + LF + "   " + compInst.getName() + "_" + reader.getReaderInst().getName() + "_Controller__" + reader.getReaderOp().getName() + "__update();" + LF;

							}
						}
					}
				}
			}

			writeFunctionText += "   return ECOA__return_status_OK;" + LF + "}" + LF + LF;
		}

		// Replace the #WRITE_DATA# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#WRITE_DATA#", writeFunctionText);
	}

}
