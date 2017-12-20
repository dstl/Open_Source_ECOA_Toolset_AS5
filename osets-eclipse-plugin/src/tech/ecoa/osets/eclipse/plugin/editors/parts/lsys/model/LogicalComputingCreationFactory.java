package tech.ecoa.osets.eclipse.plugin.editors.parts.lsys.model;

import java.util.UUID;

import org.eclipse.gef.requests.CreationFactory;

public class LogicalComputingCreationFactory implements CreationFactory {

	@Override
	public Object getNewObject() {
		LogicalComputingNode ret = new LogicalComputingNode();
		ret.setId("" + UUID.randomUUID().toString());
		ret.setOsName("linux");
		return ret;
	}

	@Override
	public Object getObjectType() {
		return LogicalComputingNode.class;
	}

}
