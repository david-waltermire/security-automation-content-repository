package gov.nist.scap.content.shredder.rules;

import gov.nist.scap.content.shredder.model.ContentException;
import gov.nist.scap.content.shredder.model.IContainer;
import gov.nist.scap.content.shredder.model.IKey;
import gov.nist.scap.content.shredder.model.IMutableIndexedDocument;

import javax.xml.namespace.QName;

import org.apache.xmlbeans.XmlCursor;

public abstract class AbstractIndexedDocumentDefinition extends AbstractDocumentDefinition implements IIndexedDocumentDefinition {
	private final IKeyDefinition keyDefinition;

	public AbstractIndexedDocumentDefinition(ISchema schema, String id, QName name, IKeyDefinition keyDefinition) {
		super(schema, id, name);
		this.keyDefinition = keyDefinition;
	}

	protected abstract IMutableIndexedDocument newIndexedDocument(XmlCursor cursor,
			IContainer<?> parentContext, IKey key) throws ContentException;

	@Override
	protected final IMutableIndexedDocument newContainer(XmlCursor cursor,
			IContainer<?> parentContext) throws ContentException {
		IKey key = keyDefinition.getKey(parentContext, cursor);
		return newIndexedDocument(cursor, parentContext, key);
	}
}
