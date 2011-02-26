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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.scapdev.content.annotation.ExternalIdentifierRef;
import org.scapdev.content.annotation.Indirect;
import org.scapdev.content.annotation.IndirectQualifier;
import org.scapdev.content.annotation.IndirectValue;
import org.scapdev.jaxb.reflection.model.JAXBModel;
import org.scapdev.jaxb.reflection.model.JAXBProperty;
import org.scapdev.jaxb.reflection.model.visitor.PropertyPathModelVisitor;

class IndirectRelationshipIdentifyingPropertyPathModelVisitor extends PropertyPathModelVisitor {
	private List<JAXBProperty> qualifierPath;
	private List<JAXBProperty> valuePath;
	private final Map<String, String> qualifierToExternalIdentifierIdMap;

	public IndirectRelationshipIdentifyingPropertyPathModelVisitor(Indirect indirect, JAXBModel model) {
		super(model);
		this.qualifierToExternalIdentifierIdMap = parseExternalIdentifierRefs(indirect);
	}

	private Map<String, String> parseExternalIdentifierRefs(Indirect indirect) {
		Map<String, String> result = new HashMap<String, String>();
		for (ExternalIdentifierRef ref : indirect.externalIdentifiers()) {
			String qualifier = ref.qualifier();
			if (qualifier.isEmpty()) {
				qualifier = null;
			}
			result.put(qualifier, ref.idRef());
		}
		if (result.isEmpty()) {
			result = Collections.emptyMap();
		}
		return result;
	}

	/**
	 * @return the valuePath
	 */
	public List<JAXBProperty> getValuePath() {
		return valuePath;
	}

	/**
	 * @return the qualifierPath
	 */
	public List<JAXBProperty> getQualifierPath() {
		return qualifierPath;
	}

	/**
	 * @return the qualifierPath
	 */
	public Map<String, String> getExternalIdentifierRefs() {
		return qualifierToExternalIdentifierIdMap;
	}

	@Override
	public boolean beforeJAXBProperty(JAXBProperty property) {
		IndirectQualifier qualifier = property.getAnnotation(IndirectQualifier.class);
		IndirectValue value = property.getAnnotation(IndirectValue.class);
		if (qualifier != null) {
			if (qualifierPath == null) {
				qualifierPath = getPropertyPath();
			} else {
				throw new ModelException("Duplicate IndirectQualifier on field: "+property.toString());
			}
		} else {
			if (value != null) {
				if (valuePath == null) {
					valuePath = getPropertyPath();
				} else {
					throw new ModelException("Duplicate IndirectValue on field: "+property.toString());
				}
			}
		}
		return false;
	}

}
