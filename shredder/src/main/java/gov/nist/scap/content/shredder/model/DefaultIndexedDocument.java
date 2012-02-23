package gov.nist.scap.content.shredder.model;

import gov.nist.scap.content.shredder.rules.IIndexedDocumentDefinition;

import org.apache.xmlbeans.XmlCursor;


public class DefaultIndexedDocument extends AbstractDocument<IIndexedDocumentDefinition>
		implements IMutableIndexedDocument {

	private final IKey key;

	public DefaultIndexedDocument(XmlCursor cursor,
			IIndexedDocumentDefinition documentDefinition,
			IContainer<?> parentContext, IKey key) throws ContentException {
		super(cursor, documentDefinition, parentContext);
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
			retval = getParentContext().getKey(keyId);
		}
		return retval;
	}
}
