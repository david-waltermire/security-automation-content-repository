package gov.nist.scap.content.model;

import gov.nist.scap.content.model.definitions.IContentNodeDefinition;

public abstract class AbstractContentNode extends AbstractEntity<IContentNodeDefinition> implements IContentNode {
	private final IKey key;
	private IVersion version;

	public AbstractContentNode(IContentNodeDefinition definition, IKey key, IContentHandle contentHandle, IContainer<?> parent) {
		super(definition, contentHandle, parent);
		this.key = key;
	}

	public IKey getKey() {
		return key;
	}

	@Override
	public IKey getKey(String keyId) {
		IKey retval;
		if (key.getId().equals(keyId)) {
			retval = key;
		} else {
			retval = getParent().getKey(keyId);
		}
		return retval;
	}

	public IVersion getVersion() {
		return version;
	}

	@Override
	public void setVersion(IVersion version) {
		this.version = version;
	}

	public void accept(IEntityVisitor visitor) {
		visitor.visit(this);
	}
}
