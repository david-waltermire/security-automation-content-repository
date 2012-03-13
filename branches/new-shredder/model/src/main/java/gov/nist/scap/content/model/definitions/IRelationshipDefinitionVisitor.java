package gov.nist.scap.content.model.definitions;


public interface IRelationshipDefinitionVisitor {
	void visit(ICompositeRelationshipDefinition definition) throws ProcessingException;
	void visit(IBoundaryIdentifierRelationshipDefinition definition) throws ProcessingException;
	void visit(IKeyedRelationshipDefinition definition) throws ProcessingException;
}
