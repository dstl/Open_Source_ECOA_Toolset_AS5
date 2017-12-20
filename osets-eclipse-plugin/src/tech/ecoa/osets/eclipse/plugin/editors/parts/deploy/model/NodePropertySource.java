package tech.ecoa.osets.eclipse.plugin.editors.parts.deploy.model;

import java.util.ArrayList;

import org.eclipse.ui.views.properties.ComboBoxPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

public class NodePropertySource implements IPropertySource {

	private Node node;

	public NodePropertySource(Node node) {
		super();
		this.node = node;
	}

	@Override
	public Object getEditableValue() {
		return null;
	}

	@Override
	public IPropertyDescriptor[] getPropertyDescriptors() {
		ArrayList<IPropertyDescriptor> properties = new ArrayList<IPropertyDescriptor>();
		if (node instanceof DeploymentNode) {
			properties.add(new TextPropertyDescriptor(NodeConstants.DEP_LOG_SYS, "Logical System"));
			properties.add(new TextPropertyDescriptor(NodeConstants.DEP_FINAL_ASSMBL, "Final Assembly"));
		} else if (node instanceof ProtectionDomainNode) {
			properties.add(new TextPropertyDescriptor(NodeConstants.GENERIC_NAME, "Name"));
			properties.add(new ComboBoxPropertyDescriptor(NodeConstants.EXEC_ON_COMP_NODE, "Execution Computing Node", getCNNames()));
			properties.add(new ComboBoxPropertyDescriptor(NodeConstants.GENERIC_COMP_PLAT, "Execution Platform", getPCNames()));
		} else if (node instanceof ComputingNodeConfigurationNode) {
			properties.add(new TextPropertyDescriptor(NodeConstants.GENERIC_NAME, "Computing Node"));
			properties.add(new TextPropertyDescriptor(NodeConstants.COMP_NODE_SCHED_INFO, "Scheduling Information"));
		} else if (node instanceof PlatformConfigurationNode) {
			properties.add(new TextPropertyDescriptor(NodeConstants.GENERIC_COMP_PLAT, "Platform"));
			properties.add(new TextPropertyDescriptor(NodeConstants.PLAT_CONF_NOTIF_MAX, "Max Notifications"));
			properties.add(new TextPropertyDescriptor(NodeConstants.LOG_PLAT_MCAST_ADDR, "Multicast Address"));
			properties.add(new TextPropertyDescriptor(NodeConstants.LOG_PLAT_PORT, "Port"));
			properties.add(new TextPropertyDescriptor(NodeConstants.LOG_PLAT_ID, "Platform Id"));
		} else if (node instanceof DeployedModuleInstanceNode) {
			properties.add(new TextPropertyDescriptor(NodeConstants.GENERIC_COMP_NAME, "Component Instance"));
			properties.add(new TextPropertyDescriptor(NodeConstants.DEP_MOD_INST_NAME, "Module Instance"));
			properties.add(new TextPropertyDescriptor(NodeConstants.DEP_MOD_PRIORITY, "Priority"));
			properties.add(new ComboBoxPropertyDescriptor(NodeConstants.PD_NAME, "Protection Domain", getPDNames()));
		} else if (node instanceof DeployedTriggerInstanceNode) {
			properties.add(new TextPropertyDescriptor(NodeConstants.GENERIC_COMP_NAME, "Component Instance"));
			properties.add(new TextPropertyDescriptor(NodeConstants.DEP_TRG_INST_NAME, "Trigger Instance"));
			properties.add(new TextPropertyDescriptor(NodeConstants.DEP_TRG_PRIORITY, "Priority"));
			properties.add(new ComboBoxPropertyDescriptor(NodeConstants.PD_NAME, "Protection Domain", getPDNames()));
		}
		return properties.toArray(new IPropertyDescriptor[0]);
	}

