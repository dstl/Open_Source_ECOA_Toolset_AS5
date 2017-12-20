/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.platformgen.gen.serialiser;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.iawg.ecoa.CLanguageSupport;
import com.iawg.ecoa.SourceFileWriter;
import com.iawg.ecoa.TypesProcessorC;
import com.iawg.ecoa.platformgen.common.WriterSupport;
import com.iawg.ecoa.systemmodel.SystemModel;
import com.iawg.ecoa.systemmodel.types.SM_Array_Type;
import com.iawg.ecoa.systemmodel.types.SM_Base_Type;
import com.iawg.ecoa.systemmodel.types.SM_Constant_Type;
import com.iawg.ecoa.systemmodel.types.SM_Enum_Type;
import com.iawg.ecoa.systemmodel.types.SM_Fixed_Array_Type;
import com.iawg.ecoa.systemmodel.types.SM_Namespace;
import com.iawg.ecoa.systemmodel.types.SM_Record_Type;
import com.iawg.ecoa.systemmodel.types.SM_Simple_Type;
import com.iawg.ecoa.systemmodel.types.SM_Type;
import com.iawg.ecoa.systemmodel.types.SM_Type.nameAndType;
import com.iawg.ecoa.systemmodel.types.SM_Variant_Record_Type;

public class SerialiserWriterC extends SourceFileWriter {
	private static final Logger LOGGER = LogManager.getLogger(SerialiserWriterC.class);
	private static final String SEP_PATTERN_31 = ", bufferptr);";
	private static final String SEP_PATTERN_A = "      case ";
	private static final String SEP_PATTERN_B = "(data.";
	private static final String SEP_PATTERN_C = "   serialise_";

	private boolean isHeader;

	private ArrayList<String> includeList = new ArrayList<String>();
	private SystemModel systemModel;

	public SerialiserWriterC(SystemModel systemModel, boolean isHeader, Path outputDir) {
		super(outputDir);
		this.systemModel = systemModel;
		this.isHeader = isHeader;

		setFileStructure();
	}

	@Override
	public void open() {
		if (isHeader) {
			super.openFile(outputDir.resolve("ELI_Out__serialiser.h"));
		} else {
			super.openFile(outputDir.resolve("ELI_Out__serialiser.c"));
		}
	}

	@Override
	protected void setFileStructure() {
		// Set the file structure
		String fileStructure = "#PREAMBLE#" + LF + "#INCLUDES#" + LF + "#SERIALISEFUNCS#" + LF;

		codeStringBuilder.append(fileStructure);
	}

	public void writeIncludes() {
		if (isHeader) {

			Iterator<Entry<String, SM_Namespace>> itIncludes = systemModel.getTypes().getNamespaces().entrySet().iterator();
			while (itIncludes.hasNext()) {
				Entry<String, SM_Namespace> namespaceEntry = itIncludes.next();
				includeList.add(namespaceEntry.getValue().getName());
			}
		} else {
			includeList.add("ELI_Out__serialiser");
			includeList.add("string");
		}

		String includeText = CLanguageSupport.generateIncludes(includeList);

		// Replace the #INCLUDES# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#INCLUDES#", includeText);

	}

	public void writePreamble() {
		String preambleText = "";

		if (isHeader) {
			preambleText += "/* File ELI_Out__serialiser.h */" + LF;
		} else {
			preambleText += "/* File ELI_Out__serialiser.c */" + LF;
		}

		// Replace the #PREAMBLE# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#PREAMBLE#", preambleText);

	}

