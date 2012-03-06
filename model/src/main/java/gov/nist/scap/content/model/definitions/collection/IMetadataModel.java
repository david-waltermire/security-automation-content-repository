package gov.nist.scap.content.model.definitions.collection;

import gov.nist.scap.content.model.definitions.IEntityDefinition;
import gov.nist.scap.content.model.definitions.IExternalIdentifier;
import gov.nist.scap.content.model.definitions.IRelationshipDefinition;

import java.util.Collection;

public interface IMetadataModel {
	Collection<String> getIndirectRelationshipIds();
	Collection<String> getKeyedRelationshipIds();
	IExternalIdentifier getExternalIdentifierInfoById(String externalIdType);
	<T extends IRelationshipDefinition> T getRelationshipInfoById(String keyedRelationshipId);
	<T extends IEntityDefinition> T getEntityInfoById(String entityType);
}
