package gov.nist.scap.content.shredder.rules.xmlbeans;

import gov.nist.scap.content.model.ICompositeRelationship;
import gov.nist.scap.content.model.IContentNode;
import gov.nist.scap.content.model.IEntity;
import gov.nist.scap.content.model.IEntityVisitor;
import gov.nist.scap.content.model.IGeneratedDocument;
import gov.nist.scap.content.model.IBoundaryIdentifierRelationship;
import gov.nist.scap.content.model.IKeyedDocument;
import gov.nist.scap.content.model.IKeyedRelationship;
import gov.nist.scap.content.model.IRelationship;
import gov.nist.scap.content.model.IRelationshipVisitor;
import gov.nist.scap.content.model.IVersion;
import gov.nist.scap.content.model.definitions.ProcessingException;
import gov.nist.scap.content.model.definitions.RuleDefinitions;
import gov.nist.scap.content.shredder.parser.ContentShredder;
import gov.nist.scap.content.shredder.parser.DataExtractingContentHandler;

import java.io.IOException;
import java.util.Collection;

import javax.xml.namespace.QName;

import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlException;
import org.junit.Test;

public class XmlbeansRulesTest {

	@Test
	public void testXmlbeansRulesFile() throws XmlException, IOException, ProcessingException {
        XmlbeansRules xmlbeansRules =
                new XmlbeansRules(this.getClass().getResourceAsStream(
                    "/test-rules.xml"));
		RuleDefinitions rules = xmlbeansRules.getRuleDefinitions();
		ContentShredder shredder = new ContentShredder(rules);
		DataExtractingContentHandler handler = new DataExtractingContentHandler();
//		shredder.shred(new File("C:\\code\\scap\\USGCB-1.2.3.1\\scap_gov.nist_USGCB-Windows-XP-firewall.xml"));
		shredder.shred(getClass().getResourceAsStream("/scap_gov.nist_USGCB-Windows-7.xml"), handler);
		Collection<? extends IEntity<?>> entities = handler.getEntities();

		EntityVisitor entityVisitor = new EntityVisitor();
		RelationshipVisitor relationshipVisitor = new RelationshipVisitor();
		for (IEntity<?> entity : entities) {
			entity.accept(entityVisitor);
			for (IRelationship<?> relationship : entity.getRelationships()) {
				relationship.accept(relationshipVisitor);
			}
		}
	}

	private static class EntityVisitor implements IEntityVisitor {

		public void visit(IKeyedDocument entity) {
			XmlCursor cursor = entity.getContentHandle().getCursor();
			QName qname = cursor.getName();

			IVersion version = entity.getVersion();
			String ver = (version != null ? version.getValue() : null);
			System.out.println(qname.toString()+": "+entity.getDefinition().getId()+": "+entity.getKey().toString()+": "+ver);
		}

		public void visit(IGeneratedDocument entity) {
			XmlCursor cursor = entity.getContentHandle().getCursor();
			QName qname = cursor.getName();
			System.out.println(qname.toString()+": "+entity.getDefinition().getId());
		}

		public void visit(IContentNode entity) {
			XmlCursor cursor = entity.getContentHandle().getCursor();
			QName qname = cursor.getName();

			IVersion version = entity.getVersion();
			String ver = (version != null ? version.getValue() : null);

			System.out.println(qname.toString()+": "+entity.getDefinition().getId()+": "+entity.getKey().toString()+": "+ver);
		}
		
	}

	private static class RelationshipVisitor implements IRelationshipVisitor {

		public void visit(ICompositeRelationship relationship) {
//			System.out.println("  Composite: "+relationship.getDefinition().getId());
		}

		public void visit(IBoundaryIdentifierRelationship relationship) {
			System.out.println("  Boundary: "+relationship.getDefinition().getId());
		}

		public void visit(IKeyedRelationship relationship) {
			System.out.println("  Keyed: "+relationship.getDefinition().getId());
		}
	}
}