	@Override
	public Object getPropertyValue(Object id) {
		if (node instanceof DeploymentNode) {
			DeploymentNode val = (DeploymentNode) node;
			if (id.equals(NodeConstants.DEP_LOG_SYS)) {
				return (val.getlSys() != null) ? val.getlSys() : "";
			} else if (id.equals(NodeConstants.DEP_FINAL_ASSMBL)) {
				return (val.getfAssmbl() != null) ? val.getfAssmbl() : "";
			}
		} else if (node instanceof ProtectionDomainNode) {
			ProtectionDomainNode val = (ProtectionDomainNode) node;
			if (id.equals(NodeConstants.GENERIC_NAME)) {
				return (val.getName() != null) ? val.getName() : "";
			} else if (id.equals(NodeConstants.EXEC_ON_COMP_NODE)) {
				return (val.getEoCompNode() != null) ? indexOf(val.getEoCompNode(), getCNNames()) : 0;
			} else if (id.equals(NodeConstants.GENERIC_COMP_PLAT)) {
				return (val.getEoCompPlatform() != null) ? indexOf(val.getEoCompPlatform(), getPCNames()) : 0;
			}
		} else if (node instanceof PlatformConfigurationNode) {
			PlatformConfigurationNode val = (PlatformConfigurationNode) node;
			if (id.equals(NodeConstants.GENERIC_COMP_PLAT)) {
				return (val.getCompPlatform() != null) ? val.getCompPlatform() : "";
			} else if (id.equals(NodeConstants.PLAT_CONF_NOTIF_MAX)) {
				return (val.getNotifMaxNumber() != null) ? val.getNotifMaxNumber() : "";
			} else if (id.equals(NodeConstants.LOG_PLAT_MCAST_ADDR)) {
				return (val.getMcastAddr() != null) ? val.getMcastAddr() : "";
			} else if (id.equals(NodeConstants.LOG_PLAT_PORT)) {
				return (val.getPort() != null) ? val.getPort() : "";
			} else if (id.equals(NodeConstants.LOG_PLAT_ID)) {
				return (val.getPlatNum() != null) ? val.getPlatNum() : "";
			}
		} else if (node instanceof ComputingNodeConfigurationNode) {
			ComputingNodeConfigurationNode val = (ComputingNodeConfigurationNode) node;
			if (id.equals(NodeConstants.GENERIC_NAME)) {
				return (val.getName() != null) ? val.getName() : "";
			} else if (id.equals(NodeConstants.COMP_NODE_SCHED_INFO)) {
				return (val.getSchedInfo() != null) ? val.getSchedInfo() : "";
			}
		} else if (node instanceof DeployedModuleInstanceNode) {
			DeployedModuleInstanceNode val = (DeployedModuleInstanceNode) node;
			ProtectionDomainNode par = (ProtectionDomainNode) val.getParent();
			if (id.equals(NodeConstants.GENERIC_COMP_NAME)) {
				return (val.getCompName() != null) ? val.getCompName() : "";
			} else if (id.equals(NodeConstants.DEP_MOD_INST_NAME)) {
				return (val.getModuleName() != null) ? val.getModuleName() : "";
			} else if (id.equals(NodeConstants.DEP_MOD_PRIORITY)) {
				return (val.getPriority() != null) ? val.getPriority() : "";
			} else if (id.equals(NodeConstants.PD_NAME)) {
				return (par.getName() != null) ? indexOf(par.getName(), getPDNames()) : 0;
			}
		} else if (node instanceof DeployedTriggerInstanceNode) {
			DeployedTriggerInstanceNode val = (DeployedTriggerInstanceNode) node;
			ProtectionDomainNode par = (ProtectionDomainNode) val.getParent();
			if (id.equals(NodeConstants.GENERIC_COMP_NAME)) {
				return (val.getCompName() != null) ? val.getCompName() : "";
			} else if (id.equals(NodeConstants.DEP_TRG_INST_NAME)) {
				return (val.getTriggerName() != null) ? val.getTriggerName() : "";
			} else if (id.equals(NodeConstants.DEP_TRG_PRIORITY)) {
				return (val.getPriority() != null) ? val.getPriority().toString() : "";
			} else if (id.equals(NodeConstants.PD_NAME)) {
				return (par.getName() != null) ? indexOf(par.getName(), getPDNames()) : 0;
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
	public void setPropertyValue(Object id, Object value) {
		String string = value.toString();
		if (node instanceof DeploymentNode) {
			DeploymentNode val = (DeploymentNode) node;
			if (id.equals(NodeConstants.DEP_LOG_SYS)) {
				val.setlSys(string);
			} else if (id.equals(NodeConstants.DEP_FINAL_ASSMBL)) {
				val.setfAssmbl(string);
			}
		} else if (node instanceof ProtectionDomainNode) {
			ProtectionDomainNode val = (ProtectionDomainNode) node;
			if (id.equals(NodeConstants.GENERIC_NAME)) {
				val.setName(string);
			} else if (id.equals(NodeConstants.EXEC_ON_COMP_NODE)) {
				String name = getCNNames()[Integer.valueOf(string)];
				val.setEoCompNode(name);
			} else if (id.equals(NodeConstants.GENERIC_COMP_PLAT)) {
				String name = getPCNames()[Integer.valueOf(string)];
				val.setEoCompPlatform(name);
			}
		} else if (node instanceof PlatformConfigurationNode) {
			PlatformConfigurationNode val = (PlatformConfigurationNode) node;
			if (id.equals(NodeConstants.GENERIC_COMP_PLAT)) {
				val.setCompPlatform(string);
			} else if (id.equals(NodeConstants.PLAT_CONF_NOTIF_MAX)) {
				val.setNotifMaxNumber(string);
			} else if (id.equals(NodeConstants.LOG_PLAT_MCAST_ADDR)) {
				val.setMcastAddr(string);
			} else if (id.equals(NodeConstants.LOG_PLAT_PORT)) {
				val.setPort(string);
			} else if (id.equals(NodeConstants.LOG_PLAT_ID)) {
				val.setPlatNum(string);
			}
		} else if (node instanceof ComputingNodeConfigurationNode) {
			ComputingNodeConfigurationNode val = (ComputingNodeConfigurationNode) node;
			if (id.equals(NodeConstants.GENERIC_NAME)) {
				val.setName(string);
			} else if (id.equals(NodeConstants.COMP_NODE_SCHED_INFO)) {
				val.setSchedInfo(string);
			}
		} else if (node instanceof DeployedModuleInstanceNode) {
			DeployedModuleInstanceNode val = (DeployedModuleInstanceNode) node;
			ProtectionDomainNode par = (ProtectionDomainNode) val.getParent();
			if (id.equals(NodeConstants.GENERIC_COMP_NAME)) {
				val.setCompName(string);
			} else if (id.equals(NodeConstants.DEP_MOD_INST_NAME)) {
				val.setModuleName(string);
			} else if (id.equals(NodeConstants.DEP_MOD_PRIORITY)) {
				val.setPriority(string);
			} else if (id.equals(NodeConstants.PD_NAME)) {
				String name = getPDNames()[Integer.valueOf(string)];
				ProtectionDomainNode chg = getNodeByName(name);
				par.getChild().remove(node);
				node.setParent(chg);
				chg.getChild().add(node);
				par.refresh();
				chg.refresh();
			}
		} else if (node instanceof DeployedTriggerInstanceNode) {
			DeployedTriggerInstanceNode val = (DeployedTriggerInstanceNode) node;
			ProtectionDomainNode par = (ProtectionDomainNode) val.getParent();
			if (id.equals(NodeConstants.GENERIC_COMP_NAME)) {
				val.setCompName(string);
			} else if (id.equals(NodeConstants.DEP_TRG_INST_NAME)) {
				val.setTriggerName(string);
			} else if (id.equals(NodeConstants.DEP_TRG_PRIORITY)) {
				val.setPriority(string);
			} else if (id.equals(NodeConstants.PD_NAME)) {
				String name = getPDNames()[Integer.valueOf(string)];
				ProtectionDomainNode chg = getNodeByName(name);
				par.getChild().remove(node);
				node.setParent(chg);
				chg.getChild().add(node);
				par.refresh();
				chg.refresh();
			}
		}
		node.refresh();
	}

	private String[] getPDNames() {
		DeploymentNode pNode = node.getRootNode();
		ArrayList<String> pdNames = new ArrayList<String>();
		pdNames.add("");
		for (Node child : pNode.getChild()) {
			if (child instanceof ProtectionDomainNode) {
				pdNames.add(((ProtectionDomainNode) child).getName());
			}
		}
		return pdNames.toArray(new String[0]);
	}

	private String[] getPCNames() {
		DeploymentNode pNode = node.getRootNode();
		ArrayList<String> pdNames = new ArrayList<String>();
		pdNames.add("");
		for (Node child : pNode.getChild()) {
			if (child instanceof PlatformConfigurationNode) {
				pdNames.add(((PlatformConfigurationNode) child).getCompPlatform());
			}
		}
		return pdNames.toArray(new String[0]);
	}

	private String[] getCNNames() {
		DeploymentNode pNode = node.getRootNode();
		ProtectionDomainNode curr = (ProtectionDomainNode) node;
		ArrayList<String> pdNames = new ArrayList<String>();
		pdNames.add("");
		for (Node child : pNode.getChild()) {
			if (child instanceof PlatformConfigurationNode) {
				if (curr.getEoCompPlatform() == null || curr.getEoCompPlatform().trim().length() == 0)
					for (Node gChild : child.getChild()) {
						if (gChild instanceof ComputingNodeConfigurationNode) {
							pdNames.add(((ComputingNodeConfigurationNode) gChild).getName());
						}
					}
				else {
					if (((PlatformConfigurationNode) child).getCompPlatform().equalsIgnoreCase(curr.getEoCompPlatform()))
						for (Node gChild : child.getChild()) {
							if (gChild instanceof ComputingNodeConfigurationNode) {
								pdNames.add(((ComputingNodeConfigurationNode) gChild).getName());
							}
						}
				}
			}
		}
		return pdNames.toArray(new String[0]);
	}

	private ProtectionDomainNode getNodeByName(String name) {
		DeploymentNode pNode = node.getRootNode();
		for (Node child : pNode.getChild()) {
			if (child instanceof ProtectionDomainNode) {
				if (((ProtectionDomainNode) child).getName().equalsIgnoreCase(name))
					return (ProtectionDomainNode) child;
			}
		}
		return null;
	}

	private int indexOf(String name, String[] names) {
		int i = 0;
		for (String val : names) {
			if (val.equalsIgnoreCase(name))
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
