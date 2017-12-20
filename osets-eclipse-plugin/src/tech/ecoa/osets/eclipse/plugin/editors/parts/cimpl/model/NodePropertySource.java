package tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model;

import java.io.IOException;
import java.util.ArrayList;

import javax.xml.bind.JAXBException;

import org.eclipse.ui.views.properties.ComboBoxPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

import tech.ecoa.osets.eclipse.plugin.util.TypesUtil;

public class NodePropertySource implements IPropertySource {

	private Node node;
	private static String[] mopTypes = Enums.getModuleOperationTypes();
	private static String[] pTypes = Enums.getParameterTypes();
	private static String[] sTypes = Enums.getServiceTypes();
	private static String[] sopTypes = Enums.getServiceOperationTypes();
	private String[] dtTypes;
	private String[] sdtTypes;

	public NodePropertySource(Node node) {
		super();
		this.node = node;
		dtTypes = getAllTypes();
		sdtTypes = getSimpleTypes();
	}

	@Override
	public Object getEditableValue() {
		return null;
	}

	@Override
	public IPropertyDescriptor[] getPropertyDescriptors() {
		ArrayList<IPropertyDescriptor> properties = new ArrayList<IPropertyDescriptor>();
		if (node instanceof ComponentImplementationNode) {
			properties.add(new TextPropertyDescriptor(NodeConstants.COMP_IMPL_COMP_DEF, "Component Definition"));
		} else if (node instanceof ModuleTypeNode) {
			properties.add(new TextPropertyDescriptor(NodeConstants.GENERIC_NAME, "Name"));
			properties.add(new ComboBoxPropertyDescriptor(NodeConstants.MOD_TYPE_SUP_MOD, "Is Supervisory Module", NodeConstants.BOOL_OPTS));
		} else if (node instanceof ModuleImplementationNode) {
			properties.add(new TextPropertyDescriptor(NodeConstants.GENERIC_NAME, "Name"));
			properties.add(new TextPropertyDescriptor(NodeConstants.GENERIC_TYPE, "Module Type"));
			properties.add(new ComboBoxPropertyDescriptor(NodeConstants.MOD_IMPL_LANG, "Language", NodeConstants.LANG_OPTS));
		} else if (node instanceof ModuleInstanceNode) {
			properties.add(new TextPropertyDescriptor(NodeConstants.GENERIC_NAME, "Name"));
			properties.add(new ComboBoxPropertyDescriptor(NodeConstants.MOD_INST_IMPL_NAME, "Implementation Name", getImplementations()));
			properties.add(new TextPropertyDescriptor(NodeConstants.GENERIC_REL_PRIORITY, "Priority"));
		} else if (node instanceof ModuleOperationNode) {
			ModuleOperationNode mNode = (ModuleOperationNode) node;
			properties.add(new TextPropertyDescriptor(NodeConstants.GENERIC_NAME, "Name"));
			properties.add(new ComboBoxPropertyDescriptor(NodeConstants.GENERIC_OP_TYPE, "Operation Type", mopTypes));
			if (mNode.getType() != null && mNode.getType().equalsIgnoreCase(Enums.ModuleOperationTypes.REQUEST_SENT.name())) {
				properties.add(new ComboBoxPropertyDescriptor(NodeConstants.MOD_OP_SYNC, "Is Synchronous", NodeConstants.BOOL_OPTS));
				properties.add(new TextPropertyDescriptor(NodeConstants.MOD_OP_TIMEOUT, "Timeout"));
			}
			if (mNode.getType() != null && (mNode.getType().equalsIgnoreCase(Enums.ModuleOperationTypes.DATA_READ.name()) || mNode.getType().equalsIgnoreCase(Enums.ModuleOperationTypes.DATA_WRITE.name()))) {
				properties.add(new ComboBoxPropertyDescriptor(NodeConstants.GENERIC_DATA_TYPE, "Data Type", getAllTypes()));
			}
		} else if (node instanceof ModuleOperationParameterNode) {
			properties.add(new TextPropertyDescriptor(NodeConstants.GENERIC_NAME, "Name"));
			properties.add(new ComboBoxPropertyDescriptor(NodeConstants.GENERIC_TYPE, "Type", pTypes));
			properties.add(new ComboBoxPropertyDescriptor(NodeConstants.GENERIC_DATA_TYPE, "Data Type", dtTypes));
		} else if (node instanceof TriggerInstanceNode) {
			properties.add(new TextPropertyDescriptor(NodeConstants.GENERIC_NAME, "Name"));
			properties.add(new TextPropertyDescriptor(NodeConstants.GENERIC_REL_PRIORITY, "Relative Priority"));
		} else if (node instanceof DynamicTriggerInstanceNode) {
			properties.add(new TextPropertyDescriptor(NodeConstants.GENERIC_NAME, "Name"));
			properties.add(new TextPropertyDescriptor(NodeConstants.GENERIC_REL_PRIORITY, "Relative Priority"));
		} else if (node instanceof ServiceNode) {
			properties.add(new TextPropertyDescriptor(NodeConstants.GENERIC_NAME, "Name"));
			properties.add(new TextPropertyDescriptor(NodeConstants.SRVC_INTF_NAME, "Service Definition"));
			properties.add(new ComboBoxPropertyDescriptor(NodeConstants.SRVC_TYPE, "Service Provision", sTypes));
		} else if (node instanceof ServiceOperationNode) {
			properties.add(new TextPropertyDescriptor(NodeConstants.GENERIC_NAME, "Name"));
			properties.add(new ComboBoxPropertyDescriptor(NodeConstants.GENERIC_OP_TYPE, "Operation Type", sopTypes));
		} else if (node instanceof ModuleInstancePropertyNode) {
			properties.add(new TextPropertyDescriptor(NodeConstants.GENERIC_NAME, "Name"));
			properties.add(new ComboBoxPropertyDescriptor(NodeConstants.GENERIC_TYPE, "Type", sdtTypes));
			properties.add(new TextPropertyDescriptor(NodeConstants.GENERIC_VALUE, "Value"));
		}
		return properties.toArray(new IPropertyDescriptor[0]);
	}

