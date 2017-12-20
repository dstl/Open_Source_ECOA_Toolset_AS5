/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.apigen.types;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.iawg.ecoa.systemmodel.types.SM_Array_Type;
import com.iawg.ecoa.systemmodel.types.SM_Constant_Type;
import com.iawg.ecoa.systemmodel.types.SM_Enum_Type;
import com.iawg.ecoa.systemmodel.types.SM_Enum_Type.SM_Enum_Value;
import com.iawg.ecoa.systemmodel.types.SM_Fixed_Array_Type;
import com.iawg.ecoa.systemmodel.types.SM_Namespace;
import com.iawg.ecoa.systemmodel.types.SM_Record_Type;
import com.iawg.ecoa.systemmodel.types.SM_Simple_Type;
import com.iawg.ecoa.systemmodel.types.SM_Type;
import com.iawg.ecoa.systemmodel.types.SM_Type.nameAndType;
import com.iawg.ecoa.systemmodel.types.SM_Variant_Record_Type;

/**
 * This class extends the abstract class ContainerTypesWriter and implements the
 * methods of that class in a way that is specific to the C language.
 * 
 *
 */
public class TypesWriterAda extends TypesWriter {
	private static final String SEP_PATTERN_101 = "uint8";
	private static final String SEP_PATTERN_A = "char8";
	private static final String SEP_PATTERN_B = "uint16";
	private static final String SEP_PATTERN_C = "uint32";
	private static final String SEP_PATTERN_D = "type ";
	private static final String SEP_PATTERN_E = "boolean8";
	private static final String SEP_PATTERN_F = "   end record;";
	private static final String SEP_PATTERN_G = "int64";
	private static final String SEP_PATTERN_H = "   record";
	private static final String SEP_PATTERN_I = "int32";
	private static final String SEP_PATTERN_J = "int16";
	private static HashMap<String, String> typeConverter;

	public static String convertTypeToAda(SM_Type smType, SM_Namespace currentNamespace) {
		// This function converts a type name (which may include a namespace
		// name) into a C name, including the apporpriate namespace

		String ret = null;
		// See if type name includes a namespace identifier
		String delimiter = ":";
		String parts[] = smType.getName().split(delimiter);
		if (parts.length == 2) {
			// The type name contained a namespace name (i.e. this type is
			// coming from another namespace)

			ret = parts[0] + // The namespace name
					"." + parts[1]; // The type name
		} else {
			// The type name did not contain a namespace name. It is either a
			// type from the predefined "ECOA" namespace or it is a type from
			// the current namespace. If the name is one of the standard types
			// we will append the "ECOA__" namespace name, otherwise we will
			// append the current namespace name.

			ret = parts[0]; // The type name

			if (ret.equals(SEP_PATTERN_E) || ret.equals("int8") || ret.equals(SEP_PATTERN_A) || ret.equals("byte") || ret.equals(SEP_PATTERN_J) || ret.equals(SEP_PATTERN_I) || ret.equals(SEP_PATTERN_G) || ret.equals(SEP_PATTERN_101) || ret.equals(SEP_PATTERN_B) || ret.equals(SEP_PATTERN_C) || ret.equals("float32") || ret.equals("double64") || ret.equals("component_states_type") || ret.equals("module_states_type") || ret.equals("hr_time") || ret.equals("global_time") || ret.equals("timestamp") || ret.equals("log")) {

				// This is a standard ECOA type so we must prepend "ECOA."
				ret = "ECOA." + typeConverter.get(ret);
			} else {
				// This is not a standard ECOA type so it must be a type from
				// within the current namespace or a different one.
				// If it is a different one, then prefix it!

				if (!smType.getNamespace().equals(currentNamespace)) {
					ret = smType.getNamespace().getName() + "." + ret;
				}
			}

		}
		return ret;
	}

