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

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import com.sun.xml.internal.bind.v2.runtime.MarshallerImpl;

import tech.ecoa.osets.eclipse.plugin.common.NamespacePrefixMapper;
import tech.ecoa.osets.eclipse.plugin.editors.parts.cdef.CompDefPropertyComposite;
import tech.ecoa.osets.eclipse.plugin.editors.parts.cdef.CompDefReferenceComposite;
import tech.ecoa.osets.eclipse.plugin.editors.parts.cdef.CompDefServiceComposite;
import tech.ecoa.osets.model.cdef.ComponentType;
import tech.ecoa.osets.model.cdef.ComponentType.Service;
import tech.ecoa.osets.model.cdef.ComponentTypeReference;
import tech.ecoa.osets.model.cdef.Property;

@SuppressWarnings({ "unchecked", "deprecation" })
public class CompDefUtil {
	private final PluginUtil util = new PluginUtil();
	private String containerName;
	private String editorText;

	public static CompDefUtil getInstance() {
		CompDefUtil util = new CompDefUtil();
		return util;
	}

	public static CompDefUtil getInstance(String containerName) {
		CompDefUtil util = new CompDefUtil();
		util.setContainerName(containerName);
		return util;
	}

	public Tree buildTreeFromContent(Composite parent) throws JAXBException {
		Tree tree = new Tree(parent, SWT.SINGLE | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		Unmarshaller unmarshaller = JAXBContext.newInstance(ComponentType.class).createUnmarshaller();
		ComponentType def = ((JAXBElement<ComponentType>) unmarshaller.unmarshal(new StringReader(editorText))).getValue();
		TreeItem lA2Item = new TreeItem(tree, 0);
		lA2Item.setText("Definitions");
		lA2Item.setExpanded(true);
		TreeItem lB1Item = new TreeItem(lA2Item, 0);
		lB1Item.setText("Required Service");
		lB1Item.setExpanded(true);
		TreeItem lB2Item = new TreeItem(lA2Item, 0);
		lB2Item.setText("Provided Service");
		lB2Item.setExpanded(true);
		TreeItem lB3Item = new TreeItem(lA2Item, 0);
		lB3Item.setText("Properties");
		lB3Item.setExpanded(true);
		for (Object obj : def.getServiceOrReferenceOrProperty()) {
			if (obj instanceof ComponentTypeReference) {
				TreeItem lCItem = new TreeItem(lB1Item, 0);
				ComponentTypeReference val = (ComponentTypeReference) obj;
				lCItem.setText(val.getName());
				lCItem.setExpanded(true);
			} else if (obj instanceof Service) {
				TreeItem lCItem = new TreeItem(lB2Item, 0);
				Service val = (Service) obj;
				lCItem.setText(val.getName());
				lCItem.setExpanded(true);
			} else if (obj instanceof Property) {
				TreeItem lCItem = new TreeItem(lB3Item, 0);
				Property val = (Property) obj;
				lCItem.setText(val.getName());
				lCItem.setExpanded(true);
			}
		}
		return tree;
	}

	public ArrayList<String> getAllClientComponents() throws IOException, JAXBException {
		ArrayList<String> ret = new ArrayList<String>();
		ArrayList<String> cDefFiles = util.getResourcesWithExtension("cdef", containerName);
		for (String file : cDefFiles) {
			String content = FileUtils.readFileToString(new File(file));
			Unmarshaller unmarshaller = JAXBContext.newInstance(ComponentType.class).createUnmarshaller();
			ComponentType def = ((JAXBElement<ComponentType>) unmarshaller.unmarshal(new StringReader(content))).getValue();
			for (Object obj : def.getServiceOrReferenceOrProperty()) {
				if (obj instanceof ComponentTypeReference)
					ret.add(((ComponentTypeReference) obj).getName());
			}
		}
		return ret;
	}

	public ArrayList<String> getAllServerComponents() throws IOException, JAXBException {
		ArrayList<String> ret = new ArrayList<String>();
		ArrayList<String> cDefFiles = util.getResourcesWithExtension("cdef", containerName);
		for (String file : cDefFiles) {
			String content = FileUtils.readFileToString(new File(file));
			Unmarshaller unmarshaller = JAXBContext.newInstance(ComponentType.class).createUnmarshaller();
			ComponentType def = ((JAXBElement<ComponentType>) unmarshaller.unmarshal(new StringReader(content))).getValue();
			for (Object obj : def.getServiceOrReferenceOrProperty()) {
				if (obj instanceof Service)
					ret.add(((Service) obj).getName());
			}
		}
		return ret;
	}

	public ArrayList<String> getAllServerProprties() throws IOException, JAXBException {
		ArrayList<String> ret = new ArrayList<String>();
		ArrayList<String> cDefFiles = util.getResourcesWithExtension("cdef", containerName);
		for (String file : cDefFiles) {
			String content = FileUtils.readFileToString(new File(file));
			Unmarshaller unmarshaller = JAXBContext.newInstance(ComponentType.class).createUnmarshaller();
			ComponentType def = ((JAXBElement<ComponentType>) unmarshaller.unmarshal(new StringReader(content))).getValue();
			for (Object obj : def.getServiceOrReferenceOrProperty()) {
				if (obj instanceof Property)
					ret.add(((Service) obj).getName());
			}
		}
		return ret;
	}

	public ArrayList<String> getAllComponents() throws IOException, JAXBException {
		ArrayList<String> ret = new ArrayList<String>();
		ret.addAll(getAllClientComponents());
		ret.addAll(getAllServerComponents());
		ret.addAll(getAllServerProprties());
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

	public ComponentType getByName(String name) throws JAXBException, IOException {
		ComponentType data = new ComponentType();
		ArrayList<String> cDefs = util.getResourcesWithExtension("cdef", containerName);
		Unmarshaller unmarshaller = JAXBContext.newInstance(ComponentType.class).createUnmarshaller();
		for (String cDef : cDefs) {
			File file = new File(cDef);
			if (file.exists() && StringUtils.contains(file.getName(), name)) {
				String content = FileUtils.readFileToString(file);
				data = ((JAXBElement<ComponentType>) unmarshaller.unmarshal(new StringReader(content))).getValue();
				break;
			}
		}
		return data;
	}

	public ComponentType getSelectedRef(String text, Class<?> clazz) throws JAXBException {
		ComponentType data = new ComponentType();
		Unmarshaller unmarshaller = JAXBContext.newInstance(ComponentType.class).createUnmarshaller();
		ComponentType cDef = ((JAXBElement<ComponentType>) unmarshaller.unmarshal(new StringReader(editorText))).getValue();
		for (Object obj : cDef.getServiceOrReferenceOrProperty()) {
			if (obj instanceof ComponentTypeReference) {
				ComponentTypeReference temp = (ComponentTypeReference) obj;
				if (temp.getName().equalsIgnoreCase(text)) {
					data.getServiceOrReferenceOrProperty().add(temp);
				}
			} else if (obj instanceof Service) {
				Service temp = (Service) obj;
				if (temp.getName().equalsIgnoreCase(text))
					data.getServiceOrReferenceOrProperty().add(temp);
			} else if (obj instanceof Property) {
				Property temp = (Property) obj;
				if (temp.getName().equalsIgnoreCase(text))
					data.getServiceOrReferenceOrProperty().add(temp);
			}
		}
		return data;
	}

	public String removeItem(TreeItem selItem, String parent, String content) throws JAXBException {
		Unmarshaller unmarshaller = JAXBContext.newInstance(ComponentType.class).createUnmarshaller();
		MarshallerImpl marshaller = (MarshallerImpl) JAXBContext.newInstance(ComponentType.class).createMarshaller();
		marshaller.setProperty(MarshallerImpl.JAXB_FORMATTED_OUTPUT, true);
		marshaller.setProperty("com.sun.xml.internal.bind.namespacePrefixMapper", new NamespacePrefixMapper());
		ComponentType sDef = ((JAXBElement<ComponentType>) unmarshaller.unmarshal(new StringReader(editorText))).getValue();
		ArrayList<Object> ops = new ArrayList<Object>();
		ops.addAll(sDef.getServiceOrReferenceOrProperty());
		int i = 0;
		for (Object obj : sDef.getServiceOrReferenceOrProperty()) {
			if (obj instanceof ComponentTypeReference && parent.equalsIgnoreCase("Required Service")) {
				if (((ComponentTypeReference) obj).getName().equalsIgnoreCase(selItem.getText()))
					ops.remove(i);
			} else if (obj instanceof Service && parent.equalsIgnoreCase("Provided Service")) {
				if (((Service) obj).getName().equalsIgnoreCase(selItem.getText()))
					ops.remove(i);
			} else if (obj instanceof Property && parent.equalsIgnoreCase("Properties")) {
				if (((Property) obj).getName().equalsIgnoreCase(selItem.getText()))
					ops.remove(i);
			}
			i++;
		}
		sDef.getServiceOrReferenceOrProperty().clear();
		sDef.getServiceOrReferenceOrProperty().addAll(ops);
		StringWriter writer = new StringWriter();
		marshaller.marshal(sDef, writer);
		return ParseUtil.removeEmptyTags(writer.toString(), ComponentType.class);
	}

	public String processAdd(boolean isEdit, String editName, Object obj, String type, String string) throws JAXBException {
		Unmarshaller unmarshaller = JAXBContext.newInstance(ComponentType.class).createUnmarshaller();
		MarshallerImpl marshaller = (MarshallerImpl) JAXBContext.newInstance(ComponentType.class).createMarshaller();
		marshaller.setProperty(MarshallerImpl.JAXB_FORMATTED_OUTPUT, true);
		marshaller.setProperty("com.sun.xml.internal.bind.namespacePrefixMapper", new NamespacePrefixMapper());
		ComponentType sDef = ((JAXBElement<ComponentType>) unmarshaller.unmarshal(new StringReader(editorText))).getValue();
		if (type != null && type.equalsIgnoreCase("Required Service")) {
			CompDefReferenceComposite comp = (CompDefReferenceComposite) obj;
			ComponentType proc = comp.getProcessedComponent();
			if (isEdit)
				processAndRemoveComponentType(sDef, editName, type);
			sDef.getServiceOrReferenceOrProperty().addAll(proc.getServiceOrReferenceOrProperty());
		} else if (type != null && type.equalsIgnoreCase("Provided Service")) {
			CompDefServiceComposite comp = (CompDefServiceComposite) obj;
			ComponentType proc = comp.getProcessedComponent();
			if (isEdit)
				processAndRemoveComponentType(sDef, editName, type);
			sDef.getServiceOrReferenceOrProperty().addAll(proc.getServiceOrReferenceOrProperty());
		} else if (type != null && type.equalsIgnoreCase("Properties")) {
			CompDefPropertyComposite comp = (CompDefPropertyComposite) obj;
			ComponentType proc = comp.getProcessedComponent();
			if (isEdit)
				processAndRemoveComponentType(sDef, editName, type);
			sDef.getServiceOrReferenceOrProperty().addAll(proc.getServiceOrReferenceOrProperty());
		}
		StringWriter writer = new StringWriter();
		marshaller.marshal(sDef, writer);
		return ParseUtil.removeEmptyTags(writer.toString(), ComponentType.class);
	}

	private void processAndRemoveComponentType(ComponentType sDef, String editName, String parent) {
		ArrayList<Object> ops = new ArrayList<Object>();
		ops.addAll(sDef.getServiceOrReferenceOrProperty());
		int i = 0;
		for (Object obj : sDef.getServiceOrReferenceOrProperty()) {
			if (obj instanceof ComponentTypeReference && parent.equalsIgnoreCase("Required Service")) {
				if (((ComponentTypeReference) obj).getName().equalsIgnoreCase(editName))
					ops.remove(i);
			} else if (obj instanceof Service && parent.equalsIgnoreCase("Provided Service")) {
				if (((Service) obj).getName().equalsIgnoreCase(editName))
					ops.remove(i);
			} else if (obj instanceof Property && parent.equalsIgnoreCase("Properties")) {
				if (((Property) obj).getName().equalsIgnoreCase(editName))
					ops.remove(i);
			}
			i++;
		}
		sDef.getServiceOrReferenceOrProperty().clear();
		sDef.getServiceOrReferenceOrProperty().addAll(ops);
	}

}