	@Override
	public Object getPropertyValue(Object id) {
		if (node instanceof ComponentImplementationNode) {
			if (id.equals(NodeConstants.COMP_IMPL_COMP_DEF)) {
				String ret = ((ComponentImplementationNode) node).getName();
				return (ret != null) ? ret : "";
			}
		} else if (node instanceof ModuleTypeNode) {
			ModuleTypeNode val = (ModuleTypeNode) node;
			if (id.equals(NodeConstants.GENERIC_NAME)) {
				String ret = val.getName();
				return (ret != null) ? ret : "";
			} else if (id.equals(NodeConstants.MOD_TYPE_SUP_MOD)) {
				if (val.isSup())
					return 0;
				else
					return 1;
			}
		} else if (node instanceof ModuleImplementationNode) {
			ModuleImplementationNode val = (ModuleImplementationNode) node;
			if (id.equals(NodeConstants.GENERIC_NAME)) {
				String ret = val.getName();
				return (ret != null) ? ret : "";
			} else if (id.equals(NodeConstants.GENERIC_TYPE)) {
				String ret = val.getType();
				return (ret != null) ? ret : "";
			} else if (id.equals(NodeConstants.MOD_IMPL_LANG)) {
				if (val.getLang() == null)
					return 0;
				else {
					if (val.getLang().equalsIgnoreCase("C")) {
						return 1;
					} else if (val.getLang().equalsIgnoreCase("C++")) {
						return 2;
					} else
						return 0;
				}
			}
		} else if (node instanceof ModuleInstanceNode) {
			ModuleInstanceNode val = (ModuleInstanceNode) node;
			if (id.equals(NodeConstants.GENERIC_NAME)) {
				String ret = val.getName();
				return (ret != null) ? ret : "";
			} else if (id.equals(NodeConstants.GENERIC_REL_PRIORITY)) {
				String ret = val.getPriority();
				return (ret != null) ? ret : "";
			} else if (id.equals(NodeConstants.MOD_INST_IMPL_NAME)) {
				if (val.getImpl() == null)
					return 0;
				else {
					return indexOfImpl(val.getImpl(), getImplementations());
				}
			}
		} else if (node instanceof ModuleOperationNode) {
			ModuleOperationNode val = (ModuleOperationNode) node;
			if (id.equals(NodeConstants.GENERIC_NAME)) {
				String ret = val.getName();
				return (ret != null) ? ret : "";
			} else if (id.equals(NodeConstants.GENERIC_OP_TYPE)) {
				if (val.getType() == null)
					return 0;
				else {
					return indexOfMop(val.getType(), Enums.getModuleOperationTypes());
				}
			} else if (id.equals(NodeConstants.MOD_OP_SYNC)) {
				if (val.isSync())
					return 0;
				else
					return 1;
			} else if (id.equals(NodeConstants.MOD_OP_TIMEOUT)) {
				String ret = val.getTimeout();
				return (ret != null) ? ret : "";
			} else if (id.equals(NodeConstants.GENERIC_DATA_TYPE)) {
				if (val.getdType() == null)
					return 0;
				else {
					return indexOfMod(val.getdType(), getAllTypes());
				}
			}
		} else if (node instanceof ModuleOperationParameterNode) {
			ModuleOperationParameterNode val = (ModuleOperationParameterNode) node;
			if (id.equals(NodeConstants.GENERIC_NAME)) {
				String ret = val.getName();
				return (ret != null) ? ret : "";
			} else if (id.equals(NodeConstants.GENERIC_TYPE)) {
				if (val.getType() == null)
					return 0;
				else {
					return indexOfMopp(val.getType(), Enums.getParameterTypes());
				}
			} else if (id.equals(NodeConstants.GENERIC_DATA_TYPE)) {
				if (val.getdType() == null)
					return 0;
				else {
					return indexOfMopd(val.getdType(), getAllTypes());
				}
			}
		} else if (node instanceof TriggerInstanceNode) {
			TriggerInstanceNode val = (TriggerInstanceNode) node;
			if (id.equals(NodeConstants.GENERIC_NAME)) {
				String ret = val.getName();
				return (ret != null) ? ret : "";
			} else if (id.equals(NodeConstants.GENERIC_REL_PRIORITY)) {
				String ret = val.getPriority();
				return (ret != null) ? ret : "";
			}
		} else if (node instanceof DynamicTriggerInstanceNode) {
			DynamicTriggerInstanceNode val = (DynamicTriggerInstanceNode) node;
			if (id.equals(NodeConstants.GENERIC_NAME)) {
				String ret = val.getName();
				return (ret != null) ? ret : "";
			} else if (id.equals(NodeConstants.GENERIC_REL_PRIORITY)) {
				String ret = val.getPriority();
				return (ret != null) ? ret : "";
			}
		} else if (node instanceof ServiceNode) {
			ServiceNode val = (ServiceNode) node;
			if (id.equals(NodeConstants.GENERIC_NAME)) {
				String ret = val.getName();
				return (ret != null) ? ret : "";
			} else if (id.equals(NodeConstants.SRVC_INTF_NAME)) {
				String ret = val.getIntf();
				return (ret != null) ? ret : "";
			} else if (id.equals(NodeConstants.SRVC_TYPE)) {
				if (val.getType() == null)
					return 0;
				else {
					return indexOfSrvc(val.getType(), Enums.getServiceTypes());
				}
			}
		} else if (node instanceof ServiceOperationNode) {
			ServiceOperationNode val = (ServiceOperationNode) node;
			if (id.equals(NodeConstants.GENERIC_NAME)) {
				String ret = val.getName();
				return (ret != null) ? ret : "";
			} else if (id.equals(NodeConstants.GENERIC_OP_TYPE)) {
				if (val.getType() == null)
					return 0;
				else {
					return indexOfSop(val.getType(), Enums.getServiceOperationTypes());
				}
			}
		} else if (node instanceof ModuleInstancePropertyNode) {
			ModuleInstancePropertyNode val = (ModuleInstancePropertyNode) node;
			if (id.equals(NodeConstants.GENERIC_NAME)) {
				String ret = val.getName();
				return (ret != null) ? ret : "";
			} else if (id.equals(NodeConstants.GENERIC_VALUE)) {
				String ret = val.getValue();
				return (ret != null) ? ret : "";
			} else if (id.equals(NodeConstants.GENERIC_TYPE)) {
				if (val.getType() == null)
					return 0;
				else {
					return indexOfType(val.getType(), sdtTypes);
				}
			}
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
	public void setPropertyValue(Object id, Object obj) {
		String value = obj.toString();
		if (node instanceof ModuleTypeNode) {
			ModuleTypeNode val = (ModuleTypeNode) node;
			if (id.equals(NodeConstants.GENERIC_NAME)) {
				val.setName(value);
				for (Node child : val.getChild()) {
					if (child instanceof ModuleImplementationNode) {
						ModuleImplementationNode cNode = (ModuleImplementationNode) child;
						cNode.setType(value);
					}
				}
			} else if (id.equals(NodeConstants.MOD_TYPE_SUP_MOD)) {
				val.setSup(Boolean.parseBoolean(NodeConstants.BOOL_OPTS[Integer.parseInt(value)]));
			}
		} else if (node instanceof ModuleImplementationNode) {
			ModuleImplementationNode val = (ModuleImplementationNode) node;
			if (id.equals(NodeConstants.GENERIC_NAME)) {
				val.setName(value);
			} else if (id.equals(NodeConstants.MOD_IMPL_LANG)) {
				val.setLang(NodeConstants.LANG_OPTS[Integer.parseInt(value)]);
			}
		} else if (node instanceof ModuleInstanceNode) {
			ModuleInstanceNode val = (ModuleInstanceNode) node;
			if (id.equals(NodeConstants.GENERIC_NAME)) {
				val.setName(value);
			} else if (id.equals(NodeConstants.GENERIC_REL_PRIORITY)) {
				val.setPriority(value);
			} else if (id.equals(NodeConstants.MOD_INST_IMPL_NAME)) {
				val.setImpl(getImplementations()[Integer.parseInt(value)]);
			}
		} else if (node instanceof ModuleOperationNode) {
			ModuleOperationNode val = (ModuleOperationNode) node;
			if (id.equals(NodeConstants.GENERIC_NAME)) {
				val.setName(value);
			} else if (id.equals(NodeConstants.GENERIC_OP_TYPE)) {
				val.setType(Enums.getModuleOperationTypes()[Integer.parseInt(value)]);
			} else if (id.equals(NodeConstants.MOD_OP_SYNC)) {
				val.setSync(Boolean.parseBoolean(NodeConstants.BOOL_OPTS[Integer.parseInt(value)]));
			} else if (id.equals(NodeConstants.MOD_OP_TIMEOUT)) {
				val.setTimeout(value);
			} else if (id.equals(NodeConstants.GENERIC_DATA_TYPE)) {
				val.setdType(getAllTypes()[Integer.parseInt(value)]);
			}
		} else if (node instanceof ModuleOperationParameterNode) {
			ModuleOperationParameterNode val = (ModuleOperationParameterNode) node;
			if (id.equals(NodeConstants.GENERIC_NAME)) {
				val.setName(value);
			} else if (id.equals(NodeConstants.GENERIC_TYPE)) {
				val.setType(Enums.getParameterTypes()[Integer.parseInt(value)]);
			} else if (id.equals(NodeConstants.GENERIC_DATA_TYPE)) {
				val.setdType(getAllTypes()[Integer.parseInt(value)]);
			}
		} else if (node instanceof TriggerInstanceNode) {
			TriggerInstanceNode val = (TriggerInstanceNode) node;
			if (id.equals(NodeConstants.GENERIC_NAME)) {
				val.setName(value);
			} else if (id.equals(NodeConstants.GENERIC_REL_PRIORITY)) {
				val.setPriority(value);
			}
		} else if (node instanceof DynamicTriggerInstanceNode) {
			DynamicTriggerInstanceNode val = (DynamicTriggerInstanceNode) node;
			if (id.equals(NodeConstants.GENERIC_NAME)) {
				val.setName(value);
			} else if (id.equals(NodeConstants.GENERIC_REL_PRIORITY)) {
				val.setPriority(value);
			}
		} else if (node instanceof ModuleInstancePropertyNode) {
			ModuleInstancePropertyNode val = (ModuleInstancePropertyNode) node;
			if (id.equals(NodeConstants.GENERIC_NAME)) {
				val.setName(value);
			} else if (id.equals(NodeConstants.GENERIC_TYPE)) {
				val.setType(sdtTypes[Integer.parseInt(value)]);
			} else if (id.equals(NodeConstants.GENERIC_VALUE)) {
				val.setValue(value);
			}
		}
		node.refreshAll(node.getRootNode());
	}

	protected String[] getImplementations() {
		ArrayList<String> names = new ArrayList<String>();
		names.add("");
		if (node instanceof ModuleInstanceNode) {
			ModuleTypeNode parent = (ModuleTypeNode) node.getParent();
			for (Node cNd : parent.getChild())
				if (cNd instanceof ModuleImplementationNode)
					names.add(((ModuleImplementationNode) cNd).getName());
		}
		return names.toArray(new String[0]);
	}

	protected String getTypeName() {
		ModuleTypeNode parent = (ModuleTypeNode) node.getParent();
		return parent.getName();
	}

	protected String[] getAllTypes() {
		ArrayList<String> ret = new ArrayList<String>();
		ret.add("");
		try {
			TypesUtil util = TypesUtil.getInstance(node.getContainerName());
			util.setFileName("");
			ret.addAll(util.getAllTypes());
		} catch (IOException | JAXBException e) {
			e.printStackTrace();
		}
		return ret.toArray(new String[0]);
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

	private Integer indexOfSop(String type, String[] types) {
		int i = 0;
		for (String name : types) {
			if (name.equalsIgnoreCase(type))
				return i;
			i++;
		}
		return 0;
	}

	private Integer indexOfSrvc(String type, String[] types) {
		int i = 0;
		for (String name : types) {
			if (name.equalsIgnoreCase(type))
				return i;
			i++;
		}
		return 0;
	}

	private Integer indexOfMopp(String type, String[] types) {
		int i = 0;
		for (String name : types) {
			if (name.equalsIgnoreCase(type))
				return i;
			i++;
		}
		return 0;
	}

	private Integer indexOfMopd(String type, String[] types) {
		int i = 0;
		for (String name : types) {
			if (name.equalsIgnoreCase(type))
				return i;
			i++;
		}
		return 0;
	}

	private Integer indexOfMod(String type, String[] types) {
		int i = 0;
		for (String name : types) {
			if (name.equalsIgnoreCase(type))
				return i;
			i++;
		}
		return 0;
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

	private Integer indexOfMop(String type, String[] types) {
		int i = 0;
		for (String name : types) {
			if (name.equalsIgnoreCase(type))
				return i;
			i++;
		}
		return 0;
	}

	private Integer indexOfImpl(String impl, String[] impls) {
		int i = 0;
		for (String name : impls) {
			if (name.equalsIgnoreCase(impl))
				return i;
			i++;
		}
		return 0;
	}

	public Node getNode() {
		return node;
	}

	public void setNode(Node node) {
		this.node = node;
	}

}
