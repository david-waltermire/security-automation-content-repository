package gov.nist.scap.content.model;

import gov.nist.scap.content.model.definitions.IEntityDefinition;

public interface IMutableEntity<DEFINITION extends IEntityDefinition> extends IContainer<DEFINITION> {
	IMutableEntity<?> getParent();
	void addRelationship(IKeyedRelationship relationship);
	void addRelationship(IBoundaryIdentifierRelationship relationship);
	void addRelationship(IRelationship<?> relationship);
	void setVersion(IVersion version);
}
