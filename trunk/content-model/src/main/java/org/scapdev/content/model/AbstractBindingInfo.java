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

import java.lang.annotation.Annotation;

import org.scapdev.jaxb.reflection.model.JAXBClass;

abstract class AbstractBindingInfo<X extends Annotation> implements BindingInfo<X> {
	private final String id;
	private final X annotation;
	private final JAXBClass jaxbClass;

	AbstractBindingInfo(String id, X annotation, JAXBClass jaxbClass) {
		this.id = id;
		this.annotation = annotation;
		this.jaxbClass = jaxbClass;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public X getAnnotation() {
		return annotation;
	}

	@Override
	public JAXBClass getJaxbClass() {
		return jaxbClass;
	}

}
