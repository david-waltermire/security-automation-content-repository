package gov.nist.scap.content.model;

import gov.nist.scap.content.definitions.IContentNodeDefinition;

public class DefaultContentNode extends AbstractContentNode {

	public DefaultContentNode(IContentNodeDefinition definition, IKey key, IContentHandle contentHandle, IMutableEntity<?> parent) throws ContentException {
		super(definition, key, contentHandle, parent);
	}
}
