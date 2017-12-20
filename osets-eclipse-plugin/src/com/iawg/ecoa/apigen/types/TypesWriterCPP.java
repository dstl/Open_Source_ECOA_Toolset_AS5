/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.apigen.types;

import java.nio.file.Path;
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
public class TypesWriterCPP extends TypesWriter {
	private static final String SEP_PATTERN_01 = "typedef struct";

	public static String convertTypeToCPP(SM_Type smType, SM_Namespace currentNamespace) {
		// This function converts a type name (which may include a namespace
		// name) into a C name, including the apporpriate namespace

		String ret = null;

		// The type name did not contain a namespace name. It is either a
		// type from the predefined "ECOA" namespace or it is a type from
		// the current namespace. If the name is one of the standard types
		// we will append the "ECOA::" namespace name, otherwise we will
		// append the current namespace name.

		ret = smType.getName(); // The type name

		if (ret.equals("boolean8") || ret.equals("int8") || ret.equals("char8") || ret.equals("byte") || ret.equals("int16") || ret.equals("int32") || ret.equals("int64") || ret.equals("uint8") || ret.equals("uint16") || ret.equals("uint32") || ret.equals("float32") || ret.equals("double64")) {
			// This is a standard ECOA type so we must prepend "ECOA__"
			ret = "ECOA::" + ret;
		} else {
			// This is not a standard ECOA type so it must be a type either from
			// within the current namespace or another one.
			// If it is a different one, then prefix it!

			if (!smType.getNamespace().equals(currentNamespace)) {
				ret = smType.getNamespace().getName().replaceAll("\\.", "::") + "::" + ret;
			}
		}

		return ret;
	}

	public TypesWriterCPP(Path outputDir, SM_Namespace namespace) {
		super(outputDir);
		this.namespace = namespace;
		this.namespaceName = namespace.getName();
	}

	@Override
	public void close() {
		// Close the header

		String[] namespaces = namespaceName.split("\\.");

		for (int i = namespaces.length - 1; i >= 0; i--) {
			codeStringBuilder.append("} /* " + namespaces[i] + " */" + LF + LF);
		}

		codeStringBuilder.append("#endif  /* _" + namespaceName.replaceAll("\\.", "__").toUpperCase() + "_HH */" + LF);
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
		super.openFile(outputDir.resolve("include/" + namespaceName.replaceAll("\\.", "__") + ".hpp"));
	}

	@Override
	protected void setFileStructure() {
		// Not required for API generation - TODO look at ways to stop this
		// being required.
	}

	public void writePreamble() {
		codeStringBuilder.append("/* File " + namespaceName.replaceAll("\\.", "__") + ".hpp */" + LF + LF +

				"#if !defined(_" + namespaceName.replaceAll("\\.", "__").toUpperCase() + "_HH)" + LF + "#define _" + namespaceName.replaceAll("\\.", "__").toUpperCase() + "_HH" + LF + LF);

		// Include the standard top level types file if this is not it!
		if (!namespaceName.equals("ECOA")) {
			codeStringBuilder.append("#include \"ECOA.hpp\" /* Fundamental types declared for the platform */" + LF + LF);
		}

		// Add uses (includes)
		for (SM_Namespace use : namespace.getUses()) {
			codeStringBuilder.append("#include \"" + use.getName().replaceAll("\\.", "__") + ".hpp\"" + LF + LF);
		}

		String[] namespaces = namespaceName.split("\\.");

		for (int i = 0; i < namespaces.length; i++) {
			codeStringBuilder.append("namespace " + namespaces[i] + " {" + LF + LF);
		}

	}

