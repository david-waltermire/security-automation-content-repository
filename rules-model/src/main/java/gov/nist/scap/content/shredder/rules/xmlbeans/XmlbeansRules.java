package gov.nist.scap.content.shredder.rules.xmlbeans;

import gov.nist.scap.content.model.definitions.AbstractKeyedField;
import gov.nist.scap.content.model.definitions.ContentMapping;
import gov.nist.scap.content.model.definitions.DefaultBoundaryIdentifierRelationshipDefinition;
import gov.nist.scap.content.model.definitions.DefaultCompositeRelationshipDefinition;
import gov.nist.scap.content.model.definitions.DefaultContentNodeDefinition;
import gov.nist.scap.content.model.definitions.DefaultExternalIdentifier;
import gov.nist.scap.content.model.definitions.DefaultGeneratedDocumentDefinition;
import gov.nist.scap.content.model.definitions.DefaultKeyDefinition;
import gov.nist.scap.content.model.definitions.DefaultKeyRefDefinition;
import gov.nist.scap.content.model.definitions.DefaultKeyedDocumentDefinition;
import gov.nist.scap.content.model.definitions.DefaultKeyedRelationshipDefinition;
import gov.nist.scap.content.model.definitions.DefaultSchema;
import gov.nist.scap.content.model.definitions.DefaultVersionDefinition;
import gov.nist.scap.content.model.definitions.DelegatingKeyedField;
import gov.nist.scap.content.model.definitions.IBoundaryIdentifierRelationshipDefinition;
import gov.nist.scap.content.model.definitions.ICompositeRelationshipDefinition;
import gov.nist.scap.content.model.definitions.IContentNodeDefinition;
import gov.nist.scap.content.model.definitions.IDocumentDefinition;
import gov.nist.scap.content.model.definitions.IEntityDefinition;
import gov.nist.scap.content.model.definitions.IExternalIdentifier;
import gov.nist.scap.content.model.definitions.IExternalIdentifierMapping;
import gov.nist.scap.content.model.definitions.IGeneratedDocumentDefinition;
import gov.nist.scap.content.model.definitions.IKeyDefinition;
import gov.nist.scap.content.model.definitions.IKeyedField;
import gov.nist.scap.content.model.definitions.IKeyedRelationshipDefinition;
import gov.nist.scap.content.model.definitions.IRelationshipDefinition;
import gov.nist.scap.content.model.definitions.ISchema;
import gov.nist.scap.content.model.definitions.IVersionDefinition;
import gov.nist.scap.content.model.definitions.QualifiedExternalIdentifierMapping;
import gov.nist.scap.content.model.definitions.RuleDefinitions;
import gov.nist.scap.content.model.definitions.StaticExternalIdentifierMapping;
import gov.nist.scap.content.model.definitions.XPathKeyedField;
import gov.nist.scap.content.model.definitions.collection.IMetadataModel;
import gov.nist.scap.schema.contentRules.x01.BoundaryRelationshipType;
import gov.nist.scap.schema.contentRules.x01.ContentMappingType;
import gov.nist.scap.schema.contentRules.x01.ContentNodeType;
import gov.nist.scap.schema.contentRules.x01.EntityQNameMappingType;
import gov.nist.scap.schema.contentRules.x01.EntityReferenceType;
import gov.nist.scap.schema.contentRules.x01.ExternalIdentifierRefMappingType;
import gov.nist.scap.schema.contentRules.x01.ExternalIdentifierType;
import gov.nist.scap.schema.contentRules.x01.FieldType;
import gov.nist.scap.schema.contentRules.x01.GeneratedDocumentType;
import gov.nist.scap.schema.contentRules.x01.IndexedDocumentType;
import gov.nist.scap.schema.contentRules.x01.IndirectRelationshipType;
import gov.nist.scap.schema.contentRules.x01.KeyFieldRefType;
import gov.nist.scap.schema.contentRules.x01.KeyRefDefinitionType;
import gov.nist.scap.schema.contentRules.x01.KeyType;
import gov.nist.scap.schema.contentRules.x01.KeyedRelationshipType;
import gov.nist.scap.schema.contentRules.x01.QualifierMappingType;
import gov.nist.scap.schema.contentRules.x01.RelationshipType;
import gov.nist.scap.schema.contentRules.x01.RulesDocument;
import gov.nist.scap.schema.contentRules.x01.RulesType;
import gov.nist.scap.schema.contentRules.x01.SchemaType;
import gov.nist.scap.schema.contentRules.x01.VersionMethodType;
import gov.nist.scap.schema.contentRules.x01.VersionType;

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
	private static final Map<VersionMethodType.Enum, IVersionDefinition.Method> versionMethodEnumToVersionMethodMap;

	static {
		Map<VersionMethodType.Enum, IVersionDefinition.Method> mapping = new HashMap<VersionMethodType.Enum, IVersionDefinition.Method>();
		mapping.put(VersionMethodType.DECIMAL, IVersionDefinition.Method.DECIMAL);
		mapping.put(VersionMethodType.SERIAL, IVersionDefinition.Method.SERIAL);
		mapping.put(VersionMethodType.TEXT, IVersionDefinition.Method.TEXT);
		versionMethodEnumToVersionMethodMap = Collections.unmodifiableMap(mapping);
	}

	private static IVersionDefinition.Method map(VersionMethodType.Enum key) {
		return versionMethodEnumToVersionMethodMap.get(key);
	}

	@SuppressWarnings("unused")
	private final RulesDocument data;
	private final RulesType rules;
	private final Map<String, IExternalIdentifier> externalIdentifiers = new HashMap<String, IExternalIdentifier>();
	private final Map<String, ISchema> schemas = new HashMap<String, ISchema>();
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
				DefaultExternalIdentifier identifier = new DefaultExternalIdentifier(node.getId(), Pattern.compile(node.getPattern().getExpression()));
				externalIdentifiers.put(identifier.getId(), identifier);
			}
		}
	}

	private void processSchema() throws XmlException {
		for (SchemaType schema : rules.getSchemaList()) {
			DefaultSchema def = new DefaultSchema(schema.getId(), URI.create(schema.getNamespace()));
			schemas.put(def.getId(), def);
			processKeys(def, schema.getKeyList());
			processContentNodes(def, schema.getContentNodeList());
			processDocuments(def, schema);
		}

		// Must process these after all keys have been processed
		for (SchemaType schema : rules.getSchemaList()) {
			ISchema def = schemas.get(schema.getId());
			processRelationships(def, schema);
		}
	}

	private void processKeys(ISchema schemaDef, List<KeyType> keyDataList) throws XmlException {
		for (KeyType key : keyDataList) {
			String keyId = key.getId();
			LinkedHashMap<String, IKeyedField> fields = getFields(key.newCursor());
			
			DefaultKeyDefinition keyDefinition = new DefaultKeyDefinition(schemaDef, keyId, new ArrayList<IKeyedField>(fields.values()));
			keys.put(keyDefinition.getId(), keyDefinition);
		}
	}

	private void processContentNodes(ISchema schemaDef, List<ContentNodeType> nodeDataList) throws XmlException {
		for (ContentNodeType node : nodeDataList) {
			IKeyDefinition keyDef = keys.get(node.getKey().getRefId());
			DefaultContentNodeDefinition def = new DefaultContentNodeDefinition(schemaDef, node.getId(), keyDef);

			if (node.isSetVersion()) {
				IVersionDefinition versionDef = getVersion(node.getVersion());
				def.setVersionDefinition(versionDef);
			}
			nodes.put(def.getId(), def);
			entities.put(def.getId(), def);
		}
	}

	private void processDocuments(ISchema schemaDef, SchemaType schema) throws XmlException {
		for (IndexedDocumentType document : schema.getIndexedDocumentList()) {
			IKeyDefinition keyDef = keys.get(document.getKey().getRefId());
			DefaultKeyedDocumentDefinition def = new DefaultKeyedDocumentDefinition(schemaDef, document.getId(), document.getQname(), keyDef);

			if (document.isSetVersion()) {
				IVersionDefinition versionDef = getVersion(document.getVersion());
				def.setVersionDefinition(versionDef);
			}

			documents.put(def.getId(), def);
			entities.put(def.getId(), def);
		}

		for (GeneratedDocumentType document : schema.getGeneratedDocumentList()) {
			IGeneratedDocumentDefinition def = new DefaultGeneratedDocumentDefinition(schemaDef, document.getId(), document.getQname());
			documents.put(def.getId(), def);
			entities.put(def.getId(), def);
		}
	}

	private void processRelationships(ISchema schemaDef, SchemaType schema) throws XmlException {
		processCompositeRelationships(schemaDef, schema);
		processKeyedRelationships(schemaDef, schema);
		processBoundaryIdentifierRelationships(schemaDef, schema);
	}

	private void processCompositeRelationships(ISchema schemaDef, SchemaType schema) throws XmlException {
		for (BoundaryRelationshipType relationship : schema.getBoundaryRelationshipList()) {

			ContentMapping contentMapping = getContentMapping(relationship.getContentMapping());
			DefaultCompositeRelationshipDefinition def = new DefaultCompositeRelationshipDefinition(schemaDef, relationship.getId(), relationship.getXpath().getExpression(), contentMapping);
			associateRelationshipWithEntities(def, relationship);
			compositeRelationships.put(def.getId(), def);
			relationships.put(def.getId(), def);
		}
	}

	private void processKeyedRelationships(ISchema schemaDef, SchemaType schema) throws XmlException {
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

	private void processBoundaryIdentifierRelationships(ISchema schemaDef,
			SchemaType schema) throws XmlException {
		for (IndirectRelationshipType relationship : schema.getIndirectRelationshipList()) {
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

	private static LinkedHashMap<String, IKeyedField> getFields(XmlCursor cursor) throws XmlException {

		LinkedHashMap<String, IKeyedField> retval = new LinkedHashMap<String, IKeyedField>();
		if (!cursor.toFirstChild()) {
			throw new RuntimeException("Unable to navigate cursor to first child");
		}

		do {
			FieldType fieldData = (FieldType)cursor.getObject();
			String name = fieldData.getName();

			AbstractKeyedField field;
			if (fieldData.isSetXpath()) {
				String xpath = fieldData.getXpath().getExpression();
//				Map<String, String> namespaces = new HashMap<String, String>();
//				fieldData.newCursor().getAllNamespaces(namespaces);
//				field = new XPathKeyedField(name, xpath, namespaces);
				field = new XPathKeyedField(name, xpath);
			} else if (fieldData.isSetKeyRef()) {
				KeyFieldRefType keyFieldRef = fieldData.getKeyRef();
				String delegateKeyId = keyFieldRef.getRefId();
				String delegateKeyFieldName = keyFieldRef.getField();

				field = new DelegatingKeyedField(name, delegateKeyId, delegateKeyFieldName);
			} else {
				throw new RuntimeException("unrecognized field definition");
			}
			if (fieldData.isSetPattern()) {
				field.setPattern(fieldData.getPattern().getExpression());
			}
			retval.put(field.getName(), field);
		} while (cursor.toNextSibling());
		return retval;
	}

	private static IVersionDefinition getVersion(VersionType version) throws XmlException {
		IVersionDefinition.Method method = map(version.getMethod());
		String xpath = version.getXpath().getExpression();
		return new DefaultVersionDefinition(method, xpath, version.getUseParentVersionWhenUndefined());
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

	public IExternalIdentifier getExternalIdentifierById(
			String id) {
		return externalIdentifiers.get(id);
	}

	public <T extends IEntityDefinition> T getEntityDefinitionById(String id) {
		IEntityDefinition result = entities.get(id);
		@SuppressWarnings("unchecked")
		T retval = (T)result;
		return retval;
	}
}
