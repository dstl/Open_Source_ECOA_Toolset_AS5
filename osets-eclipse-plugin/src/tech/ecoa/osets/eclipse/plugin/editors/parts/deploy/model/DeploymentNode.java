package tech.ecoa.osets.eclipse.plugin.editors.parts.deploy.model;

import java.util.ArrayList;

public class DeploymentNode extends Node {
	private String fAssmbl;
	private String lSys;

	public String getfAssmbl() {
		return fAssmbl;
	}

	public void setfAssmbl(String fAssmbl) {
		this.fAssmbl = fAssmbl;
	}

	public String getlSys() {
		return lSys;
	}

	public void setlSys(String lSys) {
		this.lSys = lSys;
	}

	@Override
	public ArrayList<String> validate() {
		ArrayList<String> ret = new ArrayList<>();
		if (fAssmbl == null || fAssmbl.equalsIgnoreCase("null") || fAssmbl.trim().length() == 0)
			ret.add("Final Assembly Cannot be empty");
		if (lSys == null || lSys.equalsIgnoreCase("null") || lSys.trim().length() == 0)
			ret.add("Logical System Cannot be empty");
		for (Node child : getChild()) {
			ret.addAll(child.validate());
			for (Node gChild : child.getChild()) {
				ret.addAll(gChild.validate());
			}
		}
		return ret;
	}
}
