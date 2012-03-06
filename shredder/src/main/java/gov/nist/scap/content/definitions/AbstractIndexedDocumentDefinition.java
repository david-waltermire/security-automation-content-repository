package gov.nist.scap.content.definitions;

import javax.xml.namespace.QName;

public abstract class AbstractIndexedDocumentDefinition extends AbstractDocumentDefinition implements IKeyedDocumentDefinition {
	private final IKeyDefinition keyDefinition;

	public AbstractIndexedDocumentDefinition(ISchema schema, String id, QName name, IKeyDefinition keyDefinition) {
		super(schema, id, name);
		this.keyDefinition = keyDefinition;
	}

	public IKeyDefinition getKeyDefinition() {
		return keyDefinition;
	}
}
