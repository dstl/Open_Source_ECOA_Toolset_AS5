package tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.model;

import java.util.ArrayList;

public class CompositeNode extends Node {
	private String name;
	private String def;
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

	@Override
	public ArrayList<String> validate() {
		ArrayList<String> ret = new ArrayList<>();
		if (name == null || name.equalsIgnoreCase("null") || name.trim().length() == 0)
			ret.add("Composite Name Cannot be empty");
		for (Link link : getLinks()) {
			ret.addAll(link.validate());
		}
		for (Node child : getChild()) {
			ret.addAll(child.validate());
		}
		return ret;
	}

	public String getDef() {
		return def;
	}

	public void setDef(String def) {
		this.def = def;
	}
}
