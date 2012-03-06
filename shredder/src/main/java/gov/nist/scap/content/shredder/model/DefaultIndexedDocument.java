package gov.nist.scap.content.shredder.model;

import gov.nist.scap.content.model.IMutableEntity;
import gov.nist.scap.content.model.IMutableKeyedDocument;
import gov.nist.scap.content.shredder.rules.IKeyedDocumentDefinition;


public class DefaultIndexedDocument extends AbstractEntity<IKeyedDocumentDefinition>
		implements IMutableKeyedDocument {

	private final IKey key;

	public DefaultIndexedDocument(IKeyedDocumentDefinition definition, IKey key, IContentHandle contentHandle, IMutableEntity<?> parent) throws ContentException {
		super(definition, contentHandle, parent);
		this.key = key;
	}

	public IKey getKey() {
		return key;
	}

	public void accept(IContainerVisitor visitor) {
		visitor.visit(this);
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
}
