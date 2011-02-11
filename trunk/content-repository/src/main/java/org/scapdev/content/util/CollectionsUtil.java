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
package org.scapdev.content.util;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CollectionsUtil {

	private CollectionsUtil() {
	}

	public static <S> Set<S> getMissingKeys(Map<S, ?> map, Collection<S> requiredKeys) {
		Set<S> result = new HashSet<S>(requiredKeys);
		result.removeAll(map.keySet());
		return result;
	}

	public static <S> Set<S> getExtraKeys(Map<S, ?> map, Collection<S> requiredKeys) {
		Set<S> result = new HashSet<S>(map.keySet());
		result.removeAll(requiredKeys);
		return result;
	}

	public static <S> Set<S> getMissingMembers(Collection<S> members, Collection<S> requiredMembers) {
		Set<S> result = new HashSet<S>(requiredMembers);
		result.removeAll(members);
		return result;
	}

	public static <S> Set<S> getExtraMembers(Collection<S> members, Collection<S> requiredMembers) {
		Set<S> result = new HashSet<S>(members);
		result.removeAll(requiredMembers);
		return result;
	}
}
