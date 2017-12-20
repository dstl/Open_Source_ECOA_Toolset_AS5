package tech.ecoa.osets.eclipse.plugin.editors.parts.lsys.model;

import java.util.UUID;

import org.eclipse.gef.requests.CreationFactory;

public class LogicalComputingPlatformCreationFactory implements CreationFactory {

	@Override
	public Object getNewObject() {
		LogicalComputingPlatformNode ret = new LogicalComputingPlatformNode();
		ret.setId("" + UUID.randomUUID().toString());
		return ret;
	}

	@Override
	public Object getObjectType() {
		return LogicalComputingPlatformNode.class;
	}

}
