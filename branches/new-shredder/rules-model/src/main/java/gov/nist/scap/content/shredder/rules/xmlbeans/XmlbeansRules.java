package gov.nist.scap.content.shredder.rules.xmlbeans;

import gov.nist.scap.content.model.definitions.AbstractEntityDefinition;
import gov.nist.scap.content.model.definitions.ContentMapping;
import gov.nist.scap.content.model.definitions.DefaultBoundaryIdentifierRelationshipDefinition;
import gov.nist.scap.content.model.definitions.DefaultCompositeRelationshipDefinition;
import gov.nist.scap.content.model.definitions.DefaultContentNodeDefinition;
import gov.nist.scap.content.model.definitions.DefaultExternalIdentifier;
import gov.nist.scap.content.model.definitions.DefaultGeneratedDocumentDefinition;
import gov.nist.scap.content.model.definitions.DefaultKeyDefinition;
import gov.nist.scap.content.model.definitions.DefaultKeyRefDefinition;
import gov.nist.scap.content.model.definitions.DefaultKeyedDocumentDefinition;
import gov.nist.scap.content.model.definitions.DefaultKeyedField;
import gov.nist.scap.content.model.definitions.DefaultKeyedRelationshipDefinition;
import gov.nist.scap.content.model.definitions.DefaultPropertyDefinition;
import gov.nist.scap.content.model.definitions.DefaultPropertyRef;
import gov.nist.scap.content.model.definitions.DefaultSchema;
import gov.nist.scap.content.model.definitions.DefaultVersionDefinition;
import gov.nist.scap.content.model.definitions.DefaultVersioningMethodDefinition;
import gov.nist.scap.content.model.definitions.DelegatingKeyFieldRetriever;
import gov.nist.scap.content.model.definitions.IBoundaryIdentifierRelationshipDefinition;
import gov.nist.scap.content.model.definitions.ICompositeRelationshipDefinition;
import gov.nist.scap.content.model.definitions.IContentNodeDefinition;
import gov.nist.scap.content.model.definitions.IDocumentDefinition;
import gov.nist.scap.content.model.definitions.IEntityDefinition;
import gov.nist.scap.content.model.definitions.IExternalIdentifier;
import gov.nist.scap.content.model.definitions.IExternalIdentifierMapping;
import gov.nist.scap.content.model.definitions.IKeyDefinition;
import gov.nist.scap.content.model.definitions.IKeyedField;
import gov.nist.scap.content.model.definitions.IKeyedRelationshipDefinition;
import gov.nist.scap.content.model.definitions.IPropertyDefinition;
import gov.nist.scap.content.model.definitions.IPropertyRef;
import gov.nist.scap.content.model.definitions.IRelationshipDefinition;
import gov.nist.scap.content.model.definitions.ISchemaDefinition;
import gov.nist.scap.content.model.definitions.IValueRetriever;
import gov.nist.scap.content.model.definitions.IVersionDefinition;
import gov.nist.scap.content.model.definitions.IVersioningMethodDefinition;
import gov.nist.scap.content.model.definitions.ParentPropertyRetriever;
import gov.nist.scap.content.model.definitions.QualifiedExternalIdentifierMapping;
import gov.nist.scap.content.model.definitions.RuleDefinitions;
import gov.nist.scap.content.model.definitions.StaticExternalIdentifierMapping;
import gov.nist.scap.content.model.definitions.XPathRetriever;
import gov.nist.scap.content.model.definitions.collection.IMetadataModel;
import gov.nist.scap.schema.contentRules.x01.BoundaryIdentifierRelationshipType;
import gov.nist.scap.schema.contentRules.x01.CompositeRelationshipType;
import gov.nist.scap.schema.contentRules.x01.ContentMappingType;
import gov.nist.scap.schema.contentRules.x01.ContentNodeType;
import gov.nist.scap.schema.contentRules.x01.EntityQNameMappingType;
import gov.nist.scap.schema.contentRules.x01.EntityReferenceType;
import gov.nist.scap.schema.contentRules.x01.ExternalIdentifierRefMappingType;
import gov.nist.scap.schema.contentRules.x01.ExternalIdentifierType;
import gov.nist.scap.schema.contentRules.x01.FieldType;
import gov.nist.scap.schema.contentRules.x01.GeneratedDocumentType;
import gov.nist.scap.schema.contentRules.x01.IndexedDocumentType;
import gov.nist.scap.schema.contentRules.x01.KeyFieldRefType;
import gov.nist.scap.schema.contentRules.x01.KeyRefDefinitionType;
import gov.nist.scap.schema.contentRules.x01.KeyType;
import gov.nist.scap.schema.contentRules.x01.KeyedRelationshipType;
import gov.nist.scap.schema.contentRules.x01.PropertyRefType;
import gov.nist.scap.schema.contentRules.x01.PropertyType;
import gov.nist.scap.schema.contentRules.x01.QualifierMappingType;
import gov.nist.scap.schema.contentRules.x01.RelationshipType;
import gov.nist.scap.schema.contentRules.x01.RulesDocument;
import gov.nist.scap.schema.contentRules.x01.RulesType;
import gov.nist.scap.schema.contentRules.x01.SchemaType;
import gov.nist.scap.schema.contentRules.x01.VersionType;
import gov.nist.scap.schema.contentRules.x01.VersioningMethodType;
import gov.nist.scap.schema.contentRules.x01.VersioningMethodsType;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlException;

