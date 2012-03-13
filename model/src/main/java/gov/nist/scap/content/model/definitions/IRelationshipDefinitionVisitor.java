package gov.nist.scap.content.model.definitions;


public interface IRelationshipDefinitionVisitor {
	void visit(IBoundaryRelationshipDefinition definition) throws ProcessingException;
	void visit(IIndirectRelationshipDefinition definition) throws ProcessingException;
	void visit(IKeyedRelationshipDefinition definition) throws ProcessingException;
}
