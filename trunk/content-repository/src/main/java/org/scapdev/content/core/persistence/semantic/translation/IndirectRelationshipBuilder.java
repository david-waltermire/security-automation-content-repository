/*******************************************************************************
 * The MIT License
 * 
 * Copyright (c) 2011 Paul Cichonski
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
package org.scapdev.content.core.persistence.semantic.translation;

import org.scapdev.content.model.AbstractRelationship;
import org.scapdev.content.model.Entity;
import org.scapdev.content.model.ExternalIdentifier;
import org.scapdev.content.model.ExternalIdentifierInfo;
import org.scapdev.content.model.IndirectRelationship;
import org.scapdev.content.model.IndirectRelationshipInfo;
import org.scapdev.content.model.MetadataModel;

/**
 * Builder for constructing IndirectRelationships
 *
 */
class IndirectRelationshipBuilder {
	private IndirectRelationshipInfo relationshipInfo;
	private String externalIdValue;
	private String externalIdType;
	
	IndirectRelationshipBuilder() {
		// TODO Auto-generated constructor stub
	}
	
	void setExternalIdValue(String externalIdValue) {
		this.externalIdValue = externalIdValue;
	}
	
	void setIndirectRelationshipInfo(IndirectRelationshipInfo relationshipInfo) {
		this.relationshipInfo = relationshipInfo;
	}
	
	void setExternalIdType(String externalIdType) {
		this.externalIdType = externalIdType;
	}

	/**
	 * Will build an instance of an indirect relationship. NOTE: the client must
	 * add the newly created relationship to the owning entity directly after
	 * calling this method.
	 * 
	 * @param model
	 * @param entity
	 *            - the owningEntity of the relationship
	 * @return
	 */
	IndirectRelationship build(MetadataModel model, RebuiltEntity entity){
		if (relationshipInfo == null || externalIdValue == null || externalIdType == null){
			throw new IncompleteBuildStateException("Not all values are populated");
		}
		ExternalIdentifierInfo externalIdInfo = model.getExternalIdentifierById(externalIdType);
		ExternalIdentifier externalIdentifier = new InternalExternalIdentifier(externalIdInfo, externalIdValue);
		IndirectRelationship rel = new InternalIndirectRelationship(relationshipInfo, entity, externalIdentifier);
		
		return rel;
	}
	
	
	private static class InternalExternalIdentifier extends ExternalIdentifier {

		InternalExternalIdentifier(
				ExternalIdentifierInfo externalIdentifierInfo, String value) {
			super(externalIdentifierInfo, value);
			// TODO Auto-generated constructor stub
		}
		
	}
	
	private static class InternalIndirectRelationship extends AbstractRelationship<IndirectRelationshipInfo> implements IndirectRelationship {
		private final ExternalIdentifier externalIdentifier;

		InternalIndirectRelationship(IndirectRelationshipInfo relationshipInfo, Entity owningEntity, ExternalIdentifier externalIdentifier) {
			super(relationshipInfo, owningEntity);
			this.externalIdentifier = externalIdentifier;
		}

		@Override
		public ExternalIdentifier getExternalIdentifier() {
			return externalIdentifier;
		}
		
	}
}
