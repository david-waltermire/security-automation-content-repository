/*******************************************************************************
 * The MIT License
 * 
 * Copyright (c) 2011 davidwal
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
package org.scapdev.content.core;

import java.util.Collection;
import java.util.Set;

import org.scapdev.content.core.persistence.ContentPersistenceManager;
import org.scapdev.content.core.query.Query;
import org.scapdev.content.core.query.SimpleQueryResult;
import org.scapdev.content.core.resolver.ResolutionParameters;
import org.scapdev.content.core.resolver.Resolver;
import org.scapdev.content.model.Key;

public class IndirectQuery implements Query<SimpleQueryResult> {
	private final String indirectType;
	private final Collection<String> indirectIds;
	private final Set<String> requestedEntityIds;
	private final ContentPersistenceManager persistenceManager;
	private boolean resolveReferences = false;

	public IndirectQuery(String indirectType, Collection<String> indirectIds, Set<String> requestedEntityIds, ContentPersistenceManager persistenceManager) {
		this.indirectType = indirectType;
		this.indirectIds = indirectIds;
		this.requestedEntityIds = requestedEntityIds;
		this.persistenceManager = persistenceManager;
	}

	/**
	 * @return the resolveReferences
	 */
	public boolean isResolveReferences() {
		return resolveReferences;
	}

	/**
	 * @param resolveReferences the resolveReferences to set
	 */
	public void setResolveReferences(boolean resolveReferences) {
		this.resolveReferences = resolveReferences;
	}

	@Override
	public SimpleQueryResult resolve(Resolver resolver) {
		Set<Key> keys = persistenceManager.getKeysForIndirectIds(indirectType, indirectIds, requestedEntityIds);
		
		ResolutionParameters parameters = new ResolutionParameters();
		if (resolveReferences) {
			parameters.setResolveReferences(true);
		}
		return new SimpleQueryResult(resolver.resolve(keys, parameters));
	}


//	public String toString() {
//		return new ToStringBuilder(this, ToStringStyle.DEFAULT_STYLE)
//			.append("resolveReferences", resolveReferences)
//			.toString();
//	}
}
