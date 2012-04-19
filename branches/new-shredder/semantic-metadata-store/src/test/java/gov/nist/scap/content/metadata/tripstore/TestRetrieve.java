package gov.nist.scap.content.metadata.tripstore;

import gov.nist.scap.content.model.DefaultVersion;
import gov.nist.scap.content.model.IEntity;
import gov.nist.scap.content.model.IKey;
import gov.nist.scap.content.model.IKeyedEntity;
import gov.nist.scap.content.model.IVersion;
import gov.nist.scap.content.model.KeyBuilder;
import gov.nist.scap.content.model.definitions.IEntityDefinition;
import gov.nist.scap.content.model.definitions.IExternalIdentifier;
import gov.nist.scap.content.model.definitions.IKeyedEntityDefinition;
import gov.nist.scap.content.model.definitions.RuleDefinitions;
import gov.nist.scap.content.semantic.TripleStoreFacadeMetaDataManager;
import gov.nist.scap.content.shredder.parser.ContentShredder;
import gov.nist.scap.content.shredder.parser.DataExtractingContentHandler;
import gov.nist.scap.content.shredder.rules.xmlbeans.XmlbeansRules;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;
import org.scapdev.content.core.persistence.hybrid.ContentStore;

/**
 * Smoke test the triple store code
 * @author Adam Halbardier
 *
 */
public class TestRetrieve {

    private static XmlbeansRules xmlbeansRules;
    private static TripleStoreFacadeMetaDataManager tsfdm;
    private static LinkedHashMap<String, IEntity<?>> resultMap;

    /**
     * Set up the tests by persisting an SCAP data stream
     * @throws Exception general error
     */
    @BeforeClass
    public static void setupSemanticStore() throws Exception {
//        System.setProperty(TripleStoreFacadeMetaDataManager.TRIPLE_STORE_DIR, "target/triple-store-folder");
        System.setProperty(TripleStoreFacadeMetaDataManager.RULES_FILE, "/test-rules.xml");
        //TODO Need to rework this because the rules may be in the triple store already 
        xmlbeansRules =
            new XmlbeansRules(TestRetrieve.class.getResourceAsStream(
                "/test-rules.xml"));

        String testFile = "scap-data-stream-multi-signatures.xml";

        RuleDefinitions rules = xmlbeansRules.getRuleDefinitions();
        ContentShredder shredder = new ContentShredder(rules);
        DataExtractingContentHandler handler =
            new DataExtractingContentHandler();
        shredder.shred(
            TestRetrieve.class.getResourceAsStream("/" + testFile),
            handler);
        List<? extends IEntity<?>> entities = handler.getEntities();
        ContentStore cs = new MockContentStore();
        resultMap = cs.persist(entities);

        tsfdm = TripleStoreFacadeMetaDataManager.getInstance(cs);
        tsfdm.persist(resultMap);
        
        //TODO delete this line
//        OutputStream os = new BufferedOutputStream(new FileOutputStream("statements-repo.log"));
//        tsfdm.writeOutAllStatements(os);
//        os.flush();
//        os.close();
    }

    /**
     * Test that information is retrievable by key
     * @throws Exception general exception
     */
    @Test
    public void testGetByKey() throws Exception {

        IKey dsKey = null;
        if (xmlbeansRules.getEntityDefinitionById("http://scap.nist.gov/resource/content/source/1.2#content-node-datastream") instanceof IKeyedEntityDefinition) {
            KeyBuilder kb =
                new KeyBuilder(
                    ((IKeyedEntityDefinition)xmlbeansRules.getEntityDefinitionById("http://scap.nist.gov/resource/content/source/1.2#content-node-datastream")).getKeyDefinition().getFields());
            kb.setId("http://scap.nist.gov/resource/content/source/1.2#key-datastream");
            kb.addField(
                "datastream-collection-id",
                "scap_gov.nist_collection_Win7-54-1.2.0.0.zip");
            kb.addField(
                "datastream-id",
                "scap_gov.nist_datastream_Win7-54-1.2.0.0.zip");
            
            dsKey = kb.toKey();
        }

        IKey xccdfProfileKey = null;
        IVersion xccdfProfileVersion = null;
        if (xmlbeansRules.getEntityDefinitionById("http://scap.nist.gov/resource/content/xccdf/1.2#content-node-profile") instanceof IKeyedEntityDefinition) {
            IKeyedEntityDefinition iked = (IKeyedEntityDefinition)xmlbeansRules.getEntityDefinitionById("http://scap.nist.gov/resource/content/xccdf/1.2#content-node-profile");
            KeyBuilder kb =
                new KeyBuilder(
                    iked.getKeyDefinition().getFields());
            kb.setId("http://scap.nist.gov/resource/content/xccdf/1.2#key-profile");
            kb.addField(
                "profile-id",
                "xccdf_gov.nist_profile_united_states_government_configuration_baseline_version_1.2.0.0");

            xccdfProfileKey = kb.toKey();
            xccdfProfileVersion = new DefaultVersion(iked.getVersionDefinition(), "v1.2.0.0");
        }

        IKeyedEntity<?> dsEntity = tsfdm.getEntities(dsKey, null).iterator().next();
        IKeyedEntity<?> xccdfPEntity = tsfdm.getEntities(xccdfProfileKey, xccdfProfileVersion).iterator().next();

        Assert.assertEquals("v1.2.0.0", xccdfPEntity.getVersion().getValue());
        Assert.assertTrue(dsEntity.getKey().getFieldNameToValueMap().containsKey("datastream-collection-id"));
        Assert.assertTrue(dsEntity.getKey().getFieldNameToValueMap().containsKey("datastream-id"));
        Assert.assertTrue(dsEntity.getKey().getFieldNameToValueMap().containsValue("scap_gov.nist_collection_Win7-54-1.2.0.0.zip"));
        Assert.assertTrue(dsEntity.getKey().getFieldNameToValueMap().containsValue("scap_gov.nist_datastream_Win7-54-1.2.0.0.zip"));
        
        Assert.assertNotNull(dsEntity.getParent());
    }
    
    /**
     * Test that information is retrievable by boundary id
     * @throws Exception general exception
     */
    @Test
    public void testGetCVE() throws Exception {
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
                for (IKey keyVal : entry.getValue()) {
                    keyVal.getFieldNameToValueMap().containsKey("definition-id");
                    keyVal.getFieldNameToValueMap().containsValue("oval:gov.nist.USGCB.patch:def:11593");
                    keyVal.getFieldNameToValueMap().containsValue("oval:gov.nist.USGCB.patch:def:11590");
                }
            }
    }

    /**
     * Test that every single proxy object loads without error
     * @throws Exception general exception
     */
    @Test
    public void loadAllEntities() throws Exception {
        Assert.assertNotSame(0, resultMap.size());
        for (Map.Entry<String, IEntity<?>> entry : resultMap.entrySet()) {
            IEntity<?> entity = tsfdm.getEntity(entry.getKey());
            Assert.assertTrue(entry.getValue().getDefinition().getId().equals(
                entity.getDefinition().getId()));
        }
    }
};
