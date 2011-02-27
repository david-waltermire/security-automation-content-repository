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

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.scapdev.content.model.jaxb.IndirectRelationshipType;
import org.scapdev.jaxb.reflection.model.JAXBClass;
import org.scapdev.jaxb.reflection.model.JAXBProperty;
import org.scapdev.jaxb.reflection.model.instance.PropertyPathEvaluator;

class IndirectRelationshipInfoImpl extends AbstractRelationshipInfo implements IndirectRelationshipInfo {
	private static final Logger log = Logger.getLogger(IndirectRelationshipInfoImpl.class);
	private final IndirectBindingInfo binding;
	private final MetadataModel model;

	public IndirectRelationshipInfoImpl(IndirectRelationshipType node, SchemaInfo schemaInfo, MetadataModel model, InitializingJAXBClassVisitor init) {
		super(node, schemaInfo);
		binding = init.getIndirectBindingInfo(node.getId());
		this.model = model;
	}

	@Override
	public List<IndirectRelationship> newRelationships(Object instance, Entity owningEntity) {
		List<ExternalIdentifier> externalIdentifiers = getExternalIdentifiers(instance, owningEntity);

		List<IndirectRelationship> result = new ArrayList<IndirectRelationship>(externalIdentifiers.size());
		for (ExternalIdentifier externalIdentifier : externalIdentifiers) {
			result.add(new IndirectRelationshipImpl(this, owningEntity, externalIdentifier));
		}
		return result;
	}

	private List<ExternalIdentifier> getExternalIdentifiers(Object instance, Entity owningEntity) {
		String qualifier = null;
		List<JAXBProperty> qualifierPath = binding.getQualifierPath();
		if (qualifierPath != null) {
			try {
				qualifier = PropertyPathEvaluator.evaluate(instance, qualifierPath);
			} catch (IllegalArgumentException e) {
				throw new ModelInstanceException(e);
			} catch (IllegalAccessException e) {
				throw new ModelInstanceException(e);
			} catch (InvocationTargetException e) {
				throw new ModelInstanceException(e);
			}
		}

		List<String> values;
		try {
			values = PropertyPathEvaluator.evaluateMultiple(instance, binding.getValuePath());
		} catch (IllegalArgumentException e) {
			throw new ModelInstanceException(e);
		} catch (IllegalAccessException e) {
			throw new ModelInstanceException(e);
		} catch (InvocationTargetException e) {
			throw new ModelInstanceException(e);
		}
		
		Map<String, String> qualifierToExternalIdentifierMap = binding.getExternalIdentifierRefs();
		String externalIdentifierId = qualifierToExternalIdentifierMap.get(qualifier);

		List<ExternalIdentifier> result;
		if (externalIdentifierId == null) {
			log.warn("Unable to find indirect qualifier '"+qualifier+"' for id '"+getId()+"' on entity:"+owningEntity.getKey());
			result = Collections.emptyList();
		} else {
			ExternalIdentifierInfo externalIdentifierInfo = model.getExternalIdentifierById(externalIdentifierId);
			if (externalIdentifierInfo == null) {
				throw new ModelException("Invalid external identifier: "+externalIdentifierId);
			}
			result = new ArrayList<ExternalIdentifier>(values.size());
			for (String value : values) {
				result.add(new ExternalIdentifier(externalIdentifierInfo, value));
			}
		}
		return result;
	}

	@Override
	public JAXBClass getOwningJAXBClass() {
		return binding.getJaxbClass();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof IndirectRelationshipInfoImpl))
			return false;
		IndirectRelationshipInfoImpl other = (IndirectRelationshipInfoImpl) obj;
		if (!getId().equals(other.getId())) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		int result = 13;
		result = 37 * result +  getId().hashCode();
		return result;
	}
}
