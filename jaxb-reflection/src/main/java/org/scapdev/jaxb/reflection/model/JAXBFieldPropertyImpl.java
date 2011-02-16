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
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.namespace.QName;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

class JAXBFieldPropertyImpl extends AbstractJAXBProperty implements JAXBFieldProperty {
	private final Field field;
	private final Boolean required;
	private final Boolean nillable;
	private final String xmlLocalPart;
	private final boolean list;

	public JAXBFieldPropertyImpl(Field field, Type propertyType, JAXBClass jaxbClass) {
		super(jaxbClass, propertyType);
		this.field = field;
		field.setAccessible(true);

		// Initialize attribute/element properties
		String xmlLocalPart = "##default";
		if (isElement()) {
			XmlElement xmlElement = field.getAnnotation(XmlElement.class);
			required = Boolean.valueOf(xmlElement != null && xmlElement.required());
			nillable = Boolean.valueOf(xmlElement != null && xmlElement.nillable());
			if (xmlElement != null) {
				xmlLocalPart = xmlElement.name();
			}
		} else if (isAttribute()) {
			XmlAttribute xmlAttribute = field.getAnnotation(XmlAttribute.class);
			required = Boolean.valueOf(xmlAttribute != null && xmlAttribute.required());
			nillable = null;
			if (xmlAttribute != null) {
				xmlLocalPart = xmlAttribute.name();
			}
		} else {
			required = null;
			nillable = null;
			xmlLocalPart = null;
		}

		if ("##default".equals(xmlLocalPart)) {
			xmlLocalPart = field.getName();
		}
		this.xmlLocalPart = xmlLocalPart;

		list = List.class.isAssignableFrom(field.getType());
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
	public boolean isNillable() {
		if (nillable == null) {
			throw new UnsupportedOperationException("not an element property: "+field.getDeclaringClass()+":"+field.getName());
		}
		return nillable.booleanValue();
	}

	@Override
	public boolean isRequired() {
		if (required == null) {
			throw new UnsupportedOperationException("not an element or attribute property: "+field.getDeclaringClass()+":"+field.getName());
		}
		return required.booleanValue();
	}

	@Override
	public String getXmlLocalPart() {
		if (xmlLocalPart == null) {
			throw new UnsupportedOperationException("not an element or attribute property: "+field.getDeclaringClass()+":"+field.getName());
		}
		return xmlLocalPart;
	}

	@Override
	public boolean isList() {
		return list;
	}

	@Override
	public java.lang.reflect.Type getType() {
		return field.getGenericType();
	}

	@Override
	public Class<?> getActualType() {
		if (isList()) {
			// TODO: implement
			throw new UnsupportedOperationException();
		}
		return field.getType();
	}

	@Override
	public QName getQName() {
		return new QName(getEnclosingJAXBClass().getJAXBPackage().getNamespace(), getXmlLocalPart());
	}

	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
			.append("class", field.getDeclaringClass().toString())
			.append("type", getType())
			.append("name",field.getName())
			.toString();
	}

	@Override
	public JAXBProperty newSubstitutionJAXBProperty(XmlElement element) {
		return new JAXBSubstitutionFieldPropertyImpl(element);
	}

	class JAXBSubstitutionFieldPropertyImpl implements JAXBFieldProperty {
		private final XmlElement xmlElement;
		private final String xmlLocalPart;

		public JAXBSubstitutionFieldPropertyImpl(XmlElement xmlElement) {
			this.xmlElement = xmlElement;
			if (!"##default".equals(xmlElement.name())) {
				this.xmlLocalPart = xmlElement.name();
			} else {
				xmlLocalPart = JAXBFieldPropertyImpl.this.getXmlLocalPart();
			}
		}

		@Override
		public Field getField() {
			return JAXBFieldPropertyImpl.this.getField();
		}

		@Override
		public <T extends Annotation> T getAnnotation(Class<T> annotation) {
			return JAXBFieldPropertyImpl.this.getAnnotation(annotation);
		}

		@Override
		public Object getInstance(Object classInstance)
				throws IllegalArgumentException, IllegalAccessException,
				InvocationTargetException {
			return JAXBFieldPropertyImpl.this.getInstance(classInstance);
		}

		@Override
		public JAXBClass getEnclosingJAXBClass() {
			return JAXBFieldPropertyImpl.this.getEnclosingJAXBClass();
		}

		@Override
		public String getName() {
			return JAXBFieldPropertyImpl.this.getName();
		}

		@Override
		public java.lang.reflect.Type getType() {
			return JAXBFieldPropertyImpl.this.getType();
		}

		@Override
		public Class<?> getActualType() {
			return xmlElement.type();
		}

		@Override
		public QName getQName() {
			return new QName(getEnclosingJAXBClass().getJAXBPackage().getNamespace(), getXmlLocalPart());
		}

		@Override
		public String getXmlLocalPart() {
			return xmlLocalPart;
		}

		@Override
		public Type getXmlNodeType() {
			return JAXBFieldPropertyImpl.this.getXmlNodeType();
		}

		@Override
		public boolean isAttribute() {
			return JAXBFieldPropertyImpl.this.isAttribute();
		}

		@Override
		public boolean isElement() {
			return JAXBFieldPropertyImpl.this.isElement();
		}

		@Override
		public boolean isList() {
			return JAXBFieldPropertyImpl.this.isList();
		}

		@Override
		public boolean isNillable() {
			return JAXBFieldPropertyImpl.this.isNillable();
		}

		@Override
		public boolean isRequired() {
			return JAXBFieldPropertyImpl.this.isRequired();
		}

		@Override
		public JAXBProperty newSubstitutionJAXBProperty(XmlElement element) {
			throw new UnsupportedOperationException();
		}
	}
}
