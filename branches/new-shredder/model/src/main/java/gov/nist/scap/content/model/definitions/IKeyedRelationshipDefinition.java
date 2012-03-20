package gov.nist.scap.content.model.definitions;

public interface IKeyedRelationshipDefinition extends IRelationshipDefinition {
	IKeyRef getKeyRefDefinition();
}
