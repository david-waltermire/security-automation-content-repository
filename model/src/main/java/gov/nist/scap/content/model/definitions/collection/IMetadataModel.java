package gov.nist.scap.content.model.definitions.collection;

import gov.nist.scap.content.model.definitions.IEntityDefinition;
import gov.nist.scap.content.model.definitions.IExternalIdentifier;
import gov.nist.scap.content.model.definitions.IRelationshipDefinition;

import java.util.Collection;

public interface IMetadataModel {
	Collection<String> getCompositeRelationshipIds();
	Collection<String> getBoundaryIndentifierRelationshipIds();
	Collection<String> getKeyedRelationshipIds();
	IExternalIdentifier getExternalIdentifierById(String externalIdType);
	<T extends IRelationshipDefinition> T getRelationshipDefinitionById(String keyedRelationshipId);
	<T extends IEntityDefinition> T getEntityDefinitionById(String entityType);
}
