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
import org.scapdev.jaxb.reflection.model.DefaultTypeInfo;
import org.scapdev.jaxb.reflection.model.jaxb.DefaultModel;

public class JAXBMetadataModel implements MetadataModel {
	private final DefaultModel model;
	private final Map<String, SchemaInfo> schemaMap;
	private final Map<DefaultTypeInfo, EntityInfo> entityMap;
	private final Map<DefaultTypeInfo, DocumentInfo> documentMap;
	private final Map<DefaultTypeInfo, RelationshipInfo<Object>> relationshipMap;
	private final Map<String, EntityInfo> keyIdToEntityMap;
	private final Map<String, RelationshipInfo<Object>> keyRefIdToRelationshipMap;
	private final Map<String, EntityInfo> entityIdToEntityMap;
	
	public JAXBMetadataModel() throws IOException, JAXBException {
		// Initialize JAXB reflection model
		model = new DefaultModel(this.getClass().getClassLoader());

		// Identify objects of interest
		InitializingTypeInfoVisitor init = new InitializingTypeInfoVisitor(model);
		for (DefaultTypeInfo typeInfo : model.getTypeInfos()) {
			init.visit(typeInfo);
		}

		schemaMap = new HashMap<String, SchemaInfo>();
		entityMap = new HashMap<DefaultTypeInfo, EntityInfo>();
		documentMap = new HashMap<DefaultTypeInfo, DocumentInfo>();
		relationshipMap = new HashMap<DefaultTypeInfo, RelationshipInfo<Object>>();
		keyIdToEntityMap = new HashMap<String, EntityInfo>();
		entityIdToEntityMap = new HashMap<String, EntityInfo>();
		keyRefIdToRelationshipMap = new HashMap<String, RelationshipInfo<Object>>();

		// Load metadata and associate with JAXB info
		loadMetadata(init);
	}

	private void loadMetadata(InitializingTypeInfoVisitor init) throws IOException, JAXBException {
		Unmarshaller unmarshaller = model.getJAXBContext().createUnmarshaller();

		InputStream is = this.getClass().getResourceAsStream("/META-INF/metamodels/manifest");
		BufferedReader r = new BufferedReader(new InputStreamReader(is));
		String file;
		while ((file = r.readLine()) != null) {
			String resource = "/META-INF/metamodels/"+file;

			MetaModel model = (MetaModel) unmarshaller.unmarshal(this.getClass().getResourceAsStream(resource));
			processModel(model, init);
		}
	}

	private void processModel(MetaModel metaModel, InitializingTypeInfoVisitor init) {
		for (SchemaType schemaType : metaModel.getSchemas().getSchema()) {
			SchemaInfo schema = new SchemaInfoImpl(schemaType, this, init);
			schemaMap.put(schema.getId(), schema);
		}
	}

	void registerEntity(EntityInfoImpl entity) {
		entityMap.put(entity.getBinding().getTypeInfo(), entity);
		keyIdToEntityMap.put(entity.getKeyInfo().getId(), entity);
		entityIdToEntityMap.put(entity.getId(), entity);
	}

	void registerRelationship(DefaultTypeInfo typeInfo, RelationshipInfo<Object> relationship) {
		relationshipMap.put(typeInfo, relationship);
		KeyRefInfo keyRefInfo = relationship.getKeyRefInfo();
		keyRefIdToRelationshipMap.put(keyRefInfo.getId(), relationship);
	}

	public void registerDocument(AbstractDocumentBase document) {
		documentMap.put(document.getBinding().getTypeInfo(), document);
	}

	public JAXBContext getJAXBContext() {
		return model.getJAXBContext();
	}

	public DefaultModel getModel() {
		return model;
	}

	public EntityInfo getEntityByKeyId(String keyId) {
		return keyIdToEntityMap.get(keyId);
	}

	public RelationshipInfo<Object> getRelationshipByKeyRefId(String keyRefId) {
		return keyRefIdToRelationshipMap.get(keyRefId);
	}

	public EntityInfo getEntityById(String id) {
		return entityIdToEntityMap.get(id);
	}
}
