package gov.nist.scap.content.metadata.tripstore;

import gov.nist.scap.content.exist.ExistDBContentStore;
import gov.nist.scap.content.model.ICompositeRelationship;
import gov.nist.scap.content.model.IEntity;
import gov.nist.scap.content.model.IKey;
import gov.nist.scap.content.model.IKeyedEntity;
import gov.nist.scap.content.model.IKeyedRelationship;
import gov.nist.scap.content.model.KeyBuilder;
import gov.nist.scap.content.model.definitions.IKeyedEntityDefinition;
import gov.nist.scap.content.model.definitions.RuleDefinitions;
import gov.nist.scap.content.shredder.parser.ContentShredder;
import gov.nist.scap.content.shredder.parser.DataExtractingContentHandler;
import gov.nist.scap.content.shredder.rules.xmlbeans.XmlbeansRules;

import java.util.Collection;
import java.util.Map;

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

        TripleStoreFacadeMetaDataManager tsfdm = TripleStoreFacadeMetaDataManager.getInstance(cs);
        tsfdm.loadModel(xmlbeansRules);
        tsfdm.persist(result);

        //Now retrieve
        IKey key = null;
//        if( xmlbeansRules.getEntityDefinitionById("http://scap.nist.gov/resource/content/source/1.2#document-datastream-collection") instanceof IKeyedEntityDefinition ) {
//            KeyBuilder kb = new KeyBuilder(((IKeyedEntityDefinition )xmlbeansRules.getEntityDefinitionById("http://scap.nist.gov/resource/content/source/1.2#document-datastream-collection")).getKeyDefinition().getFields());
//            kb.setId("http://scap.nist.gov/resource/content/source/1.2#key-datastream-collection");
//            kb.addField("datastream-collection-id", "scap_gov.nist_collection_Win7-54-1.2.0.0.zip");
//            key = kb.toKey();
//        }
        if( xmlbeansRules.getEntityDefinitionById("http://scap.nist.gov/resource/content/source/1.2#content-node-datastream") instanceof IKeyedEntityDefinition ) {
            KeyBuilder kb = new KeyBuilder(((IKeyedEntityDefinition )xmlbeansRules.getEntityDefinitionById("http://scap.nist.gov/resource/content/source/1.2#content-node-datastream")).getKeyDefinition().getFields());
            kb.setId("http://scap.nist.gov/resource/content/source/1.2#key-datastream");
            kb.addField("datastream-collection-id", "scap_gov.nist_collection_Win7-54-1.2.0.0.zip");
            kb.addField("datastream-id", "scap_gov.nist_datastream_Win7-54-1.2.0.0.zip");
            
            key = kb.toKey();
        }
        

        IKeyedEntity<?> entity = tsfdm.getEntity(key);

        for( Map.Entry<String, String> entry : entity.getKey().getFieldNameToValueMap().entrySet() ) {
            System.out.println(entry.getKey() + " : " + entry.getValue());
        }
        for( IKeyedRelationship rel : entity.getKeyedRelationships() ) {
            for( Map.Entry<String, String> entry : rel.getReferencedEntity().getKey().getFieldNameToValueMap().entrySet() ) {
                System.out.println(entry.getKey() + " : " + entry.getValue());
            }
        }
        //TODO need to populate parent
        for( ICompositeRelationship rel : entity.getCompositeRelationships() ) {
            System.out.println(rel.getReferencedEntity().getDefinition().getId());
        }
        if( entity.getParent() != null ) {
            if( entity.getParent().getKey("http://scap.nist.gov/resource/content/source/1.2#key-datastream-collection") != null ) {
                for( Map.Entry<String, String> entry : entity.getParent().getKey("http://scap.nist.gov/resource/content/source/1.2#key-datastream-collection").getFieldNameToValueMap().entrySet() ) {
                    System.out.println(entry.getKey() + " : " + entry.getValue());
                }
            }
        }
        
//        FileOutputStream fos = new FileOutputStream("testout.xml");
//        fos.write(entity.getContentHandle().getCursor().xmlText().getBytes("UTF-8"));
//        fos.flush();
//        fos.close();
//        
        

    }
}
