package tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model;

import java.util.ArrayList;

public class ComponentImplementationNode extends Node {
	private String name;
	private ArrayList<Link> links;

	public ArrayList<Link> getLinks() {
		if (links == null)
			links = new ArrayList<Link>();
		return links;
	}

	public void setLinks(ArrayList<Link> links) {
		this.links = links;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList<String> validate() {
		ArrayList<String> ret = new ArrayList<>();
		if (name == null || name.equalsIgnoreCase("null") || name.trim().length() == 0)
			ret.add("Name Cannot be empty");
		for (Link link : getLinks()) {
			ret.addAll(link.validate());
		}
		for (Node child : getChild()) {
			ret.addAll(child.validate());
			for (Node gChild : child.getChild()) {
				ret.addAll(gChild.validate());
				for (Node ggChild : gChild.getChild())
					ret.addAll(ggChild.validate());
			}
		}
		return ret;
	}
}
