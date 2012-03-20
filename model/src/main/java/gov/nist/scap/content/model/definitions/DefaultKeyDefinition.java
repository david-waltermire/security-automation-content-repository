package gov.nist.scap.content.model.definitions;

import java.util.List;

public class DefaultKeyDefinition extends AbstractSchemaRelatedDefinition implements IKeyDefinition {
	private final KeyedSupport keyedSupport;

	public DefaultKeyDefinition(ISchemaDefinition schema, String id, List<? extends IKeyedField> keyFields) {
		super(schema, id);
		this.keyedSupport = new KeyedSupport(keyFields);
	}

	public List<? extends IKeyedField> getFields() {
		return keyedSupport.getFields();
	}
}
