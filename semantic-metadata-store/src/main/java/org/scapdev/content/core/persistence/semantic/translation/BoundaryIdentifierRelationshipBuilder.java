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

import gov.nist.scap.content.model.DefaultBoundaryIdentifierRelationship;
import gov.nist.scap.content.model.IBoundaryIdentifierRelationship;
import gov.nist.scap.content.model.definitions.IBoundaryIdentifierRelationshipDefinition;
import gov.nist.scap.content.model.definitions.IExternalIdentifier;
import gov.nist.scap.content.model.definitions.collection.IMetadataModel;

/**
 * Builder for constructing IndirectRelationships
 *
 */
class BoundaryIdentifierRelationshipBuilder {
	private IBoundaryIdentifierRelationshipDefinition relationshipDefinition;
	private String externalIdValue;
	private String externalIdType;
	
	BoundaryIdentifierRelationshipBuilder() {
	}
	
	void setExternalIdValue(String externalIdValue) {
		this.externalIdValue = externalIdValue;
	}
	
	void setIndirectRelationshipInfo(IBoundaryIdentifierRelationshipDefinition relationshipDefinition) {
		this.relationshipDefinition = relationshipDefinition;
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
	IBoundaryIdentifierRelationship build(IMetadataModel model){
		if (relationshipDefinition == null || externalIdValue == null || externalIdType == null){
			throw new IncompleteBuildStateException("Not all values are populated");
		}
		
		IExternalIdentifier externalIdentifier = model.getExternalIdentifierById(externalIdType);
		IBoundaryIdentifierRelationship rel = new DefaultBoundaryIdentifierRelationship(relationshipDefinition, externalIdentifier, externalIdValue);
		
		return rel;
	}
}
