package gov.nist.scap.content.definitions;

import gov.nist.scap.content.model.ContentException;

public interface IEntityDefinitionVisitor<T> {
	T visit(IGeneratedDocumentDefinition definition) throws ContentException, ProcessingException;
	T visit(IKeyedDocumentDefinition definition) throws ContentException, ProcessingException;
	T visit(IContentNodeDefinition definition) throws ContentException, ProcessingException;
}
