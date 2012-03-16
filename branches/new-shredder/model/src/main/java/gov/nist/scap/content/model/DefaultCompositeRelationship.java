package gov.nist.scap.content.model;

import gov.nist.scap.content.model.definitions.ICompositeRelationshipDefinition;

public class DefaultCompositeRelationship extends AbstractReferencingRelationship<ICompositeRelationshipDefinition, IEntity<?>> implements ICompositeRelationship {

	public DefaultCompositeRelationship(
			ICompositeRelationshipDefinition definition,
			IEntity<?> parentEntity,
			IEntity<?> childEntity) {
		super(definition, parentEntity, childEntity);
	}

	public void accept(IRelationshipVisitor visitor) {
		visitor.visit(this);
	}
}
