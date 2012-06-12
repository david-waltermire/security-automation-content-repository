package gov.nist.scap.content.server;

import gov.nist.scap.content.model.IBoundaryIdentifierRelationship;
import gov.nist.scap.content.model.ICompositeRelationship;
import gov.nist.scap.content.model.IKeyedRelationship;
import gov.nist.scap.content.model.IRelationshipVisitor;
import gov.nist.scap.schema.content.entity.x01.EntityType;

public class XmlBeansEntityRelationshipVisitor implements IRelationshipVisitor {
	private final EntityType data;

	public XmlBeansEntityRelationshipVisitor(EntityType data) {
		this.data = data;
	}

	@Override
	public void visit(ICompositeRelationship relationship) {
	}

	@Override
	public void visit(IBoundaryIdentifierRelationship relationship) {
	}

	@Override
	public void visit(IKeyedRelationship relationship) {
	}

}
