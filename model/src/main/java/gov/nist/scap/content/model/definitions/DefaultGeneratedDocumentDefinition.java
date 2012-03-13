package gov.nist.scap.content.model.definitions;

import javax.xml.namespace.QName;

public class DefaultGeneratedDocumentDefinition extends AbstractDocumentDefinition implements IGeneratedDocumentDefinition {

	public DefaultGeneratedDocumentDefinition(ISchema schema, String id, QName name) {
		super(schema, id, name);
	}

	public IVersionDefinition getVersionDefinition() {
		return null;
	}

	public <T> T accept(IEntityDefinitionVisitor<T> visitor) throws ProcessingException {
		return visitor.visit(this);
	}
}
