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

import org.apache.log4j.Logger;
import org.scapdev.jaxb.reflection.instance.DefaultInstanceVisitor;
import org.scapdev.jaxb.reflection.model.ExtendedModel;
import org.scapdev.jaxb.reflection.model.PropertyInfo;
import org.scapdev.jaxb.reflection.model.TypeInfo;

public class DefaultModelVisitor<MODEL extends ExtendedModel<TYPE>, TYPE extends TypeInfo, PROPERTY extends PropertyInfo> implements ModelVisitor<MODEL, TYPE, PROPERTY> {


	private static final Logger log = Logger.getLogger(DefaultInstanceVisitor.class);

	private final TYPE typeInfo;
	private final MODEL model;

	public DefaultModelVisitor(TYPE typeInfo, MODEL model) {
		this.typeInfo = typeInfo;
		this.model = model;
	}

	protected MODEL getModel() {
		return model;
	}

	public TYPE getTypeInfo(Class<?> clazz) {
		return getModel().getTypeInfo(clazz);
	}

	public void visit() {
		log.debug("visiting type: "+typeInfo.getType().getName());
		processTypeInfo(typeInfo);
	}

	public boolean beforeTypeInfo(TYPE typeInfo) { return true; }
	public void afterTypeInfo(TYPE typeInfo) { }

	public boolean beforePropertyInfo(TYPE typeInfo, PROPERTY property) { return true; }
	public void afterPropertyInfo(TYPE typeInfo, PROPERTY property) {}

	protected void processTypeInfo(TYPE typeInfo) {
		log.trace("visiting typeInfo: "+typeInfo.getType());
		if (beforeTypeInfo(typeInfo)) {
	
			@SuppressWarnings("unchecked")
			TYPE parent = (TYPE)typeInfo.getParent();
	
			// Handle parent first
			if (parent != null) processTypeInfo(parent);
	
			// Iterate over each property, starting with attributes
			for (PropertyInfo property : typeInfo.getAttributePropertyInfos().values()) {
				log.trace("Walking attribute property: "+property.getName());
				@SuppressWarnings("unchecked")
				PROPERTY p = (PROPERTY)property;
				processPropertyInfo(typeInfo, p);
			}
			for (PropertyInfo property : typeInfo.getElementPropertyInfos().values()) {
				log.trace("Walking element property: "+property.getName());
				@SuppressWarnings("unchecked")
				PROPERTY p = (PROPERTY)property;
				processPropertyInfo(typeInfo, p);
			}
		}
		afterTypeInfo(typeInfo);
	}

	protected void processPropertyInfo(TYPE typeInfo, PROPERTY propertyInfo) {
		if (beforePropertyInfo(typeInfo, propertyInfo)) {
			ModelVisitable visitable = propertyInfo.getValue();
			visitable.visit(propertyInfo, this);
		}
		afterPropertyInfo(typeInfo, propertyInfo);
	}

}
