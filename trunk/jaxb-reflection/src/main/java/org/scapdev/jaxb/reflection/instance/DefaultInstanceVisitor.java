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

import java.lang.reflect.InvocationTargetException;

import org.apache.log4j.Logger;
import org.scapdev.jaxb.reflection.model.ExtendedModel;
import org.scapdev.jaxb.reflection.model.JAXBModelException;
import org.scapdev.jaxb.reflection.model.PropertyInfo;
import org.scapdev.jaxb.reflection.model.TypeInfo;

/**
 * 
 * @author davidwal
 *
 * @param <STATE> Visitor state type
 */
public class DefaultInstanceVisitor<MODEL extends ExtendedModel<TYPE>, TYPE extends TypeInfo, PROPERTY extends PropertyInfo> implements InstanceVisitor<MODEL, TYPE, PROPERTY> {
	private static final Logger log = Logger.getLogger(DefaultInstanceVisitor.class);

	private final Object document;
	private final MODEL model;

	public DefaultInstanceVisitor(Object document, MODEL model) {
		this.document = document;
		this.model = model;
	}

	protected MODEL getModel() {
		return model;
	}

	public TYPE getTypeInfo(Class<?> clazz) {
		return getModel().getTypeInfo(clazz);
	}

	public void visit() {
		log.debug("visiting document: "+document);
		TYPE typeInfo = model.getTypeInfo(document.getClass());
		if (beforeDocument(document, typeInfo)) {
			processNode(document, typeInfo);
		}
		afterDocument(document, typeInfo);
	}

	public boolean beforeDocument(Object document, TYPE typeInfo) { return true; }
	public void afterDocument(Object document, TYPE typeInfo) {}

	public boolean beforeNode(Object instance, TYPE typeInfo) { return true; }
	public void afterNode(Object instance, TYPE typeInfo) {}

	public void beforeTypeInfo(Object instance, TYPE typeInfo) {}
	public void afterTypeInfo(Object instance, TYPE typeInfo) {}

	public void beforePropertyInfo(Object instance, TYPE typeInfo, PROPERTY property) {}
	public void afterPropertyInfo(Object instance, TYPE typeInfo, PROPERTY property) {}

	public void handleObject(Object instance, PROPERTY propertyInfo) {}

	public final void processNode(Object instance, TYPE typeInfo) {
		log.trace("Walking node: "+(instance != null ? instance.getClass() : "(not set)"));
		if (beforeNode(instance, typeInfo)) {
			if (instance != null) processTypeInfo(instance, typeInfo);
		}
		afterNode(instance, typeInfo);
	}

	protected void processTypeInfo(Object instance, TYPE typeInfo) {
		log.trace("Walking typeInfo: "+typeInfo.getType());
		beforeTypeInfo(instance, typeInfo);

		@SuppressWarnings("unchecked")
		TYPE parent = (TYPE)typeInfo.getParent();

		// Handle parent first
		if (parent != null) processTypeInfo(instance, parent);

		// Iterate over each property, starting with attributes
		for (PropertyInfo property : typeInfo.getAttributePropertyInfos().values()) {
			log.trace("Walking attribute property: "+property.getName());
			@SuppressWarnings("unchecked")
			PROPERTY p = (PROPERTY)property;
			processPropertyInfo(instance, typeInfo, p);
		}
		for (PropertyInfo property : typeInfo.getElementPropertyInfos().values()) {
			log.trace("Walking element property: "+property.getName());
			@SuppressWarnings("unchecked")
			PROPERTY p = (PROPERTY)property;
			processPropertyInfo(instance, typeInfo, p);
		}

		afterTypeInfo(instance, typeInfo);
	}

	protected void processPropertyInfo(Object instance, TYPE typeInfo, PROPERTY propertyInfo) {
		beforePropertyInfo(instance, typeInfo, propertyInfo);
		Object obj;
		try {
			obj = propertyInfo.getInstance(instance);
		} catch (IllegalArgumentException e) {
			throw new JAXBModelException(e);
		} catch (IllegalAccessException e) {
			throw new JAXBModelException(e);
		} catch (InvocationTargetException e) {
			throw new JAXBModelException(e);
		}
		InstanceVisitable visitable = propertyInfo.getValue();
		visitable.visit(obj, propertyInfo, this);
		afterPropertyInfo(instance, typeInfo, propertyInfo);
	}
}
