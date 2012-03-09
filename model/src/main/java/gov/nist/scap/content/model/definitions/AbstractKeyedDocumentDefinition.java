package gov.nist.scap.content.model.definitions;

import javax.xml.namespace.QName;

public abstract class AbstractKeyedDocumentDefinition extends AbstractDocumentDefinition implements IKeyedDocumentDefinition {
	private final IKeyDefinition keyDefinition;
	private IVersionDefinition versionDefinition;

	public AbstractKeyedDocumentDefinition(ISchema schema, String id, QName name, IKeyDefinition keyDefinition) {
		super(schema, id, name);
		if (keyDefinition == null) {
			throw new NullPointerException("keyDefinition");
		}
		this.keyDefinition = keyDefinition;
	}

	public IKeyDefinition getKeyDefinition() {
		return keyDefinition;
	}

	public IVersionDefinition getVersionDefinition() {
		return versionDefinition;
	}

	public void setVersionDefinition(IVersionDefinition versionDefinition) {
		if (versionDefinition == null) {
			throw new NullPointerException("versionDefinition");
		}
		this.versionDefinition = versionDefinition;
	}
}
