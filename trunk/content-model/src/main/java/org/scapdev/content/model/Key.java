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
package org.scapdev.content.model;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

public class Key implements Comparable<Key> {
	private final String id;
	private final LinkedHashMap<String, String> idToValueMap;

	public Key(String id, List<String> typeIds, List<String> values) {
		this(id, typeIds.toArray(new String[typeIds.size()]), values.toArray(new String[typeIds.size()]));
	}

	public Key(String id, String[] typeIds, String[] values) {
		this.id = id;
		idToValueMap = new LinkedHashMap<String, String>();
		for (int i = 0;i<typeIds.length;i++) {
			idToValueMap.put(typeIds[i], values[i]);
		}
	}

	public Key(String id, LinkedHashMap<String, String> idToValueMap) {
		this.id = id;
		this.idToValueMap = idToValueMap;
	}

	public String getId() {
		return id;
	}

	public Set<String> getIdentifierIds() {
		return idToValueMap.keySet();
	}

	public Collection<String> getValues() {
		return idToValueMap.values();
	}

	public String toString() {
		return new StringBuilder()
				.append(id)
				.append("=")
				.append(idToValueMap.toString())
				.toString();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Key))
			return false;
		Key other = (Key) obj;
		if (!id.equals(other.id)) {
			return false;
		} else if (!idToValueMap.equals(other.idToValueMap))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		int result = 1;
		result = 37 * result +  id.hashCode();
		result = 37 * result + idToValueMap.hashCode();
		return result;
	}

	@Override
	public int compareTo(Key that) {
		if ( this == that ) return 0;

		int result = this.id.compareTo(that.id);
		if (result == 0) {
			for (String id : idToValueMap.keySet()) {
				result = idToValueMap.get(id).compareTo(that.idToValueMap.get(id));
				if (result != 0) break;
			}
		}
		return result;
	}
}
