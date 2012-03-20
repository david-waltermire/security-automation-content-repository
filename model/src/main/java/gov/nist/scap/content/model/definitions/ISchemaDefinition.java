package gov.nist.scap.content.model.definitions;

import java.net.URI;

public interface ISchemaDefinition extends IDefinition {
	URI getNamespace();
	IVersioningMethodDefinition getVersioningMethodById(String id);
	IPropertyDefinition getPropertyDefinitionById(String id);
}
