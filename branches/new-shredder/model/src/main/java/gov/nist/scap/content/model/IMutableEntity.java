package gov.nist.scap.content.model;

import gov.nist.scap.content.model.definitions.IEntityDefinition;

import java.util.List;

public interface IMutableEntity<DEFINITION extends IEntityDefinition> extends IContainer<DEFINITION> {
	IMutableEntity<?> getParent();
	void addRelationship(IRelationship<?> relationship);
	void setVersion(IVersion version);
	void addProperty(String definitionId, String value);
	void addProperty(String definitionId, List<String> values);
}
