/*******************************************************************************
 * The MIT License
 * 
 * Copyright (c) 2011 David Waltermire
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 ******************************************************************************/
package org.scapdev.content.core.persistence.hybrid;

import gov.nist.scap.content.model.IEntity;
import gov.nist.scap.content.model.IKey;
import gov.nist.scap.content.model.IKeyedEntity;
import gov.nist.scap.content.model.IVersion;
import gov.nist.scap.content.model.definitions.IEntityDefinition;
import gov.nist.scap.content.model.definitions.IExternalIdentifier;
import gov.nist.scap.content.model.definitions.ProcessingException;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.scapdev.content.core.ContentException;
import org.scapdev.content.core.query.entity.EntityQuery;

public interface MetadataStore {

	Collection<? extends IEntity<?>> getEntities(EntityQuery query) throws ProcessingException;

	/**
	 * 
	 * @param key
	 * @param version 
	 * @return the entity if the key exists or <code>null</code> if it was not found
	 * @throws ProcessingException 
	 */
	Collection<? extends IKeyedEntity<?>> getEntities(IKey key, IVersion version) throws ProcessingException;

	   /**
     * 
     * @param contentId
     * @param contentRetrieverFactory
     * @return the entity if the content ID exists or <code>null</code> if it was not found
     */
    IEntity<?> getEntity(String contentId);

	/**
	 * 
	 * @param externalIdentifier
	 * @param boundaryObjectIds a collection of non-namespace qualified identifiers
	 * @param entityTypes filter results to only these entity types or no filtering if empty
	 * @return
	 */
    @Deprecated
	Map<String, Set<? extends IKey>> getKeysForBoundaryIdentifier(IExternalIdentifier externalIdentifier, Collection<String> boundaryObjectIds, Set<? extends IEntityDefinition> entityTypes);
    /**
     * Persist the entities to the triple store
     * @param contentIdToEntityMap a map of content IDs to entities. The map must be in order such that each entity either contains no other entities, or only contains entities that appear before it in the list 
     * @return a list of persisted entity contentIds, in the same order as the passed in parameter
     * @throws ContentException error with the repo
     */
    List<String> persist(LinkedHashMap<String, IEntity<?>> contentIdToEntityMap) throws ContentException;
    /**
     * Persist the entities to the triple store
     * @param contentIdToEntityMap a map of content IDs to entities. The map must be in order such that each entity either contains no other entities, or only contains entities that appear before it in the list
     * @session a session object 
     * @return a list of persisted entity contentIds, in the same order as the passed in parameter
     * @throws ContentException error with the repo
     */
    List<String> persist(LinkedHashMap<String, IEntity<?>> contentIdToEntityMap, Object session) throws ContentException;
    boolean commit(Object session);
    boolean rollback(Object session);

//	Map<String, ? extends EntityStatistic> getEntityStatistics(Set<String> entityInfoIds, IMetadataModel model);
	void shutdown();
}
