package gov.nist.scap.content.shredder.rules;

import gov.nist.scap.content.shredder.model.ContentException;

import javax.xml.namespace.QName;

public class DefaultGeneratedDocumentDefinition extends AbstractDocumentDefinition implements IGeneratedDocumentDefinition {

	public DefaultGeneratedDocumentDefinition(ISchema schema, String id, QName name) {
		super(schema, id, name);
	}

	public <T> T accept(IEntityDefinitionVisitor<T> visitor) throws ContentException, ProcessingException {
		return visitor.visit(this);
	}
}
