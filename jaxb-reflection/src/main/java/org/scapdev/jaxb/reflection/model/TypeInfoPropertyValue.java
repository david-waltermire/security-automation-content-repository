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

import org.scapdev.jaxb.reflection.instance.InstanceVisitor;
import org.scapdev.jaxb.reflection.model.visitor.ModelVisitor;

public class TypeInfoPropertyValue implements PropertyValue {
	private final TypeInfo typeInfo;

	public TypeInfoPropertyValue(TypeInfo typeInfo) {
		this.typeInfo = typeInfo;
	}

	/** {@inheritDoc} */
	public Type getActualType() {
		return getTypeInfo().getType();
	}

	/** {@inheritDoc} */
	public Class<?> getType() {
		return getTypeInfo().getType();
	}

	/** {@inheritDoc} */
	public Collection<Class<?>> getSuperTypes() {
		throw new UnsupportedOperationException(); // TODO: return super types of this JAXB class via XmlElementRefs
	}

	public TypeInfo getTypeInfo() {
		return typeInfo;
	}

	@Override
	public <MODEL extends ExtendedModel<TYPE>, TYPE extends TypeInfo, PROPERTY extends PropertyInfo> void visit(
			Object instance, PROPERTY propertyInfo,
			InstanceVisitor<MODEL, TYPE, PROPERTY> visitor) {
		@SuppressWarnings("unchecked")
		TYPE valueTypeInfo = (TYPE)typeInfo;
		visitor.processNode(instance, valueTypeInfo);
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
