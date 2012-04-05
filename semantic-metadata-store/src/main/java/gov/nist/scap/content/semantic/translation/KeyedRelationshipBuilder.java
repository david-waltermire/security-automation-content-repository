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
package gov.nist.scap.content.semantic.translation;

import gov.nist.scap.content.model.AbstractRelationship;
import gov.nist.scap.content.model.IKey;
import gov.nist.scap.content.model.IKeyedEntity;
import gov.nist.scap.content.model.IKeyedRelationship;
import gov.nist.scap.content.model.IRelationshipVisitor;
import gov.nist.scap.content.model.definitions.IKeyedRelationshipDefinition;
import gov.nist.scap.content.model.definitions.collection.IMetadataModel;

//TODO: need to handle rebuilding the KEY AND ENTITY!!!!
class KeyedRelationshipBuilder {
	private IKeyedRelationshipDefinition keyedRelationshipInfo;
	
	private IKey relatedEntityKey;
	
	
	void setKeyedRelationshipInfo(IKeyedRelationshipDefinition keyedRelationshipInfo) {
		this.keyedRelationshipInfo = keyedRelationshipInfo;
	}
	
	void setRelatedEntityKey(IKey relatedEntityKey) {
		this.relatedEntityKey = relatedEntityKey;
	}


	
	
	/**
	 * Will build an instance of an keyed relationship. NOTE: the client must
	 * add the newly created relationship to the owning entity directly after
	 * calling this method.
	 * 
	 * @param model
	 * @param entity
	 *            - the owningEntity of the relationship
	 * @return
	 */
	IKeyedRelationship build(IMetadataModel model){
		if (keyedRelationshipInfo == null || relatedEntityKey == null){
			throw new IncompleteBuildStateException("Not all values are populated");
		}

		// TODO: eliminate the use of this internal class if possible
		IKeyedRelationship rel = new InternalKeyedRelationship(keyedRelationshipInfo, relatedEntityKey);
		
		return rel;
	}
	
	private static class InternalKeyedRelationship extends
			AbstractRelationship<IKeyedRelationshipDefinition> implements
			IKeyedRelationship {
		//key of related entity
		private IKey key;
		
		InternalKeyedRelationship(IKeyedRelationshipDefinition relationshipInfo,
				IKey key) {
			super(relationshipInfo);
			this.key = key;
		}

		public IKey getKey() {
			return key;
		}

		public IKeyedEntity<?> getReferencedEntity() {
			// TODO: figure out how to provide this information
			// Perhaps use a lazy fetch approach if the entity is not available?
			throw new UnsupportedOperationException("implement");
		}

		public void accept(IRelationshipVisitor visitor) {
			visitor.visit(this);
		}

		
	}
}
