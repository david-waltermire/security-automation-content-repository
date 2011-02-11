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
package org.scapdev.content.model.jaxb;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.scapdev.content.model.ModelException;
import org.scapdev.jaxb.reflection.model.DefaultTypeInfo;
import org.scapdev.jaxb.reflection.model.jaxb.DefaultModel;
import org.scapdev.jaxb.reflection.model.jaxb.DefaultPropertyInfo;
import org.scapdev.jaxb.reflection.model.visitor.PropertyPathModelVisitor;

abstract class AbstractIndexIdentifyingPropertyPathModelVisitor<ANNOTATION extends Annotation, FIELD extends Annotation> extends
		PropertyPathModelVisitor<DefaultModel, DefaultTypeInfo, DefaultPropertyInfo> {
	private final ANNOTATION indexedAnnotation;
	private final Map<String, List<DefaultPropertyInfo>> propertyMap;
	private final Class<FIELD> fieldClass;

	public AbstractIndexIdentifyingPropertyPathModelVisitor(ANNOTATION indexedAnnotation, DefaultTypeInfo typeInfo, DefaultModel model, Class<ANNOTATION> annotationClass, Class<FIELD> fieldClass) {
		super(typeInfo, model);
		if (typeInfo.getAnnotation(annotationClass, false) == null) {
			throw new ModelException("Type '"+typeInfo.getType().getName()+"' does not contain a "+indexedAnnotation.getClass());
		}
		propertyMap = new LinkedHashMap<String, List<DefaultPropertyInfo>>();
		this.indexedAnnotation = indexedAnnotation;
		this.fieldClass = fieldClass;
	}

	protected ANNOTATION getIndexedAnnotation() {
		return indexedAnnotation;
	}

	/**
	 * @return the propertyMap
	 */
	public Map<String, List<DefaultPropertyInfo>> getPropertyMap() {
		return propertyMap;
	}

	protected abstract List<String> getIndexedFields();
	protected abstract String getIndexedAnnotationId();
	protected abstract String getIndexedFieldId(FIELD field);

	@Override
	public void visit() {
		super.visit();

		Set<String> fields = new HashSet<String>(getIndexedFields());
		Set<String> locatedFields = propertyMap.keySet();
		if (!locatedFields.equals(fields)) {
			fields.removeAll(locatedFields);
			if (!fields.isEmpty()) {
				throw new ModelException("Unable to identify fields for "+indexedAnnotation.getClass().getName()+" '"+getIndexedAnnotationId()+"': "+fields.toString());
			}
		}
	}

	@Override
	public boolean beforePropertyInfo(DefaultTypeInfo typeInfo,
			DefaultPropertyInfo property) {
		// TODO: support elements
//		if (property.isAttribute()) {
			FIELD field = property.getAnnotation(fieldClass);
			if (field != null) {
				String id = getIndexedFieldId(field);
				if (propertyMap.containsKey(id)) {
					throw new ModelException("Duplicate field found for "+indexedAnnotation.getClass().getName()+" '"+getIndexedAnnotationId()+"': "+id);
				}
				propertyMap.put(id, getPropertyPath());
			}
//		}
		return false;
	}
}
