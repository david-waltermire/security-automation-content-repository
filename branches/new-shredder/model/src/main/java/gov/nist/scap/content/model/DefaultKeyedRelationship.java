package gov.nist.scap.content.model;

import gov.nist.scap.content.model.definitions.IKeyedRelationshipDefinition;

public class DefaultKeyedRelationship extends AbstractRelationship<IKeyedRelationshipDefinition> implements IKeyedRelationship {
	private final IKeyedEntity<?> referencedEntity;

	public DefaultKeyedRelationship(IKeyedRelationshipDefinition definition,
			IEntity<?> owningEntity, IKeyedEntity<?> referencedEntity) {
		super(definition, owningEntity);
		this.referencedEntity = referencedEntity;
	}

	public IKeyedEntity<?> getReferencedEntity() {
		return referencedEntity;
	}

	public IKey getKey() {
		return getReferencedEntity().getKey();
	}

	public void accept(IRelationshipVisitor visitor) {
		visitor.visit(this);
	}
}
