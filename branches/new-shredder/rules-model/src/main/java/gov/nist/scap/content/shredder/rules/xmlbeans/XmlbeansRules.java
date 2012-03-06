package gov.nist.scap.content.shredder.rules.xmlbeans;

import gov.nist.scap.content.shredder.rules.AbstractKeyedField;
import gov.nist.scap.content.shredder.rules.ContentMapping;
import gov.nist.scap.content.shredder.rules.DefaultBoundaryRelationshipDefinition;
import gov.nist.scap.content.shredder.rules.DefaultContentNodeDefinition;
import gov.nist.scap.content.shredder.rules.DefaultExternalIdentifier;
import gov.nist.scap.content.shredder.rules.DefaultGeneratedDocumentDefinition;
import gov.nist.scap.content.shredder.rules.DefaultIndexedDocumentDefinition;
import gov.nist.scap.content.shredder.rules.DefaultIndirectRelationshipDefinition;
import gov.nist.scap.content.shredder.rules.DefaultKeyDefinition;
import gov.nist.scap.content.shredder.rules.DefaultKeyRefDefinition;
import gov.nist.scap.content.shredder.rules.DefaultKeyedRelationshipDefinition;
import gov.nist.scap.content.shredder.rules.DefaultSchema;
import gov.nist.scap.content.shredder.rules.DelegatingKeyedField;
import gov.nist.scap.content.shredder.rules.IContentNodeDefinition;
import gov.nist.scap.content.shredder.rules.IDocumentDefinition;
import gov.nist.scap.content.shredder.rules.IEntityDefinition;
import gov.nist.scap.content.shredder.rules.IExternalIdentifier;
import gov.nist.scap.content.shredder.rules.IExternalIdentifierMapping;
import gov.nist.scap.content.shredder.rules.IGeneratedDocumentDefinition;
import gov.nist.scap.content.shredder.rules.IKeyedDocumentDefinition;
import gov.nist.scap.content.shredder.rules.IKeyDefinition;
import gov.nist.scap.content.shredder.rules.IKeyRefDefinition;
import gov.nist.scap.content.shredder.rules.IKeyedField;
import gov.nist.scap.content.shredder.rules.ISchema;
import gov.nist.scap.content.shredder.rules.QualifiedExternalIdentifierMapping;
import gov.nist.scap.content.shredder.rules.RuleDefinitions;
import gov.nist.scap.content.shredder.rules.StaticExternalIdentifierMapping;
import gov.nist.scap.content.shredder.rules.XPathKeyedField;
import gov.nist.scap.schema.contentRules.x01.BoundaryRelationshipType;
import gov.nist.scap.schema.contentRules.x01.ContentMappingType;
import gov.nist.scap.schema.contentRules.x01.ContentNodeType;
import gov.nist.scap.schema.contentRules.x01.EntityQNameMappingType;
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
import gov.nist.scap.schema.contentRules.x01.RulesDocument;
import gov.nist.scap.schema.contentRules.x01.RulesType;
import gov.nist.scap.schema.contentRules.x01.SchemaType;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlException;

public class XmlbeansRules {
	@SuppressWarnings("unused")
	private final RulesDocument data;
	private final RulesType rules;
	private final Map<String, IExternalIdentifier> externalIdentifiers = new HashMap<String, IExternalIdentifier>();
	private final Map<String, ISchema> schemas = new HashMap<String, ISchema>();
	private final Map<String, IKeyDefinition> keys = new HashMap<String, IKeyDefinition>();
	private final Map<String, IKeyRefDefinition> keyRefs = new HashMap<String, IKeyRefDefinition>();
	private final Map<String, IContentNodeDefinition> nodes = new HashMap<String, IContentNodeDefinition>();
	private final Map<String, IDocumentDefinition> documents = new HashMap<String, IDocumentDefinition>();
	private final Map<String, IEntityDefinition> entities = new HashMap<String, IEntityDefinition>();

