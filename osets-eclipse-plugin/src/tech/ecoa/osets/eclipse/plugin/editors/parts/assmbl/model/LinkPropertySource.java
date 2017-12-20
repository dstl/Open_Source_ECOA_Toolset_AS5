package tech.ecoa.osets.eclipse.plugin.editors.parts.assmbl.model;

import java.util.ArrayList;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

public class LinkPropertySource implements IPropertySource {
	private Link link;

	private static final String RANK = "Rank";

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
		properties.add(new TextPropertyDescriptor(RANK, "Rank"));
		return properties.toArray(new IPropertyDescriptor[0]);
	}

	@Override
	public Object getPropertyValue(Object id) {
		if (id.equals(RANK)) {
			return (link.getRank() != null) ? link.getRank() : "";
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
		if (id.equals(RANK)) {
			link.setRank(value.toString());
		}
		link.refresh();
	}

	public Link getLink() {
		return link;
	}

	public void setLink(Link link) {
		this.link = link;
	}

}
