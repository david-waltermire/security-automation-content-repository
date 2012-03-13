package gov.nist.scap.content.model.definitions;


public interface IEntityDefinitionVisitor<T> {
	T visit(IGeneratedDocumentDefinition definition) throws ProcessingException;
	T visit(IKeyedDocumentDefinition definition) throws ProcessingException;
	T visit(IContentNodeDefinition definition) throws ProcessingException;
}
