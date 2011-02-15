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
package org.scapdev.content.model.jaxb;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.scapdev.content.model.DocumentInfo;
import org.scapdev.content.model.EntityInfo;
import org.scapdev.content.model.KeyRefInfo;
import org.scapdev.content.model.MetadataModel;
import org.scapdev.content.model.RelationshipInfo;
import org.scapdev.content.model.SchemaInfo;
import org.scapdev.jaxb.reflection.JAXBContextFactory;
import org.scapdev.jaxb.reflection.model.JAXBClass;
import org.scapdev.jaxb.reflection.model.jaxb.JAXBModel;

public class JAXBMetadataModel implements MetadataModel {
	private final JAXBModel model;
	private final JAXBContext context;
	private final Map<String, SchemaInfo> schemaMap;
	private final Map<JAXBClass, EntityInfo> entityMap;
	private final Map<JAXBClass, DocumentInfo> documentMap;
	private final Map<JAXBClass, RelationshipInfo> relationshipMap;
	private final Map<String, EntityInfo> keyIdToEntityMap;
	private final Map<String, RelationshipInfo> keyRefIdToRelationshipMap;
	private final Map<String, EntityInfo> entityIdToEntityMap;
	
	public JAXBMetadataModel() throws IOException, JAXBException, ClassNotFoundException {
		// Initialize JAXB reflection model
		ClassLoader loader = this.getClass().getClassLoader();
		context = JAXBContextFactory.getJAXBContext(loader);
		model = JAXBModel.newInstanceFromPackageNames(JAXBContextFactory.getPackagesForContext(context), loader);

		// Identify objects of interest
		InitializingJAXBClassVisitor init = new InitializingJAXBClassVisitor(model);
		model.visit(init);

		schemaMap = new HashMap<String, SchemaInfo>();
		entityMap = new HashMap<JAXBClass, EntityInfo>();
		documentMap = new HashMap<JAXBClass, DocumentInfo>();
		relationshipMap = new HashMap<JAXBClass, RelationshipInfo>();
		keyIdToEntityMap = new HashMap<String, EntityInfo>();
		entityIdToEntityMap = new HashMap<String, EntityInfo>();
		keyRefIdToRelationshipMap = new HashMap<String, RelationshipInfo>();

		// Load metadata and associate with JAXB info
		loadMetadata(init);
	}

	private void loadMetadata(InitializingJAXBClassVisitor init) throws IOException, JAXBException {
		Unmarshaller unmarshaller = context.createUnmarshaller();

		InputStream is = this.getClass().getResourceAsStream("/META-INF/metamodels/manifest");
		BufferedReader r = new BufferedReader(new InputStreamReader(is));
		String file;
		while ((file = r.readLine()) != null) {
			String resource = "/META-INF/metamodels/"+file;

			MetaModel model = (MetaModel) unmarshaller.unmarshal(this.getClass().getResourceAsStream(resource));
			processModel(model, init);
		}
	}

	private void processModel(MetaModel metaModel, InitializingJAXBClassVisitor init) {
		for (SchemaType schemaType : metaModel.getSchemas().getSchema()) {
			SchemaInfo schema = new SchemaInfoImpl(schemaType, this, init);
			schemaMap.put(schema.getId(), schema);
		}
	}

	void registerEntity(EntityInfoImpl entity) {
		entityMap.put(entity.getBinding().getJaxbClass(), entity);
		keyIdToEntityMap.put(entity.getKeyInfo().getId(), entity);
		entityIdToEntityMap.put(entity.getId(), entity);
	}

	void registerRelationship(JAXBClass typeInfo, RelationshipInfo relationship) {
		relationshipMap.put(typeInfo, relationship);
		KeyRefInfo keyRefInfo = relationship.getKeyRefInfo();
		keyRefIdToRelationshipMap.put(keyRefInfo.getId(), relationship);
	}

	public void registerDocument(AbstractDocumentBase document) {
		documentMap.put(document.getBinding().getJaxbClass(), document);
	}

	public JAXBContext getJAXBContext() {
		return context;
	}

	public JAXBModel getModel() {
		return model;
	}

	public EntityInfo getEntityByKeyId(String keyId) {
		return keyIdToEntityMap.get(keyId);
	}

	public RelationshipInfo getRelationshipByKeyRefId(String keyRefId) {
		return keyRefIdToRelationshipMap.get(keyRefId);
	}

	public EntityInfo getEntityById(String id) {
		return entityIdToEntityMap.get(id);
	}
}
