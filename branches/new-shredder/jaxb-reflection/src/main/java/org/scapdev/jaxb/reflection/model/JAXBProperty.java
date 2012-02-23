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
import java.lang.reflect.InvocationTargetException;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.namespace.QName;

public interface JAXBProperty {
	public enum Type {
		ELEMENT,
		ATTRIBUTE;
	}
//	/**
//	 * Retrieves the Java Method getter associated with the property
//	 * @return the Java Method associated with the property
//	 */
//	Method getGetter();
//	/**
//	 * Retrieves the PropertyValue object directly associated with the property
//	 * @return the PropertyValue associated with the property
//	 */
//	PropertyValue getValue();
	Type getXmlNodeType();
	boolean isRequired();
	boolean isNillable();
	/**
	 * Retrieves the JAXBClass records of the bound XML class for which this property is a member
	 * @return the JAXBClass record of the associated XML bound class
	 */
	JAXBClass getEnclosingJAXBClass();
	/**
	 * Retrieves the properties' name
	 * @return the property name
	 */
	String getName();
	/**
	 * @return <code>true</code> if the property represents a bound XML
	 * 		attribute, <code>false</code> otherwise
	 */
	boolean isAttribute();
	/**
	 * @return <code>true</code> if the property represents a bound XML
	 * 		element, <code>false</code> otherwise
	 */
	boolean isElement();
	/**
	 * Retrieves the instance associated with the property
	 * @param classInstance the Java object to retrieve the property value from 
	 * @return the instance associated with the property
	 * @throws IllegalArgumentException (from Java) when invoking a Java method
	 * 		that is an instance method and the specified object argument is not
	 * 		an instance of the class or interface declaring the underlying
	 * 		method (or of a subclass or implementor thereof); if the number of
	 * 		actual and formal parameters differ; if an unwrapping conversion for
	 * 		primitive arguments fails; or if, after possible unwrapping, a
	 * 		parameter value cannot be converted to the corresponding formal
	 * 		parameter type by a method invocation conversion.
	 * @throws IllegalAccessException (from Java) when invoking a Java method
	 * 		object that enforces Java language access control and the underlying
	 * 		method is inaccessible.
	 * @throws InvocationTargetException (from Java) when invoking a Java method
	 * 		where the underlying method throws an exception.
 	 */
	Object getInstance(Object classInstance) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException;
	<T extends Annotation> T getAnnotation(Class<T> annotation);
	String getXmlLocalPart();
	boolean isList();
	/**
	 * Gets the Type of the property.  This is commonly the Type of the field or
	 * getter/setter.
	 * @return
	 */
	java.lang.reflect.Type getType();
	/**
	 * Gets the actual type within the List (if used), the effective type if
	 * substitution is used or the actual type if otherwise.
	 * @return
	 */
	Class<?> getActualType();
	JAXBProperty newSubstitutionJAXBProperty(XmlElement element);
	QName getQName();
}
