package gov.nist.scap.content.model.definitions;


public interface IBoundaryIdentifierRelationshipDefinition extends IRelationshipDefinition {
	XPathRetriever getValueRetriever();
	IExternalIdentifierMapping getQualifierMapping();
}
