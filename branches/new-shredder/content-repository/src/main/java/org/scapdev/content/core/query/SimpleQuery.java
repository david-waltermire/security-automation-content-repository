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
package org.scapdev.content.core.query;

import gov.nist.scap.content.shredder.model.IKey;

import java.util.Collections;
import java.util.Set;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.scapdev.content.core.resolver.ResolutionParameters;
import org.scapdev.content.core.resolver.Resolver;


public class SimpleQuery implements Query<SimpleQueryResult> {
	private final Set<IKey> keys;
	private boolean resolveReferences = false;

	public SimpleQuery(IKey key) {
		this.keys = Collections.singleton(key);
	}

	public SimpleQuery(Set<IKey> keys) {
		this.keys = Collections.unmodifiableSet(keys);
	}

	public SimpleQuery(SimpleQuery query, Set<IKey> keys) {
		this.resolveReferences = query.isResolveReferences();
		this.keys = Collections.unmodifiableSet(keys);
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
		ResolutionParameters parameters = new ResolutionParameters();
		if (resolveReferences) {
			parameters.setResolveReferences(true);
		}
		return new SimpleQueryResult(resolver.resolve(keys, parameters));
	}


	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.DEFAULT_STYLE)
			.append("resolveReferences", resolveReferences)
			.append("keys", keys)
			.toString();
	}
}
