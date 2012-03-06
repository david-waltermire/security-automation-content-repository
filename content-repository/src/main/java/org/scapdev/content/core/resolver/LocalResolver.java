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
package org.scapdev.content.core.resolver;

import gov.nist.scap.content.model.IEntity;
import gov.nist.scap.content.model.IKey;
import gov.nist.scap.content.model.IKeyedRelationship;
import gov.nist.scap.content.model.definitions.collection.IMetadataModel;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.scapdev.content.core.PersistenceContext;
import org.scapdev.content.core.persistence.ContentPersistenceManager;

public class LocalResolver implements Resolver {
	private final PersistenceContext persistenceContext;
	private final Resolver delegate;


	public LocalResolver(PersistenceContext persistenceContext) {
		this(persistenceContext, null);
	}

	public LocalResolver(PersistenceContext persistenceContext, Resolver delegate) {
		this.persistenceContext = persistenceContext;
		this.delegate = delegate;
	}

	@Override
	public Map<IKey, IEntity<?>> resolve(Set<IKey> keys, ResolutionParameters parameters) throws UnresolvableKeysException {
		ResolutionState state = new ResolutionState(keys);
		do {
			resolveResolvable(state, parameters);
			if (!state.getUnresolvableKeys().isEmpty()) {
				if (delegate == null) {
					UnresolvableKeysException e = new UnresolvableKeysException(state.getUnresolvableKeys().toString());
					e.setUnresolvableKeys(state.getUnresolvableKeys());
					throw e;
				} else {
					delegate.resolveResolvable(state, parameters);
				}
			}
		} while (!state.getUnresolvableKeys().isEmpty());
		return Collections.unmodifiableMap(state.getRetrievedFragments());
	}

	public void resolveResolvable(ResolutionState state, ResolutionParameters parameters) {
		while (!state.getUnresolvedKeys().isEmpty()) {
			resolveInternal(state, parameters);
		}
	}

	private void resolveInternal(ResolutionState state, ResolutionParameters parameters) {
		Map<IKey, IEntity<?>> retrievedFragments = state.getRetrievedFragments();
		Set<IKey> unresolvableKeys = state.getUnresolvableKeys();
		Set<IKey> unresolvedKeys = new HashSet<IKey>();

		IMetadataModel model = persistenceContext.getMetadataModel();
		ContentPersistenceManager contentPersistenceManager = persistenceContext.getContentPersistenceManager();
		for (IKey key : state.getUnresolvedKeys()) {
			IEntity<?> handle = contentPersistenceManager.getEntityByKey(key, model);
			if (handle == null) {
				unresolvableKeys.add(key);
			} else {
				retrievedFragments.put(key, handle);
				if (parameters.isResolveReferences()) {
					Set<IKey> relatedKeys = generateRelatedKeys(handle, parameters);
					relatedKeys.removeAll(retrievedFragments.keySet());
					unresolvedKeys.addAll(relatedKeys);
				}
			}
		}
		state.setUnresolvedKeys(unresolvedKeys);
	}

	private Set<IKey> generateRelatedKeys(IEntity<?> entity, ResolutionParameters parameters) {
		Set<IKey> keys = new LinkedHashSet<IKey>();
		for (IKeyedRelationship relationship : entity.getKeyedRelationships()) {
			IKey key = relationship.getKey();
			keys.add(key);
		}
		return keys;
	}
}
