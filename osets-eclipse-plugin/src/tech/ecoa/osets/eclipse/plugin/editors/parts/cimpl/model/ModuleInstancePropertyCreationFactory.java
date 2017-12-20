package tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model;

import java.util.UUID;

import org.eclipse.gef.requests.CreationFactory;

public class ModuleInstancePropertyCreationFactory implements CreationFactory {

	@Override
	public Object getNewObject() {
		ModuleInstancePropertyNode node = new ModuleInstancePropertyNode();
		node.setId(UUID.randomUUID().toString());
		return node;
	}

	@Override
	public Object getObjectType() {
		return ModuleInstancePropertyNode.class;
	}

}
