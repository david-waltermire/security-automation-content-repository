package org.scapdev.content.core.resolver.retriever;

import gov.nist.scap.content.model.IEntity;
import gov.nist.scap.content.model.IKey;
import gov.nist.scap.content.model.IKeyedEntity;
import gov.nist.scap.content.model.IVersion;
import gov.nist.scap.content.model.definitions.IEntityDefinition;
import gov.nist.scap.content.model.definitions.IExternalIdentifier;
import gov.nist.scap.content.model.definitions.ProcessingException;

import java.util.Collection;
import java.util.Set;

import org.scapdev.content.core.persistence.IEntityFilter;

public interface IRetriever {
	Collection<? extends IKeyedEntity<?>> retrieve(IKey key, IVersion version,
			IEntityFilter<IKeyedEntity<?>> filter) throws ProcessingException;
	Collection<? extends IEntity<?>> retrieve(
			IExternalIdentifier externalIdentifier,
			Set<String> boundaryObjectIds,
			Set<? extends IEntityDefinition> entityTypes,
			IEntityFilter<IEntity<?>> filter) throws ProcessingException;
}
