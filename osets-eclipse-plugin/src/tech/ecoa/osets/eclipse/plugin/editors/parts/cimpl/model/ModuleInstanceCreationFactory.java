package tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model;

import java.util.UUID;

import org.eclipse.gef.requests.CreationFactory;

public class ModuleInstanceCreationFactory implements CreationFactory {

	@Override
	public Object getNewObject() {
		ModuleInstanceNode node = new ModuleInstanceNode();
		node.setId(UUID.randomUUID().toString());
		return node;
	}

	@Override
	public Object getObjectType() {
		return ModuleInstanceNode.class;
	}

}
