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

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.scapdev.content.core.PersistenceContext;
import org.scapdev.content.core.persistence.ContentPersistenceManager;
import org.scapdev.content.model.Entity;
import org.scapdev.content.model.Key;
import org.scapdev.content.model.KeyedRelationship;
import org.scapdev.content.model.MetadataModel;

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
	public Map<Key, Entity> resolve(Set<Key> keys, ResolutionParameters parameters) throws UnresolvableKeysException {
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
		Map<Key, Entity> retrievedFragments = state.getRetrievedFragments();
		Set<Key> unresolvableKeys = state.getUnresolvableKeys();
		Set<Key> unresolvedKeys = new HashSet<Key>();

		MetadataModel model = persistenceContext.getMetadataModel();
		ContentPersistenceManager contentPersistenceManager = persistenceContext.getContentPersistenceManager();
		for (Key key : state.getUnresolvedKeys()) {
			Entity handle = contentPersistenceManager.getEntityByKey(key, model);
			if (handle == null) {
				unresolvableKeys.add(key);
			} else {
				retrievedFragments.put(key, handle);
				if (parameters.isResolveReferences()) {
					Set<Key> relatedKeys = generateRelatedKeys(handle, parameters);
					relatedKeys.removeAll(retrievedFragments.keySet());
					unresolvedKeys.addAll(relatedKeys);
				}
			}
		}
		state.setUnresolvedKeys(unresolvedKeys);
	}

	private Set<Key> generateRelatedKeys(Entity entity, ResolutionParameters parameters) {
		Set<Key> keys = new LinkedHashSet<Key>();
		for (KeyedRelationship relationship : entity.getKeyedRelationships()) {
			Key key = relationship.getKey();
			keys.add(key);
		}
		return keys;
	}
}
