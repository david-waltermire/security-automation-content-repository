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
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlElements;

import org.scapdev.jaxb.reflection.instance.visitor.InstanceVisitor;
import org.scapdev.jaxb.reflection.model.ExtendedModel;
import org.scapdev.jaxb.reflection.model.JAXBModelException;
import org.scapdev.jaxb.reflection.model.Model;
import org.scapdev.jaxb.reflection.model.PropertyInfo;
import org.scapdev.jaxb.reflection.model.PropertyValue;
import org.scapdev.jaxb.reflection.model.TypeInfo;
import org.scapdev.jaxb.reflection.model.TypeInfoPropertyValue;
import org.scapdev.jaxb.reflection.model.visitor.ModelVisitor;

/**
 * Represents an Object container that supports references to multiple TypeInfo objects
 * 
 * @author David Waltermire
 *
 */
public class MultiJAXBPropertyValue implements PropertyValue {
	public enum TYPE {
		XML_ELEMENTS,
		XML_ELEMENT_REFS;
	}
	private final Class<?> clazz;
	@SuppressWarnings("unused")
	private final TYPE type;
	private final Map<String, XmlElementInfo> nameToXmlElementInfoMap = new HashMap<String, XmlElementInfo>();

	private final Set<Class<?>> classes = new HashSet<Class<?>>();

	public MultiJAXBPropertyValue(Class<?> clazz, Field field, Model model) {
		this.clazz = clazz;
//		if (Modifier.isProtected(field.getModifiers())) {
//			field.setAccessible(true);
//		}
		XmlElements xmlElements = field.getAnnotation(XmlElements.class);
		if (xmlElements != null) {
			type = TYPE.XML_ELEMENTS;
			for (XmlElement xmlElement : xmlElements.value()) {
				XmlElementInfo info = new XmlElementInfo(xmlElement, model);
				nameToXmlElementInfoMap.put(info.getName(), info);
				classes.add(xmlElement.type());
			}
		} else if (field.isAnnotationPresent(XmlElementRefs.class)) {
			type = TYPE.XML_ELEMENT_REFS;
		} else {
			throw new JAXBModelException(clazz.getName());
		}
		// TODO: parse the XmlElements or XmlElementRefs annotation
	}

	/** {@inheritDoc} */
	public Type getActualType() {
		return clazz;
	}

	/** {@inheritDoc} */
	public Class<?> getType() {
		return clazz;
	}

	/** {@inheritDoc} */
	public Collection<Class<?>> getSuperTypes() {
		return classes;
	}

	/**
	 * @return the nameToXmlElementInfoMap
	 */
	public final Map<String, XmlElementInfo> getNameToXmlElementInfoMap() {
		return nameToXmlElementInfoMap;
	}

	public static class XmlElementInfo {
		private final String name;
		private final TypeInfoPropertyValue value;

		protected XmlElementInfo(XmlElement xmlElement, Model model) {
			this.name = xmlElement.name();
			this.value = new TypeInfoPropertyValue(model.getTypeInfo(xmlElement.type()));
		}

		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}

		/**
		 * @return the typeInfo
		 */
		public TypeInfoPropertyValue getValue() {
			return value;
		}
	}

	@Override
	public <MODEL extends ExtendedModel<TYPE_INFO>, TYPE_INFO extends TypeInfo, PROPERTY extends PropertyInfo> void visit(
			Object instance, PROPERTY propertyInfo,
			InstanceVisitor<MODEL, TYPE_INFO, PROPERTY> visitor) {

		TYPE_INFO valueTypeInfo = (TYPE_INFO)visitor.getTypeInfo(instance.getClass());
		if (valueTypeInfo != null) {
			visitor.processNode(instance, valueTypeInfo);
		} else {
			throw new UnsupportedOperationException("unknown content: "+instance.getClass());
		}
	}

	@Override
	public <MODEL extends ExtendedModel<TYPE_INFO>, TYPE_INFO extends TypeInfo, PROPERTY extends PropertyInfo> void visit(
			PROPERTY propertyInfo, ModelVisitor<MODEL, TYPE_INFO, PROPERTY> visitor) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object getInstance(Object instance) {
		return instance;
	}
}
