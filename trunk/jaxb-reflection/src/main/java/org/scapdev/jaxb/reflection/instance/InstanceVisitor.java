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
package org.scapdev.jaxb.reflection.instance;

import org.scapdev.jaxb.reflection.model.ExtendedModel;
import org.scapdev.jaxb.reflection.model.PropertyInfo;
import org.scapdev.jaxb.reflection.model.TypeInfo;

public interface InstanceVisitor<MODEL extends ExtendedModel<TYPE>, TYPE extends TypeInfo, PROPERTY extends PropertyInfo> {

	TYPE getTypeInfo(Class<?> clazz);

	boolean beforeDocument(Object document, TYPE typeInfo);
	void afterDocument(Object document, TYPE typeInfo);

	boolean beforeNode(Object instance, TYPE typeInfo);
	void afterNode(Object instance, TYPE typeInfo);

	void beforeTypeInfo(Object instance, TYPE typeInfo);
	void afterTypeInfo(Object instance, TYPE typeInfo);

	void beforePropertyInfo(Object instance, TYPE typeInfo, PROPERTY property);
	void afterPropertyInfo(Object instance, TYPE typeInfo, PROPERTY property);

	void handleObject(Object instance, PROPERTY propertyInfo);

	void processNode(Object instance, TYPE typeInfo);

}
