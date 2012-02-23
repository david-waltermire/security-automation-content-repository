package gov.nist.scap.content.shredder.model;

import gov.nist.scap.content.shredder.rules.IKeyedRelationshipDefinition;

public class DefaultKeyedRelationship extends AbstractRelationship<IKeyedRelationshipDefinition> implements IKeyedRelationship {
	private final IEntity<?> referencedEntity;

	public DefaultKeyedRelationship(IKeyedRelationshipDefinition definition,
			IEntity<?> owningEntity, IEntity<?> referencedEntity) {
		super(definition, owningEntity);
		this.referencedEntity = referencedEntity;
	}

	public IEntity<?> getReferencedEntity() {
		return referencedEntity;
	}
}
