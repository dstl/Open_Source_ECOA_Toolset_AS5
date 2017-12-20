package tech.ecoa.osets.eclipse.plugin.editors.parts.assmbl.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;

import org.apache.commons.io.FilenameUtils;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.views.properties.ComboBoxPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

import tech.ecoa.osets.eclipse.plugin.common.Constants;
import tech.ecoa.osets.eclipse.plugin.editors.parts.assmbl.commands.ComponentPropertyNodeDeleteCommand;
import tech.ecoa.osets.eclipse.plugin.editors.parts.assmbl.commands.LinkDeleteCommand;
import tech.ecoa.osets.eclipse.plugin.util.CompDefUtil;
import tech.ecoa.osets.eclipse.plugin.util.PluginUtil;
import tech.ecoa.osets.eclipse.plugin.util.TypesUtil;
import tech.ecoa.osets.model.cdef.ComponentType;
import tech.ecoa.osets.model.cdef.ComponentType.Service;
import tech.ecoa.osets.model.cdef.ComponentTypeReference;
import tech.ecoa.osets.model.cdef.EcoaInterface;
import tech.ecoa.osets.model.cdef.Property;

public class NodePropertySource implements IPropertySource {

	private Node node;
	private ArrayList<Link> modifiedLinks;
	private ArrayList<Node> properties;
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
			properties.add(new ComboBoxPropertyDescriptor(NodeConstants.COMPONENT_TYPE, "Component Type", getComponentDefs()));
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
		} else if (node instanceof ReferenceNode) {
			properties.add(new TextPropertyDescriptor(NodeConstants.GENERIC_NAME, "Name"));
			properties.add(new TextPropertyDescriptor(NodeConstants.GENERIC_INTF, "Definition"));
		}
		return properties.toArray(new IPropertyDescriptor[0]);
	}

	private String[] getComponentDefs() {
		ArrayList<String> ret = new ArrayList<String>();
		ret.add("");
		ArrayList<String> cDef = new PluginUtil().getResourcesWithExtension("cdef", node.getContainerName());
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
			return indexOf(((ComponentNode) node).getType());
		} else if (id.equals(NodeConstants.COMPONENT_NAME)) {
			String ret = ((ComponentNode) node).getName();
			return (ret != null) ? ret : "";
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

	@Override
	public boolean isPropertySet(Object id) {
		return false;
	}

	@Override
	public void resetPropertyValue(Object id) {
	}

	@Override
	public void setPropertyValue(Object id, Object value) {
		if (id.equals(NodeConstants.COMPOSITE_NAME)) {
			CompositeNode cNode = (CompositeNode) node;
			cNode.setName(value.toString());
			cNode.refresh();
		}
		if (id.equals(NodeConstants.COMPONENT_NAME)) {
			ComponentNode cNode = (ComponentNode) node;
			cNode.setName(value.toString());
			cNode.refresh();
		}
		if (id.equals(NodeConstants.GENERIC_PROP) && (node instanceof ComponentPropertyNode)) {
			ComponentPropertyNode cNode = (ComponentPropertyNode) node;
			cNode.setName(value.toString());
			cNode.refresh();
		}
		if (id.equals(NodeConstants.GENERIC_PROP) && (node instanceof CompositePropertyNode)) {
			CompositePropertyNode cNode = (CompositePropertyNode) node;
			cNode.setName(value.toString());
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
		if (id.equals(NodeConstants.COMPONENT_TYPE)) {
			Shell shell = Display.getDefault().getActiveShell();
			boolean confirm = MessageDialog.openQuestion(shell, "Confirm Modify", "Changing Component Type. Cannot UNDO / REDO. Are you sure?");
			if (confirm) {
				ComponentNode cNode = (ComponentNode) node;
				String type = getComponentDefs()[Integer.valueOf(value.toString())];
				try {
					CompoundCommand cCmd = new CompoundCommand();
					getModifiedlinks(cNode);
					if (modifiedLinks != null && modifiedLinks.size() > 0) {
						for (Link link : modifiedLinks) {
							LinkDeleteCommand lCmd = new LinkDeleteCommand();
							lCmd.setLink(link);
							lCmd.setSource(lCmd.getLink().getSource());
							lCmd.setTarget(lCmd.getLink().getTarget());
							cCmd.add(lCmd);
						}
					}
					getProperties(cNode);
					if (properties != null && properties.size() > 0) {
						for (Node child : properties) {
							ComponentPropertyNodeDeleteCommand pCmd = new ComponentPropertyNodeDeleteCommand();
							pCmd.setNode((ComponentPropertyNode) child);
							pCmd.setParent(cNode);
							cCmd.add(pCmd);
						}
					}
					cCmd.execute();
					ComponentType def = CompDefUtil.getInstance(cNode.getContainerName()).getByName(type);
					if (def != null && def.getServiceOrReferenceOrProperty() != null && def.getServiceOrReferenceOrProperty().size() > 0) {
						cNode.getChild().clear();
						int i = 1;
						for (Object obj : def.getServiceOrReferenceOrProperty()) {
							if (obj instanceof Service) {
								Service srvc = (Service) obj;
								ServiceNode sNode = new ServiceNode();
								sNode.setContainerName(cNode.getContainerName());
								sNode.setId("" + UUID.randomUUID().toString());
								sNode.setParent(cNode);
								sNode.setName(srvc.getName());
								sNode.setIntf(((EcoaInterface) srvc.getInterface().getValue()).getSyntax());
								sNode.setConstraints(new Rectangle(i * 50, i * 50, ServiceNode.DEF_WIDTH, ServiceNode.DEF_HEIGHT));
								cNode.getChild().add(sNode);
							} else if (obj instanceof ComponentTypeReference) {
								ComponentTypeReference ref = (ComponentTypeReference) obj;
								ReferenceNode rNode = new ReferenceNode();
								rNode.setContainerName(cNode.getContainerName());
								rNode.setId("" + UUID.randomUUID().toString());
								rNode.setParent(cNode);
								rNode.setName(ref.getName());
								rNode.setIntf(((EcoaInterface) ref.getInterface().getValue()).getSyntax());
								rNode.setConstraints(new Rectangle(i * 50, i * 50, ReferenceNode.DEF_WIDTH, ReferenceNode.DEF_HEIGHT));
								cNode.getChild().add(rNode);
							} else if (obj instanceof Property) {
								Property ref = (Property) obj;
								ComponentPropertyNode rNode = new ComponentPropertyNode();
								rNode.setContainerName(cNode.getContainerName());
								rNode.setId("" + UUID.randomUUID().toString());
								rNode.setParent(cNode);
								rNode.setName(ref.getName());
								rNode.setType(getType(ref));
								rNode.setConstraints(new Rectangle(i * 50, i * 50, ReferenceNode.DEF_WIDTH, ReferenceNode.DEF_HEIGHT));
								cNode.getChild().add(rNode);
							}
							i++;
						}
					}
					cNode.setType(type);
					cNode.refreshAll(cNode.getCompositeNode());
				} catch (JAXBException | IOException e) {
					e.printStackTrace();
				}
			}
		}
		node.refresh();
	}

	private String getType(Property val) {
		Map<QName, String> attrib = val.getOtherAttributes();
		if (attrib == null)
			return "";
		else {
			if (attrib.get(Constants.TYPE_QNAME) == null)
				return "";
			else
				return attrib.get(Constants.TYPE_QNAME);
		}
	}

	private void getModifiedlinks(ComponentNode node) {
		modifiedLinks = new ArrayList<Link>();
		CompositeNode root = node.getCompositeNode();
		for (Node child : node.getChild()) {
			for (Link link : root.getLinks()) {
				if (link.getSource().equals(child) || link.getTarget().equals(child)) {
					modifiedLinks.add(link);
				}
			}
		}
	}

	private void getProperties(ComponentNode node) {
		properties = new ArrayList<Node>();
		for (Node child : node.getChild()) {
			if (child instanceof ComponentPropertyNode)
				properties.add(child);
		}
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

	private int indexOfType(String type, String[] types) {
		int i = 0;
		for (String name : types) {
			if (name.equalsIgnoreCase(type))
				return i;
			i++;
		}
		return 0;
	}

	private Integer indexOf(String type) {
		int ret = 0;
		for (String str : getComponentDefs()) {
			if (str.equalsIgnoreCase(type))
				return ret;
			ret++;
		}
		return ret;
	}

	@Override
	public Object getEditableValue() {
		return null;
	}

	public Node getNode() {
		return node;
	}

	public void setNode(Node node) {
		this.node = node;
	}

	public ArrayList<String> validate() {
		ArrayList<String> ret = new ArrayList<>();
		return ret;
	}
}
