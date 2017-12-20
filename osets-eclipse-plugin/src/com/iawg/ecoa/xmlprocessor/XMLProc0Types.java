/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.xmlprocessor;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.iawg.ecoa.jaxbclasses.step0Types.Array;
import com.iawg.ecoa.jaxbclasses.step0Types.Constant;
import com.iawg.ecoa.jaxbclasses.step0Types.EnumValue;
import com.iawg.ecoa.jaxbclasses.step0Types.Field;
import com.iawg.ecoa.jaxbclasses.step0Types.FixedArray;
import com.iawg.ecoa.jaxbclasses.step0Types.Library;
import com.iawg.ecoa.jaxbclasses.step0Types.Record;
import com.iawg.ecoa.jaxbclasses.step0Types.Simple;
import com.iawg.ecoa.jaxbclasses.step0Types.Union;
import com.iawg.ecoa.jaxbclasses.step0Types.Use;
import com.iawg.ecoa.jaxbclasses.step0Types.VariantRecord;
import com.iawg.ecoa.systemmodel.SystemModel;
import com.iawg.ecoa.systemmodel.types.SM_Array_Type;
import com.iawg.ecoa.systemmodel.types.SM_Constant_Type;
import com.iawg.ecoa.systemmodel.types.SM_Enum_Type;
import com.iawg.ecoa.systemmodel.types.SM_Fixed_Array_Type;
import com.iawg.ecoa.systemmodel.types.SM_Namespace;
import com.iawg.ecoa.systemmodel.types.SM_Record_Type;
import com.iawg.ecoa.systemmodel.types.SM_Simple_Type;
import com.iawg.ecoa.systemmodel.types.SM_Type;
import com.iawg.ecoa.systemmodel.types.SM_Type.nameAndType;
import com.iawg.ecoa.systemmodel.types.SM_Variant_Record_Type;

/**
 * This class processes a step 0 XML file that defines all of the types used in
 * an ECOA project.
 * 
 * @author Shaun Cullimore
 */
public class XMLProc0Types {
	private static final Logger LOGGER = LogManager.getLogger(XMLProc0Types.class);
	private static List<ECOAFile> arrLstLibraryFile = new ArrayList<ECOAFile>();

	private void checkType(SystemModel systemModel, String currentNamespace, String typeName) {
		boolean exists = false;

		// Check to see if fully qualified
		if (typeName.contains(":")) {
			String[] s = typeName.split(":");
			String qualNamespace = s[0];
			String qualTypeName = s[1];

			SM_Namespace namespace = systemModel.getTypes().getNamespace(qualNamespace);
			exists = namespace.typeExists(qualTypeName);
		} else {
			// Check if in current namespace
			SM_Namespace currNamespace = systemModel.getTypes().getNamespace(currentNamespace);
			exists = currNamespace.typeExists(typeName);

			if (!exists) {
				// Finally check to see if it's in the ECOA namespace
				SM_Namespace ecoaNamespace = systemModel.getTypes().getNamespace("ECOA");
				exists = ecoaNamespace.typeExists(typeName);
			}
		}

		if (!exists) {
			LOGGER.info("ERROR: Type: " + typeName + " Not Found.");
			
		}
	}

	private SM_Type getReferencedType(SystemModel systemModel, String currentNamespaceName, String typeName) {
		// Check to see if fully qualified
		if (typeName.contains(":")) {
			String[] s = typeName.split(":");
			String qualNamespace = s[0];
			String qualTypeName = s[1];

			SM_Namespace namespace = systemModel.getTypes().getNamespace(qualNamespace);
			return namespace.getType(qualTypeName);
		} else {
			// Check if in current namespace
			if (systemModel.getTypes().getNamespace(currentNamespaceName).typeExists(typeName)) {
				return systemModel.getTypes().getNamespace(currentNamespaceName).getType(typeName);
			} else if (systemModel.getTypes().getNamespace("ECOA").typeExists(typeName)) {
				// Finally check to see if it's in the ECOA namespace
				return systemModel.getTypes().getNamespace("ECOA").getType(typeName);
			} else {
				LOGGER.info("ERROR: Referenced Type: " + typeName + " Not Found.");
				
				return null;
			}
		}
	}

	public void parseFile(Path typeFile) {
		XMLFileProcessor pxfp = new XMLFileProcessor("ecoa-types-1.0.xsd", "com.iawg.ecoa.jaxbclasses.step0Types");

		Library library = (Library) pxfp.parseFile(typeFile);
		ECOAFile libraryFile = new ECOAFile(typeFile, library);
		arrLstLibraryFile.add(libraryFile);
	}

