package tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.model;

import java.io.IOException;
import java.util.ArrayList;

import javax.xml.bind.JAXBException;

import org.apache.commons.io.FilenameUtils;
import org.eclipse.ui.views.properties.ComboBoxPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

import tech.ecoa.osets.eclipse.plugin.util.PluginUtil;
import tech.ecoa.osets.eclipse.plugin.util.TypesUtil;

public class NodePropertySource implements IPropertySource {

	private Node node;
	private String[] sdtTypes;

	public NodePropertySource(Node node) {
		super();
		this.node = node;
		sdtTypes = getSimpleTypes();
	}

	@Override
	public IPropertyDescriptor[] getPropertyDescriptors() {
		ArrayList<IPropertyDescriptor> properties = new ArrayList<IPropertyDescriptor>();
		if (node instanceof CompositeNode) {
			properties.add(new TextPropertyDescriptor(NodeConstants.COMPOSITE_NAME, "Composite Name"));
		} else if (node instanceof ComponentNode) {
			properties.add(new TextPropertyDescriptor(NodeConstants.COMPONENT_NAME, "Component Name"));
			properties.add(new TextPropertyDescriptor(NodeConstants.COMPONENT_TYPE, "Component Type"));
			properties.add(new ComboBoxPropertyDescriptor(NodeConstants.COMPONENT_INST, "Implementation", getComponentImpl()));
		} else if (node instanceof ComponentPropertyNode) {
			properties.add(new TextPropertyDescriptor(NodeConstants.GENERIC_PROP, "Property Name"));
			properties.add(new ComboBoxPropertyDescriptor(NodeConstants.GENERIC_TYPE, "Type", sdtTypes));
			properties.add(new TextPropertyDescriptor(NodeConstants.GENERIC_VAL, "Property Value"));
		} else if (node instanceof CompositePropertyNode) {
			properties.add(new TextPropertyDescriptor(NodeConstants.GENERIC_PROP, "Property Name"));
			properties.add(new ComboBoxPropertyDescriptor(NodeConstants.GENERIC_TYPE, "Type", sdtTypes));
			properties.add(new TextPropertyDescriptor(NodeConstants.GENERIC_VAL, "Property Value"));
		} else if (node instanceof ServiceNode) {
			properties.add(new TextPropertyDescriptor(NodeConstants.GENERIC_NAME, "Name"));
			properties.add(new TextPropertyDescriptor(NodeConstants.GENERIC_INTF, "Definition"));
		} else if (node instanceof ServiceNode) {
			properties.add(new TextPropertyDescriptor(NodeConstants.GENERIC_NAME, "Name"));
			properties.add(new TextPropertyDescriptor(NodeConstants.GENERIC_INTF, "Interface"));
		} else if (node instanceof ReferenceNode) {
			properties.add(new TextPropertyDescriptor(NodeConstants.GENERIC_NAME, "Name"));
			properties.add(new TextPropertyDescriptor(NodeConstants.GENERIC_INTF, "Interface"));
		}
		return properties.toArray(new IPropertyDescriptor[0]);
	}

	private String[] getComponentImpl() {
		ArrayList<String> ret = new ArrayList<String>();
		ret.add("");
		ArrayList<String> cDef = new PluginUtil().getResourcesWithExtension("cimpl", node.getContainerName());
		for (String str : cDef) {
			String name = FilenameUtils.getBaseName(str);
			ret.add(name);
		}
		return ret.toArray(new String[0]);
	}

