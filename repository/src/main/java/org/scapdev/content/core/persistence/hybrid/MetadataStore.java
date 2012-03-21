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
import gov.nist.scap.content.model.definitions.IEntityDefinition;
import gov.nist.scap.content.model.definitions.IExternalIdentifier;
import gov.nist.scap.content.model.definitions.ProcessingException;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public interface MetadataStore {

	/**
	 * 
	 * @param key
	 * @return the entity if the key exists or <code>null</code> if it was not found
	 * @throws ProcessingException 
	 */
	IKeyedEntity<?> getEntity(IKey key) throws ProcessingException;

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
	Map<String, Set<? extends IKey>> getKeysForBoundaryIdentifier(IExternalIdentifier externalIdentifier, Collection<String> boundaryObjectIds, Set<? extends IEntityDefinition> entityTypes);
	void persist(Map<String, IEntity<?>> contentIdToEntityMap);
//	Map<String, ? extends EntityStatistic> getEntityStatistics(Set<String> entityInfoIds, IMetadataModel model);
	void shutdown();
}
