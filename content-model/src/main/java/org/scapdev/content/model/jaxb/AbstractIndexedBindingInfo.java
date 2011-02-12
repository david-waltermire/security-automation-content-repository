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
package org.scapdev.content.model.jaxb;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.scapdev.jaxb.reflection.model.DefaultTypeInfo;
import org.scapdev.jaxb.reflection.model.jaxb.DefaultPropertyInfo;

abstract class AbstractIndexedBindingInfo<ANNOTATION extends Annotation> extends AbstractBindingInfo<ANNOTATION> implements IndexedBindingInfo<ANNOTATION> {
	private final Map<String, List<DefaultPropertyInfo>> propertyMap;

	protected AbstractIndexedBindingInfo(String id, ANNOTATION annotation, Map<String, List<DefaultPropertyInfo>> propertyMap, DefaultTypeInfo typeInfo) {
		super(id, annotation, typeInfo);
		this.propertyMap = Collections.unmodifiableMap(propertyMap);
	}

	/** {@inheritDoc} */
	public Map<String, List<DefaultPropertyInfo>> getPropertyMap() {
		return propertyMap;
	}
}