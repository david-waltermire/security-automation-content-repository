package gov.nist.scap.content.model.definitions;

public class AbstractSchemaRelatedDefinition extends AbstractDefinition
		implements ISchemaRelatedDefinition {

	private final ISchemaDefinition schema;

	public AbstractSchemaRelatedDefinition(ISchemaDefinition schema, String id) {
		super(id);

		if (schema == null) {
			throw new NullPointerException("schema");
		}

		this.schema = schema;
	}

	/**
	 * @return the schema
	 */
	public ISchemaDefinition getSchema() {
		return schema;
	}
}
