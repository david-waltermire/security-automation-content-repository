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

import gov.nist.scap.content.model.DefaultKeyedRelationship;
import gov.nist.scap.content.model.IKeyedEntity;
import gov.nist.scap.content.model.IKeyedRelationship;
import gov.nist.scap.content.model.definitions.IKeyedEntityDefinition;
import gov.nist.scap.content.model.definitions.IKeyedRelationshipDefinition;
import gov.nist.scap.content.semantic.IPersistenceContext;
import gov.nist.scap.content.semantic.entity.KeyedEntityProxy;
import gov.nist.scap.content.semantic.exceptions.IncompleteBuildStateException;

import org.openrdf.model.URI;
import org.openrdf.repository.RepositoryException;

/**
 * A builder to create keyed relationships for statements about an entity
 * 
 * @author Adam Halbardier
 */
public class KeyedRelationshipBuilder {
    private IKeyedRelationshipDefinition keyedRelationshipInfo;

    private URI relatedEntity;
    private IPersistenceContext ipc;

    /**
     * default constructor
     * 
     * @param ipc the persistence context
     */
    public KeyedRelationshipBuilder(IPersistenceContext ipc) {
        this.ipc = ipc;
    }

    /**
     * set the definition of the relationship
     * 
     * @param keyedRelationshipInfo the definition of the relationship
     */
    public void setKeyedRelationshipInfo(
            IKeyedRelationshipDefinition keyedRelationshipInfo) {
        this.keyedRelationshipInfo = keyedRelationshipInfo;
    }

    /**
     * set the target entity URI
     * 
     * @param relatedEntity the target entity
     */
    public void setRelatedEntityURI(URI relatedEntity) {
        this.relatedEntity = relatedEntity;
    }

    /**
     * Will build an instance of an keyed relationship.
     * 
     * @return the relationship that must be added to the owning entity
     */
    public IKeyedRelationship build() {
        if (keyedRelationshipInfo == null || relatedEntity == null) {
            throw new IncompleteBuildStateException(
                "Not all values are populated");
        }
        try {
            return new DefaultKeyedRelationship(
                keyedRelationshipInfo,
                new KeyedEntityProxy<IKeyedEntityDefinition, IKeyedEntity<IKeyedEntityDefinition>>(
                    ipc,
                    relatedEntity));
        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }
    }
}
