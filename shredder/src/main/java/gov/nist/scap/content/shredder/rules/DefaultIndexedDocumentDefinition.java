package gov.nist.scap.content.shredder.rules;

import gov.nist.scap.content.shredder.model.ContentException;

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
