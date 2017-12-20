/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.platformgen.gen.serialiser;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
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

public class DeserialiserWriterC extends SourceFileWriter {
	private static final Logger LOGGER = LogManager.getLogger(DeserialiserWriterC.class);
	private static final String SEP_PATTERN_21 = "(bufferptr, &data->";
	private boolean isHeader;

	private ArrayList<String> includeList = new ArrayList<String>();
	private SystemModel systemModel;

	public DeserialiserWriterC(SystemModel systemModel, boolean isHeader, Path outputDir) {
		super(outputDir);
		this.systemModel = systemModel;
		this.isHeader = isHeader;

		setFileStructure();
	}

	@Override
	public void open() {
		if (isHeader) {
			super.openFile(outputDir.resolve("ELI_In__deserialiser.h"));
		} else {
			super.openFile(outputDir.resolve("ELI_In__deserialiser.c"));
		}
	}

	@Override
	protected void setFileStructure() {
		String fileStructure;
		fileStructure = "#PREAMBLE#" + LF + "#INCLUDES#" + LF + "#DESERIALISEFUNCS#" + LF;

		codeStringBuilder.append(fileStructure);

	}

	public void writeDeserialiseFunctions() {

		String funcText = "";

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

					funcText += "void deserialise_" + typeName + "(unsigned char **bufferptr, " + typeName + " *data)";

					if (isHeader) {
						funcText += ";" + LF;
					} else {
						funcText += LF + "{" + LF;

						if (obj instanceof SM_Base_Type) {
							SM_Base_Type baseType = (SM_Base_Type) obj;
							Integer size = baseType.getSize();

							if (size > 1) {
								funcText += "#ifdef LITTLE_ENDIAN" + LF + "   unsigned char *srcPtr = (unsigned char *)*bufferptr;" + LF + "   unsigned char *destPtr = (unsigned char *)data;" + LF;
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
							funcText += "   memcpy(data, *bufferptr, " + size.toString() + ");" + LF;
							if (size > 1) {
								funcText += "#endif" + LF;
							}
							funcText += "   *bufferptr += " + size.toString() + ";" + LF;
						} else if (obj instanceof SM_Simple_Type) {

							SM_Simple_Type simpleType = (SM_Simple_Type) obj;
							SM_Type typeType = simpleType.getType();
							String typetypeName = TypesProcessorC.convertParameterToC(typeType);

							funcText += "   deserialise_" + typetypeName + "(bufferptr, data);" + LF;
						} else if (obj instanceof SM_Enum_Type) {

							SM_Enum_Type enumType = (SM_Enum_Type) obj;
							SM_Type typeType = enumType.getType();
							String typetypeName = TypesProcessorC.convertParameterToC(typeType);

							funcText += "   deserialise_" + typetypeName + "(bufferptr, data);" + LF;
						} else if (obj instanceof SM_Record_Type) {
							SM_Record_Type recordType = (SM_Record_Type) obj;
							List<nameAndType> elements = recordType.getType();

							for (nameAndType element : elements) {
								SM_Type typeType = element.getReferencedType();
								String typetypeName = TypesProcessorC.convertParameterToC(typeType);

								funcText += "   deserialise_" + typetypeName + SEP_PATTERN_21 + element.getName() + ");" + LF;
							}
						} else if (obj instanceof SM_Variant_Record_Type) {
							SM_Variant_Record_Type recordType = (SM_Variant_Record_Type) obj;
							List<nameAndType> elements = recordType.getTypes();
							HashMap<String, List<nameAndType>> unions = recordType.getUnionTypes();

							funcText += "   /* selector...*/" + LF;
							SM_Type selectorType = recordType.getSelectorType().getReferencedType();
							String selectortypeName = TypesProcessorC.convertParameterToC(selectorType);

							funcText += "   deserialise_" + selectortypeName + SEP_PATTERN_21 + recordType.getSelectorType().getName() + ");" + LF + "   /* fields...*/" + LF;

							for (nameAndType element : elements) {
								SM_Type typeType = element.getReferencedType();
								String typetypeName = TypesProcessorC.convertParameterToC(typeType);

								funcText += "   deserialise_" + typetypeName + SEP_PATTERN_21 + element.getName() + ");" + LF;
							}
							funcText += "   /* unions...*/" + LF + "   switch (data->" + recordType.getSelectorType().getName() + ") {" + LF;
							Iterator<Entry<String, List<nameAndType>>> itUnions = unions.entrySet().iterator();
							while (itUnions.hasNext()) {

								Entry<String, List<nameAndType>> unionEntry = itUnions.next();

								switch (recordType.getSelectorTypeType()) {
								case NUMERIC:
									funcText += "      case " + unionEntry.getKey() + ":" + LF;
									break;
								case BOOLEAN:
									funcText += "      case " + "ECOA__" + unionEntry.getKey() + ":" + LF;
									break;
								case ENUM:
									funcText += "      case " + TypesProcessorC.convertParameterToC(recordType.getSelectorType().getReferencedType()) + "_" + unionEntry.getKey() + ":" + LF;
									break;
								case ILLEGAL:
									LOGGER.info("ERROR: Selector '" + recordType.getSelectorType().getName() + "' of type '" + recordType.getSelectorType().getReferencedType().getName() + "' is invalid for a union selector.");
									
								default:
									LOGGER.info("ERROR: Selector '" + recordType.getSelectorType().getName() + "' of type '" + recordType.getSelectorType().getReferencedType().getName() + "' is invalid for a union selector.");
									
								}
								for (nameAndType union : unionEntry.getValue()) {
									SM_Type typeType = union.getReferencedType();
									String typetypeName = TypesProcessorC.convertParameterToC(typeType);

									funcText += "         deserialise_" + typetypeName + "(bufferptr, &data->u_" + recordType.getSelectorType().getName() + "." + union.getName() + ");" + LF;
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
								funcText += "   memcpy(data, *bufferptr, " + num + ");" + LF + "   *bufferptr += " + num + ";" + LF;
							} else {
								funcText += "   for(i=0;i<" + num + ";i++)" + LF + "   {" + LF + "      deserialise_" + typetypeName + "(bufferptr, ((" + typetypeName + " *)data) + i);" + LF + "   }" + LF;
							}
						} else if (obj instanceof SM_Array_Type) {

							SM_Array_Type arrayType = (SM_Array_Type) obj;
							SM_Type typeType = arrayType.getType();
							String typetypeName = TypesProcessorC.convertParameterToC(typeType);

							funcText += "   int i;" + LF + LF + "   deserialise_ECOA__uint32(bufferptr, &data->current_size);" + LF;

							if (typeType instanceof SM_Base_Type && ((SM_Base_Type) typeType).getSize() == 1) {
								funcText += "   memcpy(&data->data, *bufferptr, data->current_size);" + LF + "   *bufferptr += data->current_size;" + LF;
							} else {
								funcText += "   for(i=0;i<data->current_size;i++)" + LF + "   {" + LF + "      deserialise_" + typetypeName + "(bufferptr, ((" + typetypeName + " *)data->data) + i);" + LF + "   }" + LF;
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

		// Replace the #DESERIALISEFUNCS# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#DESERIALISEFUNCS#", funcText);

	}

	public void writeIncludes() {
		if (isHeader) {

			Iterator<Entry<String, SM_Namespace>> itIncludes = systemModel.getTypes().getNamespaces().entrySet().iterator();
			while (itIncludes.hasNext()) {
				Entry<String, SM_Namespace> namespaceEntry = itIncludes.next();
				includeList.add(namespaceEntry.getValue().getName());
			}
		} else {
			includeList.add("ELI_In__deserialiser");
			includeList.add("string");
		}

		String includeText = CLanguageSupport.generateIncludes(includeList);

		// Replace the #INCLUDES# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#INCLUDES#", includeText);

	}

	public void writePreamble() {
		String preambleText = "";

		if (isHeader) {
			preambleText += "/* File ELI_In__deserialiser.h */" + LF;
		} else {
			preambleText += "/* File ELI_In__deserialiser.c */" + LF;
		}

		// Replace the #PREAMBLE# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#PREAMBLE#", preambleText);

	}

}
