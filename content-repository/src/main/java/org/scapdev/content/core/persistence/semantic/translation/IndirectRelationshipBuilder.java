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

import gov.nist.scap.content.shredder.metamodel.IMetadataModel;
import gov.nist.scap.content.shredder.model.DefaultIndirectRelationship;
import gov.nist.scap.content.shredder.model.IIndirectRelationship;
import gov.nist.scap.content.shredder.rules.IExternalIdentifier;
import gov.nist.scap.content.shredder.rules.IIndirectRelationshipDefinition;

/**
 * Builder for constructing IndirectRelationships
 *
 */
class IndirectRelationshipBuilder {
	private IIndirectRelationshipDefinition relationshipDefinition;
	private String externalIdValue;
	private String externalIdType;
	
	IndirectRelationshipBuilder() {
		// TODO Auto-generated constructor stub
	}
	
	void setExternalIdValue(String externalIdValue) {
		this.externalIdValue = externalIdValue;
	}
	
	void setIndirectRelationshipInfo(IIndirectRelationshipDefinition relationshipDefinition) {
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
	IIndirectRelationship build(IMetadataModel model, RebuiltEntity<?> entity){
		if (relationshipDefinition == null || externalIdValue == null || externalIdType == null){
			throw new IncompleteBuildStateException("Not all values are populated");
		}
		
		IExternalIdentifier externalIdentifier = model.getExternalIdentifierInfoById(externalIdType);
		IIndirectRelationship rel = new DefaultIndirectRelationship(relationshipDefinition, entity, externalIdentifier, externalIdValue);
		
		return rel;
	}
}
