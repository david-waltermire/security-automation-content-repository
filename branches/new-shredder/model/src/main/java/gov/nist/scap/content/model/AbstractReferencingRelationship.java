package gov.nist.scap.content.model;

import gov.nist.scap.content.model.definitions.IRelationshipDefinition;

public abstract class AbstractReferencingRelationship<DEFINITION extends IRelationshipDefinition, REFERENCE extends IEntity<?>> extends
		AbstractRelationship<DEFINITION> implements
		IReferencingRelationship<DEFINITION, REFERENCE> {

	private final REFERENCE referencedEntity;

	public AbstractReferencingRelationship(DEFINITION definition,
			IEntity<?> owningEntity, REFERENCE referencedEntity) {
		super(definition, owningEntity);
		this.referencedEntity = referencedEntity;
	}

	public REFERENCE getReferencedEntity() {
		return referencedEntity;
	}

}
