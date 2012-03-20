package gov.nist.scap.content.model;

import gov.nist.scap.content.model.definitions.ICompositeRelationshipDefinition;

public class DefaultCompositeRelationship extends AbstractReferencingRelationship<ICompositeRelationshipDefinition, IEntity<?>> implements ICompositeRelationship {

	public DefaultCompositeRelationship(
			ICompositeRelationshipDefinition definition,
			IEntity<?> childEntity) {
		super(definition, childEntity);
	}

	public void accept(IRelationshipVisitor visitor) {
		visitor.visit(this);
	}
}
