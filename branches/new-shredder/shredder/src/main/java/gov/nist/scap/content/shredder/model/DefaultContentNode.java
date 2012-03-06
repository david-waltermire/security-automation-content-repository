package gov.nist.scap.content.shredder.model;

import gov.nist.scap.content.model.IMutableEntity;
import gov.nist.scap.content.shredder.rules.IContentNodeDefinition;

public class DefaultContentNode extends AbstractContentNode {

	public DefaultContentNode(IContentNodeDefinition definition, IKey key, IContentHandle contentHandle, IMutableEntity<?> parent) throws ContentException {
		super(definition, key, contentHandle, parent);
	}
}
