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

import gov.nist.scap.content.model.ICompositeRelationship;
import gov.nist.scap.content.model.IEntity;
import gov.nist.scap.content.model.IKeyedEntity;
import gov.nist.scap.content.model.definitions.ICompositeRelationshipDefinition;
import gov.nist.scap.content.model.definitions.IEntityDefinition;
import gov.nist.scap.content.model.definitions.IKeyedEntityDefinition;
import gov.nist.scap.content.model.definitions.collection.IMetadataModel;
import gov.nist.scap.content.semantic.IPersistenceContext;
import gov.nist.scap.content.semantic.entity.EntityBuilder;
import gov.nist.scap.content.semantic.entity.EntityProxy;
import gov.nist.scap.content.semantic.entity.KeyedEntityProxy;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.repository.RepositoryException;

/**
 * <p>A manager to build up composite relationships</p>
 * 
 * @see CompositeRelationshipBuilder
 */
class CompositeRelationshipStatementManager implements RegenerationStatementManager {
	private final String baseURI;
    private final IPersistenceContext persistContext;
	
	//all IDs of compositeIdentifierRelationships
	private Collection<String> compositeRelationshipIds;
	
	// key = boundary_boject_id....this map is what this class builds
	private Map<String, CompositeRelationshipBuilder> compositeRelationships = new HashMap<String, CompositeRelationshipBuilder>();
	
	private URI parentEntityURI = null;
	
    
	
	CompositeRelationshipStatementManager(String baseURI,
        IPersistenceContext persistContext) {
	    this.baseURI = baseURI;
	    this.persistContext = persistContext;
	    this.compositeRelationshipIds = persistContext.getOntology().getCompositeRelationshipIds();
	}
	
	/**
	 * Scans triple and processes it if it is relevant to compositeRelationships
	 * @param statement
	 * @return true if triple was processed in some way, false if it was just ignored.
	 */
	public boolean scan(Statement statement){
		URI predicate = statement.getPredicate();
		if (compositeRelationshipIds.contains(predicate.stringValue())){
			try {
                populateCompositeRelationshipInfo(persistContext.getOntology(), statement);
            } catch (RepositoryException e) {
                throw new RuntimeException(e);
            }
			return true;
		} else if ( predicate.equals(persistContext.getOntology().HAS_PARENT_RELATIONSHIP_TO.URI) ) {
	        parentEntityURI = (URI)statement.getObject();
		}
		return false;
	}
	
	/**
	 * <p>
	 * Called after all triples are processed to populate re-constituted entity
	 * with the IndirectRelationships found in the graph.
	 * </p>
	 * 
	 * @param entity
	 *            - to populate.
	 * @throws RepositoryException 
	 */
	public void populateEntity(EntityBuilder builder){
		for (CompositeRelationshipBuilder compositeRelBuilder : compositeRelationships.values()){
			ICompositeRelationship compositeRelBuilderRelationship = compositeRelBuilder.build(persistContext.getOntology());
			builder.addRelationship(compositeRelBuilderRelationship);
		}
		try {
            builder.setParent(new KeyedEntityProxy<IKeyedEntityDefinition, IKeyedEntity<IKeyedEntityDefinition>>(baseURI, persistContext, parentEntityURI));
        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }
	}
	
	/**
	 * <p>
	 * Helper method to populate RelatioshipInfo based on the predicate found
	 * within the triple. Assumes Statement passed in contains a predicate that
	 * is a subProperty of HAS_BOUNDARY_OBJECT_RELATIONSHIP.
	 * </p>
	 * 
	 * @param model
	 * @param statement
	 * @throws RepositoryException 
	 * 
	 * @see RelationshipInfo
	 */
	private void populateCompositeRelationshipInfo(IMetadataModel model, Statement statement) throws RepositoryException{
		// hit on an boundaryIdRelationship of some type
		String compositeRelationshipId = statement.getPredicate().stringValue();
		String compositeURI = statement.getObject().stringValue();
		CompositeRelationshipBuilder compositeRelBuilder = compositeRelationships.get(compositeURI);
		if (compositeRelBuilder == null){
			compositeRelBuilder = new CompositeRelationshipBuilder();
			compositeRelBuilder.setCompositeRelationshipInfo((ICompositeRelationshipDefinition)model.getRelationshipDefinitionById(compositeRelationshipId));
			compositeRelBuilder.setRelatedEntity(new EntityProxy<IEntityDefinition,IEntity<IEntityDefinition>>(baseURI,persistContext,persistContext.getRepository().getValueFactory().createURI(compositeURI)));
			compositeRelationships.put(compositeURI, compositeRelBuilder);
		}
	}
	
}
