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

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.log4j.Logger;
import org.scapdev.jaxb.reflection.model.JAXBClass;
import org.scapdev.jaxb.reflection.model.JAXBModel;
import org.scapdev.jaxb.reflection.model.JAXBModelException;
import org.scapdev.jaxb.reflection.model.JAXBProperty;

/**
 * 
 * @author davidwal
 *
 * @param <STATE> Visitor state type
 */
public class DefaultInstanceVisitor implements InstanceVisitor {
	private static final Logger log = Logger.getLogger(DefaultInstanceVisitor.class);

	private final Object document;
	private final JAXBModel model;

	public DefaultInstanceVisitor(Object document, JAXBModel model) {
		this.document = document;
		this.model = model;
	}

	protected JAXBModel getModel() {
		return model;
	}

	public JAXBClass getJAXBClass(Class<?> clazz) {
		return getModel().getClass(clazz);
	}

	public void visit() {
		log.debug("visiting document: "+document);

		Class<?> clazz;
		Object effectiveInstance;
		if (JAXBElement.class.isAssignableFrom(document.getClass())) {
			JAXBElement<?> element = (JAXBElement<?>) document;
			clazz = element.getDeclaredType();
			effectiveInstance = element.getValue();
		} else {
			clazz = document.getClass();
			effectiveInstance = document;
		}
		
		JAXBClass jaxbClass = model.getClass(clazz);
		if (beforeDocument(document, jaxbClass)) {
			processNode(effectiveInstance);
		}
		afterDocument(document, jaxbClass);
	}

	public boolean beforeDocument(Object document, JAXBClass jaxbClass) { return true; }
	public void afterDocument(Object document, JAXBClass jaxbClass) {}

	public boolean beforeNode(Object instance, JAXBClass jaxbClass) { return true; }
	public boolean beforeNode(JAXBElement<?> instance, JAXBClass jaxbClass) { return true; }
	
	public void afterNode(Object instance, JAXBClass jaxbClass) {}
	public void afterNode(JAXBElement<?> instance, JAXBClass jaxbClass) {}

	public void beforeJAXBClass(Object instance, JAXBClass jaxbClass) {}
	public void afterJAXBClass(Object instance, JAXBClass jaxbClass) {}

	public void beforeJAXBProperty(Object instance, JAXBProperty property) {}
	public void afterJAXBProperty(Object instance, JAXBProperty property) {}

	public void handleObject(Object instance, JAXBProperty property) {}

	private void processNode(Object instance) {
		log.trace("Walking node: "+(instance != null ? instance.getClass() : "(not set)"));
		if (instance != null) {
//			Object effectiveInstance = instance;
//			if (JAXBElement.class.isAssignableFrom(instance.getClass())) {
//				JAXBElement<?> element = (JAXBElement<?>)instance;
//				effectiveInstance = element.getValue();
//			}
//			JAXBClass jaxbClass = model.getClass(effectiveInstance.getClass());
//			if (beforeNode(instance, jaxbClass)) {
//				processJAXBClass(effectiveInstance, jaxbClass);
//			}
//			afterNode(instance, jaxbClass);
			if (JAXBElement.class.isAssignableFrom(instance.getClass())) {
				JAXBElement<?> element = (JAXBElement<?>)instance;
				JAXBClass jaxbClass = model.getClass(element.getDeclaredType());
				if (beforeNode(element, jaxbClass)) {
					instance = element.getValue();
					if (instance != null) {
						processJAXBClass(instance, jaxbClass);
					}
				}
				afterNode(element, jaxbClass);
			} else {
				JAXBClass jaxbClass = model.getClass(instance.getClass());
				if (beforeNode(instance, jaxbClass)) {
					processJAXBClass(instance, jaxbClass);
				}
				afterNode(instance, jaxbClass);
			}
		}

	}

	protected void processJAXBClass(Object instance, JAXBClass jaxbClass) {
		log.trace("Walking JAXB class: "+jaxbClass.getType().getName());
		beforeJAXBClass(instance, jaxbClass);

		JAXBClass parent = jaxbClass.getSuperclass();

		// Handle parent first
		if (parent != null) processJAXBClass(instance, parent);

		// Iterate over each property, starting with attributes
		for (JAXBProperty property : jaxbClass.getAttributeProperties().values()) {
			log.trace("Walking attribute property: "+property.getName());
			processJAXBProperty(instance, property);
		}
		for (JAXBProperty property : jaxbClass.getElementProperties().values()) {
			log.trace("Walking element property: "+property.getName());
			processJAXBProperty(instance, property);
		}
		afterJAXBClass(instance, jaxbClass);
	}

	protected void processJAXBProperty(Object instance, JAXBProperty property) {
		beforeJAXBProperty(instance, property);
		Object obj;
		try {
			obj = property.getInstance(instance);
		} catch (IllegalArgumentException e) {
			throw new JAXBModelException(e);
		} catch (IllegalAccessException e) {
			throw new JAXBModelException(e);
		} catch (InvocationTargetException e) {
			throw new JAXBModelException(e);
		}
		processPropertyValue(obj, property);
		afterJAXBProperty(instance, property);
	}

	private void processPropertyValue(Object instance, JAXBProperty property) {
		if (instance == null) {
			handleObject(null, property);
		} else {
			Class<?> clazz = instance.getClass();
			if (List.class.isAssignableFrom(clazz)) {
				List<?> list = (List<?>)instance;
				processList(list, property);
			} else if (JAXBElement.class.isAssignableFrom(clazz)) {
				processNode(instance);
			} else if (instance.getClass().isAnnotationPresent(XmlType.class)) {
				processNode(instance);
			} else {
				handleObject(instance, property);
			}
		}
	}

	private void processList(List<?> list, JAXBProperty property) {
		for (Object instance : list) {
			processPropertyValue(instance, property);
		}
	}
}
