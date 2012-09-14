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
package org.scapdev.content.core.persistence;

import gov.nist.scap.content.model.IEntity;
import gov.nist.scap.content.model.IKey;
import gov.nist.scap.content.model.IKeyedEntity;
import gov.nist.scap.content.model.IVersion;
import gov.nist.scap.content.model.definitions.IEntityDefinition;
import gov.nist.scap.content.model.definitions.IExternalIdentifier;
import gov.nist.scap.content.model.definitions.ProcessingException;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.scapdev.content.core.ContentException;
import org.scapdev.content.core.query.entity.EntityQuery;


public interface ContentPersistenceManager {
	Collection<? extends IEntity<?>> getEntities(EntityQuery query) throws ProcessingException;
	/**
	 * 
	 * @param key
	 * @param version the entity version to retrieve if not <code>null</code>
	 * @param filter 
	 * @return
	 * @throws ProcessingException
	 */
	Collection<? extends IKeyedEntity<?>> getEntities(IKey key,
			IVersion version,
			IEntityFilter<IKeyedEntity<?>> filter) throws ProcessingException;
	Collection<? extends IEntity<?>> getEntities(
			IExternalIdentifier externalIdentifier,
			Set<String> boundaryObjectIds,
			Set<? extends IEntityDefinition> entityTypes,
			IEntityFilter<IEntity<?>> filter) throws ProcessingException;
    IEntity<?> getEntity(String contentId) throws ProcessingException;
	/**
	 * 
	 * @param externalIdentifier
	 * @param boundaryObjectIds a collection of non-namespace qualified identifiers
	 * @param filter filter results to only these entity types or no filtering if empty
	 * @return a mapping of external identifiers to keys
	 */
	Map<String, Set<? extends IKey>> getKeysForBoundaryIdentifier(
			IExternalIdentifier externalIdentifier,
			Collection<String> boundaryObjectIds,
			Set<? extends IEntityDefinition> entityTypes);
	List<String> storeEntities(List<? extends IEntity<?>> entities) throws ContentException;
	
	public <T extends IEntity<?>> Collection<? extends T> getEntities(
			EntityQuery query, Class<T> clazz) throws ProcessingException;
	
	public <T extends IEntity<?>> Collection<? extends T> getEntities(
			EntityQuery query, boolean areKeyedEntities) throws ProcessingException;
	public Iterator<String> getAllTopLevelEntities();

			
//	Map<String, ? extends EntityStatistic> getEntityStatistics(Set<String> entityInfoIds, IMetadataModel model);
	void shutdown();
}
