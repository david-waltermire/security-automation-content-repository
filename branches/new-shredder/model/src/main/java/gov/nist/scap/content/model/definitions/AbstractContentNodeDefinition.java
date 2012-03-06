package gov.nist.scap.content.model.definitions;


public abstract class AbstractContentNodeDefinition extends AbstractEntityDefinition implements IContentNodeDefinition {

	private final IKeyDefinition keyDefinition;

	public AbstractContentNodeDefinition(ISchema schema, String id, IKeyDefinition keyDefinition) {
		super(schema, id);
		this.keyDefinition = keyDefinition;
	}

	public IKeyDefinition getKeyDefinition() {
		return keyDefinition;
	}
}
