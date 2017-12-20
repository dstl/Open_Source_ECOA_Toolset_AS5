package tech.ecoa.osets.eclipse.plugin.editors.parts.lsys.model;

import java.util.ArrayList;

public class LogicalSystemNode extends Node {
	private String name;

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
			ret.add("System Name Cannot be empty");
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
