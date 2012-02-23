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

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Test;

public class CollectionsUtilTest {
	private static List<Data<String>> data = new LinkedList<Data<String>>();

	@BeforeClass
	public static void setup() {
		data.add(
				/** single missing key */
				new Data<String>(
						new String[]{ "test1", "test2", "test3" },
						new String[]{ "test1", "test2", "test3", "test4" },
						new String[]{ "test4" },
						new String[]{ }
				)
		);
		data.add(
				/** single extra key */
				new Data<String>(
						new String[]{ "test1", "test2", "test3" },
						new String[]{ "test1", "test2" },
						new String[]{ },
						new String[]{ "test3" }
				)
		);
	}

	@Test
	public void testGetMissingKeys() {
		for (Data<String> item : data) {
			assertEquals(item.getMissingKeys(), CollectionsUtil.getMissingKeys(item.getTestMap(), item.getRequiredKeys()));
		}
	}

	@Test
	public void testGetExtraKeys() {
		for (Data<String> item : data) {
			assertEquals(item.getExtraKeys(), CollectionsUtil.getExtraKeys(item.getTestMap(), item.getRequiredKeys()));
		}
	}

	private static class Data<T> {
		private final Map<T, ?> testMap;
		private final Collection<T> requiredKeys;
		private final Set<T> missingKeys;
		private final Set<T> extraKeys;

		protected Data(T[] testKeys, T[] requiredKeys, T[] missingKeys, T[] extraKeys) {
			this(new HashSet<T>(Arrays.asList(testKeys)), Arrays.asList(requiredKeys), new HashSet<T>(Arrays.asList(missingKeys)), new HashSet<T>(Arrays.asList(extraKeys)));
		}

		protected Data(Set<T> testKeys, Collection<T> requiredKeys, Set<T> missingKeys, Set<T> extraKeys) {
			this.testMap = new HashMap<T, String>();
			for (T key : testKeys) {
				testMap.put(key, null);
			}
			this.requiredKeys = requiredKeys;
			this.missingKeys = missingKeys;
			this.extraKeys = extraKeys;
		}

		/**
		 * @return the testMap
		 */
		public Map<T, ?> getTestMap() {
			return testMap;
		}

		/**
		 * @return the requiredKeys
		 */
		public Collection<T> getRequiredKeys() {
			return requiredKeys;
		}

		/**
		 * @return the missingKeys
		 */
		public Set<T> getMissingKeys() {
			return missingKeys;
		}

		/**
		 * @return the extraKeys
		 */
		public Set<T> getExtraKeys() {
			return extraKeys;
		}
	}
}
