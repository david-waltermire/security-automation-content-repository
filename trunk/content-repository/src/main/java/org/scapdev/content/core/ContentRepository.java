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
package org.scapdev.content.core;

import java.io.IOException;
import java.util.Collection;
import java.util.Set;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.scapdev.content.core.persistence.ContentPersistenceManager;
import org.scapdev.content.core.persistence.hybrid.DefaultHybridContentPersistenceManager;
import org.scapdev.content.core.query.DefaultQueryProcessor;
import org.scapdev.content.core.query.Query;
import org.scapdev.content.core.query.QueryProcessor;
import org.scapdev.content.core.query.QueryResult;
import org.scapdev.content.core.query.SimpleQuery;
import org.scapdev.content.core.resolver.LocalResolver;
import org.scapdev.content.core.resolver.Resolver;
import org.scapdev.content.core.writer.DefaultInstanceWriter;
import org.scapdev.content.core.writer.InstanceWriter;
import org.scapdev.content.core.writer.NamespaceMapper;
import org.scapdev.content.model.Key;
import org.scapdev.content.model.MetadataModel;
import org.scapdev.content.model.MetadataModelFactory;
import org.scapdev.content.model.processor.jaxb.JAXBEntityProcessor;

/**
 * This class encapsolates all content repository functionality.  It has methods
 * that enable importing and retrieving content from the repository.
 */
public class ContentRepository {
	private final JAXBEntityProcessor jaxbEntityProcessor;
	private final Resolver resolver;
	private final QueryProcessor queryProcessor;
	private final DefaultPersistenceContext persistenceContext;

	public ContentRepository() throws IOException, JAXBException, ClassNotFoundException {
		persistenceContext = new DefaultPersistenceContext();
		persistenceContext.setMetadataModel(MetadataModelFactory.newInstance());
//		persistenceContext.setContentPersistenceManager(new MemoryResidentPersistenceManager()());
		persistenceContext.setContentPersistenceManager(new DefaultHybridContentPersistenceManager());
		jaxbEntityProcessor = new JAXBEntityProcessor(persistenceContext);
		resolver = new LocalResolver(persistenceContext);
		queryProcessor = new DefaultQueryProcessor();
	}

	/**
	 * Retrieves the metadata schema model associated with this repository. This
	 * instance is automatically generated using JAXB reflection based on classes
	 * on the classpath.
	 * @return the metadata schema model instance
	 */
	public MetadataModel getMetadataModel() {
		return persistenceContext.getMetadataModel();
	}

	public ContentPersistenceManager getContentPersistenceManager() {
		return persistenceContext.getContentPersistenceManager();
	}

	public void setContentPersistenceManager(ContentPersistenceManager contentPersistenceManager) {
		persistenceContext.setContentPersistenceManager(contentPersistenceManager);
	}

	public QueryProcessor getQueryProcessor() {
		return queryProcessor;
	}

	public Resolver getResolver() {
		return resolver;
	}

	/**
	 * Retrieves the JAXB-based processor that supports importing content into
	 * the metadata repository.
	 * @see JAXBEntityProcessor#newImporter()
	 * @return the entity processor for the content repository
	 */
	public JAXBEntityProcessor getJaxbEntityProcessor() {
		return jaxbEntityProcessor;
	}

	/**
	 * This convenience method executes a basic query that retrieves the entity
	 * associated with the provided key
	 * @param key the entity key
	 * @return a query result containing the entity associated with the key
	 * @throws IOException
	 */
	public QueryResult query(Key key) throws IOException {
		return query(key, false);
	}

	/**
	 * This convenience method executes a basic query that retrieves the entity
	 * associated with the provided key
	 * @param key the entity key
	 * @param resolveReferences if <code>true</code> relationships on the entity
	 * 		matching the key will be resolved recursively causing all related
	 * 		entities to also be retrieved in the query result
	 * @return a query result containing the entity associated with the key
	 * @throws IOException
	 */
	public QueryResult query(Key key, boolean resolveReferences) throws IOException {
		SimpleQuery query = new SimpleQuery(key);
		query.setResolveReferences(resolveReferences);
		return query(query);
	}

	/**
	 * This convenience method executes a query that retrieves entities of a
	 * specified type that are associated with external identifiers of a
	 * specified type.
	 * @param externalIdType the external identifier type to query for 
	 * @param externalIds a collection of external identifier values to query
	 * 		for that are of the type defined by the externalIdType parameter
	 * @param requestedEntityTypes the type of entities to return that have an
	 * 		association with the specified external identifiers
	 * @param resolveReferences if <code>true</code> relationships on the matching
	 * 		entities will be resolved recursively causing all related
	 * 		entities to also be retrieved in the query result
	 * @return a query result containing the entities associated with the
	 * 		external identifiers
	 * @throws IOException
	 */
	public QueryResult query(String externalIdType, Collection<String> externalIds, Set<String> requestedEntityTypes, boolean resolveReferences) throws IOException {
		IndirectQuery query = new IndirectQuery(externalIdType, externalIds, requestedEntityTypes, getContentPersistenceManager());
		query.setResolveReferences(resolveReferences);
		return query(query);
	}

	/**
	 * 
	 * @param <RESULT> the type of the query result that will be produced by the
	 * 		query.
	 * @param query the query to execute against the repository
	 * @return the query result
	 */
	public <RESULT extends QueryResult> RESULT query(Query<RESULT> query) {
		RESULT queryResult = queryProcessor.query(query, resolver);
		return queryResult;
	}

	/**
	 * This method should be called to cleanly shutdown the repository.
	 */
	public void shutdown() {
		jaxbEntityProcessor.shutdown();
	}

	/**
	 * Retrieves an instance writer that can be used to assemble a query
	 * result into an XML document containing the queried entities.
	 * @return
	 * @throws JAXBException
	 */
	public InstanceWriter newInstanceWriter() throws JAXBException {
		Marshaller marshaller = getMetadataModel().getJAXBContext().createMarshaller();
		NamespaceMapper mapper = new NamespaceMapper(getMetadataModel());
		marshaller.setProperty("com.sun.xml.bind.namespacePrefixMapper", mapper);
		return new DefaultInstanceWriter(marshaller, getMetadataModel());
	}
}
