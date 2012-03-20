package gov.nist.scap.content.model.definitions;

import java.util.List;

public interface IKeyDefinition extends ISchemaRelatedDefinition {
	List<? extends IKeyedField> getFields();
}
