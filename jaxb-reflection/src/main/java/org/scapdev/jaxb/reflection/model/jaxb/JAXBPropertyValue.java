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
import java.util.Collection;

import javax.xml.bind.JAXBElement;

import org.scapdev.jaxb.reflection.instance.InstanceVisitor;
import org.scapdev.jaxb.reflection.model.DelegatingPropertyValue;
import org.scapdev.jaxb.reflection.model.ExtendedModel;
import org.scapdev.jaxb.reflection.model.PropertyInfo;
import org.scapdev.jaxb.reflection.model.PropertyValue;
import org.scapdev.jaxb.reflection.model.PropertyValueFactory;
import org.scapdev.jaxb.reflection.model.TypeInfo;
import org.scapdev.jaxb.reflection.model.visitor.ModelVisitor;

public class JAXBPropertyValue implements DelegatingPropertyValue {
	private Type actualType;
	private final PropertyValue delegate;

	public JAXBPropertyValue(ParameterizedType parameterizedType, Field field, PropertyValueFactory factory) {
		this.actualType = parameterizedType.getRawType();
		this.delegate = factory.createDelegate(parameterizedType.getActualTypeArguments()[0], field);
	}

	/** {@inheritDoc} */
	public Type getActualType() {
		return actualType;
	}

	/** {@inheritDoc} */
	public Class<?> getType() {
		return delegate.getType();
	}

	/** {@inheritDoc} */
	public Collection<Class<?>> getSuperTypes() {
		return delegate.getSuperTypes();
	}

	/** {@inheritDoc} */
	public PropertyValue getDelegate() {
		return delegate;
	}

	@Override
	public <MODEL extends ExtendedModel<TYPE>, TYPE extends TypeInfo, PROPERTY extends PropertyInfo> void visit(
			Object instance, PROPERTY propertyInfo,
			InstanceVisitor<MODEL, TYPE, PROPERTY> visitor) {
		if (instance != null) {
			JAXBElement<?> element = (JAXBElement<?>)instance;
	
			// Skip processing the delgate and process the declared type
			TYPE valueTypeInfo = visitor.getTypeInfo(element.getDeclaredType());
			if (!element.isNil()) {
				visitor.processNode(element.getValue(), valueTypeInfo);
			}
		}
	}

	@Override
	public <MODEL extends ExtendedModel<TYPE>, TYPE extends TypeInfo, PROPERTY extends PropertyInfo> void visit(
			PROPERTY propertyInfo, ModelVisitor<MODEL, TYPE, PROPERTY> visitor) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object getInstance(Object instance) {
		JAXBElement<?> element = (JAXBElement<?>)instance;
		return element.getValue();
	}
}