	public TypesWriterAda(Path outputDir, SM_Namespace namespace) {
		super(outputDir);
		this.namespace = namespace;
		this.namespaceName = namespace.getName();

		typeConverter = new HashMap<String, String>();

		typeConverter.put(SEP_PATTERN_E, "Boolean_8_Type");
		typeConverter.put("int8", "Signed_8_Type");
		typeConverter.put(SEP_PATTERN_A, "Character_8_Type");
		typeConverter.put("byte", "Byte_Type");
		typeConverter.put(SEP_PATTERN_J, "Signed_16_Type");
		typeConverter.put(SEP_PATTERN_I, "Signed_32_Type");
		typeConverter.put(SEP_PATTERN_G, "Signed_64_Type");
		typeConverter.put(SEP_PATTERN_101, "Unsigned_8_Type");
		typeConverter.put(SEP_PATTERN_B, "Unsigned_16_Type");
		typeConverter.put(SEP_PATTERN_C, "Unsigned_32_Type");
		typeConverter.put("float32", "Float_32_Type");
		typeConverter.put("double64", "Float_64_Type");
		typeConverter.put("component_states_type", "Component_States_Type");
		typeConverter.put("module_states_type", "Module_States_Type");
		typeConverter.put("hr_time", "Hr_Time_Type");
		typeConverter.put("global_time", "Global_Time_Type");
		typeConverter.put("timestamp", "Timestamp_Type");
		typeConverter.put("log", "Log_Type");

	}

	@Override
	public void close() {
		// Close the header
		codeStringBuilder.append("end " + namespaceName + ";" + LF + LF);

		super.close();
	}

	@Override
	public void generate() {
		// Open the file
		open();

		// Write the start of file
		writePreamble();

		// Write the type declarations
		writeTypeDecl();

		// Close the file.
		close();
	}

	@Override
	public void open() {
		super.openFile(outputDir.resolve("include/" + namespaceName.replaceAll("\\.", "-") + ".ads"));
	}

	@Override
	protected void setFileStructure() {
		// Not required for API generation - TODO look at ways to stop this
		// being required.
	}

	public void writePreamble() {

		codeStringBuilder.append("-- File " + namespaceName.replaceAll("\\.", "-") + ".ads" + LF + LF);

		// Include the standard top level types file if this is not it!
		if (!namespaceName.equals("ECOA")) {
			codeStringBuilder.append("-- Standard ECOA Types" + LF + "with ECOA;" + LF + LF);
		}

		// Add uses (includes)
		for (SM_Namespace use : namespace.getUses()) {
			codeStringBuilder.append("with " + use.getName() + ";" + LF + LF);
		}
	}

