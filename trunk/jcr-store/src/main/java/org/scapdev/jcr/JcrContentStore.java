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
package org.scapdev.jcr;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.Binary;
import javax.jcr.LoginException;
import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.jcr.Value;
import javax.jcr.ValueFactory;
import javax.jcr.nodetype.NodeTypeManager;
import javax.jcr.nodetype.NodeTypeTemplate;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.jackrabbit.core.RepositoryImpl;
import org.apache.jackrabbit.core.config.RepositoryConfig;
import org.apache.log4j.Logger;
import org.scapdev.content.core.persistence.hybrid.AbstractContentRetriever;
import org.scapdev.content.core.persistence.hybrid.ContentRetriever;
import org.scapdev.content.core.persistence.hybrid.ContentStore;
import org.scapdev.content.model.Entity;
import org.scapdev.content.model.MetadataModel;
import org.xml.sax.InputSource;


public class JcrContentStore implements ContentStore {
	
	private static final Logger LOG = Logger.getLogger(JcrContentStore.class);
	public static final String JCR_REPO_NAME = "SACR_jcr_repo";
	
	private static int showCount = 20;
	private Repository repo = null;
	private Session session;
	private Node entityRoot;
	public static final String TYPE_NAME_ENTITIES = "Entities";
	public static final String TYPE_NAME_ENTITY = "Entity";
	public static final String TYPE_NAME_CONTENT = "Content";
	
	public static final String REPOSITORY_HOME_PROPERTY = "repository.home";
	public static final String REPOSITORY_CONFIG_FILE_PROPERTY = "repository.location";
	public static final String REPOSITORY_NAME_PROPERTY = "repository.name";
	
	private String repositoryConfigFileName;  // location of repository.xml file 
	private String repositoryHome;            // directory containing the repository 

	public JcrContentStore()  {
		initRepoStructure();
	}
		
	@Override
	@SuppressWarnings("unchecked")
	public JAXBElement<Object> getContent(String contentId, MetadataModel model)  {
		JAXBElement<Object> result = null;
//		LOG.info("Getting content for content id: " + contentId);
		Node node = null;
		try {
			node = session.getNodeByIdentifier(contentId);
		}
		catch (Exception e) {
			LOG.error("Error retrieving jcr content for content id: " + contentId, e);
		} 
		if (node == null) {
			LOG.info("Node not retrieved for content id: " + contentId);
		} else {
//			LOG.info("Unmarshalling content for node: " + contentId);
			try {			
				Property contentProperty = node.getProperty(TYPE_NAME_CONTENT);
				Value value = contentProperty.getValue();
				Binary binary = value.getBinary();
				InputStream inputStream = binary.getStream();
				Unmarshaller unmarshaller = model.getJAXBContext().createUnmarshaller();
				result = (JAXBElement<Object>) unmarshaller.unmarshal(inputStream);

			} catch (Exception e) {
				LOG.error("Error unmarshalling 'content' property Value for content id: " + contentId, e);
			} 
		}
		return result;
	}

	@Override
	public Map<String, Entity> persist(List<? extends Entity> entities, MetadataModel model) {
//		LOG.info("persist called for Entity: " );
		Map<String, Entity> result = new HashMap<String, Entity>();
		for (Entity entity : entities) {
//			LOG.info("persist called for Entity: " + entity.getKey().getId() );
			JAXBElement<Object> element = entity.getObject();
			String contentId = null;
			try {
				Marshaller marshaller = model.getJAXBContext().createMarshaller();
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				marshaller.marshal(element, baos);
				ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
				Node entityNode = entityRoot.addNode(TYPE_NAME_ENTITY);
//				LOG.info("EntityNode created, name: " + entityNode.getName() );
				entityNode.setPrimaryType(TYPE_NAME_ENTITY);
				ValueFactory factory = session.getValueFactory();
				Binary binary = factory.createBinary(bais);
//				showEntityContent(binary);
				Value value = factory.createValue(binary);
				entityNode.setProperty(TYPE_NAME_CONTENT, value);				
				contentId =  entityNode.getIdentifier();
//				LOG.info("Saving content for id: " + contentId );
				session.save();
				result.put(contentId, entity);
			} catch (Exception e) {
				LOG.error("Error creating 'content' property Value", e);
				throw new IllegalStateException("Error creating 'content' property Value", e);
			}
		}
		return result;
	}
	
