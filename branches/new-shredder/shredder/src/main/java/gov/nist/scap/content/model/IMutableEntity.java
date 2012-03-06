package gov.nist.scap.content.model;

import gov.nist.scap.content.shredder.model.IContainer;
import gov.nist.scap.content.shredder.model.IIndirectRelationship;
import gov.nist.scap.content.shredder.model.IKeyedRelationship;
import gov.nist.scap.content.shredder.model.IRelationship;
import gov.nist.scap.content.shredder.rules.IEntityDefinition;

public interface IMutableEntity<DEFINITION extends IEntityDefinition> extends IContainer<DEFINITION> {
	IMutableEntity<?> getParent();
	void addRelationship(IKeyedRelationship relationship);
	void addRelationship(IIndirectRelationship relationship);
	void addRelationship(IRelationship<?> relationship);
}
