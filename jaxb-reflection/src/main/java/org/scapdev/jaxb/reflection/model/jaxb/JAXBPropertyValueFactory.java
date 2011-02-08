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

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlElements;

import org.scapdev.jaxb.reflection.model.JAXBModelException;
import org.scapdev.jaxb.reflection.model.ListPropertyValue;
import org.scapdev.jaxb.reflection.model.Model;
import org.scapdev.jaxb.reflection.model.ObjectPropertyValue;
import org.scapdev.jaxb.reflection.model.PropertyValue;
import org.scapdev.jaxb.reflection.model.PropertyValueFactory;
import org.scapdev.jaxb.reflection.model.TypeInfo;
import org.scapdev.jaxb.reflection.model.TypeInfoPropertyValue;

public class JAXBPropertyValueFactory implements PropertyValueFactory {
	private final Model model;

	JAXBPropertyValueFactory(Model model) {
		this.model = model;
	}

	public PropertyValue createPropertyValue(Field field) {
		Class<?> type = field.getType();
		PropertyValue result;
		if (List.class.isAssignableFrom(type)) {
			result = createDelegate((ParameterizedType)field.getGenericType(), field);
		} else if (JAXBElement.class.isAssignableFrom(type)) {
			result = createDelegate((ParameterizedType)field.getGenericType(), field);
		} else {
			result = createDelegate(type, field);
		}
		return result;
	}

	public PropertyValue createDelegate(Type type, Field field) {
		if (type instanceof ParameterizedType) {
			ParameterizedType parameterizedType = (ParameterizedType)type;
			Class<?> clazz = (Class<?>)parameterizedType.getRawType();
			if (JAXBElement.class.isAssignableFrom(clazz)) {
//				log.error("JAXBElement: class: "+field.getDeclaringClass()+" field: "+field.getName());
				return new JAXBPropertyValue(parameterizedType, field, this);
			} else if (List.class.isAssignableFrom(clazz)) {
				return new ListPropertyValue(parameterizedType, field, this);
			}
		} else if (type instanceof WildcardType) {
			WildcardType wildcardType = (WildcardType)type;
			if (wildcardType.getLowerBounds().length == 0 && wildcardType.getUpperBounds().length == 1) {
				// ? extends class
				return createDelegate(wildcardType.getUpperBounds()[0], field);
			}
			throw new JAXBModelException("Invalid binding for field: "+field);
		}
		return createDelegateObject(type, field);
	}

	private PropertyValue createDelegateObject(Type type, Field field) {
//		System.out.println("Field: "+field+" Type: "+type);
		Class<?> clazz = (Class<?>)type;
		TypeInfo typeInfo = model.getTypeInfo(clazz);
		if (typeInfo != null) {
			return new TypeInfoPropertyValue(typeInfo);
		} else if (field.isAnnotationPresent(XmlElements.class)) {
			return new MultiJAXBPropertyValue(clazz, field, model);
		} else if (field.isAnnotationPresent(XmlElementRefs.class)) {
			return new MultiJAXBPropertyValue(clazz, field, model);
//			throw new ModelException("Invalid binding for field: "+field);
		}
		return new ObjectPropertyValue(clazz);
	}

}
