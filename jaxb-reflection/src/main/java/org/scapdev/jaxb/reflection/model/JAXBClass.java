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
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * This interface describes the general properties of a bound XML class
 * @author David Waltermire
 *
 */
public interface JAXBClass {
	/**
	 * Retrieves the type of the underlying bound class
	 * @return the bound class
	 */
	Class<?> getType();
	XmlType getXmlType();
	JAXBPackage getJAXBPackage();
	XmlAccessType getXmlAccessType();
	/**
	 * Get the parent JAXBClass that this type extends
	 * @return the JAXBClass of the parent class or <code>null</code> if the type does not extend an other type
	 */
	JAXBClass getSuperclass();

	/**
	 * Identifies if this type is an XML root element
	 * @return <code>true</code> if this type is an XML root element or <code>false</code> otherwise
	 */
	boolean isGlobalElement();
	XmlRootElement getRootElement();
	/**
	 * Identifies if the bound class associated with this type is abstract
	 * @return <code>true</code> if this type is abstract or <code>false</code> otherwise
	 */
	boolean isAbstract();

	<T extends Annotation> T getAnnotation(Class<T> annotationClass, boolean checkParent);

	Set<String> getElementPropertyNames();
	/**
	 * Retrieves the property descriptors for each property defined on this type
	 * @return a map of property names to property descriptors
	 */
	Map<String, JAXBProperty> getProperties();
	/**
	 * Retrieves the property descriptors for properties that correspond to XML attribute properties defined on this type
	 * @return a map of property names to property descriptors
	 */
	Map<String, JAXBProperty> getAttributeProperties();
	/**
	 * Retrieves the property descriptors for properties that correspond to XML element properties defined on this type
	 * @return a map of property names to property descriptors
	 */
	Map<String, JAXBProperty> getElementProperties();
	/**
	 * Retrieves the property descriptor for a specific property defined on this type
	 * @param property the property name
	 * @return a property descriptor for the specified property or <code>null</code> if no such property exists 
	 */
	JAXBProperty getProperty(String property);
}
