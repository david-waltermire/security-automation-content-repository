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
package org.scapdev.jaxb.reflection.model.jaxb;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.scapdev.content.core.util.BeanUtil;
import org.scapdev.jaxb.reflection.model.AbstractTypeInfo;
import org.scapdev.jaxb.reflection.model.JAXBModelException;
import org.scapdev.jaxb.reflection.model.MutableTypeInfo;
import org.scapdev.jaxb.reflection.model.PropertyInfo;
import org.scapdev.jaxb.reflection.model.PropertyValue;
import org.scapdev.jaxb.reflection.model.PropertyValueFactory;

public class DefaultPropertyInfo implements PropertyInfo {
	private final Field field;
	private final MutableTypeInfo<?> typeInfo;

	private PropertyValue value;

	public DefaultPropertyInfo(Field field, AbstractTypeInfo<?> typeInfo, PropertyValueFactory factory) {
		this.field = field;
		this.typeInfo = typeInfo;
		int modifiers = field.getModifiers();
		if (Modifier.isPrivate(modifiers) || Modifier.isProtected(modifiers)) {
			field.setAccessible(true);
		}
		value = factory.createPropertyValue(field);
	}

	public MutableTypeInfo<?> getTypeInfo() {
		return typeInfo;
	}

	public String getName() {
		return field.getName();
	}

	public PropertyValue getValue() {
		return value;
	}

	/**
	 * @return the field
	 */
	public Field getField() {
		return field;
	}

	public Method getGetter() {
		
		try {
			return BeanUtil.getPropertyGetter(field, typeInfo.getType(), true);
		} catch (NoSuchMethodException e) {
			String name;
			if (field.isAnnotationPresent(XmlAttribute.class)) {
				name = field.getAnnotation(XmlAttribute.class).name();
			} else if (field.isAnnotationPresent(XmlElement.class)) {
				name = field.getAnnotation(XmlElement.class).name();
			} else {
				throw(new JAXBModelException(e));
			}
			try {
				return typeInfo.getType().getDeclaredMethod("get"+name);
			} catch (NoSuchMethodException e2) {
				throw new JAXBModelException(e2);
			}
		}
	}

	public boolean isAttribute() {
		return field.isAnnotationPresent(XmlAttribute.class);
	}

	public boolean isElement() {
		return !isAttribute();
	}

	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
			.append("field", field)
			.append("typeInfo", typeInfo.getType())
			.append("value", value)
			.toString();
	}

	@Override
	public Object getInstance(Object classInstance) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		return getGetter().invoke(classInstance);
	}

	@Override
	public <T extends Annotation> T getAnnotation(Class<T> annotation) {
		return field.getAnnotation(annotation);
	}
}
