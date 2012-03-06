package gov.nist.scap.content.shredder.model;

import gov.nist.scap.content.model.IMutableContentNode;
import gov.nist.scap.content.model.IMutableEntity;
import gov.nist.scap.content.shredder.rules.IContentNodeDefinition;

public abstract class AbstractContentNode extends AbstractEntity<IContentNodeDefinition> implements IMutableContentNode {
	private final IKey key;

	public AbstractContentNode(IContentNodeDefinition definition, IKey key, IContentHandle contentHandle, IMutableEntity<?> parent) throws ContentException {
		super(definition, contentHandle, parent);
		this.key = key;
	}

	public IKey getKey() {
		return key;
	}

	public IKey getKey(String keyId) {
		IKey retval;
		if (key.getId().equals(keyId)) {
			retval = key;
		} else {
			retval = getParent().getKey(keyId);
		}
		return retval;
	}

	public void accept(IContainerVisitor visitor) {
		visitor.visit(this);
	}
}
