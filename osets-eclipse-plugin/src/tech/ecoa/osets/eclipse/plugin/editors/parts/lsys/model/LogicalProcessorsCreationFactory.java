package tech.ecoa.osets.eclipse.plugin.editors.parts.lsys.model;

import java.util.UUID;

import org.eclipse.gef.requests.CreationFactory;

public class LogicalProcessorsCreationFactory implements CreationFactory {

	@Override
	public Object getNewObject() {
		LogicalProcessorsNode ret = new LogicalProcessorsNode();
		ret.setId("" + UUID.randomUUID().toString());
		return ret;
	}

	@Override
	public Object getObjectType() {
		return LogicalProcessorsNode.class;
	}

}
