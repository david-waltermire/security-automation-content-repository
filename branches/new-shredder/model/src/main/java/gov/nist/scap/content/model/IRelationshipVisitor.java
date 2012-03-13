package gov.nist.scap.content.model;

public interface IRelationshipVisitor {
	void visit(ICompositeRelationship relationship);
	void visit(IBoundaryIdentifierRelationship relationship);
	void visit(IKeyedRelationship relationship);
}