public class XmlbeansRules implements IMetadataModel {

	@SuppressWarnings("unused")
	private final RulesDocument data;
	private final RulesType rules;
	private final Map<String, IExternalIdentifier> externalIdentifiers = new HashMap<String, IExternalIdentifier>();
	private final Map<String, IVersioningMethodDefinition> versioningMethods = new HashMap<String, IVersioningMethodDefinition>();
	private final Map<String, ISchemaDefinition> schemas = new HashMap<String, ISchemaDefinition>();
	private final Map<String, IKeyDefinition> keys = new HashMap<String, IKeyDefinition>();
	private final Map<String, IContentNodeDefinition> nodes = new HashMap<String, IContentNodeDefinition>();
	private final Map<String, IDocumentDefinition> documents = new HashMap<String, IDocumentDefinition>();
	private final Map<String, IEntityDefinition> entities = new HashMap<String, IEntityDefinition>();
	private final Map<String, IRelationshipDefinition> relationships = new HashMap<String, IRelationshipDefinition>();
	private final Map<String, IKeyedRelationshipDefinition> keyedRelationships = new HashMap<String, IKeyedRelationshipDefinition>();
	private final Map<String, IBoundaryIdentifierRelationshipDefinition> boundaryIdentifierRelationships = new HashMap<String, IBoundaryIdentifierRelationshipDefinition>();
	private final Map<String, ICompositeRelationshipDefinition> compositeRelationships = new HashMap<String, ICompositeRelationshipDefinition>();

	public XmlbeansRules(File file) throws XmlException, IOException {
		this(RulesDocument.Factory.parse(file));
	}

    public XmlbeansRules(InputStream is) throws XmlException, IOException {
        this(RulesDocument.Factory.parse(is));
    }
	
	public XmlbeansRules(RulesDocument data) throws XmlException {
		this.data = data;
		this.rules = data.getRules();

		processExternalIdentifiers();
		processSchema();
	}

	public RuleDefinitions getRuleDefinitions() {
		RuleDefinitions retval = new RuleDefinitions();
		for (IDocumentDefinition def : documents.values()) {
			retval.add(def);
		}
		return retval;
	}

	private void processExternalIdentifiers() {
		if (rules.isSetExternalIdentifiers()) {
			for (ExternalIdentifierType node : rules.getExternalIdentifiers().getExternalIdentifierList()) {
				DefaultExternalIdentifier identifier = new DefaultExternalIdentifier(node.getId(), node.getNamespace().getValue(), Pattern.compile(node.getPattern().getExpression()));
				externalIdentifiers.put(identifier.getId(), identifier);
			}
		}
		if (rules.isSetVersioningMethods()) {
			for (VersioningMethodType node : rules.getVersioningMethods().getVersioningMethodList()) {
				DefaultVersioningMethodDefinition method = new DefaultVersioningMethodDefinition(node.getId());
				versioningMethods.put(method.getId(), method);
			}
		}
	}

