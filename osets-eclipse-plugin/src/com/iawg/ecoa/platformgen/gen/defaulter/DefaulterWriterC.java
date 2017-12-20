/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.platformgen.gen.defaulter;

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

@SuppressWarnings("unused")
public class DefaulterWriterC extends SourceFileWriter {
	private static final Logger LOGGER = LogManager.getLogger(DefaulterWriterC.class);
	private static final String SEP_PATTERN_51 = "uint64";
	private static final String SEP_PATTERN_A = "      case ";
	private static final String SEP_PATTERN_B = "   default_";
	private static final String SEP_PATTERN_C = "int64";
	private static final String SEP_PATTERN_D = "(&ptr->";
	private static final String SEP_PATTERN_E = "   *ptr = ";
	private static final String SEP_PATTERN_F = "   deserialise_";
	private boolean isHeader;

	private ArrayList<String> includeList = new ArrayList<String>();
	private SystemModel systemModel;

	public DefaulterWriterC(SystemModel systemModel, boolean isHeader, Path outputDir) {
		super(outputDir);
		this.systemModel = systemModel;
		this.isHeader = isHeader;

		setFileStructure();
	}

	private String generateBaseType(String name, String minRange) {
		String baseTypeText = "";

		switch (name) {
		case "boolean8":
			if (minRange == null) {
				minRange = "ECOA__FALSE";
			}
			break;
		case "uint8":
			if (minRange == null) {
				minRange = "ECOA__UINT8_MIN";
			}
			break;
		case "byte":
			if (minRange == null) {
				minRange = "ECOA__BYTE_MIN";
			}
			break;
		case "uint16":
			if (minRange == null) {
				minRange = "ECOA__UINT16_MIN";
			}
			break;
		case "uint32":
			if (minRange == null) {
				minRange = "ECOA__UINT32_MIN";
			}
			break;
		case SEP_PATTERN_51:
			if (minRange == null) {
				minRange = "ECOA__UINT64_MIN";
			}
			break;
		case "int8":
			if (minRange == null) {
				minRange = "ECOA__INT8_MIN";
			}
			break;
		case "char8":
			if (minRange == null) {
				minRange = "ECOA__CHAR8_MIN";
			}
			break;
		case "int16":
			if (minRange == null) {
				minRange = "ECOA__INT16_MIN";
			}
			break;
		case "int32":
			if (minRange == null) {
				minRange = "ECOA__INT32_MIN";
			}
			break;
		case SEP_PATTERN_C:
			if (minRange == null) {
				minRange = "ECOA__INT64_MIN";
			}
			break;
		case "float32":
			if (minRange == null) {
				minRange = "ECOA__FLOAT32_MIN";
			}
			break;
		case "double64":
			if (minRange == null) {
				minRange = "ECOA__DOUBLE64_MIN";
			}
			break;
		default:
			break;
		}

		baseTypeText += SEP_PATTERN_E + minRange + ";";

		return minRange;

	}

	@Override
	public void open() {
		if (isHeader) {
			super.openFile(outputDir.resolve("Defaulter.h"));
		} else {
			super.openFile(outputDir.resolve("Defaulter.c"));
		}
	}

	@Override
	protected void setFileStructure() {
		// Set the file structure
		String fileStructure;
		fileStructure = "#PREAMBLE#" + LF + "#INCLUDES#" + LF + "#DEFAULTFUNCS#" + LF;

		codeStringBuilder.append(fileStructure);

	}

	public void writeDefaulterFunctions() {
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

						if (baseType.getName().equals(SEP_PATTERN_51) || baseType.getName().equals(SEP_PATTERN_C)) {
							funcText += "#if defined(ECOA_64BIT_SUPPORT)" + LF;
						}
					}

