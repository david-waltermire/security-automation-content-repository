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

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.scapdev.content.core.model.context.instance.EntityHandle;
import org.scapdev.content.model.Key;

public class ResolutionState {
	private Set<Key> unresolvedKeys;
	private final Set<Key> unresolvableKeys;
	private final Map<Key, EntityHandle> retrievedFragments;

	public ResolutionState(Set<Key> initalKeys) {
		unresolvedKeys = new HashSet<Key>(initalKeys);
		unresolvableKeys = new HashSet<Key>();
		retrievedFragments = new TreeMap<Key, EntityHandle>();
	}

	/**
	 * @return the unresolvedKeys
	 */
	public Set<Key> getUnresolvedKeys() {
		return unresolvedKeys;
	}

	/**
	 * @param unresolvedKeys the unresolvedKeys to set
	 */
	public void setUnresolvedKeys(Set<Key> unresolvedKeys) {
		this.unresolvedKeys = unresolvedKeys;
	}

	/**
	 * @return the unresolvableKeys
	 */
	public Set<Key> getUnresolvableKeys() {
		return unresolvableKeys;
	}

	/**
	 * @return the retrievedFragments
	 */
	public Map<Key, EntityHandle> getRetrievedFragments() {
		return retrievedFragments;
	}

}
