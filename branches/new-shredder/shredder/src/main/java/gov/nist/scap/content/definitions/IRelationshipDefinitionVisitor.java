package gov.nist.scap.content.definitions;

import gov.nist.scap.content.model.ContentException;

public interface IRelationshipDefinitionVisitor {
	void visit(IBoundaryRelationshipDefinition definition) throws ContentException, ProcessingException;
	void visit(IIndirectRelationshipDefinition definition) throws ContentException, ProcessingException;
	void visit(IKeyedRelationshipDefinition definition) throws ContentException, ProcessingException;
}
