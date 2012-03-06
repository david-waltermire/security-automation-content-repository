package gov.nist.scap.content.model.definitions;

import javax.xml.namespace.QName;

public abstract class AbstractDocumentDefinition extends AbstractEntityDefinition implements IDocumentDefinition {
	private final QName name;

	public AbstractDocumentDefinition(ISchema schema, String id, QName name) {
		super(schema, id);
		this.name = name;
	}

	public QName getName() {
		return name;
	}
}