	private void processSchema() throws XmlException {
		for (SchemaType schema : rules.getSchemaList()) {
			DefaultSchema def = new DefaultSchema(schema.getId(), URI.create(schema.getNamespace()));
			schemas.put(def.getId(), def);
			if (schema.isSetVersioningMethods()) {
				processVersioningMethods(def, schema.getVersioningMethods());
			}
			processKeys(def, schema.getKeyList());
			processProperties(def, schema.getPropertyList());
			processContentNodes(def, schema.getContentNodeList());
			processDocuments(def, schema);
		}

		// Must process these after all keys have been processed
		for (SchemaType schema : rules.getSchemaList()) {
			ISchemaDefinition def = schemas.get(schema.getId());
			processRelationships(def, schema);
		}
	}

	private static void processVersioningMethods(DefaultSchema def,
			VersioningMethodsType versioningMethods) {
		for (VersioningMethodType node : versioningMethods.getVersioningMethodList()) {
			DefaultVersioningMethodDefinition method = new DefaultVersioningMethodDefinition(node.getId());
			def.addVersioningMethodDefinition(method);
		}
	}

	private void processKeys(ISchemaDefinition schemaDef, List<KeyType> keyDataList) {
		for (KeyType key : keyDataList) {
			String keyId = key.getId();
			LinkedHashMap<String, IKeyedField> fields = getFields(key.newCursor());
			
			DefaultKeyDefinition keyDefinition = new DefaultKeyDefinition(schemaDef, keyId, new ArrayList<IKeyedField>(fields.values()));
			keys.put(keyDefinition.getId(), keyDefinition);
		}
	}

	private static void processProperties(DefaultSchema def, List<PropertyType> properties) {
		for (PropertyType property : properties) {
			DefaultPropertyDefinition propDef = new DefaultPropertyDefinition(property.getId());
			def.addProperty(propDef);
		}
	}

	private void processContentNodes(ISchemaDefinition schemaDef, List<ContentNodeType> nodeDataList) throws XmlException {
		for (ContentNodeType node : nodeDataList) {
			IKeyDefinition keyDef = keys.get(node.getKey().getRefId());
			DefaultContentNodeDefinition def = new DefaultContentNodeDefinition(schemaDef, node.getId(), keyDef);

			if (node.isSetVersion()) {
				IVersionDefinition versionDef = getVersion(schemaDef, node.getVersion());
				def.setVersionDefinition(versionDef);
			}

			processPropertyRefs(def, node.getPropertyRefList(), schemaDef);

			nodes.put(def.getId(), def);
			entities.put(def.getId(), def);
		}
	}

	private static void processPropertyRefs(AbstractEntityDefinition def,
			List<PropertyRefType> propertyRefList, ISchemaDefinition schemaDef) {
		for (PropertyRefType node : propertyRefList) {
			IPropertyRef propertyRefDef = getPropertyRef(schemaDef, node);
			def.addPropertyRefDefinition(propertyRefDef);
		}
	}

	private void processDocuments(ISchemaDefinition schemaDef, SchemaType schema) throws XmlException {
		for (IndexedDocumentType document : schema.getIndexedDocumentList()) {
			IKeyDefinition keyDef = keys.get(document.getKey().getRefId());
			DefaultKeyedDocumentDefinition def = new DefaultKeyedDocumentDefinition(schemaDef, document.getId(), document.getQname(), keyDef);

			if (document.isSetVersion()) {
				IVersionDefinition versionDef = getVersion(schemaDef, document.getVersion());
				def.setVersionDefinition(versionDef);
			}

			processPropertyRefs(def, document.getPropertyRefList(), schemaDef);

			documents.put(def.getId(), def);
			entities.put(def.getId(), def);
		}

		for (GeneratedDocumentType document : schema.getGeneratedDocumentList()) {
			DefaultGeneratedDocumentDefinition def = new DefaultGeneratedDocumentDefinition(schemaDef, document.getId(), document.getQname());

			processPropertyRefs(def, document.getPropertyRefList(), schemaDef);

			documents.put(def.getId(), def);
			entities.put(def.getId(), def);
		}
	}

	private void processRelationships(ISchemaDefinition schemaDef, SchemaType schema) throws XmlException {
		processCompositeRelationships(schemaDef, schema);
		processKeyedRelationships(schemaDef, schema);
		processBoundaryIdentifierRelationships(schemaDef, schema);
	}

