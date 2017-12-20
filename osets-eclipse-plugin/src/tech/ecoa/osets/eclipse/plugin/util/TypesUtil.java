/*
 * Copyright 2017, BAE Systems Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package tech.ecoa.osets.eclipse.plugin.util;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import com.sun.xml.internal.bind.v2.runtime.MarshallerImpl;

import tech.ecoa.osets.eclipse.plugin.common.ECOAPreDefinedTypes;
import tech.ecoa.osets.eclipse.plugin.common.NamespacePrefixMapper;
import tech.ecoa.osets.eclipse.plugin.editors.parts.types.ArrayTypesComposite;
import tech.ecoa.osets.eclipse.plugin.editors.parts.types.ConstantTypesComposite;
import tech.ecoa.osets.eclipse.plugin.editors.parts.types.EnumTypesComposite;
import tech.ecoa.osets.eclipse.plugin.editors.parts.types.FixedArrayTypesComposite;
import tech.ecoa.osets.eclipse.plugin.editors.parts.types.RecordTypesComposite;
import tech.ecoa.osets.eclipse.plugin.editors.parts.types.SimpleTypesComposite;
import tech.ecoa.osets.eclipse.plugin.editors.parts.types.VarRecTypesComposite;
import tech.ecoa.osets.model.types.Array;
import tech.ecoa.osets.model.types.Constant;
import tech.ecoa.osets.model.types.DataTypes;
import tech.ecoa.osets.model.types.EBasic;
import tech.ecoa.osets.model.types.Enum;
import tech.ecoa.osets.model.types.Field;
import tech.ecoa.osets.model.types.FixedArray;
import tech.ecoa.osets.model.types.Library;
import tech.ecoa.osets.model.types.Record;
import tech.ecoa.osets.model.types.Simple;
import tech.ecoa.osets.model.types.Union;
import tech.ecoa.osets.model.types.Use;
import tech.ecoa.osets.model.types.VariantRecord;

@SuppressWarnings("deprecation")
public class TypesUtil {

	private final PluginUtil util = new PluginUtil();
	private String containerName;
	private String editorText;
	private String fileName;

	public static TypesUtil getInstance(String containerName) {
		TypesUtil util = new TypesUtil();
		util.setContainerName(containerName);
		return util;
	}

	public static TypesUtil getInstance() {
		TypesUtil util = new TypesUtil();
		return util;
	}

	public Tree buildTreeFromContent(Composite parent) throws JAXBException {
		Tree tree = new Tree(parent, SWT.SINGLE | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		Unmarshaller unmarshaller = JAXBContext.newInstance(Library.class).createUnmarshaller();
		Library lib = (Library) unmarshaller.unmarshal(new StringReader(editorText));
		TreeItem lA1Item = new TreeItem(tree, 0);
		lA1Item.setText("Uses");
		lA1Item.setExpanded(true);
		for (Use use : lib.getUse()) {
			TreeItem lB1Item = new TreeItem(lA1Item, 0);
			lB1Item.setText(use.getLibrary());
			lB1Item.setExpanded(true);
		}
		TreeItem lA2Item = new TreeItem(tree, 0);
		lA2Item.setText("Types");
		lA2Item.setExpanded(true);
		TreeItem lB1Item = new TreeItem(lA2Item, 0);
		lB1Item.setText("Simple");
		lB1Item.setExpanded(true);
		TreeItem lB2Item = new TreeItem(lA2Item, 0);
		lB2Item.setText("Constant");
		lB2Item.setExpanded(true);
		TreeItem lB3Item = new TreeItem(lA2Item, 0);
		lB3Item.setText("Enum");
		lB3Item.setExpanded(true);
		TreeItem lB4Item = new TreeItem(lA2Item, 0);
		lB4Item.setText("Array");
		lB4Item.setExpanded(true);
		TreeItem lB5Item = new TreeItem(lA2Item, 0);
		lB5Item.setText("Record");
		lB5Item.setExpanded(true);
		TreeItem lB6Item = new TreeItem(lA2Item, 0);
		lB6Item.setText("Fixed Array");
		lB6Item.setExpanded(true);
		TreeItem lB7Item = new TreeItem(lA2Item, 0);
		lB7Item.setText("Variant Record");
		lB7Item.setExpanded(true);
		for (Object obj : lib.getTypes().getSimpleOrRecordOrConstant()) {
			if (obj instanceof Simple) {
				TreeItem lCItem = new TreeItem(lB1Item, 0);
				Simple val = (Simple) obj;
				lCItem.setText(val.getName());
				lCItem.setExpanded(true);
			} else if (obj instanceof Constant) {
				TreeItem lCItem = new TreeItem(lB2Item, 0);
				Constant val = (Constant) obj;
				lCItem.setText(val.getName());
				lCItem.setExpanded(true);
			} else if (obj instanceof tech.ecoa.osets.model.types.Enum) {
				TreeItem lCItem = new TreeItem(lB3Item, 0);
				tech.ecoa.osets.model.types.Enum val = (tech.ecoa.osets.model.types.Enum) obj;
				lCItem.setText(val.getName());
				lCItem.setExpanded(true);
			} else if (obj instanceof Array) {
				TreeItem lCItem = new TreeItem(lB4Item, 0);
				Array val = (Array) obj;
				lCItem.setText(val.getName());
				lCItem.setExpanded(true);
			} else if (obj instanceof Record) {
				TreeItem lCItem = new TreeItem(lB5Item, 0);
				Record val = (Record) obj;
				lCItem.setText(val.getName());
				lCItem.setExpanded(true);
			} else if (obj instanceof FixedArray) {
				TreeItem lCItem = new TreeItem(lB6Item, 0);
				FixedArray val = (FixedArray) obj;
				lCItem.setText(val.getName());
				lCItem.setExpanded(true);
			} else if (obj instanceof VariantRecord) {
				TreeItem lCItem = new TreeItem(lB7Item, 0);
				VariantRecord val = (VariantRecord) obj;
				lCItem.setText(val.getName());
				lCItem.setExpanded(true);
			}
		}
		return tree;
	}

	public String removeItem(TreeItem selItem, String parent, String content) throws JAXBException {
		Unmarshaller unmarshaller = JAXBContext.newInstance(Library.class).createUnmarshaller();
		MarshallerImpl marshaller = (MarshallerImpl) JAXBContext.newInstance(Library.class).createMarshaller();
		marshaller.setProperty(MarshallerImpl.JAXB_FORMATTED_OUTPUT, true);
		marshaller.setProperty("com.sun.xml.internal.bind.namespacePrefixMapper", new NamespacePrefixMapper());
		Library lib = (Library) unmarshaller.unmarshal(new StringReader(editorText));
		ArrayList<Object> types = new ArrayList<Object>();
		types.addAll(lib.getTypes().getSimpleOrRecordOrConstant());
		int i = 0;
		for (Object obj : lib.getTypes().getSimpleOrRecordOrConstant()) {
			if (obj instanceof Simple && parent.equalsIgnoreCase("Simple")) {
				if (((Simple) obj).getName().equalsIgnoreCase(selItem.getText()))
					types.remove(i);
			} else if (obj instanceof Constant && parent.equalsIgnoreCase("Constant")) {
				if (((Constant) obj).getName().equalsIgnoreCase(selItem.getText()))
					types.remove(i);
			} else if (obj instanceof tech.ecoa.osets.model.types.Enum && parent.equalsIgnoreCase("Enum")) {
				if (((tech.ecoa.osets.model.types.Enum) obj).getName().equalsIgnoreCase(selItem.getText()))
					types.remove(i);
			} else if (obj instanceof Array && parent.equalsIgnoreCase("Array")) {
				if (((Array) obj).getName().equalsIgnoreCase(selItem.getText()))
					types.remove(i);
			} else if (obj instanceof Record && parent.equalsIgnoreCase("Record")) {
				if (((Record) obj).getName().equalsIgnoreCase(selItem.getText()))
					types.remove(i);
			} else if (obj instanceof FixedArray && parent.equalsIgnoreCase("Fixed Array")) {
				if (((FixedArray) obj).getName().equalsIgnoreCase(selItem.getText()))
					types.remove(i);
			} else if (obj instanceof VariantRecord && parent.equalsIgnoreCase("Variant Record")) {
				if (((VariantRecord) obj).getName().equalsIgnoreCase(selItem.getText()))
					types.remove(i);
			}
			i++;
		}
		lib.getTypes().getSimpleOrRecordOrConstant().clear();
		lib.getTypes().getSimpleOrRecordOrConstant().addAll(types);
		StringWriter writer = new StringWriter();
		marshaller.marshal(lib, writer);
		return ParseUtil.removeEmptyTags(writer.toString(), Library.class);
	}

	public String processAdd(boolean isEdit, String editName, Object obj, String type, String content) throws JAXBException {
		Unmarshaller unmarshaller = JAXBContext.newInstance(Library.class).createUnmarshaller();
		MarshallerImpl marshaller = (MarshallerImpl) JAXBContext.newInstance(Library.class).createMarshaller();
		marshaller.setProperty(MarshallerImpl.JAXB_FORMATTED_OUTPUT, true);
		marshaller.setProperty("com.sun.xml.internal.bind.namespacePrefixMapper", new NamespacePrefixMapper());
		Library lib = (Library) unmarshaller.unmarshal(new StringReader(editorText));
		Library tempA = null;
		if (type != null && type.equalsIgnoreCase("Array")) {
			ArrayTypesComposite comp = (ArrayTypesComposite) obj;
			Array proc = comp.getProcessedArray();
			tempA = buildLibraryFromArrayType(proc);
			if (isEdit)
				processAndRemoveType(lib, editName, type);
			lib.getTypes().getSimpleOrRecordOrConstant().addAll(tempA.getTypes().getSimpleOrRecordOrConstant());
		} else if (type != null && type.equalsIgnoreCase("Constant")) {
			ConstantTypesComposite comp = (ConstantTypesComposite) obj;
			Constant proc = comp.getProcessedConstant();
			tempA = buildLibraryFromConstantType(proc);
			if (isEdit)
				processAndRemoveType(lib, editName, type);
			lib.getTypes().getSimpleOrRecordOrConstant().addAll(tempA.getTypes().getSimpleOrRecordOrConstant());
		} else if (type != null && type.equalsIgnoreCase("Enum")) {
			EnumTypesComposite comp = (EnumTypesComposite) obj;
			Enum proc = comp.getProcessedEnum();
			tempA = buildLibraryFromEnumType(proc);
			if (isEdit)
				processAndRemoveType(lib, editName, type);
			lib.getTypes().getSimpleOrRecordOrConstant().addAll(tempA.getTypes().getSimpleOrRecordOrConstant());
		} else if (type != null && type.equalsIgnoreCase("Simple")) {
			SimpleTypesComposite comp = (SimpleTypesComposite) obj;
			Simple proc = comp.getProcessedSimple();
			tempA = buildLibraryFromSimpleType(proc);
			if (isEdit)
				processAndRemoveType(lib, editName, type);
			lib.getTypes().getSimpleOrRecordOrConstant().addAll(tempA.getTypes().getSimpleOrRecordOrConstant());
		} else if (type != null && type.equalsIgnoreCase("Record")) {
			RecordTypesComposite comp = (RecordTypesComposite) obj;
			Record proc = comp.getProcessedRecord();
			tempA = buildLibraryFromRecordType(proc);
			if (isEdit)
				processAndRemoveType(lib, editName, type);
			lib.getTypes().getSimpleOrRecordOrConstant().addAll(tempA.getTypes().getSimpleOrRecordOrConstant());
		} else if (type != null && type.equalsIgnoreCase("Fixed Array")) {
			FixedArrayTypesComposite comp = (FixedArrayTypesComposite) obj;
			FixedArray proc = comp.getProcessedFixedArray();
			tempA = buildLibraryFromFixedArrayType(proc);
			if (isEdit)
				processAndRemoveType(lib, editName, type);
			lib.getTypes().getSimpleOrRecordOrConstant().addAll(tempA.getTypes().getSimpleOrRecordOrConstant());
		} else if (type != null && type.equalsIgnoreCase("Variant Record")) {
			VarRecTypesComposite comp = (VarRecTypesComposite) obj;
			VariantRecord proc = comp.getProcessedVarRec();
			tempA = buildLibraryFromVarRecType(proc);
			if (isEdit)
				processAndRemoveType(lib, editName, type);
			lib.getTypes().getSimpleOrRecordOrConstant().addAll(tempA.getTypes().getSimpleOrRecordOrConstant());
		}
		Set<String> uses = new TreeSet<String>();
		for (Use use : lib.getUse())
			uses.add(use.getLibrary());
		for (Use use : tempA.getUse())
			uses.add(use.getLibrary());
		lib.getUse().clear();
		for (String use : uses) {
			Use u = new Use();
			u.setLibrary(use);
			lib.getUse().add(u);
		}
		StringWriter writer = new StringWriter();
		marshaller.marshal(lib, writer);
		return ParseUtil.removeEmptyTags(writer.toString(), Library.class);
	}

	private void processAndRemoveType(Library lib, String editName, String parent) {
		ArrayList<Object> types = new ArrayList<Object>();
		types.addAll(lib.getTypes().getSimpleOrRecordOrConstant());
		int i = 0;
		for (Object obj : lib.getTypes().getSimpleOrRecordOrConstant()) {
			if (obj instanceof Simple && parent.equalsIgnoreCase("Simple")) {
				if (((Simple) obj).getName().equalsIgnoreCase(editName))
					types.remove(i);
			} else if (obj instanceof Constant && parent.equalsIgnoreCase("Constant")) {
				if (((Constant) obj).getName().equalsIgnoreCase(editName))
					types.remove(i);
			} else if (obj instanceof tech.ecoa.osets.model.types.Enum && parent.equalsIgnoreCase("Enum")) {
				if (((tech.ecoa.osets.model.types.Enum) obj).getName().equalsIgnoreCase(editName))
					types.remove(i);
			} else if (obj instanceof Array && parent.equalsIgnoreCase("Array")) {
				if (((Array) obj).getName().equalsIgnoreCase(editName))
					types.remove(i);
			} else if (obj instanceof Record && parent.equalsIgnoreCase("Record")) {
				if (((Record) obj).getName().equalsIgnoreCase(editName))
					types.remove(i);
			} else if (obj instanceof FixedArray && parent.equalsIgnoreCase("Fixed Array")) {
				if (((FixedArray) obj).getName().equalsIgnoreCase(editName))
					types.remove(i);
			} else if (obj instanceof VariantRecord && parent.equalsIgnoreCase("Variant Record")) {
				if (((VariantRecord) obj).getName().equalsIgnoreCase(editName))
					types.remove(i);
			}
			i++;
		}
		lib.getTypes().getSimpleOrRecordOrConstant().clear();
		lib.getTypes().getSimpleOrRecordOrConstant().addAll(types);
	}

	public Array getSelectedArrayType(String text, Class<Array> clazz) throws JAXBException {
		Array arr = new Array();
		Unmarshaller unmarshaller = JAXBContext.newInstance(Library.class).createUnmarshaller();
		Library lib = (Library) unmarshaller.unmarshal(new StringReader(editorText));
		for (Object obj : lib.getTypes().getSimpleOrRecordOrConstant()) {
			if (obj instanceof Array) {
				Array temp = (Array) obj;
				if (temp.getName().equalsIgnoreCase(text))
					arr = temp;
			}
		}
		return arr;
	}

	public Constant getSelectedConstantType(String text, Class<Constant> clazz) throws JAXBException {
		Constant cnst = new Constant();
		Unmarshaller unmarshaller = JAXBContext.newInstance(Library.class).createUnmarshaller();
		Library lib = (Library) unmarshaller.unmarshal(new StringReader(editorText));
		for (Object obj : lib.getTypes().getSimpleOrRecordOrConstant()) {
			if (obj instanceof Constant) {
				Constant temp = (Constant) obj;
				if (temp.getName().equalsIgnoreCase(text))
					cnst = temp;
			}
		}
		return cnst;
	}

	public Enum getSelectedEnumType(String text, Class<Enum> clazz) throws JAXBException {
		Enum enm = new Enum();
		Unmarshaller unmarshaller = JAXBContext.newInstance(Library.class).createUnmarshaller();
		Library lib = (Library) unmarshaller.unmarshal(new StringReader(editorText));
		for (Object obj : lib.getTypes().getSimpleOrRecordOrConstant()) {
			if (obj instanceof Enum) {
				Enum temp = (Enum) obj;
				if (temp.getName().equalsIgnoreCase(text))
					enm = temp;
			}
		}
		return enm;
	}

	public Simple getSelectedSimpleType(String text, Class<Simple> clazz) throws JAXBException {
		Simple simple = new Simple();
		Unmarshaller unmarshaller = JAXBContext.newInstance(Library.class).createUnmarshaller();
		Library lib = (Library) unmarshaller.unmarshal(new StringReader(editorText));
		for (Object obj : lib.getTypes().getSimpleOrRecordOrConstant()) {
			if (obj instanceof Simple) {
				Simple temp = (Simple) obj;
				if (temp.getName().equalsIgnoreCase(text))
					simple = temp;
			}
		}
		return simple;
	}

	public Record getSelectedRecordType(String text, Class<Record> clazz) throws JAXBException {
		Record rec = new Record();
		Unmarshaller unmarshaller = JAXBContext.newInstance(Library.class).createUnmarshaller();
		Library lib = (Library) unmarshaller.unmarshal(new StringReader(editorText));
		for (Object obj : lib.getTypes().getSimpleOrRecordOrConstant()) {
			if (obj instanceof Record) {
				Record temp = (Record) obj;
				if (temp.getName().equalsIgnoreCase(text))
					rec = temp;
			}
		}
		return rec;
	}

	public FixedArray getSelectedFixedArrayType(String text, Class<FixedArray> clazz) throws JAXBException {
		FixedArray fArr = new FixedArray();
		Unmarshaller unmarshaller = JAXBContext.newInstance(Library.class).createUnmarshaller();
		Library lib = (Library) unmarshaller.unmarshal(new StringReader(editorText));
		for (Object obj : lib.getTypes().getSimpleOrRecordOrConstant()) {
			if (obj instanceof FixedArray) {
				FixedArray temp = (FixedArray) obj;
				if (temp.getName().equalsIgnoreCase(text))
					fArr = temp;
			}
		}
		return fArr;
	}

	public VariantRecord getSelectedVarRecType(String text, Class<VariantRecord> clazz) throws JAXBException {
		VariantRecord vRec = new VariantRecord();
		Unmarshaller unmarshaller = JAXBContext.newInstance(Library.class).createUnmarshaller();
		Library lib = (Library) unmarshaller.unmarshal(new StringReader(editorText));
		for (Object obj : lib.getTypes().getSimpleOrRecordOrConstant()) {
			if (obj instanceof VariantRecord) {
				VariantRecord temp = (VariantRecord) obj;
				if (temp.getName().equalsIgnoreCase(text))
					vRec = temp;
			}
		}
		return vRec;
	}

	private HashMap<String, Library> getLibraryTypes() throws IOException, JAXBException {
		HashMap<String, Library> ret = new HashMap<String, Library>();
		ArrayList<String> types = util.getResourcesWithExtension("types", containerName);
		for (String type : types) {
			File file = new File(type);
			String content = FileUtils.readFileToString(file);
			JAXBContext ctx = JAXBContext.newInstance(Library.class);
			Library lib = (Library) ctx.createUnmarshaller().unmarshal(new StringReader(content));
			ret.put(LibraryUtil.getLibraryNameFromFile(file.getName()), lib);
		}
		return ret;
	}

	public ArrayList<String> getAllSimpleTypeNames() throws IOException, JAXBException {
		ArrayList<String> ret = new ArrayList<String>();
		HashMap<String, Library> vals = getLibraryTypes();
		Iterator<String> keys = vals.keySet().iterator();
		while (keys.hasNext()) {
			String key = keys.next();
			Library lib = vals.get(key);
			for (Object obj : lib.getTypes().getSimpleOrRecordOrConstant())
				if (obj instanceof Simple)
					ret.add((!fileName.equalsIgnoreCase(key)) ? (key + ":" + ((Simple) obj).getName()) : ((Simple) obj).getName());
		}
		return ret;
	}

	public ArrayList<String> getAllRecordTypeNames() throws IOException, JAXBException {
		ArrayList<String> ret = new ArrayList<String>();
		HashMap<String, Library> vals = getLibraryTypes();
		Iterator<String> keys = vals.keySet().iterator();
		while (keys.hasNext()) {
			String key = keys.next();
			Library lib = vals.get(key);
			for (Object obj : lib.getTypes().getSimpleOrRecordOrConstant())
				if (obj instanceof Record)
					ret.add((!fileName.equalsIgnoreCase(key)) ? (key + ":" + ((Record) obj).getName()) : ((Record) obj).getName());
		}
		return ret;
	}

	public ArrayList<String> getAllConstantTypeNames() throws IOException, JAXBException {
		ArrayList<String> ret = new ArrayList<String>();
		HashMap<String, Library> vals = getLibraryTypes();
		Iterator<String> keys = vals.keySet().iterator();
		while (keys.hasNext()) {
			String key = keys.next();
			Library lib = vals.get(key);
			for (Object obj : lib.getTypes().getSimpleOrRecordOrConstant())
				if (obj instanceof Constant)
					ret.add((!fileName.equalsIgnoreCase(key)) ? (key + ":" + ((Constant) obj).getName()) : ((Constant) obj).getName());
		}
		return ret;
	}

	public ArrayList<String> getAllVariantRecordTypeNames() throws IOException, JAXBException {
		ArrayList<String> ret = new ArrayList<String>();
		HashMap<String, Library> vals = getLibraryTypes();
		Iterator<String> keys = vals.keySet().iterator();
		while (keys.hasNext()) {
			String key = keys.next();
			Library lib = vals.get(key);
			for (Object obj : lib.getTypes().getSimpleOrRecordOrConstant())
				if (obj instanceof VariantRecord)
					ret.add((!fileName.equalsIgnoreCase(key)) ? (key + ":" + ((VariantRecord) obj).getName()) : ((VariantRecord) obj).getName());
		}
		return ret;
	}

	public ArrayList<String> getAllArrayTypeNames() throws IOException, JAXBException {
		ArrayList<String> ret = new ArrayList<String>();
		HashMap<String, Library> vals = getLibraryTypes();
		Iterator<String> keys = vals.keySet().iterator();
		while (keys.hasNext()) {
			String key = keys.next();
			Library lib = vals.get(key);
			for (Object obj : lib.getTypes().getSimpleOrRecordOrConstant())
				if (obj instanceof Array)
					ret.add((!fileName.equalsIgnoreCase(key)) ? (key + ":" + ((Array) obj).getName()) : ((Array) obj).getName());
		}
		return ret;
	}

	public ArrayList<String> getAllFixedArrayTypeNames() throws IOException, JAXBException {
		ArrayList<String> ret = new ArrayList<String>();
		HashMap<String, Library> vals = getLibraryTypes();
		Iterator<String> keys = vals.keySet().iterator();
		while (keys.hasNext()) {
			String key = keys.next();
			Library lib = vals.get(key);
			for (Object obj : lib.getTypes().getSimpleOrRecordOrConstant())
				if (obj instanceof FixedArray)
					ret.add((!fileName.equalsIgnoreCase(key)) ? (key + ":" + ((FixedArray) obj).getName()) : ((FixedArray) obj).getName());
		}
		return ret;
	}

	public ArrayList<String> getAllEnumTypeNames() throws IOException, JAXBException {
		ArrayList<String> ret = new ArrayList<String>();
		HashMap<String, Library> vals = getLibraryTypes();
		Iterator<String> keys = vals.keySet().iterator();
		while (keys.hasNext()) {
			String key = keys.next();
			Library lib = vals.get(key);
			for (Object obj : lib.getTypes().getSimpleOrRecordOrConstant())
				if (obj instanceof Enum)
					ret.add((!fileName.equalsIgnoreCase(key)) ? (key + ":" + ((Enum) obj).getName()) : ((Enum) obj).getName());
		}
		return ret;
	}

	public HashMap<String, Simple> getAllSimpleTypes() throws IOException, JAXBException {
		HashMap<String, Simple> ret = new HashMap<String, Simple>();
		HashMap<String, Library> vals = getLibraryTypes();
		Iterator<String> keys = vals.keySet().iterator();
		while (keys.hasNext()) {
			String key = keys.next();
			Library lib = vals.get(key);
			for (Object obj : lib.getTypes().getSimpleOrRecordOrConstant())
				if (obj instanceof Simple)
					ret.put(key, (Simple) obj);
		}
		return ret;
	}

	public HashMap<String, Record> getAllRecordTypes() throws IOException, JAXBException {
		HashMap<String, Record> ret = new HashMap<String, Record>();
		HashMap<String, Library> vals = getLibraryTypes();
		Iterator<String> keys = vals.keySet().iterator();
		while (keys.hasNext()) {
			String key = keys.next();
			Library lib = vals.get(key);
			for (Object obj : lib.getTypes().getSimpleOrRecordOrConstant())
				if (obj instanceof Record)
					ret.put(key, (Record) obj);
		}
		return ret;
	}

	public HashMap<String, Constant> getAllConstantTypes() throws IOException, JAXBException {
		HashMap<String, Constant> ret = new HashMap<String, Constant>();
		HashMap<String, Library> vals = getLibraryTypes();
		Iterator<String> keys = vals.keySet().iterator();
		while (keys.hasNext()) {
			String key = keys.next();
			Library lib = vals.get(key);
			for (Object obj : lib.getTypes().getSimpleOrRecordOrConstant())
				if (obj instanceof Constant)
					ret.put(key, (Constant) obj);
		}
		return ret;
	}

	public HashMap<String, VariantRecord> getAllVariantRecordTypes() throws IOException, JAXBException {
		HashMap<String, VariantRecord> ret = new HashMap<String, VariantRecord>();
		HashMap<String, Library> vals = getLibraryTypes();
		Iterator<String> keys = vals.keySet().iterator();
		while (keys.hasNext()) {
			String key = keys.next();
			Library lib = vals.get(key);
			for (Object obj : lib.getTypes().getSimpleOrRecordOrConstant())
				if (obj instanceof VariantRecord)
					ret.put(key, (VariantRecord) obj);
		}
		return ret;
	}

	public HashMap<String, Array> getAllArrayTypes() throws IOException, JAXBException {
		HashMap<String, Array> ret = new HashMap<String, Array>();
		HashMap<String, Library> vals = getLibraryTypes();
		Iterator<String> keys = vals.keySet().iterator();
		while (keys.hasNext()) {
			String key = keys.next();
			Library lib = vals.get(key);
			for (Object obj : lib.getTypes().getSimpleOrRecordOrConstant())
				if (obj instanceof Array)
					ret.put(key, (Array) obj);
		}
		return ret;
	}

	public HashMap<String, FixedArray> getAllFixedArrayTypes() throws IOException, JAXBException {
		HashMap<String, FixedArray> ret = new HashMap<String, FixedArray>();
		HashMap<String, Library> vals = getLibraryTypes();
		Iterator<String> keys = vals.keySet().iterator();
		while (keys.hasNext()) {
			String key = keys.next();
			Library lib = vals.get(key);
			for (Object obj : lib.getTypes().getSimpleOrRecordOrConstant())
				if (obj instanceof FixedArray)
					ret.put(key, (FixedArray) obj);
		}
		return ret;
	}

	public HashMap<String, tech.ecoa.osets.model.types.Enum> getAllEnumTypes() throws IOException, JAXBException {
		HashMap<String, tech.ecoa.osets.model.types.Enum> ret = new HashMap<String, tech.ecoa.osets.model.types.Enum>();
		HashMap<String, Library> vals = getLibraryTypes();
		Iterator<String> keys = vals.keySet().iterator();
		while (keys.hasNext()) {
			String key = keys.next();
			Library lib = vals.get(key);
			for (Object obj : lib.getTypes().getSimpleOrRecordOrConstant())
				if (obj instanceof tech.ecoa.osets.model.types.Enum)
					ret.put(key, (tech.ecoa.osets.model.types.Enum) obj);
		}
		return ret;
	}

	public ArrayList<String> getAllBasicTypes() {
		ArrayList<String> ret = new ArrayList<String>();
		for (EBasic bas : EBasic.values())
			ret.add(bas.value());
		return ret;
	}

	public ArrayList<String> getAllPreDefinedTypes() {
		ArrayList<String> ret = new ArrayList<String>();
		for (ECOAPreDefinedTypes bas : ECOAPreDefinedTypes.values())
			ret.add("ECOA:" + bas.name());
		return ret;
	}

	public ArrayList<String> getAllTypesForSimpleWizard() throws IOException, JAXBException {
		ArrayList<String> ret = new ArrayList<String>();
		ret.addAll(getAllBasicTypes());
		ret.addAll(getAllPreDefinedTypes());
		ret.addAll(getAllSimpleTypeNames());
		return ret;
	}

	public Library buildLibraryFromSimpleType(Simple simple) {
		Library ret = new Library();
		DataTypes types = new DataTypes();
		ret.setTypes(types);
		if (StringUtils.containsIgnoreCase(simple.getType(), ":")) {
			String[] vals = StringUtils.split(simple.getType(), ":");
			simple.setType(simple.getType());
			Use use = new Use();
			use.setLibrary(LibraryUtil.getLibraryUseNameFromName(vals[0]));
			if (!existing(use, ret))
				ret.getUse().add(use);
		}
		ret.getTypes().getSimpleOrRecordOrConstant().add(simple);
		return ret;
	}

	public Library buildLibraryFromConstantType(Constant cnst) {
		Library ret = new Library();
		DataTypes types = new DataTypes();
		ret.setTypes(types);
		if (StringUtils.containsIgnoreCase(cnst.getType(), ":")) {
			String[] vals = StringUtils.split(cnst.getType(), ":");
			cnst.setType(cnst.getType());
			Use use = new Use();
			use.setLibrary(LibraryUtil.getLibraryUseNameFromName(vals[0]));
			if (!existing(use, ret))
				ret.getUse().add(use);
		}
		ret.getTypes().getSimpleOrRecordOrConstant().add(cnst);
		return ret;
	}

	public Library buildLibraryFromEnumType(tech.ecoa.osets.model.types.Enum enm) {
		Library ret = new Library();
		DataTypes types = new DataTypes();
		ret.setTypes(types);
		if (StringUtils.containsIgnoreCase(enm.getType(), ":")) {
			String[] vals = StringUtils.split(enm.getType(), ":");
			enm.setType(enm.getType());
			Use use = new Use();
			use.setLibrary(LibraryUtil.getLibraryUseNameFromName(vals[0]));
			if (!existing(use, ret))
				ret.getUse().add(use);
		}
		ret.getTypes().getSimpleOrRecordOrConstant().add(enm);
		return ret;
	}

	public Library buildLibraryFromArrayType(Array arr) {
		Library ret = new Library();
		DataTypes types = new DataTypes();
		ret.setTypes(types);
		if (StringUtils.containsIgnoreCase(arr.getItemType(), ":")) {
			String[] vals = StringUtils.split(arr.getItemType(), ":");
			arr.setItemType(arr.getItemType());
			Use use = new Use();
			use.setLibrary(LibraryUtil.getLibraryUseNameFromName(vals[0]));
			if (!existing(use, ret))
				ret.getUse().add(use);
		}
		ret.getTypes().getSimpleOrRecordOrConstant().add(arr);
		return ret;
	}

	public Library buildLibraryFromFixedArrayType(FixedArray fArr) {
		Library ret = new Library();
		DataTypes types = new DataTypes();
		ret.setTypes(types);
		if (StringUtils.containsIgnoreCase(fArr.getItemType(), ":")) {
			String[] vals = StringUtils.split(fArr.getItemType(), ":");
			fArr.setItemType(fArr.getItemType());
			Use use = new Use();
			use.setLibrary(LibraryUtil.getLibraryUseNameFromName(vals[0]));
			if (!existing(use, ret))
				ret.getUse().add(use);
		}
		ret.getTypes().getSimpleOrRecordOrConstant().add(fArr);
		return ret;
	}

	public Library buildLibraryFromRecordType(Record rec) {
		Library ret = new Library();
		DataTypes types = new DataTypes();
		ret.setTypes(types);
		List<Field> fields = new ArrayList<Field>();
		fields.addAll(rec.getField());
		rec.getField().clear();
		for (Field field : fields) {
			if (StringUtils.containsIgnoreCase(field.getType(), ":")) {
				String[] vals = StringUtils.split(field.getType(), ":");
				field.setType(field.getType());
				Use use = new Use();
				use.setLibrary(LibraryUtil.getLibraryUseNameFromName(vals[0]));
				if (!existing(use, ret))
					ret.getUse().add(use);
			}
			rec.getField().add(field);
		}
		ret.getTypes().getSimpleOrRecordOrConstant().add(rec);
		return ret;
	}

	public Library buildLibraryFromVarRecType(VariantRecord vRec) {
		Library ret = new Library();
		DataTypes types = new DataTypes();
		ret.setTypes(types);
		List<Field> fields = new ArrayList<Field>();
		fields.addAll(vRec.getField());
		vRec.getField().clear();
		for (Field field : fields) {
			if (StringUtils.containsIgnoreCase(field.getType(), ":")) {
				String[] vals = StringUtils.split(field.getType(), ":");
				field.setType(field.getType());
				Use use = new Use();
				use.setLibrary(LibraryUtil.getLibraryUseNameFromName(vals[0]));
				if (!existing(use, ret))
					ret.getUse().add(use);
			}
			vRec.getField().add(field);
		}
		List<Union> unions = new ArrayList<Union>();
		unions.addAll(vRec.getUnion());
		vRec.getUnion().clear();
		for (Union union : unions) {
			if (StringUtils.containsIgnoreCase(union.getType(), ":")) {
				String[] vals = StringUtils.split(union.getType(), ":");
				union.setType(union.getType());
				Use use = new Use();
				use.setLibrary(LibraryUtil.getLibraryUseNameFromName(vals[0]));
				if (!existing(use, ret))
					ret.getUse().add(use);
			}
			vRec.getUnion().add(union);
		}
		ret.getTypes().getSimpleOrRecordOrConstant().add(vRec);
		return ret;
	}

	private boolean existing(Use use, Library lib) {
		boolean ret = false;
		for (Use u : lib.getUse())
			ret = ret || (u.getLibrary().equalsIgnoreCase(use.getLibrary()));
		return ret;
	}

	public ArrayList<String> getAllTypes() throws IOException, JAXBException {
		ArrayList<String> ret = new ArrayList<String>();
		ret.addAll(getAllBasicTypes());
		ret.addAll(getAllPreDefinedTypes());
		ret.addAll(getAllSimpleTypeNames());
		ret.addAll(getAllRecordTypeNames());
		ret.addAll(getAllConstantTypeNames());
		ret.addAll(getAllVariantRecordTypeNames());
		ret.addAll(getAllFixedArrayTypeNames());
		ret.addAll(getAllEnumTypeNames());
		ret.addAll(getAllArrayTypeNames());
		return ret;
	}

	public String getContainerName() {
		return containerName;
	}

	public void setContainerName(String containerName) {
		this.containerName = containerName;
	}

	public String getEditorText() {
		return editorText;
	}

	public void setEditorText(String editorText) {
		this.editorText = editorText;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

}
