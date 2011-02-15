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
package org.scapdev.jaxb.reflection.model.instance;

import org.scapdev.jaxb.reflection.model.JAXBClass;
import org.scapdev.jaxb.reflection.model.JAXBProperty;

public interface InstanceVisitor {

	JAXBClass getJAXBClass(Class<?> clazz);

	boolean beforeDocument(Object document, JAXBClass jaxbClass);
	void afterDocument(Object document, JAXBClass jaxbClass);

	boolean beforeNode(Object instance, JAXBClass jaxbClass);
	void afterNode(Object instance, JAXBClass jaxbClass);

	void beforeJAXBClass(Object instance, JAXBClass jaxbClass);
	void afterJAXBClass(Object instance, JAXBClass jaxbClass);

	void beforeJAXBProperty(Object instance, JAXBProperty property);
	void afterJAXBProperty(Object instance, JAXBProperty property);

	void handleObject(Object instance, JAXBProperty property);
}
