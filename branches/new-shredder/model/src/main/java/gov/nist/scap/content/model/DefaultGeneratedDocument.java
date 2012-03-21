package gov.nist.scap.content.model;


import gov.nist.scap.content.model.definitions.IGeneratedDocumentDefinition;

public class DefaultGeneratedDocument extends AbstractEntity<IGeneratedDocumentDefinition> implements IMutableGeneratedDocument {

	public DefaultGeneratedDocument(IGeneratedDocumentDefinition definition, IContentHandle contentHandle, IContainer<?> parent) {
		super(definition, contentHandle, parent);
	}

	public void accept(IEntityVisitor visitor) {
		visitor.visit(this);
	}

	public IVersion getVersion() {
		return null;
	}

	public void setVersion(IVersion version) {
		throw new UnsupportedOperationException();
	}
}
