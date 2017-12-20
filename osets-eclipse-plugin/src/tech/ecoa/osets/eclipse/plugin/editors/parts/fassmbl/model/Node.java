package tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.ui.views.properties.IPropertySource;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class Node extends Observable implements IAdaptable {

	private String id;
	private Point location;
	private List<Node> child;
	private Node parent;
	private int height;
	private int width;
	private List<Link> inLinks;
	private List<Link> outLinks;
	private String containerName;
	private IPropertySource propertySource = null;

	public Point getAnchor(Point curr, int level) {
		int x = 0, y = 0;
		switch (level) {
		case 0:
			return curr;
		case 1:
			Rectangle lArect = getParent().getConstraints();
			x = curr.x - lArect.x;
			y = curr.y - lArect.y;
			return new Point(x, y);
		case 2:
			Rectangle lBpRect = getParent().getConstraints();
			Rectangle lBgpRect = getParent().getParent().getConstraints();
			x = curr.x - (lBpRect.x + lBgpRect.x);
			y = curr.y - (lBpRect.y + lBgpRect.y);
			return new Point(x, y);
		case 3:
			Rectangle lCpRect = getParent().getConstraints();
			Rectangle lCgpRect = getParent().getParent().getConstraints();
			Rectangle lCggpRect = getParent().getParent().getParent().getConstraints();
			x = curr.x - (lCpRect.x + lCpRect.x + lCggpRect.x);
			y = curr.y - (lCgpRect.y + lCgpRect.y + lCggpRect.y);
			return new Point(x, y);
		}
		return curr;
	}

	public Point getAbsolute(Point curr, int level) {
		int x = 0, y = 0;
		switch (level) {
		case 0:
			return new Point(x, y);
		case 1:
			Rectangle lArect = getParent().getConstraints();
			x = curr.x + lArect.x;
			y = curr.y + lArect.y;
			return new Point(x, y);
		case 2:
			Rectangle lBpRect = getParent().getConstraints();
			Rectangle lBgpRect = getParent().getParent().getConstraints();
			x = curr.x + (lBpRect.x + lBgpRect.x);
			y = curr.y + (lBpRect.y + lBgpRect.y);
			return new Point(x, y);
		case 3:
			Rectangle lCpRect = getParent().getConstraints();
			Rectangle lCgpRect = getParent().getParent().getConstraints();
			Rectangle lCggpRect = getParent().getParent().getParent().getConstraints();
			x = curr.x + (lCpRect.x + lCpRect.x + lCggpRect.x);
			y = curr.y + (lCgpRect.y + lCgpRect.y + lCggpRect.y);
			return new Point(x, y);
		}
		return curr;
	}

	public void refresh() {
		setChanged();
		notifyObservers();
	}

	public CompositeNode getCompositeNode() {
		if (this instanceof CompositeNode)
			return (CompositeNode) this;
		else if (this instanceof ComponentNode || this instanceof CompositePropertyNode)
			return (CompositeNode) this.getParent();
		else if (this instanceof ComponentPropertyNode || this instanceof ServiceNode || this instanceof ReferenceNode)
			return (CompositeNode) this.getParent().getParent();
		return null;
	}

	public void refreshAll(CompositeNode node) {
		node.setChanged();
		node.notifyObservers();
		for (Node pNode : node.getChild()) {
			pNode.setChanged();
			pNode.notifyObservers();
			for (Node cNode : pNode.getChild()) {
				cNode.setChanged();
				cNode.notifyObservers();
			}
		}
		for (Link lnk : node.getLinks()) {
			lnk.refresh();
		}
	}

	public Rectangle getConstraints() {
		return new Rectangle(location.x, location.y, width, height);
	}

	public void setConstraints(Rectangle rect) {
		this.location = new Point(rect.x, rect.y);
		this.width = rect.width;
		this.height = rect.height;
	}

	public Point getLocation() {
		return location;
	}

	public void setLocation(Point location) {
		this.location = location;
	}

	public List<Node> getChild() {
		if (child == null)
			child = new ArrayList<Node>();
		return child;
	}

	public void setChild(List<Node> child) {
		this.child = child;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Node) {
			Node curr = (Node) obj;
			return this.id.equalsIgnoreCase(curr.getId());
		} else {
			return false;
		}
	}

	public Node getParent() {
		return parent;
	}

	public void setParent(Node parent) {
		this.parent = parent;
	}

	public List<Link> getInLinks() {
		if (inLinks == null)
			inLinks = new ArrayList<Link>();
		return inLinks;
	}

	public void setInLinks(List<Link> inLinks) {
		this.inLinks = inLinks;
	}

	public List<Link> getOutLinks() {
		if (outLinks == null)
			outLinks = new ArrayList<Link>();
		return outLinks;
	}

	public void setOutLinks(List<Link> outLinks) {
		this.outLinks = outLinks;
	}

	public String getContainerName() {
		return containerName;
	}

	public void setContainerName(String containerName) {
		this.containerName = containerName;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public Object getAdapter(Class adapter) {
		if (adapter == IPropertySource.class) {
			if (propertySource == null)
				propertySource = new NodePropertySource(this);
			return propertySource;
		}
		return null;
	}

	public ArrayList<String> validate() {
		ArrayList<String> ret = new ArrayList<>();
		return ret;
	}
}
