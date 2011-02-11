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

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;

import org.scapdev.jaxb.reflection.instance.InstanceVisitor;
import org.scapdev.jaxb.reflection.model.visitor.ModelVisitor;

public class ObjectPropertyValue implements PropertyValue {
	private final Class<?> type;

	public ObjectPropertyValue(Class<?> type) {
		this.type = type;
	}

	/** {@inheritDoc} */
	public Type getActualType() {
		return type;
	}

	/** {@inheritDoc} */
	public Class<?> getType() {
		return type;
	}

	/** {@inheritDoc} */
	public Collection<Class<?>> getSuperTypes() {
		return Collections.<Class<?>>singleton(type);
	}

	@Override
	public <MODEL extends ExtendedModel<TYPE>, TYPE extends TypeInfo, PROPERTY extends PropertyInfo> void visit(
			Object instance, PROPERTY propertyInfo,
			InstanceVisitor<MODEL, TYPE, PROPERTY> visitor) {
		visitor.handleObject(instance, propertyInfo);
	}

	@Override
	public <MODEL extends ExtendedModel<TYPE>, TYPE extends TypeInfo, PROPERTY extends PropertyInfo> void visit(
			PROPERTY propertyInfo, ModelVisitor<MODEL, TYPE, PROPERTY> visitor) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object getInstance(Object instance) {
		return instance;
	}
}
