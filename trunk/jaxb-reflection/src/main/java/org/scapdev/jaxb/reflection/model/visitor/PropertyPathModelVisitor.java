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
package org.scapdev.jaxb.reflection.model.visitor;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.scapdev.jaxb.reflection.model.ExtendedModel;
import org.scapdev.jaxb.reflection.model.PropertyInfo;
import org.scapdev.jaxb.reflection.model.TypeInfo;

public class PropertyPathModelVisitor<MODEL extends ExtendedModel<TYPE>, TYPE extends TypeInfo, PROPERTY extends PropertyInfo> extends DefaultModelVisitor<MODEL, TYPE, PROPERTY> {
	private final LinkedList<PROPERTY> propertyPath = new LinkedList<PROPERTY>();

	public PropertyPathModelVisitor(TYPE typeInfo, MODEL model) {
		super(typeInfo, model);
	}

	public List<PROPERTY> getPropertyPath() {
		return new ArrayList<PROPERTY>(propertyPath);
	}

	public final PROPERTY getCurrentPropertyInfo() {
		return propertyPath.peekLast();
	}

	@SuppressWarnings("unchecked")
	public TYPE getCurrentTypeInfo() {
		return (TYPE)getCurrentPropertyInfo().getTypeInfo();
	}

	@Override
	protected void processPropertyInfo(TYPE typeInfo, PROPERTY propertyInfo) {
		pushPropertyInfo(propertyInfo);
		super.processPropertyInfo(typeInfo, propertyInfo);
		popPropertyInfo(propertyInfo);
	}

	private void popPropertyInfo(PROPERTY info) {
		PROPERTY poppedInfo = propertyPath.pollLast();
		assert(poppedInfo == info);
	}

	private void pushPropertyInfo(PROPERTY info) {
		propertyPath.addLast(info);
	}
}