	private void processCompositeRelationships(ISchemaDefinition schemaDef, SchemaType schema) throws XmlException {
		for (CompositeRelationshipType relationship : schema.getCompositeRelationshipList()) {

			ContentMapping contentMapping = getContentMapping(relationship.getContentMapping());
			DefaultCompositeRelationshipDefinition def = new DefaultCompositeRelationshipDefinition(schemaDef, relationship.getId(), relationship.getXpath().getExpression(), contentMapping);
			associateRelationshipWithEntities(def, relationship);
			compositeRelationships.put(def.getId(), def);
			relationships.put(def.getId(), def);
		}
	}

	private void processKeyedRelationships(ISchemaDefinition schemaDef, SchemaType schema) throws XmlException {
		for (KeyedRelationshipType relationship : schema.getKeyedRelationshipList()) {
			KeyRefDefinitionType keyRef = relationship.getKeyRef();
			LinkedHashMap<String, IKeyedField> fields = getFields(keyRef.newCursor());
			IKeyDefinition keyDefinition = keys.get(keyRef.getRefId());
			
			DefaultKeyRefDefinition keyRefDefinition = new DefaultKeyRefDefinition(new ArrayList<IKeyedField>(fields.values()), keyDefinition);

			String locationXpath = (relationship.isSetXpath() ? relationship.getXpath().getExpression() : null);
			DefaultKeyedRelationshipDefinition def = new DefaultKeyedRelationshipDefinition(schemaDef, relationship.getId(), locationXpath, keyRefDefinition);
			associateRelationshipWithEntities(def, relationship);
			keyedRelationships.put(def.getId(), def);
			relationships.put(def.getId(), def);
		}
	}

	private void processBoundaryIdentifierRelationships(ISchemaDefinition schemaDef,
			SchemaType schema) throws XmlException {
		for (BoundaryIdentifierRelationshipType relationship : schema.getBoundaryIdentifierRelationshipList()) {
			String locationXpath = (relationship.isSetXpath() ? relationship.getXpath().getExpression() : null);
			IExternalIdentifierMapping mapping = null;
			if (relationship.isSetExternalIdentifier()) {
				mapping = new StaticExternalIdentifierMapping(externalIdentifiers.get(relationship.getExternalIdentifier().getRefId()));
			} else if (relationship.isSetQualifierMapping()) {
				QualifierMappingType data = relationship.getQualifierMapping();
				QualifiedExternalIdentifierMapping qualifiedMapping = new QualifiedExternalIdentifierMapping(data.getXpath().getExpression());
				for (ExternalIdentifierRefMappingType item : data.getExternalIdentifierList()) {
					qualifiedMapping.addQualifier(item.getQualifierValue(), externalIdentifiers.get(item.getRefId()));
				}
				mapping = qualifiedMapping;
			}

			DefaultBoundaryIdentifierRelationshipDefinition def = new DefaultBoundaryIdentifierRelationshipDefinition(schemaDef, relationship.getId(), locationXpath, relationship.getValue().getXpath().getExpression(), mapping);
			associateRelationshipWithEntities(def, relationship);
			boundaryIdentifierRelationships.put(def.getId(), def);
			relationships.put(def.getId(), def);
		}
	}

	private void associateRelationshipWithEntities(
			IRelationshipDefinition def,
			RelationshipType relationship) {

		for (EntityReferenceType entityRef : relationship.getEntityRefList()) {
			String entityId = entityRef.getRefId();
			IEntityDefinition entity = entities.get(entityId);
			if (entity == null) {
				throw new RuntimeException("Unable to find the entity with the id: "+entityId);
			}
			entity.addRelationship(def);
		}
	}

	private ContentMapping getContentMapping(ContentMappingType contentMapping) {
		ContentMapping retval = new ContentMapping();

		if (contentMapping.isSetDefault()) {
			retval.setDefaultContentDefinition(entities.get(contentMapping.getDefault().getRefId()));
		}

		for (EntityQNameMappingType qnameMapping : contentMapping.getQnameMappingList()) {
			retval.addContentDefinition(qnameMapping.getQname(), entities.get(qnameMapping.getRefId()));
		}
		return retval;
	}