	@Override
	public Object getPropertyValue(Object id) {
		if (id.equals(NodeConstants.COMPOSITE_NAME)) {
			String ret = ((CompositeNode) node).getName();
			return (ret != null) ? ret : "";
		} else if (id.equals(NodeConstants.COMPONENT_TYPE)) {
			String ret = ((ComponentNode) node).getType();
			return (ret != null) ? ret : "";
		} else if (id.equals(NodeConstants.COMPONENT_NAME)) {
			String ret = ((ComponentNode) node).getName();
			return (ret != null) ? ret : "";
		} else if (id.equals(NodeConstants.COMPONENT_INST)) {
			return indexOf(((ComponentNode) node).getInst());
		} else if (id.equals(NodeConstants.GENERIC_PROP) && (node instanceof ComponentPropertyNode)) {
			String ret = ((ComponentPropertyNode) node).getName();
			return (ret != null) ? ret : "";
		} else if (id.equals(NodeConstants.GENERIC_PROP) && (node instanceof CompositePropertyNode)) {
			String ret = ((CompositePropertyNode) node).getName();
			return (ret != null) ? ret : "";
		} else if (id.equals(NodeConstants.GENERIC_VAL) && (node instanceof ComponentPropertyNode)) {
			String ret = ((ComponentPropertyNode) node).getValue();
			return (ret != null) ? ret : "";
		} else if (id.equals(NodeConstants.GENERIC_VAL) && (node instanceof CompositePropertyNode)) {
			String ret = ((CompositePropertyNode) node).getValue();
			return (ret != null) ? ret : "";
		} else if (id.equals(NodeConstants.GENERIC_TYPE) && (node instanceof ComponentPropertyNode)) {
			if (((ComponentPropertyNode) node).getType() == null)
				return 0;
			else {
				return indexOfType(((ComponentPropertyNode) node).getType(), sdtTypes);
			}
		} else if (id.equals(NodeConstants.GENERIC_TYPE) && (node instanceof CompositePropertyNode)) {
			if (((CompositePropertyNode) node).getType() == null)
				return 0;
			else {
				return indexOfType(((CompositePropertyNode) node).getType(), sdtTypes);
			}
		} else if (id.equals(NodeConstants.GENERIC_NAME) && (node instanceof ServiceNode)) {
			String ret = ((ServiceNode) node).getName();
			return (ret != null) ? ret : "";
		} else if (id.equals(NodeConstants.GENERIC_INTF) && (node instanceof ServiceNode)) {
			String ret = ((ServiceNode) node).getIntf();
			return (ret != null) ? ret : "";
		} else if (id.equals(NodeConstants.GENERIC_NAME) && (node instanceof ReferenceNode)) {
			String ret = ((ReferenceNode) node).getName();
			return (ret != null) ? ret : "";
		} else if (id.equals(NodeConstants.GENERIC_INTF) && (node instanceof ReferenceNode)) {
			String ret = ((ReferenceNode) node).getIntf();
			return (ret != null) ? ret : "";
		}
		return "";
	}

	private Integer indexOf(String inst) {
		int ret = 0;
		for (String str : getComponentImpl()) {
			if (str.equalsIgnoreCase(inst))
				return ret;
			ret++;
		}
		return ret;
	}

	@Override
	public boolean isPropertySet(Object id) {
		return false;
	}

	@Override
	public void resetPropertyValue(Object id) {
	}

	@Override
	public void setPropertyValue(Object id, Object value) {
		if (id.equals(NodeConstants.COMPONENT_INST)) {
			ComponentNode cNode = (ComponentNode) node;
			cNode.setInst(getComponentImpl()[Integer.valueOf(value.toString())]);
			cNode.refresh();
		}
		if (id.equals(NodeConstants.GENERIC_VAL) && (node instanceof ComponentPropertyNode)) {
			ComponentPropertyNode cNode = (ComponentPropertyNode) node;
			cNode.setValue(value.toString());
			cNode.refresh();
		}
		if (id.equals(NodeConstants.GENERIC_VAL) && (node instanceof CompositePropertyNode)) {
			CompositePropertyNode cNode = (CompositePropertyNode) node;
			cNode.setValue(value.toString());
			cNode.refresh();
		}
		if (id.equals(NodeConstants.GENERIC_TYPE) && (node instanceof ComponentPropertyNode)) {
			ComponentPropertyNode cNode = (ComponentPropertyNode) node;
			cNode.setType(sdtTypes[Integer.parseInt(value.toString())]);
			cNode.refresh();
		}
		if (id.equals(NodeConstants.GENERIC_TYPE) && (node instanceof CompositePropertyNode)) {
			CompositePropertyNode cNode = (CompositePropertyNode) node;
			cNode.setType(sdtTypes[Integer.parseInt(value.toString())]);
			cNode.refresh();
		}
		node.refresh();
	}

	private int indexOfType(String type, String[] types) {
		int i = 0;
		for (String name : types) {
			if (name.equalsIgnoreCase(type))
				return i;
			i++;
		}
		return 0;
	}

	@Override
	public Object getEditableValue() {
		return null;
	}

	private String[] getSimpleTypes() {
		ArrayList<String> ret = new ArrayList<String>();
		ret.add("");
		try {
			TypesUtil util = TypesUtil.getInstance(node.getContainerName());
			util.setFileName("");
			ret.addAll(util.getAllTypesForSimpleWizard());
		} catch (IOException | JAXBException e) {
			e.printStackTrace();
		}
		return ret.toArray(new String[0]);
	}

	public Node getNode() {
		return node;
	}

	public void setNode(Node node) {
		this.node = node;
	}

}
