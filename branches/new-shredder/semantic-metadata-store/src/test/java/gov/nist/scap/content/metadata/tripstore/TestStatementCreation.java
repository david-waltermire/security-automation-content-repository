package gov.nist.scap.content.metadata.tripstore;

import gov.nist.scap.content.model.IEntity;
import gov.nist.scap.content.model.definitions.RuleDefinitions;
import gov.nist.scap.content.shredder.parser.ContentShredder;
import gov.nist.scap.content.shredder.parser.DataExtractingContentHandler;
import gov.nist.scap.content.shredder.rules.xmlbeans.XmlbeansRules;

import java.util.Collection;
import java.util.Map;

import org.junit.Test;
import org.scapdev.content.core.persistence.hybrid.ContentStore;
import org.scapdev.content.core.persistence.semantic.TripleStoreFacadeMetaDataManager;

public class TestStatementCreation {

//    @Test
//    public void testStatementCreation() throws Exception {
//
//        String testFile = "scap-data-stream-multi-signatures.xml";
//
//        XmlbeansRules xmlbeansRules =
//            new XmlbeansRules(this.getClass().getResourceAsStream(
//                "/test-rules.xml"));
//        RuleDefinitions rules = xmlbeansRules.getRuleDefinitions();
//        ContentShredder shredder = new ContentShredder(rules);
//        DataExtractingContentHandler handler =
//            new DataExtractingContentHandler();
//        shredder.shred(
//            this.getClass().getResourceAsStream("/" + testFile),
//            handler);
//        TripleStoreFacadeMetaDataManager tsfdm = TripleStoreFacadeMetaDataManager.getInstance();
//        tsfdm.loadModel(xmlbeansRules);
//        
//        Collection<? extends IEntity<?>> entities = handler.getEntities();
//        
//        ContentStore cs = new MockContentStore();
//        Map<String, IEntity<?>> entityMap = cs.persist(entities);
//
//        tsfdm.persist(entityMap);
//        
//
//    }
}
