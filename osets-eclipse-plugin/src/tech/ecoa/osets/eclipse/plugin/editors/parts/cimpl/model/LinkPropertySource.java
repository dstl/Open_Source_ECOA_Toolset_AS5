package tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model;

import java.util.ArrayList;

import org.eclipse.ui.views.properties.ComboBoxPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

public class LinkPropertySource implements IPropertySource {
	private Link link;

	private static final String TYPE = "Type";
	private static final String SINST = "SourceInstanceName";
	private static final String TINST = "TargetInstanceName";
	private static final String PERIOD = "Period";

	public LinkPropertySource(Link link) {
		this.link = link;
	}

	@Override
	public Object getEditableValue() {
		return null;
	}

	@Override
	public IPropertyDescriptor[] getPropertyDescriptors() {
		ArrayList<IPropertyDescriptor> properties = new ArrayList<IPropertyDescriptor>();
		properties.add(new TextPropertyDescriptor(TYPE, "Type"));
		if (link.getSource() instanceof ModuleOperationNode) {
			properties.add(new ComboBoxPropertyDescriptor(SINST, "Source Instance Name", getInstances((ModuleTypeNode) link.getSource().getParent())));
		} else {
			properties.add(new TextPropertyDescriptor(SINST, "Source Instance Name"));
		}
		if (link.getTarget() instanceof ModuleOperationNode) {
			properties.add(new ComboBoxPropertyDescriptor(TINST, "Target Instance Name", getInstances((ModuleTypeNode) link.getTarget().getParent())));
		} else {
			properties.add(new TextPropertyDescriptor(TINST, "Target Instance Name"));
		}
		if (link.getSource() instanceof TriggerInstanceTerminalNode || link.getTarget() instanceof TriggerInstanceTerminalNode)
			properties.add(new TextPropertyDescriptor(PERIOD, "Period"));
		return properties.toArray(new IPropertyDescriptor[0]);
	}

	@Override
	public Object getPropertyValue(Object id) {
		if (id.equals(TYPE)) {
			return (link.getType() != null) ? link.getType() : "";
		} else if (id.equals(PERIOD)) {
			if (link.getSource() instanceof TriggerInstanceTerminalNode || link.getTarget() instanceof TriggerInstanceTerminalNode) {
				return (link.getPeriod() != null) ? link.getPeriod() : "";
			}
		} else if (id.equals(SINST)) {
			if (link.getSource() instanceof ModuleOperationNode) {
				return getInstIndex(link.getsInst(), getInstances((ModuleTypeNode) link.getSource().getParent()));
			} else {
				return (link.getsInst() != null) ? link.getsInst() : "";
			}
		} else if (id.equals(TINST)) {
			if (link.getTarget() instanceof ModuleOperationNode) {
				return getInstIndex(link.gettInst(), getInstances((ModuleTypeNode) link.getTarget().getParent()));
			} else {
				return (link.gettInst() != null) ? link.gettInst() : "";
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
		if (id.equals(PERIOD)) {
			link.setPeriod(value);
		} else if (id.equals(SINST)) {
			if (link.getSource() instanceof ModuleOperationNode) {
				link.setsInst(getInstances((ModuleTypeNode) link.getSource().getParent())[Integer.parseInt(value)]);
			} else {
				link.setsInst(value);
			}
		} else if (id.equals(TINST)) {
			if (link.getTarget() instanceof ModuleOperationNode) {
				link.settInst(getInstances((ModuleTypeNode) link.getTarget().getParent())[Integer.parseInt(value)]);
			} else {
				link.settInst(value);
			}
		}
		link.refresh();
	}

	protected String[] getInstances(ModuleTypeNode node) {
		ArrayList<String> names = new ArrayList<String>();
		names.add("");
		for (Node cNd : node.getChild())
			if (cNd instanceof ModuleInstanceNode)
				names.add(((ModuleInstanceNode) cNd).getName());
		return names.toArray(new String[0]);
	}

	protected int getInstIndex(String inst, String[] insts) {
		int i = 0;
		for (String name : insts) {
			if (name.equalsIgnoreCase(inst))
				return i;
			i++;
		}
		return 0;
	}

	public Link getLink() {
		return link;
	}

	public void setLink(Link link) {
		this.link = link;
	}

}
