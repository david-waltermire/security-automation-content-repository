package gov.nist.scap.content.server;

import gov.nist.scap.content.model.IBoundaryIdentifierRelationship;
import gov.nist.scap.content.model.ICompositeRelationship;
import gov.nist.scap.content.model.IKeyedRelationship;
import gov.nist.scap.content.model.IRelationshipVisitor;
import gov.nist.scap.schema.content.entity.x01.BoundaryIdentifierRelationshipDocument;
import gov.nist.scap.schema.content.entity.x01.CompositeRelationshipDocument;
import gov.nist.scap.schema.content.entity.x01.EntityRelationshipType;
import gov.nist.scap.schema.content.entity.x01.EntityType;
import gov.nist.scap.schema.content.entity.x01.KeyedRelationshipDocument;

import javax.xml.namespace.QName;

public class XmlBeansEntityRelationshipVisitor implements IRelationshipVisitor {
	private final EntityType data;
	
	private static final QName BOUNDARY_IDENTIFIER_REL_QNAME = BoundaryIdentifierRelationshipDocument.type.getDocumentElementName();
	private static final QName COMPOSITE_REL_QNAME = CompositeRelationshipDocument.type.getDocumentElementName();
	private static final QName KEYED_REL_QNAME = KeyedRelationshipDocument.type.getDocumentElementName();
	
	public XmlBeansEntityRelationshipVisitor(EntityType data) {
		this.data = data;
	}

	@Override
	public void visit(ICompositeRelationship relationship) {
		EntityRelationshipType ert = data.addNewRelationship();
		ert.setPredicate(relationship.getDefinition().getId());
		ert.setObject(relationship.getReferencedEntity().getId());
		// change the element at the end to avoid invalidating the object before the sets
		ert.newCursor().setName(COMPOSITE_REL_QNAME);
	}

	@Override
	public void visit(IBoundaryIdentifierRelationship relationship) {
		EntityRelationshipType ert = data.addNewRelationship();
		ert.setPredicate(relationship.getDefinition().getId());
		ert.setObject(relationship.getValue());
		// change the element at the end to avoid invalidating the object before the sets
		ert.newCursor().setName(BOUNDARY_IDENTIFIER_REL_QNAME);
	}

	@Override
	public void visit(IKeyedRelationship relationship) {
		EntityRelationshipType ert = data.addNewRelationship();
		ert.setPredicate(relationship.getDefinition().getId());
		ert.setObject(relationship.getReferencedEntity().getId());
		// change the element at the end to avoid invalidating the object before the sets
		ert.newCursor().setName(KEYED_REL_QNAME);
	}

}