	public void writeTypeDecl() {
		Map<String, SM_Type> types = namespace.getTypes();
		Iterator<Entry<String, SM_Type>> it = types.entrySet().iterator();

		while (it.hasNext()) {
			Entry<String, SM_Type> entry = it.next();
			SM_Type type = entry.getValue();

			// Simple Types
			if (type instanceof SM_Simple_Type) {
				SM_Simple_Type simpleType = (SM_Simple_Type) type;

				codeStringBuilder.append("/* Simple type");
				if (simpleType.getMinRange() != null) {
					codeStringBuilder.append(", min is " + simpleType.getMinRange());
				} else if (simpleType.getMaxRange() != null) {
					codeStringBuilder.append(", max is " + simpleType.getMaxRange());
				} else if (simpleType.getUnit() != null) {
					codeStringBuilder.append(", units are " + simpleType.getUnit());
				}
				codeStringBuilder.append(" */" + LF + "typedef " + convertTypeToCPP(simpleType.getType(), namespace) + " " + simpleType.getName() + ";" + LF + LF);
			}
			// Record Types
			else if (type instanceof SM_Record_Type) {
				SM_Record_Type recordType = (SM_Record_Type) type;

				codeStringBuilder.append("/* Record type */" + LF + SEP_PATTERN_01 + LF + "{" + LF);
				for (nameAndType nAndT : recordType.getType()) {
					codeStringBuilder.append("   " + convertTypeToCPP(nAndT.getReferencedType(), namespace) + " " + nAndT.getName() + ";" + LF);
				}
				codeStringBuilder.append("} " + recordType.getName() + ";" + LF + LF);
			}
			// Variant Record Types
			else if (type instanceof SM_Variant_Record_Type) {
				SM_Variant_Record_Type variantRecord = (SM_Variant_Record_Type) type;

				codeStringBuilder.append("/* VariantRecord type */" + LF + SEP_PATTERN_01 + LF + "{" + LF + "   " + convertTypeToCPP(variantRecord.getSelectorType().getReferencedType(), namespace) + " " + variantRecord.getSelectorType().getName() + "; /* Discriminant */" + LF);
				for (nameAndType nAndT : variantRecord.getTypes()) {
					codeStringBuilder.append("   " + convertTypeToCPP(nAndT.getReferencedType(), namespace) + " " + nAndT.getName() + ";");
				}

				Map<String, List<nameAndType>> unionTypes = variantRecord.getUnionTypes();
				Iterator<Entry<String, List<nameAndType>>> itUnion = unionTypes.entrySet().iterator();

				codeStringBuilder.append(LF + "   union" + LF + "   {" + LF);
				while (itUnion.hasNext()) {
					Entry<String, List<nameAndType>> entryUnion = itUnion.next();
					for (nameAndType nAndT : entryUnion.getValue()) {
						codeStringBuilder.append("      " + convertTypeToCPP(nAndT.getReferencedType(), namespace) + " " + nAndT.getName() + "; /* Applies when discriminant is " + entryUnion.getKey() + " */" + LF);
					}
				}
				codeStringBuilder.append("   } u_" + variantRecord.getSelectorType().getName() + ";" + LF + "} " + variantRecord.getName() + ";" + LF + LF);
			}
			// Array Types
			else if (type instanceof SM_Array_Type) {
				SM_Array_Type arrayType = (SM_Array_Type) type;

				codeStringBuilder.append("/* Array type */" + LF + "const ECOA::uint32 " + arrayType.getName() + "_MAXSIZE " + " = " + arrayType.getMaxNumber() + ";" + LF + SEP_PATTERN_01 + LF + "{" + LF + "   ECOA::uint32 current_size;" + LF + "   " + convertTypeToCPP(arrayType.getType(), namespace) + " data[" + arrayType.getName() + "_MAXSIZE];" + LF + "} " + arrayType.getName() + ";" + LF + LF);
			}
			// Fixed Array Types
			else if (type instanceof SM_Fixed_Array_Type) {
				SM_Fixed_Array_Type fixedArrayType = (SM_Fixed_Array_Type) type;

				codeStringBuilder.append("/* Fixed array type */" + LF + "const ECOA::uint32 " + fixedArrayType.getName() + "_MAXSIZE " + " = " + fixedArrayType.getMaxNumber() + ";" + LF + "typedef " + convertTypeToCPP(fixedArrayType.getType(), namespace) + " " + fixedArrayType.getName() + "[" + fixedArrayType.getName() + "_MAXSIZE];" + LF + LF);
			}
			// Enum Types
			else if (type instanceof SM_Enum_Type) {
				SM_Enum_Type enumType = (SM_Enum_Type) type;

				codeStringBuilder.append("/* Enum type */" + LF + "struct " + enumType.getName() + LF + "{" + LF + "   " + convertTypeToCPP(enumType.getType(), namespace) + " value;" + LF + "   enum EnumValues {" + LF);
				Integer count = 0;
				String enumValStr = "";
				for (SM_Enum_Value enumVal : enumType.getEnumValues()) {
					if (enumVal.getValnum() != null) {
						enumValStr = enumVal.getValnum();
					} else {
						enumValStr = count.toString();
					}
					codeStringBuilder.append("      " + enumVal.getName() + " = " + enumValStr);

					count++;
					if (count < enumType.getEnumValues().size())
						codeStringBuilder.append("," + LF);
					else
						codeStringBuilder.append(LF);
				}

				codeStringBuilder.append("   };" + LF + "inline void operator = (" + convertTypeToCPP(enumType.getType(), namespace) + " i) { value = i; }" + LF + "inline operator " + convertTypeToCPP(enumType.getType(), namespace) + "() const { return value; }" + LF + "};" + LF + LF);
			}
			// Constant Types
			else if (type instanceof SM_Constant_Type) {
				SM_Constant_Type constantType = (SM_Constant_Type) type;

				codeStringBuilder.append("static const " + convertTypeToCPP(constantType.getType(), namespace) + " " + constantType.getName() + " = " + constantType.getValue() + ";" + LF + LF);
			}
		}
	}

}
