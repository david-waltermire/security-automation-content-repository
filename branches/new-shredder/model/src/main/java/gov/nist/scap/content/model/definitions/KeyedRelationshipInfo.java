package gov.nist.scap.content.model.definitions;

import gov.nist.scap.content.model.AbstractEntity;
import gov.nist.scap.content.model.DefaultKeyedRelationship;
import gov.nist.scap.content.model.IKey;
import gov.nist.scap.content.model.IKeyedEntity;

public class KeyedRelationshipInfo {
	private final IKeyedRelationshipDefinition definition;
	private final AbstractEntity<?> containingEntity;
	private final IKey key;

	public KeyedRelationshipInfo(IKeyedRelationshipDefinition definition, AbstractEntity<?> containingEntity, IKey key) {
		this.definition = definition;
		this.containingEntity = containingEntity;
		this.key = key;
	}

	public IKey getKey() {
		return key;
	}
	
	public void applyRelationship(IKeyedEntity<?> referencedEntity) {
		containingEntity.addRelationship(new DefaultKeyedRelationship(definition, referencedEntity));
	}
}
