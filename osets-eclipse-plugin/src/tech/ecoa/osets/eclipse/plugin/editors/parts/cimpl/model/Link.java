package tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.ui.views.properties.IPropertySource;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class Link extends Observable implements IAdaptable {
	private String name;
	private Node source;
	private Node target;
	private String desc;
	private List<Point> bPoints;
	private String containerName;
	private String type;
	private String sInst;
	private String tInst;
	private String period;
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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getPeriod() {
		return period;
	}

	public void setPeriod(String period) {
		this.period = period;
	}

	public String getsInst() {
		return sInst;
	}

	public void setsInst(String sInst) {
		this.sInst = sInst;
	}

	public String gettInst() {
		return tInst;
	}

	public void settInst(String tInst) {
		this.tInst = tInst;
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
		if (sInst == null || sInst.equalsIgnoreCase("null") || sInst.trim().length() == 0)
			ret.add("Source Instance Name Cannot be empty");
		if (tInst == null || tInst.equalsIgnoreCase("null") || tInst.trim().length() == 0)
			ret.add("Target Instance Name Cannot be empty");
		if (source instanceof TriggerInstanceNode || target instanceof TriggerInstanceNode || source instanceof DynamicTriggerInstanceNode || target instanceof DynamicTriggerInstanceNode)
			if (period == null || period.equalsIgnoreCase("null") || period.trim().length() == 0)
				ret.add("Module Type Cannot be empty");
		return ret;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
