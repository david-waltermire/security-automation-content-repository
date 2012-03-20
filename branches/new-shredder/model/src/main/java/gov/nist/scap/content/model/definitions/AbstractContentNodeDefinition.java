package gov.nist.scap.content.model.definitions;


public abstract class AbstractContentNodeDefinition extends AbstractEntityDefinition implements IContentNodeDefinition {

	private final IKeyDefinition keyDefinition;
	private IVersionDefinition versionDefinition;

	public AbstractContentNodeDefinition(ISchemaDefinition schema, String id, IKeyDefinition keyDefinition) {
		super(schema, id);
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
