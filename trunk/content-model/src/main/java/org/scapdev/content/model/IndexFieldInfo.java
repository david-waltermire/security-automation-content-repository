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
package org.scapdev.content.model;

import java.util.List;

import org.scapdev.jaxb.reflection.model.JAXBProperty;

/**
 * Represents an index field that is associated with an index key or key
 * reference within the XML schema meta model.
 */
public interface IndexFieldInfo extends SchemaComponent {

	/**
	 * Retrieves the sequence of properties, XML elements or attributes, that
	 * need to be traversed to retrieve the value associated with an instance of
	 * this field.
	 * 
	 * @return the propertyPath
	 */
	List<JAXBProperty> getPropertyPath();

	/**
	 * Retrieves the field value for an instance of XML bound data using the
	 * property path associated with this field.
	 * 
	 * @param instance the bound instance of the XML graph
	 * @return the value at the tail of the property path or <code>null</code>
	 *         if no value exists.
	 * @throws ModelInstanceException if there is a problem accessing the underlying field or method
	 */
	String getValue(Object instance) throws ModelInstanceException;
}
