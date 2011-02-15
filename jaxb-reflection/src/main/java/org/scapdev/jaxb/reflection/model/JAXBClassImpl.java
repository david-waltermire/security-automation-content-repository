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
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

public class JAXBClassImpl implements JAXBClass {
	private final Class<?> clazz;
	private final JAXBPackage jaxbPackage;
	private final XmlType xmlType;
	private Map<String, JAXBProperty> properties;
	private Map<String, JAXBProperty> attributeProperties;
	private Map<String, JAXBProperty> elementProperties;

	public JAXBClassImpl(Class<?> clazz, JAXBPackage jaxbPackage) {
		this.xmlType = clazz.getAnnotation(XmlType.class);
		if (xmlType == null) {
			throw new InvalidJAXBClassException(clazz.getName());
		}
		this.clazz = clazz;
		this.jaxbPackage = jaxbPackage;
	}

	public Set<String> getElementPropertyNames() {
		LinkedHashSet<String> result = new LinkedHashSet<String>();
		for (String elementName : xmlType.propOrder()) {
			result.add(elementName);
		}
		return result;
	}

	public Class<?> getType() {
		return clazz;
	}

	public XmlType getXmlType() {
		return xmlType;
	}

	public JAXBPackage getJAXBPackage() {
		return jaxbPackage;
	}

	public JAXBClass getSuperclass() {
		return jaxbPackage.getJAXBModel().getClass(clazz.getSuperclass());
	}

	public XmlRootElement getRootElement() {
		return clazz.getAnnotation(XmlRootElement.class);
	}

	public boolean isGlobalElement() {
		return getRootElement() != null;
	}

	public boolean isAbstract() {
		return Modifier.isAbstract(clazz.getModifiers());
	}

	public XmlAccessType getXmlAccessType() {
		// This annotation is inherited, so this call gets the value from this
		// class and all super classes
		XmlAccessorType annotation = clazz.getAnnotation(XmlAccessorType.class);
		XmlAccessType result;
		if (annotation == null) {
			// JAXB defines that the package should now be checked
			result = jaxbPackage.getXmlAccessType();
		} else {
			result = annotation.value();
		}
		return result;
	}

	@Override
	public <T extends Annotation> T getAnnotation(Class<T> annotationClass, boolean checkParent) {
		T annotation = getType().getAnnotation(annotationClass);
		if (annotation == null && checkParent == true) {
			JAXBClass parent = getSuperclass();
			if (parent != null) {
				annotation = parent.getAnnotation(annotationClass, checkParent);
			}
		}
		return annotation;
	}

	@Override
	public Map<String, JAXBProperty> getAttributeProperties() {
		if (properties == null) {
			generateProperties();
		}
		return attributeProperties;
	}

	@Override
	public Map<String, JAXBProperty> getElementProperties() {
		if (properties == null) {
			generateProperties();
		}
		return elementProperties;
	}

	@Override
	public JAXBProperty getProperty(String property) {
		if (properties == null) {
			generateProperties();
		}
		return properties.get(property);
	}

	@Override
	public Map<String, JAXBProperty> getProperties() {
		if (properties == null) {
			generateProperties();
		}
		return properties;
	}

	/**
	 * Generates the JAXBProperty descriptors for this type
	 */
	public final void generateProperties() {
		Iterator<JAXBProperty> iterator;
		switch (getXmlAccessType()) {
		case FIELD:
			iterator = new FieldIterator(this);
			break;
		case PROPERTY:
			iterator = new MethodIterator(this);
			break;
		case PUBLIC_MEMBER:
			iterator = new PublicMethodIterator(this);
			break;
		default:
			throw new UnsupportedOperationException("invalid XmlAccessType: "+getXmlAccessType().toString());
		}

		Map<String, JAXBProperty> properties = new LinkedHashMap<String, JAXBProperty>();
		Map<String, JAXBProperty> attributes = new LinkedHashMap<String, JAXBProperty>();
		Map<String, JAXBProperty> elements = new LinkedHashMap<String, JAXBProperty>();

		while (iterator.hasNext()) {
			JAXBProperty jaxbProperty = iterator.next();

			if (jaxbProperty.isAttribute()) {
				// Attribute
				attributes.put(jaxbProperty.getName(), jaxbProperty);
			} else if (jaxbProperty.isElement()) {
				// Element
				elements.put(jaxbProperty.getName(), jaxbProperty);
			}

			properties.put(jaxbProperty.getName(), jaxbProperty);
		}
		if (properties.isEmpty()) {
			this.properties = Collections.emptyMap();
			this.attributeProperties = Collections.emptyMap();
			this.elementProperties = Collections.emptyMap();
		} else {
			this.properties = Collections.unmodifiableMap(properties);
			if (attributes.isEmpty()) {
				this.attributeProperties = Collections.emptyMap();
			} else {
				this.attributeProperties = Collections.unmodifiableMap(attributes);
			}
			if (elements.isEmpty()) {
				this.elementProperties = Collections.emptyMap();
			} else {
				this.elementProperties = Collections.unmodifiableMap(elements);
			}
		}
	}
}
