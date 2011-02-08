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

import org.scapdev.jaxb.reflection.instance.visitor.InstanceVisitable;
import org.scapdev.jaxb.reflection.model.visitor.ModelVisitable;


public interface PropertyValue extends InstanceVisitable, ModelVisitable {
//	 *  The type of this property value segment */
	/**
	 * Retrieves the type (e.g. field type) of this property value segment
	 * @return a java class object
	 */
	Type getActualType();
	/**
	 * Retrieves the type internal type of this property value segment 
	 * @return a java class object
	 */
	Class<?> getType();
	/**
	 * Retrieves the collection of possible types that may extend the internal type of this property value segment 
	 * @return a collection of java class objects
	 */
	Collection<Class<?>> getSuperTypes();
	Object getInstance(Object instance);
}
