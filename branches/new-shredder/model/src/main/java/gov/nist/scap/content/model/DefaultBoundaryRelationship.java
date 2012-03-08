package gov.nist.scap.content.model;

import gov.nist.scap.content.model.definitions.IBoundaryRelationshipDefinition;

public class DefaultBoundaryRelationship extends AbstractRelationship<IBoundaryRelationshipDefinition> implements IBoundaryRelationship {
	private final IEntity<?> containingObject;

	public DefaultBoundaryRelationship(
			IBoundaryRelationshipDefinition definition,
			IEntity<?> owningEntity,
			IEntity<?> containingObject) {
		super(definition, owningEntity);
		this.containingObject = containingObject;
	}

	public IEntity<?> getContainingObject() {
		return containingObject;
	}

	public void accept(IRelationshipVisitor visitor) {
		visitor.visit(this);
	}
}
