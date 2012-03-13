package gov.nist.scap.content.model;

import gov.nist.scap.content.model.definitions.IKeyedDocumentDefinition;


public class DefaultKeyedDocument extends AbstractEntity<IKeyedDocumentDefinition>
		implements IMutableKeyedDocument {

	private final IKey key;
	private IVersion version;

	public DefaultKeyedDocument(IKeyedDocumentDefinition definition, IKey key, IContentHandle contentHandle, IMutableEntity<?> parent) {
		super(definition, contentHandle, parent);
		this.key = key;
	}

	public IKey getKey() {
		return key;
	}

	public void accept(IEntityVisitor visitor) {
		visitor.visit(this);
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

	public void setVersion(IVersion version) {
		this.version = version;
	}
}