	/*
	 * For debugging
	 */
	private void showEntityContent(Binary binary) {
		if (showCount <= 0)
			return;
		showCount--;
		try {
			InputStream is = binary.getStream();
			BufferedReader r = new BufferedReader(new InputStreamReader(is));
			String line;
			while ((line = r.readLine()) != null) {
				LOG.info(line);
			}			
		} catch (RepositoryException e) {
			LOG.error("error displaying content",e);
		} catch (IOException e) {
			LOG.error("IO error displaying content",e);
		}
	}

	@Override
	public ContentRetriever getContentRetriever(String contentId, MetadataModel model) {
		return new InternalContentRetriever(contentId, model);
	}
	
	@Override
	public void shutdown() {
		session.logout();
		RepositoryImpl repoImpl = (RepositoryImpl) repo;
		repoImpl.shutdown();
	}

	private class InternalContentRetriever extends AbstractContentRetriever<Object> {

		public InternalContentRetriever(String contentId, MetadataModel model) {
			super(contentId, model);
		}

		@Override
		protected JAXBElement<Object> getContentInternal(String contentId, MetadataModel model) {
			return JcrContentStore.this.getContent(contentId, model);
		}
	}
	
	protected void initRepoStructure() {
		repositoryHome = System.getProperty(REPOSITORY_HOME_PROPERTY, "jcr");
		repositoryConfigFileName = System.getProperty(REPOSITORY_CONFIG_FILE_PROPERTY, "/WEB-INF/repository/repository.xml");
		
		File repHome = new File(repositoryHome);
		if (!repHome.exists()) {
			throw new IllegalStateException("Repository home directory does not exist: " + repHome.getAbsolutePath());
		}
		File repConfigFile = new File(repositoryConfigFileName);
		if (!repConfigFile.exists()) {
			throw new IllegalStateException("Repository.xml file does not exist: " + repConfigFile.getAbsolutePath());
		}
		try {
			InputStream in = new FileInputStream(repConfigFile);
			InputSource inSource = new InputSource(in);
	        RepositoryConfig config = RepositoryConfig.create(inSource, repHome.getAbsolutePath());
	        repo = RepositoryImpl.create(config);
		} catch (FileNotFoundException e) {
			throw new IllegalStateException("Error reading repository.xml: " + repConfigFile.getAbsolutePath(), e);
		} catch (RepositoryException e) {
			throw new IllegalStateException("Error creating Repository - " 
					+ " repository.xml=" + repConfigFile.getAbsolutePath()
					+ " repository home=" + repHome.getAbsolutePath(), e);
		}
		session = getSession();
		Node root = null;
		try {
			NodeTypeManager ntm = session.getWorkspace().getNodeTypeManager();
			NodeTypeTemplate entityNodeType = ntm.createNodeTypeTemplate();
			entityNodeType.setDeclaredSuperTypeNames(new String[]{"nt:unstructured"});
			entityNodeType.setOrderableChildNodes(true);
			entityNodeType.setName(TYPE_NAME_ENTITY);
			entityNodeType.setQueryable(true);
			ntm.registerNodeType(entityNodeType, true);
			root = session.getRootNode();			
		} catch(RepositoryException e) {
			throw new IllegalStateException("Error accessing JCR repository root", e);
		}		
		try {
			if (!root.hasNode(TYPE_NAME_ENTITIES)) {
				entityRoot = root.addNode(TYPE_NAME_ENTITIES);
				session.save();
			} else {
				entityRoot = root.getNode(TYPE_NAME_ENTITIES);
			}
		} catch (RepositoryException e) {
			throw new IllegalStateException("Error initializing entities node", e);
		}	
	}
	
	public Repository getRepository() {
		return repo;
	}

	public Session getSession() {
		Session session = null;
		try {
			session = repo.login(new SimpleCredentials("username", "password".toCharArray()));
		} catch (LoginException e) {
			LOG.error("Login to repository failed",e);
		} catch (RepositoryException e) {
			LOG.error("Repository error getting Session", e);
		}
		return session;
	}

	public String getRepositoryConfigFileName() {
		return repositoryConfigFileName;
	}

	public String getRepositoryHome() {
		return repositoryHome;
	}
}
