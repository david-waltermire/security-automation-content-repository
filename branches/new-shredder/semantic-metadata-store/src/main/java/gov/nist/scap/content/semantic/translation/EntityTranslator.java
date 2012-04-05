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
/**
 * 
 */
package gov.nist.scap.content.semantic.translation;

import gov.nist.scap.content.model.IEntity;
import gov.nist.scap.content.model.IKey;
import gov.nist.scap.content.model.definitions.ProcessingException;
import gov.nist.scap.content.semantic.IPersistenceContext;
import gov.nist.scap.content.semantic.MetaDataOntology;
import gov.nist.scap.content.semantic.entity.EntityBuilder;
import gov.nist.scap.content.semantic.managers.BoundaryIdentifierRelationshipStatementManager;
import gov.nist.scap.content.semantic.managers.CompositeRelationshipStatementManager;
import gov.nist.scap.content.semantic.managers.KeyStatementManager;
import gov.nist.scap.content.semantic.managers.KeyedRelationshipStatementManager;
import gov.nist.scap.content.semantic.managers.RegenerationStatementManager;
import gov.nist.scap.content.semantic.managers.VersionStatementManager;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.scapdev.content.core.persistence.hybrid.ContentRetrieverFactory;

/**
 * Translates entities across the different modeling languages.
 */
public class EntityTranslator extends
		AbstractSemanticTranslator{
    private final String baseURI;
    private final IPersistenceContext persistContext;
    private final MetaDataOntology ontology;
    
	/**
	 * 
	 * @param baseURI - - the base URI to use for all RDF individuals produced by this translator
	 * @param ontology
	 * @param factory
	 */
	public EntityTranslator(String baseURI, IPersistenceContext persistContext) {
		super(baseURI, persistContext.getRepository().getValueFactory());
		this.baseURI = baseURI;
	    this.persistContext = persistContext;
	    this.ontology = persistContext.getOntology();
	}

	/**
	 * 
	 * @param statements
	 *            - all statements to constitute entity
	 * @param relatedEntityStatements
	 *            - all statement to constitute relatedEntityKeys from
	 *            KeyedRelationships
	 * @param model
	 * @param contentRetrieverFactory
	 * @return
	 * @throws ProcessingException 
	 */
	public <T extends IEntity<?>> T translateToJava(Set<Statement> statements, Map<URI, IKey> relatedEntityKeys, ContentRetrieverFactory contentRetrieverFactory) throws ProcessingException {
		List<RegenerationStatementManager> managers = new LinkedList<RegenerationStatementManager>(); 
		managers.add(new BoundaryIdentifierRelationshipStatementManager(persistContext.getOntology()));
		managers.add(new KeyedRelationshipStatementManager(persistContext.getOntology(), factory, relatedEntityKeys));
        managers.add(new CompositeRelationshipStatementManager(baseURI, persistContext));
		managers.add(new KeyStatementManager(persistContext.getOntology()));
		managers.add(new VersionStatementManager(persistContext));

		EntityBuilder builder = new EntityBuilder();
		for (Statement statement : statements){
			URI predicate = statement.getPredicate();
			//first handle entity specific predicates
			if (predicate.equals(ontology.HAS_CONTENT_ID.URI)){
				String contentId = statement.getObject().stringValue();
				builder.setContentRetriever((contentRetrieverFactory.newContentRetriever(contentId)));
			}
			if (predicate.equals(ontology.HAS_ENTITY_TYPE.URI)){
				String entityType = statement.getObject().stringValue();
				builder.setEntityDefinition(ontology.getEntityDefinitionById(entityType));
			}
			//now handle rest of graph
			for (RegenerationStatementManager statementManager : managers){
				if (statementManager.scan(statement)){
					continue;
				}
			}
		}

		for (RegenerationStatementManager statementManager : managers){
			statementManager.populateEntity(builder);
		}

		@SuppressWarnings("unchecked")
		T retval = (T)builder.build();
		return retval;
	}
}
