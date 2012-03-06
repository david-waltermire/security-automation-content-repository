package gov.nist.scap.content.definitions;

import gov.nist.scap.content.model.ContentException;

import javax.xml.namespace.QName;

public class DefaultIndexedDocumentDefinition extends
		AbstractIndexedDocumentDefinition {

	public DefaultIndexedDocumentDefinition(ISchema schema, String id, QName name,
			IKeyDefinition keyDefinition) {
		super(schema, id, name, keyDefinition);
	}

	public <T> T accept(IEntityDefinitionVisitor<T> visitor) throws ContentException, ProcessingException {
		return visitor.visit(this);
	}
}
