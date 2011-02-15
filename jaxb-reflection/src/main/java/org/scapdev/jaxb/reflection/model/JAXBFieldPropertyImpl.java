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

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

class JAXBFieldPropertyImpl extends AbstractJAXBProperty implements JAXBFieldProperty {
	private final Field field;

	public JAXBFieldPropertyImpl(Field field, Type propertyType, JAXBClass jaxbClass) {
		super(jaxbClass, propertyType);
		this.field = field;
		field.setAccessible(true);
	}

	@Override
	public <T extends Annotation> T getAnnotation(Class<T> annotation) {
		return field.getAnnotation(annotation);
	}

	@Override
	public Field getField() {
		return field;
	}

	@Override
	public Object getInstance(Object instance) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		return field.get(instance);
	}

	@Override
	public String getName() {
		return field.getName();
	}

	@Override
	public boolean isAttribute() {
		return Type.ATTRIBUTE.equals(getType());
	}

	@Override
	public boolean isElement() {
		return Type.ELEMENT.equals(getType());
	}

	@Override
	public boolean isNillable() {
		boolean result;
		if (isElement()) {
			XmlElement xmlElement = field.getAnnotation(XmlElement.class);
			result = (xmlElement != null && xmlElement.nillable());
		} else {
			throw new UnsupportedOperationException("not an element: "+field.getDeclaringClass()+":"+field.getName());
		}
		return result;
	}

	@Override
	public boolean isRequired() {
		boolean result;
		if (isElement()) {
			XmlElement xmlElement = field.getAnnotation(XmlElement.class);
			result = (xmlElement != null && xmlElement.required());
		} else if (isAttribute()) {
			XmlAttribute xmlAttribute = field.getAnnotation(XmlAttribute.class);
			result = (xmlAttribute != null && xmlAttribute.required());
		} else {
			throw new UnsupportedOperationException("not an element: "+field.getDeclaringClass()+":"+field.getName());
		}
		return result;
	}
}
