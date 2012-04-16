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

import org.scapdev.content.core.persistence.ContentPersistenceManager;
import org.scapdev.content.core.persistence.IEntityFilter;

public class LocalRetriever implements IRetriever {
	private final ContentPersistenceManager contentPersistenceManager;

	public LocalRetriever(ContentPersistenceManager cpm) {
		this.contentPersistenceManager = cpm;
	}

	public Collection<? extends IKeyedEntity<?>> retrieve(IKey key, IVersion version,
			IEntityFilter<IKeyedEntity<?>> filter) throws ProcessingException {
		return contentPersistenceManager.getEntities(key, version, filter);
	}

	public Collection<? extends IEntity<?>> retrieve(
			IExternalIdentifier externalIdentifier,
			Set<String> boundaryObjectIds,
			Set<? extends IEntityDefinition> entityTypes,
			IEntityFilter<IEntity<?>> filter) throws ProcessingException {
		return contentPersistenceManager.getEntities(
				externalIdentifier,
				boundaryObjectIds,
				entityTypes,
				filter);
	}
}
