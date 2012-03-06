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
import gov.nist.scap.content.shredder.model.IKey;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class ResolutionState {
	private Set<IKey> unresolvedKeys;
	private final Set<IKey> unresolvableKeys;
	private final Map<IKey, IEntity<?>> retrievedFragments;

	public ResolutionState(Set<IKey> initalKeys) {
		unresolvedKeys = new HashSet<IKey>(initalKeys);
		unresolvableKeys = new HashSet<IKey>();
		retrievedFragments = new TreeMap<IKey, IEntity<?>>();
	}

	/**
	 * @return the unresolvedKeys
	 */
	public Set<IKey> getUnresolvedKeys() {
		return unresolvedKeys;
	}

	/**
	 * @param unresolvedKeys the unresolvedKeys to set
	 */
	public void setUnresolvedKeys(Set<IKey> unresolvedKeys) {
		this.unresolvedKeys = unresolvedKeys;
	}

	/**
	 * @return the unresolvableKeys
	 */
	public Set<IKey> getUnresolvableKeys() {
		return unresolvableKeys;
	}

	/**
	 * @return the retrievedFragments
	 */
	public Map<IKey, IEntity<?>> getRetrievedFragments() {
		return retrievedFragments;
	}

}
