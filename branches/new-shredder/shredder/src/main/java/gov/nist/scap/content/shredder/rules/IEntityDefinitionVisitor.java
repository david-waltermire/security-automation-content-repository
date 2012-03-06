package gov.nist.scap.content.shredder.rules;

import gov.nist.scap.content.shredder.model.ContentException;

public interface IEntityDefinitionVisitor<T> {
	T visit(IGeneratedDocumentDefinition definition) throws ContentException, ProcessingException;
	T visit(IKeyedDocumentDefinition definition) throws ContentException, ProcessingException;
	T visit(IContentNodeDefinition definition) throws ContentException, ProcessingException;
}
