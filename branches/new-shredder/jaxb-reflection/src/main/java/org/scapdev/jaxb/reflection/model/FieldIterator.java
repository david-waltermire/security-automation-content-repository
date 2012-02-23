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
package org.scapdev.jaxb.reflection.model;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

import javax.xml.bind.annotation.XmlAttribute;

import org.scapdev.jaxb.reflection.model.JAXBProperty.Type;

class FieldIterator extends JAXBPropertyIterator<Field> {
	private final Set<String> elements;

	public FieldIterator(JAXBClass jaxbClass) {
		super(jaxbClass);
		elements = jaxbClass.getElementPropertyNames();
	}

	@Override
	protected Collection<Field> getProperties() {
		return Arrays.asList(jaxbClass.getType().getDeclaredFields());
	}

	@Override
	protected boolean isJaxbProperty(Field type) {
		return true;
	}

	@Override
	protected JAXBProperty newInstance(Field type, JAXBProperty.Type propertyType) {
		return new JAXBFieldPropertyImpl(type, propertyType, getJaxbClass());
	}

	@Override
	protected Type getType(Field type) {
		Type result = null;
		if (elements.contains(type.getName())) {
			result = Type.ELEMENT;
		} else if (type.isAnnotationPresent(XmlAttribute.class)) {
			result = Type.ATTRIBUTE;
		}
		return result;
	}
}