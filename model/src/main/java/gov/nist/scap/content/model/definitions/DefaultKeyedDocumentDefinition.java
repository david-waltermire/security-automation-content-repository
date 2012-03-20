package gov.nist.scap.content.model.definitions;

import javax.xml.namespace.QName;

public class DefaultKeyedDocumentDefinition extends
		AbstractKeyedDocumentDefinition {

	public DefaultKeyedDocumentDefinition(ISchemaDefinition schema, String id, QName name,
			IKeyDefinition keyDefinition) {
		super(schema, id, name, keyDefinition);
	}

	public <T> T accept(IEntityDefinitionVisitor<T> visitor) throws ProcessingException {
		return visitor.visit(this);
	}
}
