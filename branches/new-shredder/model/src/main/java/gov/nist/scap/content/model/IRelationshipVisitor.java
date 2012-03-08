package gov.nist.scap.content.model;

public interface IRelationshipVisitor {
	void visit(IBoundaryRelationship relationship);
	void visit(IIndirectRelationship relationship);
	void visit(IKeyedRelationship relationship);
}