	public XmlbeansRules(File file) throws XmlException, IOException {
		this(RulesDocument.Factory.parse(file));
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
			processKeyRefs(def, schema.getKeyRefList());
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

	private void processContentNodes(ISchema schemaDef, List<ContentNodeType> nodeDataList) {
		for (ContentNodeType node : nodeDataList) {
			IKeyDefinition keyDef = keys.get(node.getKey().getRefId());
			IContentNodeDefinition def = new DefaultContentNodeDefinition(schemaDef, node.getId(), keyDef);
			nodes.put(def.getId(), def);
			entities.put(def.getId(), def);
		}
	}

	private void processDocuments(ISchema schemaDef, SchemaType schema) {
		for (IndexedDocumentType document : schema.getIndexedDocumentList()) {
			IKeyDefinition keyDef = keys.get(document.getKey().getRefId());
			IKeyedDocumentDefinition def = new DefaultIndexedDocumentDefinition(schemaDef, document.getId(), document.getQname(), keyDef);
			documents.put(def.getId(), def);
			entities.put(def.getId(), def);
		}

		for (GeneratedDocumentType document : schema.getGeneratedDocumentList()) {
			IGeneratedDocumentDefinition def = new DefaultGeneratedDocumentDefinition(schemaDef, document.getId(), document.getQname());
			documents.put(def.getId(), def);
			entities.put(def.getId(), def);
		}
	}

	private void processKeyRefs(ISchema schemaDef, List<KeyRefDefinitionType> keyRefDataList) throws XmlException {
		for (KeyRefDefinitionType keyRef : keyRefDataList) {
			String keyRefId = keyRef.getId();
			LinkedHashMap<String, IKeyedField> fields = getFields(keyRef.newCursor());
			IKeyDefinition keyDefinition = keys.get(keyRef.getRefId());
			
			DefaultKeyRefDefinition keyRefDefinition = new DefaultKeyRefDefinition(schemaDef, keyRefId, new ArrayList<IKeyedField>(fields.values()), keyDefinition);
			keyRefs.put(keyRefDefinition.getId(), keyRefDefinition);
		}
	}

	private void processRelationships(ISchema schemaDef, SchemaType schema) throws XmlException {
		processBoundaryRelationships(schemaDef, schema);
		processKeyedRelationships(schemaDef, schema);
		processIndirectRelationships(schemaDef, schema);
	}

	private void processBoundaryRelationships(ISchema schemaDef, SchemaType schema) throws XmlException {
		for (BoundaryRelationshipType relationship : schema.getBoundaryRelationshipList()) {
			IEntityDefinition entity = entities.get(relationship.getEntityRef().getRefId());

			ContentMapping contentMapping = getContentMapping(relationship.getContentMapping());
			DefaultBoundaryRelationshipDefinition def = new DefaultBoundaryRelationshipDefinition(schemaDef, relationship.getId(), relationship.getXpath().getExpression(), contentMapping);
			entity.addRelationship(def);
		}
	}

	private void processKeyedRelationships(ISchema schemaDef, SchemaType schema) throws XmlException {
		for (KeyedRelationshipType relationship : schema.getKeyedRelationshipList()) {
			IEntityDefinition entity = entities.get(relationship.getEntityRef().getRefId());

			IKeyRefDefinition keyRefDefinition = keyRefs.get(relationship.getKeyRef().getRefId());

			String locationXpath = (relationship.isSetXpath() ? relationship.getXpath().getExpression() : null);
			DefaultKeyedRelationshipDefinition def = new DefaultKeyedRelationshipDefinition(schemaDef, relationship.getId(), locationXpath, keyRefDefinition);
			entity.addRelationship(def);
		}
	}

	private void processIndirectRelationships(ISchema schemaDef,
			SchemaType schema) throws XmlException {
		for (IndirectRelationshipType relationship : schema.getIndirectRelationshipList()) {
			IEntityDefinition entity = entities.get(relationship.getEntityRef().getRefId());

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

			DefaultIndirectRelationshipDefinition def = new DefaultIndirectRelationshipDefinition(schemaDef, relationship.getId(), locationXpath, relationship.getValue().getXpath().getExpression(), mapping);
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
			throw new RuntimeException();
		}

		do {
			FieldType fieldData = (FieldType)cursor.getObject();
			String name = fieldData.getName();

			AbstractKeyedField field;
			if (fieldData.isSetXpath()) {
				String xpath = fieldData.getXpath().getExpression();
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
}