	public void writeSerialiseFunctions() {

		String funcText = "";

		// Now Generate code
		Iterator<Entry<String, SM_Namespace>> itNamespace = systemModel.getTypes().getNamespaces().entrySet().iterator();
		while (itNamespace.hasNext()) {
			Entry<String, SM_Namespace> namespaceEntry = itNamespace.next();

			Iterator<Entry<String, SM_Type>> itType = namespaceEntry.getValue().getTypes().entrySet().iterator();
			while (itType.hasNext()) {
				Entry<String, SM_Type> type = itType.next();
				if (!(type.getValue() instanceof SM_Constant_Type)) {
					String typeName = TypesProcessorC.convertParameterToC(type.getValue());

					Object obj = type.getValue();

					if (obj instanceof SM_Base_Type) {
						SM_Base_Type baseType = (SM_Base_Type) obj;

						if (baseType.getName().equals("uint64") || baseType.getName().equals("int64")) {
							funcText += "#if defined(ECOA_64BIT_SUPPORT)" + LF;
						}
					}

					funcText += "void serialise_" + typeName + "(" + typeName + " data, unsigned char **bufferptr)";
					if (isHeader) {
						funcText += ";" + LF;
					} else {
						funcText += LF + "{" + LF;

						if (obj instanceof SM_Base_Type) {
							SM_Base_Type baseType = (SM_Base_Type) obj;
							Integer size = baseType.getSize();

							if (size > 1) {
								funcText += "#ifdef LITTLE_ENDIAN" + LF + "   unsigned char *srcPtr = (unsigned char *)&data;" + LF + "   unsigned char *destPtr = (unsigned char *)*bufferptr;" + LF;
								switch (size) {
								case 2:
									funcText += "   destPtr[0] = srcPtr[1];" + LF + "   destPtr[1] = srcPtr[0];" + LF;
									break;
								case 4:
									funcText += "   destPtr[0] = srcPtr[3];" + LF + "   destPtr[1] = srcPtr[2];" + LF + "   destPtr[2] = srcPtr[1];" + LF + "   destPtr[3] = srcPtr[0];" + LF;
									break;
								case 8:
									funcText += "   destPtr[0] = srcPtr[7];" + LF + "   destPtr[1] = srcPtr[6];" + LF + "   destPtr[2] = srcPtr[5];" + LF + "   destPtr[3] = srcPtr[4];" + LF + "   destPtr[4] = srcPtr[3];" + LF + "   destPtr[5] = srcPtr[2];" + LF + "   destPtr[6] = srcPtr[1];" + LF + "   destPtr[7] = srcPtr[0];" + LF;
									break;
								default:
									break;
								}
								funcText += "#else" + LF;
							}
							funcText += "   memcpy( *bufferptr, &data, " + size.toString() + ");" + LF;
							if (size > 1) {
								funcText += "#endif" + LF;
							}
							funcText += "   *bufferptr += " + size.toString() + ";" + LF + LF;

						} else if (obj instanceof SM_Simple_Type) {

							SM_Simple_Type simpleType = (SM_Simple_Type) obj;
							SM_Type typeType = simpleType.getType();
							String typetypeName = TypesProcessorC.convertParameterToC(typeType);

							funcText += SEP_PATTERN_C + typetypeName + "(data, bufferptr);" + LF;
						} else if (obj instanceof SM_Enum_Type) {

							SM_Enum_Type enumType = (SM_Enum_Type) obj;
							SM_Type typeType = enumType.getType();
							String typetypeName = TypesProcessorC.convertParameterToC(typeType);

							funcText += SEP_PATTERN_C + typetypeName + "(data, bufferptr);" + LF;
						} else if (obj instanceof SM_Record_Type) {
							SM_Record_Type recordType = (SM_Record_Type) obj;
							List<nameAndType> elements = recordType.getType();

							for (nameAndType element : elements) {
								SM_Type typeType = element.getReferencedType();
								String typetypeName = TypesProcessorC.convertParameterToC(typeType);

								funcText += SEP_PATTERN_C + typetypeName + SEP_PATTERN_B + element.getName() + SEP_PATTERN_31 + LF;
							}
						} else if (obj instanceof SM_Variant_Record_Type) {
							SM_Variant_Record_Type recordType = (SM_Variant_Record_Type) obj;
							List<nameAndType> elements = recordType.getTypes();
							Map<String, List<nameAndType>> unions = recordType.getUnionTypes();

							funcText += "   /* selector...*/" + LF;
							SM_Type selectorType = recordType.getSelectorType().getReferencedType();
							String selectortypeName = TypesProcessorC.convertParameterToC(selectorType);

							funcText += SEP_PATTERN_C + selectortypeName + SEP_PATTERN_B + recordType.getSelectorType().getName() + SEP_PATTERN_31 + LF + "   /* fields...*/" + LF;

							for (nameAndType element : elements) {
								SM_Type typeType = element.getReferencedType();
								String typetypeName = TypesProcessorC.convertParameterToC(typeType);

								funcText += SEP_PATTERN_C + typetypeName + SEP_PATTERN_B + element.getName() + SEP_PATTERN_31 + LF;
							}
							funcText += "   /* unions...*/" + LF + "   switch (data." + recordType.getSelectorType().getName() + ") {" + LF;

							Iterator<Entry<String, List<nameAndType>>> itUnions = unions.entrySet().iterator();
							while (itUnions.hasNext()) {

								Entry<String, List<nameAndType>> unionEntry = itUnions.next();

								switch (recordType.getSelectorTypeType()) {
								case NUMERIC:
									funcText += SEP_PATTERN_A + unionEntry.getKey() + ":" + LF;
									break;
								case BOOLEAN:
									funcText += SEP_PATTERN_A + "ECOA__" + unionEntry.getKey() + ":" + LF;
									break;
								case ENUM:
									funcText += SEP_PATTERN_A + TypesProcessorC.convertParameterToC(recordType.getSelectorType().getReferencedType()) + "_" + unionEntry.getKey() + ":" + LF;
									break;
								case ILLEGAL:
									LOGGER.info("ERROR: Selector '" + recordType.getSelectorType().getName() + "' of type '" + recordType.getSelectorType().getReferencedType().getName() + "' is invalid for a union selector.");
									
								default:
									LOGGER.info("ERROR: Selector '" + recordType.getSelectorType().getName() + "' of type '" + recordType.getSelectorType().getReferencedType().getName() + "' is invalid for a union selector.");
									
								}
								for (nameAndType union : unionEntry.getValue()) {
									SM_Type typeType = union.getReferencedType();
									String typetypeName = TypesProcessorC.convertParameterToC(typeType);

									funcText += "         serialise_" + typetypeName + "(data.u_" + recordType.getSelectorType().getName() + "." + union.getName() + SEP_PATTERN_31 + LF;
								}
								funcText += "         break;" + LF;
							}
							funcText += "      default:" + LF + "         break;" + LF + "   }" + LF;

						} else if (obj instanceof SM_Fixed_Array_Type) {

							SM_Fixed_Array_Type arrayType = (SM_Fixed_Array_Type) obj;
							SM_Type typeType = arrayType.getType();
							String typetypeName = TypesProcessorC.convertParameterToC(typeType);
							Integer num = arrayType.getMaxNumber();

							funcText += "   int i;" + LF + LF;

							if (typeType instanceof SM_Base_Type && ((SM_Base_Type) typeType).getSize() == 1) {
								funcText += "   memcpy( *bufferptr, data, " + num + ");" + LF + "   *bufferptr += " + num + ";" + LF;

							} else {
								funcText += "   for(i=0;i<" + num + ";i++)" + LF + "   {" + LF + "      serialise_" + typetypeName + "(data[i], bufferptr);" + LF + "   }" + LF;
							}
						} else if (obj instanceof SM_Array_Type) {

							SM_Array_Type arrayType = (SM_Array_Type) obj;
							SM_Type typeType = arrayType.getType();
							String typetypeName = TypesProcessorC.convertParameterToC(typeType);

							funcText += "   int i;" + LF + LF + "   serialise_ECOA__uint32(data.current_size, bufferptr);" + LF;

							if (typeType instanceof SM_Base_Type && ((SM_Base_Type) typeType).getSize() == 1) {
								funcText += "   memcpy( *bufferptr, &data.data, data.current_size);" + LF + "   *bufferptr += data.current_size;" + LF;

							} else {
								funcText += "   for(i=0;i<data.current_size;i++)" + LF + "   {" + LF + "      serialise_" + typetypeName + "(data.data[i], bufferptr);" + LF + "   }" + LF;
							}
						}

						funcText += "}" + LF + LF;

					}

					if (obj instanceof SM_Base_Type) {
						SM_Base_Type baseType = (SM_Base_Type) obj;

						if (baseType.getName().equals("uint64") || baseType.getName().equals("int64")) {
							funcText += "#endif" + LF;
						}
					}

				}
			}
		}

		// Replace the #SERIALISEFUNCS# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#SERIALISEFUNCS#", funcText);

	}

}
