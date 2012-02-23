package gov.nist.scap.content.shredder.rules;

public interface IKeyedRelationshipDefinition extends IRelationshipDefinition {
	IKeyRefDefinition getKeyRefDefinition();
}