	public void writeTypeDecl() {

		codeStringBuilder.append("package " + namespaceName + " is" + LF + LF);

		Map<String, SM_Type> types = namespace.getTypes();
		Iterator<Entry<String, SM_Type>> it = types.entrySet().iterator();

		while (it.hasNext()) {
			Entry<String, SM_Type> entry = it.next();
			SM_Type type = entry.getValue();

			// Simple Types
			if (type instanceof SM_Simple_Type) {
				SM_Simple_Type simpleType = (SM_Simple_Type) type;

				codeStringBuilder.append("-- Simple type");
				if (simpleType.getMinRange() != null) {
					codeStringBuilder.append(", min is " + simpleType.getMinRange());
				} else if (simpleType.getMaxRange() != null) {
					codeStringBuilder.append(", max is " + simpleType.getMaxRange());
				} else if (simpleType.getUnit() != null) {
					codeStringBuilder.append(", units are " + simpleType.getUnit());
				}
				codeStringBuilder.append(LF + SEP_PATTERN_D + simpleType.getName() + " is new " + convertTypeToAda(simpleType.getType(), namespace) + ";" + LF + LF);
			}
			// Record Types
			else if (type instanceof SM_Record_Type) {
				SM_Record_Type recordType = (SM_Record_Type) type;

				codeStringBuilder.append("-- Record type" + LF + SEP_PATTERN_D + recordType.getName() + " is" + LF + SEP_PATTERN_H + LF);

				for (nameAndType nAndT : recordType.getType()) {
					codeStringBuilder.append("      " + nAndT.getName() + " : " + convertTypeToAda(nAndT.getReferencedType(), namespace) + ";" + LF);
				}
				codeStringBuilder.append(SEP_PATTERN_F + LF + LF);
			}
			// Variant Record Types
			else if (type instanceof SM_Variant_Record_Type) {
				SM_Variant_Record_Type variantRecord = (SM_Variant_Record_Type) type;

				codeStringBuilder.append("-- VariantRecord type" + LF + SEP_PATTERN_D + variantRecord.getName() + " (" + variantRecord.getSelectorType().getName() + " : " + convertTypeToAda(variantRecord.getSelectorType().getReferencedType(), namespace) + ")" + " is" + LF + SEP_PATTERN_H + LF);

				for (nameAndType nAndT : variantRecord.getTypes()) {
					codeStringBuilder.append("      " + nAndT.getName() + " : " + convertTypeToAda(nAndT.getReferencedType(), namespace) + ";" + LF);
				}

				Map<String, List<nameAndType>> unionTypes = variantRecord.getUnionTypes();
				Iterator<Entry<String, List<nameAndType>>> itUnion = unionTypes.entrySet().iterator();
				codeStringBuilder.append("      case " + variantRecord.getSelectorType().getName() + " is" + LF);
				while (itUnion.hasNext()) {
					Entry<String, List<nameAndType>> entryUnion = itUnion.next();
					// Where the selector type is a language discrete type, we
					// need to type cast...
					SM_Type selectorType = variantRecord.getSelectorType().getReferencedType();
					String stName = selectorType.getName();
					if (stName.equals(SEP_PATTERN_E) || stName.equals("int8") || stName.equals(SEP_PATTERN_A) || stName.equals("byte") || stName.equals(SEP_PATTERN_J) || stName.equals(SEP_PATTERN_I) || stName.equals(SEP_PATTERN_G) || stName.equals(SEP_PATTERN_101) || stName.equals(SEP_PATTERN_B) || stName.equals(SEP_PATTERN_C)) {
						codeStringBuilder.append("         when " + convertTypeToAda(selectorType, namespace) + "(" + entryUnion.getKey() + ") =>" + LF);
					} else {
						// Otherwise, just plant the selector type value name
						codeStringBuilder.append("         when " + selectorType.getName() + "_" + entryUnion.getKey() + " =>" + LF);
					}
					// For each union with this selector value...
					for (nameAndType nAndT : entryUnion.getValue()) {
						codeStringBuilder.append("            " + nAndT.getName() + " : " + convertTypeToAda(nAndT.getReferencedType(), namespace) + ";" + LF);
					}
				}

				codeStringBuilder.append("         when others =>" + LF + "            null;" + LF + "      end case;" + LF + SEP_PATTERN_F + LF + LF);
			}
			// Array Types
			else if (type instanceof SM_Array_Type) {
				SM_Array_Type arrayType = (SM_Array_Type) type;

				codeStringBuilder.append("-- Array type" + LF + SEP_PATTERN_D + arrayType.getName() + "_Index is range 0.." + arrayType.getMaxNumber() + "-1;" + LF + SEP_PATTERN_D + arrayType.getName() + "_Data is array" + LF + "    (" + arrayType.getName() + "_Index) of " + convertTypeToAda(arrayType.getType(), namespace) + ";" + LF + SEP_PATTERN_D + arrayType.getName() + " is" + LF + SEP_PATTERN_H + LF + "      Current_Size : " + arrayType.getName() + "_Index;" + LF + "      Data         : " + arrayType.getName() + "_Data;" + LF + SEP_PATTERN_F + LF + LF);
			}
			// Fixed Array Types
			else if (type instanceof SM_Fixed_Array_Type) {
				SM_Fixed_Array_Type fixedArrayType = (SM_Fixed_Array_Type) type;

				codeStringBuilder.append("-- Fixed Array type" + LF + SEP_PATTERN_D + fixedArrayType.getName() + "_Index is range 0.." + fixedArrayType.getMaxNumber() + "-1;" + LF + SEP_PATTERN_D + fixedArrayType.getName() + " is array" + LF + "    (" + fixedArrayType.getName() + "_Index) of " + convertTypeToAda(fixedArrayType.getType(), namespace) + ";" + LF + LF);
			}
			// Enum Types
			else if (type instanceof SM_Enum_Type) {
				SM_Enum_Type enumType = (SM_Enum_Type) type;

				codeStringBuilder.append("-- Enum type" + LF + SEP_PATTERN_D + enumType.getName() + " is new " + convertTypeToAda(enumType.getType(), namespace) + ";" + LF);

				Integer count = 0;
				String enumValStr = "";
				for (SM_Enum_Value enumVal : enumType.getEnumValues()) {
					if (enumVal.getValnum() != null) {
						enumValStr = enumVal.getValnum();
					} else {
						enumValStr = count.toString();
					}

					codeStringBuilder.append(enumType.getName() + "_" + enumVal.getName() + " : constant " + enumType.getName() + " := " + enumValStr + ";" + LF);
					count++;
				}
				codeStringBuilder.append(LF);
			}
			// Constant Types
			else if (type instanceof SM_Constant_Type) {
				SM_Constant_Type constantType = (SM_Constant_Type) type;

				codeStringBuilder.append(constantType.getName() + " : constant " + convertTypeToAda(constantType.getType(), namespace) + " := " + constantType.getValue() + ";" + LF + LF);
			}
		}
	}

}
