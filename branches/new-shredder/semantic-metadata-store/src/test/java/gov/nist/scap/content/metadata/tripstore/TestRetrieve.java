package gov.nist.scap.content.metadata.tripstore;

import gov.nist.scap.content.exist.ExistDBContentStore;
import gov.nist.scap.content.model.IEntity;
import gov.nist.scap.content.model.IKey;
import gov.nist.scap.content.model.IKeyedEntity;
import gov.nist.scap.content.model.KeyBuilder;
import gov.nist.scap.content.model.definitions.IEntityDefinition;
import gov.nist.scap.content.model.definitions.IExternalIdentifier;
import gov.nist.scap.content.model.definitions.IKeyedEntityDefinition;
import gov.nist.scap.content.model.definitions.RuleDefinitions;
import gov.nist.scap.content.shredder.parser.ContentShredder;
import gov.nist.scap.content.shredder.parser.DataExtractingContentHandler;
import gov.nist.scap.content.shredder.rules.xmlbeans.XmlbeansRules;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.scapdev.content.core.persistence.semantic.TripleStoreFacadeMetaDataManager;

public class TestRetrieve {

    @Test
    public void testGetEntity() throws Exception {

        XmlbeansRules xmlbeansRules =
            new XmlbeansRules(this.getClass().getResourceAsStream(
                "/test-rules.xml"));

        String testFile = "scap-data-stream-multi-signatures.xml";

        RuleDefinitions rules = xmlbeansRules.getRuleDefinitions();
        ContentShredder shredder = new ContentShredder(rules);
        DataExtractingContentHandler handler =
            new DataExtractingContentHandler();
        shredder.shred(
            this.getClass().getResourceAsStream("/" + testFile),
            handler);
        Collection<? extends IEntity<?>> entities = handler.getEntities();
        ExistDBContentStore cs = new ExistDBContentStore();
        Map<String, IEntity<?>> result = cs.persist(entities);

        TripleStoreFacadeMetaDataManager tsfdm =
            TripleStoreFacadeMetaDataManager.getInstance(cs);
        tsfdm.loadModel(xmlbeansRules);
        tsfdm.persist(result);

        // Now retrieve
        IKey key = null;
//        if (xmlbeansRules.getEntityDefinitionById("http://scap.nist.gov/resource/content/source/1.2#document-datastream-collection") instanceof IKeyedEntityDefinition) {
//            KeyBuilder kb =
//                new KeyBuilder(
//                    ((IKeyedEntityDefinition)xmlbeansRules.getEntityDefinitionById("http://scap.nist.gov/resource/content/source/1.2#document-datastream-collection")).getKeyDefinition().getFields());
//            kb.setId("http://scap.nist.gov/resource/content/source/1.2#key-datastream-collection");
//            kb.addField(
//                "datastream-collection-id",
//                "scap_gov.nist_collection_Win7-54-1.2.0.0.zip");
//            key = kb.toKey();
//        }
//        if (xmlbeansRules.getEntityDefinitionById("http://scap.nist.gov/resource/content/source/1.2#content-node-datastream") instanceof IKeyedEntityDefinition) {
//            KeyBuilder kb =
//                new KeyBuilder(
//                    ((IKeyedEntityDefinition)xmlbeansRules.getEntityDefinitionById("http://scap.nist.gov/resource/content/source/1.2#content-node-datastream")).getKeyDefinition().getFields());
//            kb.setId("http://scap.nist.gov/resource/content/source/1.2#key-datastream");
//            kb.addField(
//                "datastream-collection-id",
//                "scap_gov.nist_collection_Win7-54-1.2.0.0.zip");
//            kb.addField(
//                "datastream-id",
//                "scap_gov.nist_datastream_Win7-54-1.2.0.0.zip");
//
//            key = kb.toKey();
//        }
        if (xmlbeansRules.getEntityDefinitionById("http://scap.nist.gov/resource/content/xccdf/1.2#content-node-profile") instanceof IKeyedEntityDefinition) {
            KeyBuilder kb =
                new KeyBuilder(
                    ((IKeyedEntityDefinition)xmlbeansRules.getEntityDefinitionById("http://scap.nist.gov/resource/content/xccdf/1.2#content-node-profile")).getKeyDefinition().getFields());
            kb.setId("http://scap.nist.gov/resource/content/xccdf/1.2#key-profile");
            kb.addField(
                "profile-id",
                "xccdf_gov.nist_profile_united_states_government_configuration_baseline_version_1.2.0.0");

            key = kb.toKey();
        }

        IKeyedEntity<?> entity = tsfdm.getEntity(key);
        System.out.println(entity.getVersion().getValue());
        
        
        //
        // for( Map.Entry<String, String> entry :
        // entity.getKey().getFieldNameToValueMap().entrySet() ) {
        // System.out.println(entry.getKey() + " : " + entry.getValue());
        // }
        // for( IKeyedRelationship rel : entity.getKeyedRelationships() ) {
        // for( Map.Entry<String, String> entry :
        // rel.getReferencedEntity().getKey().getFieldNameToValueMap().entrySet()
        // ) {
        // System.out.println(entry.getKey() + " : " + entry.getValue());
        // }
        // }
        // //TODO need to populate parent
        // for( ICompositeRelationship rel : entity.getCompositeRelationships()
        // ) {
        // System.out.println(rel.getReferencedEntity().getDefinition().getId());
        // }
        // if( entity.getParent() != null ) {
        // if(
        // entity.getParent().getKey("http://scap.nist.gov/resource/content/source/1.2#key-datastream-collection")
        // != null ) {
        // for( Map.Entry<String, String> entry :
        // entity.getParent().getKey("http://scap.nist.gov/resource/content/source/1.2#key-datastream-collection").getFieldNameToValueMap().entrySet()
        // ) {
        // System.out.println(entry.getKey() + " : " + entry.getValue());
        // }
        // }
        // }

        // TODO revisit why I can't iterate through all without crashing
//        Assert.assertNotSame(0, result.size());
//        for (Map.Entry<String, IEntity<?>> entry : result.entrySet()) {
//            IEntity<?> entity = tsfdm.getEntity(entry.getKey());
//            Assert.assertTrue(entry.getValue().getDefinition().getId().equals(
//                entity.getDefinition().getId()));
//            System.out.println(entry.getValue().getDefinition().getId());
//            break;
//        }

        IExternalIdentifier ied =
            xmlbeansRules.getExternalIdentifierById("http://cve.mitre.org/resource/content/cve#external-identifier-cve");
        Collection<String> externalIds = new HashSet<String>();
        externalIds.add("CVE-2009-2510");
        externalIds.add("CVE-2009-2524");
        Set<IEntityDefinition> defs = new HashSet<IEntityDefinition>();
        defs.add(xmlbeansRules.getEntityDefinitionById("http://oval.mitre.org/resource/content/definition/5#content-node-definition"));
        Map<String, Set<? extends IKey>> keys =
            tsfdm.getKeysForBoundaryIdentifier(ied, externalIds, defs);
        for (Map.Entry<String, Set<? extends IKey>> entry : keys.entrySet()) {
            System.out.println(entry.getKey());
            for (IKey keyVal : entry.getValue()) {
                for (Map.Entry<String, String> keyEntry : keyVal.getFieldNameToValueMap().entrySet()) {
                    System.out.println(keyEntry.getKey() + " : "
                        + keyEntry.getValue());
                }
            }
        }
    }
};
