package gov.nist.scap.content.model;

import gov.nist.scap.content.model.definitions.IContentNodeDefinition;

public class DefaultContentNode extends AbstractContentNode {

	public DefaultContentNode(IContentNodeDefinition definition, IKey key, IContentHandle contentHandle, IContainer<?> parent) {
		super(definition, key, contentHandle, parent);
	}
}