	private void processTypeFile(SystemModel systemModel, ECOAFile lf) {
		Library lib = (Library) lf.getObject();

		// Create the namespace if it hasn't already been created / file already
		// been processed
		if (!systemModel.getTypes().namespaceExists(lf.getNamespaceName())) {
			// Create the new namespace.
			SM_Namespace namespace = new SM_Namespace(lf.getNamespaceName());
			systemModel.getTypes().addNamespace(namespace);

			// Process any used libraries
			for (Use use : lib.getUses()) {
				if (systemModel.getTypes().namespaceExists(use.getLibrary())) {
					namespace.addUse(systemModel.getTypes().getNamespace(use.getLibrary()));
				} else {
					// We need to process this "use" file first...
					// TODO - should we handle possibility of circular
					// references? Is that allowed in ECOA?!
					for (ECOAFile useLf : arrLstLibraryFile) {
						if (useLf.getNamespaceName().equals(use.getLibrary())) {
							processTypeFile(systemModel, useLf);
						}
					}

					namespace.addUse(systemModel.getTypes().getNamespace(use.getLibrary()));
				}
			}

			for (Object o : lib.getTypes().getSimplesAndRecordsAndConstants()) {
				if (o instanceof Simple) {
					Simple so = (Simple) o;
					String st = so.getType();

					checkType(systemModel, namespace.getName(), st);

					SM_Type referencedType = getReferencedType(systemModel, namespace.getName(), st);
					// TODO - should probably check ranges.
					SM_Simple_Type simpleType = new SM_Simple_Type(so.getName(), referencedType.isSimple(), referencedType, namespace);
					if (so.getMinRange() != null) {
						simpleType.setMinRange(so.getMinRange());
					}
					if (so.getMaxRange() != null) {
						simpleType.setMaxRange(so.getMaxRange());
					}
					simpleType.setUnit(so.getUnit());

					namespace.addType(simpleType.getName(), simpleType);
				} else if (o instanceof Record) {
					Record ro = (Record) o;
					List<nameAndType> referencedTypes = new ArrayList<nameAndType>();

					for (Field field : ro.getFields()) {
						checkType(systemModel, namespace.getName(), field.getType());

						nameAndType nAndT = new nameAndType(field.getName());
						nAndT.setReferencedType(getReferencedType(systemModel, namespace.getName(), field.getType()));
						referencedTypes.add(nAndT);
					}

					namespace.addType(ro.getName(), new SM_Record_Type(ro.getName(), referencedTypes, namespace));
				} else if (o instanceof VariantRecord) {
					VariantRecord vro = (VariantRecord) o;
					String st = vro.getSelectType();

					List<nameAndType> referencedTypes = new ArrayList<nameAndType>();

					checkType(systemModel, namespace.getName(), st);

					nameAndType selectNandT = new nameAndType(vro.getSelectName());
					selectNandT.setReferencedType(getReferencedType(systemModel, namespace.getName(), st));

					for (Field field : vro.getFields()) {
						checkType(systemModel, namespace.getName(), field.getType());

						nameAndType nAndT = new nameAndType(field.getName());
						nAndT.setReferencedType(getReferencedType(systemModel, namespace.getName(), field.getType()));
						referencedTypes.add(nAndT);
					}

					HashMap<String, List<nameAndType>> unionTypes = new HashMap<String, List<nameAndType>>();

					for (Union union : vro.getUnions()) {
						checkType(systemModel, namespace.getName(), union.getType());

						nameAndType nAndT = new nameAndType(union.getName());
						nAndT.setReferencedType(getReferencedType(systemModel, namespace.getName(), union.getType()));
						String whenName = union.getWhen();

						List<nameAndType> unionArray = null;

						if (unionTypes.containsKey(whenName)) {
							unionArray = unionTypes.get(whenName);
							unionTypes.remove(whenName);
						} else {
							unionArray = new ArrayList<nameAndType>();
						}

						unionArray.add(nAndT);
						unionTypes.put(whenName, unionArray);
					}

					namespace.addType(vro.getName(), new SM_Variant_Record_Type(vro.getName(), selectNandT, referencedTypes, unionTypes, namespace));
				} else if (o instanceof Array) {
					Array ao = (Array) o;
					String at = ao.getItemType();
					checkType(systemModel, namespace.getName(), at);

					SM_Type referencedType = getReferencedType(systemModel, namespace.getName(), at);

					namespace.addType(ao.getName(), new SM_Array_Type(ao.getName(), referencedType, Integer.parseInt(ao.getMaxNumber()), namespace));
				} else if (o instanceof FixedArray) {
					FixedArray fao = (FixedArray) o;
					String at = fao.getItemType();
					checkType(systemModel, namespace.getName(), at);

					SM_Type referencedType = getReferencedType(systemModel, namespace.getName(), at);

					namespace.addType(fao.getName(), new SM_Fixed_Array_Type(fao.getName(), referencedType, Integer.parseInt(fao.getMaxNumber()), namespace));
				} else if (o instanceof com.iawg.ecoa.jaxbclasses.step0Types.Enum) {
					com.iawg.ecoa.jaxbclasses.step0Types.Enum eo = (com.iawg.ecoa.jaxbclasses.step0Types.Enum) o;
					String et = eo.getType();

					checkType(systemModel, namespace.getName(), et);
					SM_Enum_Type enumType = new SM_Enum_Type(eo.getName(), systemModel.getTypes().getType(et), namespace);

					// Add enum values to model.
					for (EnumValue enumVal : eo.getValues()) {
						enumType.addEnumValue(enumVal.getName(), enumVal.getValnum());
					}

					namespace.addType(enumType.getName(), enumType);
				} else if (o instanceof com.iawg.ecoa.jaxbclasses.step0Types.Constant) {
					Constant co = (Constant) o;

					String ct = co.getType();

					checkType(systemModel, namespace.getName(), ct);

					SM_Type referencedType = getReferencedType(systemModel, namespace.getName(), ct);
					// TODO - should probably check ranges.
					SM_Constant_Type constantType = new SM_Constant_Type(co.getName(), referencedType, co.getValue(), namespace);

					namespace.addType(constantType.getName(), constantType);
				} else {
					LOGGER.info("         - Uninterpreted type " + o.getClass().getName());
				}
			}
		}
	}

	public void updateSystemModel(SystemModel systemModel) {
		for (ECOAFile lf : arrLstLibraryFile) {
			processTypeFile(systemModel, lf);
		}

		// If you uncomment the following lines it allows the
		// serialiser/deserialiser
		// to be generated without a deployment
		// Serialiser serialiser = new Serialiser();
		// serialiser.generate("D:", systemModel);
	}
}
