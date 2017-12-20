package tech.ecoa.osets.eclipse.plugin.editors.parts.lsys.model;

import java.util.ArrayList;

import org.eclipse.ui.views.properties.ComboBoxPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

public class NodePropertySource implements IPropertySource {

	private static final String[] ENDIAN_OPTS = new String[] { "", "LITTLE", "BIG" };
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
		if (node instanceof LogicalSystemNode) {
			properties.add(new TextPropertyDescriptor(NodeConstants.GENERIC_ID, "System Id"));
		} else if (node instanceof LogicalComputingPlatformNode) {
			properties.add(new TextPropertyDescriptor(NodeConstants.GENERIC_ID, "Platform Id"));
		} else if (node instanceof LogicalComputingNode) {
			properties.add(new TextPropertyDescriptor(NodeConstants.GENERIC_ID, "Node Id"));
			properties.add(new ComboBoxPropertyDescriptor(NodeConstants.LOG_SYS_NODE_ENDIAN, "Endianess", ENDIAN_OPTS));
			properties.add(new TextPropertyDescriptor(NodeConstants.LOG_SYS_OS_NAME, "OS Name"));
			properties.add(new TextPropertyDescriptor(NodeConstants.LOG_SYS_OS_VER, "OS Version"));
			properties.add(new TextPropertyDescriptor(NodeConstants.LOG_SYS_AVAIL_MEM_GB, "Available Memory (GB)"));
			properties.add(new TextPropertyDescriptor(NodeConstants.LOG_SYS_MOD_SWITCH_TIME, "Switch Time (µs)"));
		} else if (node instanceof LogicalProcessorsNode) {
			properties.add(new TextPropertyDescriptor(NodeConstants.LOG_SYS_PROC_NUM, "Number"));
			properties.add(new TextPropertyDescriptor(NodeConstants.LOG_SYS_PROC_TYPE, "Type"));
			properties.add(new TextPropertyDescriptor(NodeConstants.LOG_SYS_STEP_DUR, "Step Duration (ns)"));
		}
		return properties.toArray(new IPropertyDescriptor[0]);
	}

	@Override
	public Object getPropertyValue(Object id) {
		if (node instanceof LogicalSystemNode) {
			if (id.equals(NodeConstants.GENERIC_ID)) {
				String ret = ((LogicalSystemNode) node).getName();
				return (ret != null) ? ret : "";
			}
		} else if (node instanceof LogicalComputingPlatformNode) {
			if (id.equals(NodeConstants.GENERIC_ID)) {
				String ret = ((LogicalComputingPlatformNode) node).getName();
				return (ret != null) ? ret : "";
			}
		} else if (node instanceof LogicalComputingNode) {
			LogicalComputingNode val = (LogicalComputingNode) node;
			if (id.equals(NodeConstants.GENERIC_ID)) {
				String ret = val.getName();
				return (ret != null) ? ret : "";
			} else if (id.equals(NodeConstants.LOG_SYS_NODE_ENDIAN)) {
				if ((val.getEndianess() != null)) {
					if (val.getEndianess().equalsIgnoreCase("LITTLE"))
						return 1;
					else if (val.getEndianess().equalsIgnoreCase("BIG"))
						return 2;
					else
						return 0;
				} else
					return 0;
			} else if (id.equals(NodeConstants.LOG_SYS_OS_NAME)) {
				return (val.getOsName() != null) ? val.getOsName() : "";
			} else if (id.equals(NodeConstants.LOG_SYS_OS_VER)) {
				return (val.getOsVer() != null) ? val.getOsVer() : "";
			} else if (id.equals(NodeConstants.LOG_SYS_AVAIL_MEM_GB)) {
				return (val.getAvailMem() != null) ? val.getAvailMem() : "";
			} else if (id.equals(NodeConstants.LOG_SYS_MOD_SWITCH_TIME)) {
				return (val.getMst() != null) ? val.getMst() : "";
			}
		} else if (node instanceof LogicalProcessorsNode) {
			LogicalProcessorsNode val = (LogicalProcessorsNode) node;
			if (id.equals(NodeConstants.LOG_SYS_PROC_NUM)) {
				return (val.getNum() != null) ? val.getNum() : "";
			} else if (id.equals(NodeConstants.LOG_SYS_PROC_TYPE)) {
				return (val.getType() != null) ? val.getType() : "";
			} else if (id.equals(NodeConstants.LOG_SYS_STEP_DUR)) {
				return (val.getStepDur() != null) ? val.getStepDur() : "";
			}
		}
		return null;
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
		if (node instanceof LogicalSystemNode) {
			if (id.equals(NodeConstants.GENERIC_ID)) {
				((LogicalSystemNode) node).setName(string);
			}
		} else if (node instanceof LogicalComputingPlatformNode) {
			if (id.equals(NodeConstants.GENERIC_ID)) {
				((LogicalComputingPlatformNode) node).setName(string);
			}
		} else if (node instanceof LogicalComputingNode) {
			LogicalComputingNode val = (LogicalComputingNode) node;
			if (id.equals(NodeConstants.GENERIC_ID)) {
				val.setName(string);
			} else if (id.equals(NodeConstants.LOG_SYS_NODE_ENDIAN)) {
				switch (Integer.valueOf(string)) {
				case 1:
					val.setEndianess("LITTLE");
					break;
				case 2:
					val.setEndianess("BIG");
					break;
				}
			} else if (id.equals(NodeConstants.LOG_SYS_OS_NAME)) {
				val.setOsName(string);
			} else if (id.equals(NodeConstants.LOG_SYS_OS_VER)) {
				val.setOsVer(string);
			} else if (id.equals(NodeConstants.LOG_SYS_AVAIL_MEM_GB)) {
				val.setAvailMem(string);
			} else if (id.equals(NodeConstants.LOG_SYS_MOD_SWITCH_TIME)) {
				val.setMst(string);
			}
		} else if (node instanceof LogicalProcessorsNode) {
			LogicalProcessorsNode val = (LogicalProcessorsNode) node;
			if (id.equals(NodeConstants.LOG_SYS_PROC_NUM)) {
				val.setNum(string);
			} else if (id.equals(NodeConstants.LOG_SYS_PROC_TYPE)) {
				val.setType(string);
			} else if (id.equals(NodeConstants.LOG_SYS_STEP_DUR)) {
				val.setStepDur(string);
			}
		}
		node.refresh();
	}

	public Node getNode() {
		return node;
	}

	public void setNode(Node node) {
		this.node = node;
	}

}
