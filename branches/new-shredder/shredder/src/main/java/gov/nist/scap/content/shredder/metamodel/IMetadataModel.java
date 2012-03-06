package gov.nist.scap.content.shredder.metamodel;

import gov.nist.scap.content.definitions.IEntityDefinition;
import gov.nist.scap.content.definitions.IExternalIdentifier;
import gov.nist.scap.content.definitions.IRelationshipDefinition;

import java.util.Collection;

public interface IMetadataModel {

	Collection<String> getIndirectRelationshipIds();

	Collection<String> getKeyedRelationshipIds();

	IExternalIdentifier getExternalIdentifierInfoById(
			String externalIdType);

	<T extends IRelationshipDefinition> T getRelationshipInfoById(String keyedRelationshipId);

	<T extends IEntityDefinition> T getEntityInfoById(String entityType);

}
