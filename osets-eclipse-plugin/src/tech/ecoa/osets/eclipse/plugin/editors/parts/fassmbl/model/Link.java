package tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import org.apache.commons.lang3.math.NumberUtils;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.ui.views.properties.IPropertySource;

@SuppressWarnings({ "unchecked", "rawtypes", "deprecation" })
public class Link extends Observable implements IAdaptable {
	private String name;
	private Node source;
	private Node target;
	private String desc;
	private List<Point> bPoints;
	private String containerName;
	private String rank;
	private IPropertySource propertySource = null;

	public void refresh() {
		setChanged();
		notifyObservers();
	}

	public Node getSource() {
		return source;
	}

	public void setSource(Node source) {
		this.source = source;
	}

	public Node getTarget() {
		return target;
	}

	public void setTarget(Node target) {
		this.target = target;
		setChanged();
		notifyObservers();
	}

	public String getId() {
		return desc;
	}

	public void setId(String desc) {
		this.desc = desc;
	}

	@Override
	public boolean equals(Object obj) {
		boolean ret = false;
		if (obj instanceof Link) {
			Link curr = (Link) obj;
			ret = curr.getName().equalsIgnoreCase(getName());
		}
		return ret;
	}

	public List<Point> getbPoints() {
		if (bPoints == null)
			bPoints = new ArrayList<Point>();
		return bPoints;
	}

	public void setbPoints(List<Point> bPoints) {
		this.bPoints = bPoints;
	}

	public String getContainerName() {
		return containerName;
	}

	public void setContainerName(String containerName) {
		this.containerName = containerName;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getRank() {
		return rank;
	}

	public void setRank(String rank) {
		this.rank = rank;
	}

	@Override
	public Object getAdapter(Class adapter) {
		if (adapter == IPropertySource.class) {
			if (propertySource == null)
				propertySource = new LinkPropertySource(this);
			return propertySource;
		}
		return null;
	}

	public ArrayList<String> validate() {
		ArrayList<String> ret = new ArrayList<>();
		if (!NumberUtils.isNumber(rank))
			ret.add("Rank cannot be empty or a Non Numeric value");
		return ret;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
