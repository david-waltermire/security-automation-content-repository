package gov.nist.scap.content.shredder.rules;

import gov.nist.scap.content.shredder.model.ContentException;
import gov.nist.scap.content.shredder.model.DefaultIndexedDocument;
import gov.nist.scap.content.shredder.model.IContainer;
import gov.nist.scap.content.shredder.model.IKey;

import javax.xml.namespace.QName;

import org.apache.xmlbeans.XmlCursor;

public class DefaultIndexedDocumentDefinition extends
		AbstractIndexedDocumentDefinition {

	public DefaultIndexedDocumentDefinition(ISchema schema, String id, QName name,
			IKeyDefinition keyDefinition) {
		super(schema, id, name, keyDefinition);
	}

	@Override
	protected DefaultIndexedDocument newIndexedDocument(XmlCursor cursor,
			IContainer<?> parentContext, IKey key) throws ContentException {
		return new DefaultIndexedDocument(cursor, this, parentContext, key);
	}
}
