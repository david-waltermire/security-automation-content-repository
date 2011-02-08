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
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * This abstract implementation of a {@link TypeInfo} provides general functionality relating to bound XML classes
 * @author David Waltermire
 *
 */
public abstract class AbstractTypeInfo<X extends PropertyInfo> implements MutableTypeInfo<X> {
	private final Class<?> type;
	private final SchemaModel schemaModel;
	private MutableTypeInfo<X> parent;
	private Map<String, X> properties;
	private Map<String, X> attributeProperties;
	private Map<String, X> elementProperties;

	public AbstractTypeInfo(Class<?> type, SchemaModel schemaModel) {
		this.type = type;
		this.schemaModel = schemaModel;
	}

	/** {@inheritDoc} */
	public Class<?> getType() {
		return type;
	}

	public MutableTypeInfo<X> getParent() {
		return parent;
	}

	public void setParent(MutableTypeInfo<X> parent) {
		this.parent = parent;
	}

	public SchemaModel getSchemaModel() {
		return schemaModel;
	}

	/** {@inheritDoc} */
	public Map<String, X> getPropertyInfos() {
		return properties;
	}

	/** {@inheritDoc} */
	public Map<String, X> getAttributePropertyInfos() {
		return attributeProperties;
	}

	/** {@inheritDoc} */
	public Map<String, X> getElementPropertyInfos() {
		return elementProperties;
	}

	/** {@inheritDoc} */
	public X getPropertyInfo(String property) {
		return properties.get(property);
	}

	/**
	 * Generates the PropertyInfo descriptors for this type
	 */
	public final void generateProperties(ModelFactory<?, X> factory) {
		if (properties == null) {
	
			Map<String, X> properties = new LinkedHashMap<String, X>();
			Map<String, X> attributes = new LinkedHashMap<String, X>();
			Map<String, X> elements = new LinkedHashMap<String, X>();
			Field[] fields = this.getType().getDeclaredFields();
			for (Field field : fields) {
				X propertyInfo = factory.newPropertyInfo(field, this);//new JAXBPropertyInfo(field, this);
				if (propertyInfo.isAttribute()) {
					// Attribute
					attributes.put(propertyInfo.getName(), propertyInfo);
				} else {
					// Element
					elements.put(propertyInfo.getName(), propertyInfo);
				}
				properties.put(propertyInfo.getName(), propertyInfo);
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

	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
			.append("type", type)
			.append("parent", parent)
			.append("attributes", getAttributePropertyInfos())
			.append("elements", getElementPropertyInfos())
			.toString();
	}

	public XmlRootElement getRootElement() {
		return type.getAnnotation(XmlRootElement.class);
	}

	public boolean isRootElement() {
		return getRootElement() != null;
	}

	public boolean isAbstract() {
		return Modifier.isAbstract(type.getModifiers());
	}

	public <T extends Annotation> T getAnnotation(Class<T> annotationClass, boolean checkParent) {
		T annotation = getType().getAnnotation(annotationClass);
		if (annotation == null && checkParent == true) {
			TypeInfo parent = getParent();
			if (parent != null) {
				annotation = parent.getAnnotation(annotationClass, checkParent);
			}
		}
		return annotation;
	}
}
