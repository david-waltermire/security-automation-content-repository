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

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;

import org.scapdev.jaxb.reflection.model.JAXBProperty.Type;

class MethodIterator extends JAXBPropertyIterator<Method> {
	public MethodIterator(JAXBClass jaxbClass) {
		super(jaxbClass);
	}

	@Override
	protected Collection<Method> getProperties() {
		return Arrays.asList(getJaxbClass().getType().getDeclaredMethods());
	}

	@Override
	protected boolean isJaxbProperty(Method type) {
		// TODO: check for getters and setters
		return true;
	}

	@Override
	protected JAXBProperty newInstance(Method type, Type propertyType) {
		throw new UnsupportedOperationException();
//		return new JAXBMethodPropertyImpl(type, propertyType, getJaxbClass());
	}

	@Override
	protected Type getType(Method type) {
		throw new UnsupportedOperationException();
	}
}