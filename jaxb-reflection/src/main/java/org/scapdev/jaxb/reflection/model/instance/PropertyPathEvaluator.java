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
package org.scapdev.jaxb.reflection.model.instance;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.JAXBElement;

import org.scapdev.jaxb.reflection.model.JAXBProperty;
/**
 * @author David Waltermire
 */
public class PropertyPathEvaluator {
	private PropertyPathEvaluator() {}

	/**
	 * Evaluates a property path against an instance returning the value at the
	 * end of the path.
	 * @param <T> the type of the JAXBProperty
	 * @param instance the JAXB instance to evaluate
	 * @param path the property path to follow
	 * @return the value at the end of the property path
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public static String evaluate(Object instance, List<JAXBProperty> path) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		for (JAXBProperty propertyInfo : path) {
			if (instance == null) break;
			instance = propertyInfo.getInstance(instance);
			instance = getValue(instance);
		}
		String retval = null;
		if (instance != null) {
			retval = instance.toString();
		}
		return retval;
	}

	private static Object getValue(Object instance) {
		if (instance != null) {
			Class<?> clazz = instance.getClass();
			if (List.class.isAssignableFrom(clazz)) {
				throw new UnsupportedOperationException(clazz.getName());
			} else if (JAXBElement.class.isAssignableFrom(clazz)) {
				@SuppressWarnings("unchecked")
				JAXBElement<Object> element = (JAXBElement<Object>) instance;
				return getValue(element.getValue());
			} else {
				// Leaf or XML type
				return instance;
			}
		}
		return null;
	}

	public static List<String> evaluateMultiple(Object instance, List<JAXBProperty> path) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		if (instance == null) return Collections.emptyList();

		Collection<Object> propertyData = Collections.singletonList(instance);
		for (JAXBProperty propertyInfo : path) {
			if (propertyData.isEmpty()) break;

			Collection<Object> instanceData = new LinkedList<Object>();
			for (Object obj : propertyData) {
				Object nextInstance = propertyInfo.getInstance(obj);
				instanceData.addAll(getValueMultiple(nextInstance));
			}
			propertyData = instanceData;
		}

		List<String> result;
		if (propertyData.isEmpty()) {
			result = Collections.emptyList();
		} else {
			result = new ArrayList<String>(propertyData.size());
			for (Object obj : propertyData) {
				result.add(obj.toString());
			}
		}
		return result;
	}

	private static List<Object> getValueMultiple(Object instance) {
		List<Object> result = Collections.emptyList();

		if (instance != null) {
			Class<?> clazz = instance.getClass();
			if (List.class.isAssignableFrom(clazz)) {
				@SuppressWarnings("unchecked")
				List<Object> data = (List<Object>) instance;
				result = data;
			} else if (JAXBElement.class.isAssignableFrom(clazz)) {
				@SuppressWarnings("unchecked")
				JAXBElement<Object> element = (JAXBElement<Object>) instance;
				result = getValueMultiple(element.getValue());
			} else {
				result = Collections.singletonList(instance);
			}
		}
		return result;
	}
}
