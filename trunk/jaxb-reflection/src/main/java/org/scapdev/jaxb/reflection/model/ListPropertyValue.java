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
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.scapdev.jaxb.reflection.instance.InstanceVisitor;
import org.scapdev.jaxb.reflection.model.visitor.ModelVisitor;

public class ListPropertyValue implements DelegatingPropertyValue {
	private final Type type;
	private final PropertyValue delegate;

	public ListPropertyValue(ParameterizedType parameterizedType, Field field, PropertyValueFactory factory) {
		this.type = parameterizedType.getRawType();
		this.delegate = factory.createDelegate(parameterizedType.getActualTypeArguments()[0], field);
	}

	/** {@inheritDoc} */
	public Type getActualType() {
		return type;
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
			Object instance, PROPERTY propertyInfo, InstanceVisitor<MODEL, TYPE, PROPERTY> visitor) {
		List<?> list = (List<?>)instance;
		for (Object obj : list) {
			delegate.visit(obj, propertyInfo, visitor);
		}

	}

	@Override
	public <MODEL extends ExtendedModel<TYPE>, TYPE extends TypeInfo, PROPERTY extends PropertyInfo> void visit(
			PROPERTY propertyInfo, ModelVisitor<MODEL, TYPE, PROPERTY> visitor) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object getInstance(Object instance) {
		List<?> list = (List<?>)instance;
		List<Object> result = new ArrayList<Object>(list.size());
		for (Object obj : list) {
			result.add(delegate.getInstance(obj));
		}
		return instance;
	}
}
