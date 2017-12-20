package tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model;

import java.util.UUID;

import org.eclipse.gef.requests.CreationFactory;

public class ModuleImplementationCreationFactory implements CreationFactory {

	@Override
	public Object getNewObject() {
		ModuleImplementationNode ret = new ModuleImplementationNode();
		ret.setId(UUID.randomUUID().toString());
		return ret;
	}

	@Override
	public Object getObjectType() {
		return ModuleImplementationNode.class;
	}

}
