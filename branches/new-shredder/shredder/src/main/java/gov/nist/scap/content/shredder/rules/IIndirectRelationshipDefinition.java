package gov.nist.scap.content.shredder.rules;


public interface IIndirectRelationshipDefinition extends IRelationshipDefinition {
	XPathRetriever getValueRetriever();
	IExternalIdentifierMapping getQualifierMapping();
}