					funcText += "void default_" + typeName + "(" + typeName + " *ptr)";
					if (isHeader) {
						funcText += ";" + LF;
					} else {
						funcText += LF + "{" + LF;

						if (obj instanceof SM_Base_Type) {
							SM_Base_Type baseType = (SM_Base_Type) obj;

							funcText += SEP_PATTERN_E + generateBaseType(baseType.getName(), null) + ";" + LF;

						} else if (obj instanceof SM_Simple_Type) {

							SM_Simple_Type simpleType = (SM_Simple_Type) obj;
							SM_Type typeType = simpleType.getType();
							String minRange = simpleType.getMinRange();

							// Determine what the base type is
							while (!(typeType instanceof SM_Base_Type)) {
								SM_Simple_Type smtypeType = (SM_Simple_Type) typeType;
								if (minRange == null) {
									minRange = smtypeType.getMinRange();
								}
								typeType = smtypeType.getType();
							}

							SM_Base_Type btype = (SM_Base_Type) typeType;

							funcText += SEP_PATTERN_E + generateBaseType(btype.getName(), minRange) + ";" + LF;

						} else if (obj instanceof SM_Enum_Type) {

							SM_Enum_Type enumType = (SM_Enum_Type) obj;
							SM_Type typeType = enumType.getType();
							String typetypeName = TypesProcessorC.convertParameterToC(typeType);

							funcText += SEP_PATTERN_B + typetypeName + "(ptr);" + LF;
						} else if (obj instanceof SM_Record_Type) {
							SM_Record_Type recordType = (SM_Record_Type) obj;
							List<nameAndType> elements = recordType.getType();

							for (nameAndType element : elements) {
								SM_Type typeType = element.getReferencedType();
								String typetypeName = TypesProcessorC.convertParameterToC(typeType);

								funcText += SEP_PATTERN_B + typetypeName + SEP_PATTERN_D + element.getName() + ");" + LF;
							}
						} else if (obj instanceof SM_Variant_Record_Type) {
							SM_Variant_Record_Type recordType = (SM_Variant_Record_Type) obj;
							List<nameAndType> elements = recordType.getTypes();
							Map<String, List<nameAndType>> unions = recordType.getUnionTypes();

							funcText += "   /* selector...*/" + LF;
							SM_Type selectorType = recordType.getSelectorType().getReferencedType();
							String selectortypeName = TypesProcessorC.convertParameterToC(selectorType);

							// TODO - this is NOT correct, as it should be
							// setting the selector default to the lowest
							// numerical value for the type of the
							// selector (enum/numerical).
							// currently it just calls the defaulting operation
							// for the type, which may or may not be correct for
							// its use as a selector.
							funcText += SEP_PATTERN_B + selectortypeName + SEP_PATTERN_D + recordType.getSelectorType().getName() + ");" + LF + "   /* fields...*/" + LF;

							for (nameAndType element : elements) {
								SM_Type typeType = element.getReferencedType();
								String typetypeName = TypesProcessorC.convertParameterToC(typeType);

								funcText += SEP_PATTERN_B + typetypeName + SEP_PATTERN_D + element.getName() + ");" + LF;
							}
							funcText += "   /* unions...*/" + LF + "   switch (ptr->" + recordType.getSelectorType().getName() + ") {" + LF;
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

									funcText += "         default_" + typetypeName + "(&ptr->u_" + recordType.getSelectorType().getName() + "." + union.getName() + ");" + LF;
								}
								funcText += "         break;" + LF;
							}
							funcText += "      default:" + LF + "         break;" + LF + "   }" + LF;

						} else if (obj instanceof SM_Fixed_Array_Type) {

							SM_Fixed_Array_Type arrayType = (SM_Fixed_Array_Type) obj;
							SM_Type typeType = arrayType.getType();
							String typetypeName = TypesProcessorC.convertParameterToC(typeType);
							Integer num = arrayType.getMaxNumber();

							funcText += "   int i;" + LF + LF + "   for(i=0;i<" + num + ";i++)" + LF + "   {" + LF + "      default_" + typetypeName + "(((" + typetypeName + " *)ptr) + i);" + LF + "   }" + LF;
						} else if (obj instanceof SM_Array_Type) {

							SM_Array_Type arrayType = (SM_Array_Type) obj;
							SM_Type typeType = arrayType.getType();
							String typetypeName = TypesProcessorC.convertParameterToC(typeType);

							funcText += "   int i;" + LF + LF + "   default_ECOA__uint32(&ptr->current_size);" + LF + "   for(i=0;i<" + arrayType.getMaxNumber() + ";i++)" + LF + "   {" + LF + "      default_" + typetypeName + "(((" + typetypeName + " *)ptr->data) + i);" + LF + "   }" + LF;
						}

						funcText += "}" + LF + LF;

					}

					if (obj instanceof SM_Base_Type) {
						SM_Base_Type baseType = (SM_Base_Type) obj;

						if (baseType.getName().equals(SEP_PATTERN_51) || baseType.getName().equals(SEP_PATTERN_C)) {
							funcText += "#endif" + LF;
						}
					}

				}
			}
		}

		// Replace the #DEFAULTFUNCS# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#DEFAULTFUNCS#", funcText);

	}

	public void writeIncludes() {

		if (isHeader) {

			Iterator<Entry<String, SM_Namespace>> itIncludes = systemModel.getTypes().getNamespaces().entrySet().iterator();
			while (itIncludes.hasNext()) {
				Entry<String, SM_Namespace> namespaceEntry = itIncludes.next();
				includeList.add(namespaceEntry.getValue().getName());
			}
		} else {
			includeList.add("Defaulter");
			includeList.add("string");
		}

		String includeText = CLanguageSupport.generateIncludes(includeList);

		// Replace the #INCLUDES# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#INCLUDES#", includeText);
	}

	public void writePreamble() {
		String preambleText = "";

		if (isHeader) {
			preambleText += "/* File Defaulter.h */" + LF;
		} else {
			preambleText += "/* File Defaulter.c */" + LF;
		}

		// Replace the #PREAMBLE# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#PREAMBLE#", preambleText);
	}

}
