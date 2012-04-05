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
package gov.nist.scap.content.semantic.builders;

import gov.nist.scap.content.model.DefaultCompositeRelationship;
import gov.nist.scap.content.model.ICompositeRelationship;
import gov.nist.scap.content.model.IEntity;
import gov.nist.scap.content.model.definitions.ICompositeRelationshipDefinition;
import gov.nist.scap.content.model.definitions.collection.IMetadataModel;
import gov.nist.scap.content.semantic.exceptions.IncompleteBuildStateException;

/**
 * Builder for constructing CompositeRelationships
 *
 */
public class CompositeRelationshipBuilder {
	private ICompositeRelationshipDefinition relationshipDefinition;
    private IEntity<?> relatedEntity;
	
    /**
     * set the composite relationship definition
     * @param relationshipDefinition composite relationship definition
     */
    public void setCompositeRelationshipInfo(ICompositeRelationshipDefinition relationshipDefinition) {
		this.relationshipDefinition = relationshipDefinition;
	}
	
    /**
     * set the target entity of a composite relationship (the child)
     * @param relatedEntity the target entity of a composite relationship (the child)
     */
    public void setRelatedEntity(IEntity<?> relatedEntity) {
        this.relatedEntity = relatedEntity;
    }

	/**
	 * Will build an instance of a composite relationship (parent to child)
	 * @param model the metadata model
	 * @return the composite relationship to be added to the parent owning entity
	 */
    public ICompositeRelationship build(IMetadataModel model){
		if (relationshipDefinition == null || relatedEntity == null){
			throw new IncompleteBuildStateException("Not all values are populated");
		}
		
		ICompositeRelationship rel = new DefaultCompositeRelationship(relationshipDefinition, relatedEntity);
		
		return rel;
	}
}
