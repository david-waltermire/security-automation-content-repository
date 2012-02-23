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

import java.util.Collection;
import java.util.Iterator;

abstract class JAXBPropertyIterator<PROPERTY_TYPE> implements Iterator<JAXBProperty> {
	protected final JAXBClass jaxbClass;
	private final Iterator<PROPERTY_TYPE> propertyIterator;

	public JAXBPropertyIterator(JAXBClass jaxbClass) {
		this.jaxbClass = jaxbClass;
		this.propertyIterator = getProperties().iterator();
	}

	
	/**
	 * @return the jaxbClass
	 */
	public JAXBClass getJaxbClass() {
		return jaxbClass;
	}

	protected abstract Collection<PROPERTY_TYPE> getProperties();
	protected abstract boolean isJaxbProperty(PROPERTY_TYPE type);
	protected abstract JAXBProperty newInstance(PROPERTY_TYPE type, JAXBProperty.Type propertyType);
	protected abstract JAXBProperty.Type getType(PROPERTY_TYPE type);

	@Override
	public boolean hasNext() {
		return propertyIterator.hasNext();
	}

	@Override
	public JAXBProperty next() {
		PROPERTY_TYPE nextProperty = propertyIterator.next();
		JAXBProperty.Type type = getType(nextProperty);
		while (nextProperty != null && type == null && !isJaxbProperty(nextProperty)) {
			nextProperty = propertyIterator.next();
			type = getType(nextProperty);
		}

		JAXBProperty result;
		if (nextProperty == null) {
			result = null;
		} else {
			result = newInstance(nextProperty, type);
		}
		return result;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}
	
}