	private static LinkedHashMap<String, IKeyedField> getFields(XmlCursor cursor) {

		LinkedHashMap<String, IKeyedField> retval = new LinkedHashMap<String, IKeyedField>();
		if (!cursor.toFirstChild()) {
			throw new RuntimeException("Unable to navigate cursor to first child");
		}

		do {
			FieldType fieldData = (FieldType)cursor.getObject();
			String name = fieldData.getName();

			IValueRetriever retriever;
			if (fieldData.isSetXpath()) {
				String xpath = fieldData.getXpath().getExpression();
//				Map<String, String> namespaces = new HashMap<String, String>();
//				fieldData.newCursor().getAllNamespaces(namespaces);
//				field = new XPathKeyedField(name, xpath, namespaces);
				retriever = new XPathRetriever(xpath);
			} else if (fieldData.isSetKeyRef()) {
				KeyFieldRefType keyFieldRef = fieldData.getKeyRef();
				String delegateKeyId = keyFieldRef.getRefId();
				String delegateKeyFieldName = keyFieldRef.getField();
				
				retriever = new DelegatingKeyFieldRetriever(delegateKeyId, delegateKeyFieldName);
			} else {
				throw new RuntimeException("unrecognized field definition");
			}
			DefaultKeyedField field = new DefaultKeyedField(name, retriever);

			if (fieldData.isSetPattern()) {
				field.setPattern(fieldData.getPattern().getExpression());
			}
			retval.put(field.getName(), field);
		} while (cursor.toNextSibling());
		return retval;
	}

	private IVersionDefinition getVersion(ISchemaDefinition schemaDef, VersionType version) throws XmlException {
		IVersioningMethodDefinition method = getVersioningMethodById(schemaDef, version.getVersioningMethod().getRefId());
		String xpath = version.getXpath().getExpression();
		return new DefaultVersionDefinition(method, xpath, version.getUseParentVersionWhenUndefined());
	}

	private static IPropertyRef getPropertyRef(
			ISchemaDefinition schemaDef,
			PropertyRefType node) {
		String propertyDefId = node.getRefId();
		IPropertyDefinition propertyDef = schemaDef.getPropertyDefinitionById(propertyDefId);

		IValueRetriever retriever;
		if (node.isSetXpath()) {
			String xpath = node.getXpath().getExpression();
			retriever = new XPathRetriever(xpath);
		} else if (node.isSetParentPropertyRef()) {
			String parentPropertyRef = node.getParentPropertyRef().getRefId();
			retriever = new ParentPropertyRetriever(parentPropertyRef);
		} else {
			throw new UnsupportedOperationException("Retriever construct is unsupported");
		}

		DefaultPropertyRef propertyRef = new DefaultPropertyRef(propertyDef, retriever);
		if (node.isSetPattern()) {
			propertyRef.setPattern(node.getPattern().getExpression());
		}
		return propertyRef;
	}

	public Collection<String> getCompositeRelationshipIds() {
		return Collections.unmodifiableSet(compositeRelationships.keySet());
	}

	public Collection<String> getBoundaryIndentifierRelationshipIds() {
		return Collections.unmodifiableSet(boundaryIdentifierRelationships.keySet());
	}

	public Collection<String> getKeyedRelationshipIds() {
		return Collections.unmodifiableSet(keyedRelationships.keySet());
	}

	public <T extends IRelationshipDefinition> T getRelationshipDefinitionById(
			String id) {
		IRelationshipDefinition result = relationships.get(id);
		@SuppressWarnings("unchecked")
		T retval = (T)result;
		return retval;
	}

	public <T extends IEntityDefinition> T getEntityDefinitionById(String id) {
		IEntityDefinition result = entities.get(id);
		@SuppressWarnings("unchecked")
		T retval = (T)result;
		return retval;
	}

	public IExternalIdentifier getExternalIdentifierById(
			String id) {
		return externalIdentifiers.get(id);
	}
	
	public <T extends IKeyDefinition> T getKeyById(String id) {
		IKeyDefinition result = keys.get(id);
		@SuppressWarnings("unchecked")
		T retval = (T)result;
		return retval;
	}


	private IVersioningMethodDefinition getVersioningMethodById(
			ISchemaDefinition schemaDef, String id) {
		IVersioningMethodDefinition retval = versioningMethods.get(id);
		if (retval == null) {
			retval = schemaDef.getVersioningMethodById(id);
		}
		return retval;
	}

	
	
}
