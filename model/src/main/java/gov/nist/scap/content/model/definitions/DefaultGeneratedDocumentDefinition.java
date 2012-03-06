package gov.nist.scap.content.model.definitions;

import gov.nist.scap.content.model.ContentException;

import javax.xml.namespace.QName;

public class DefaultGeneratedDocumentDefinition extends AbstractDocumentDefinition implements IGeneratedDocumentDefinition {

	public DefaultGeneratedDocumentDefinition(ISchema schema, String id, QName name) {
		super(schema, id, name);
	}

	public <T> T accept(IEntityDefinitionVisitor<T> visitor) throws ContentException, ProcessingException {
		return visitor.visit(this);
	}
}
