package gov.nist.scap.content.definitions;

public abstract class AbstractDefinition implements IDefinition {
	private final ISchema schema;
	private final String id;

	public AbstractDefinition(ISchema schema, String id) {
		if (schema == null) {
			throw new NullPointerException("schema");
		}

		if (id == null) {
			throw new NullPointerException("id");
		} else if (id.isEmpty()) {
			throw new IllegalArgumentException("id must not be empty");
		}

		this.schema = schema;
		this.id = id;
	}

	/**
	 * @return the schema
	 */
	public ISchema getSchema() {
		return schema;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
}
