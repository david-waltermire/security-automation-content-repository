package gov.nist.scap.content.shredder.rules.xmlbeans;

import gov.nist.scap.content.model.ContentException;
import gov.nist.scap.content.model.definitions.ProcessingException;
import gov.nist.scap.content.model.definitions.RuleDefinitions;
import gov.nist.scap.content.shredder.model.IEntity;
import gov.nist.scap.content.shredder.parser.ContentShredder;
import gov.nist.scap.content.shredder.parser.DataExtractingContentHandler;
import gov.nist.scap.signature.Signature;
import gov.nist.scap.signature.XMLValidator;
import gov.nist.scap.signature.config.ValidateSigConfig;
import gov.nist.scap.signature.enums.CanonicalizationType;
import gov.nist.scap.signature.enums.HashType;
import gov.nist.scap.signature.enums.SignatureType;
import gov.nist.scap.signature.model.IReferenceValidationResult;
import gov.nist.scap.signature.model.ISignatureValidationResult;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.transform.OutputKeys;

import junit.framework.Assert;

import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlCursor.TokenType;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlOptions;
import org.exist.xmldb.EXistResource;
import org.junit.Test;
import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Database;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.CollectionManagementService;
import org.xmldb.api.modules.XMLResource;

public class ExistTest {

	private static final String URI = "xmldb:exist://localhost:8080/exist/xmlrpc/db/";
	private static Collection col = null;

	@Test
	public void testXmlbeansRulesFile() throws XmlException, IOException,
			ContentException, ProcessingException, Exception {

		String resId = null;
		final String driver = "org.exist.xmldb.DatabaseImpl";
		// initialize database driver
		Class<?> cl = Class.forName(driver);
		Database database = (Database) cl.newInstance();
		database.setProperty("create-database", "true");
		DatabaseManager.registerDatabase(database);
		col = getOrCreateCollection("dsig");
		col.setProperty(OutputKeys.INDENT, "no");

		XmlbeansRules xmlbeansRules = new XmlbeansRules(new File("test.xml"));

		RuleDefinitions rules = xmlbeansRules.getRuleDefinitions();
		ContentShredder shredder = new ContentShredder(rules);
		DataExtractingContentHandler handler = new DataExtractingContentHandler();
		// shredder.shred(new
		// File("C:\\code\\scap\\USGCB-1.2.3.1\\scap_gov.nist_USGCB-Windows-XP-firewall.xml"));
		//Sign it
        File fSign = new File("scap_gov.nist_USGCB-Windows-7.xml");
		File fSigned = new File("scap_gov.nist_USGCB-Windows-7_signed.xml");
        Signature.Builder builder = new Signature.Builder();
        builder.newEnvelopedReference().uri("#xpointer(/)").canonicalization(
            CanonicalizationType.INCLUSIVE_1_0);
        builder.hashType(HashType.SHA256).sigType(SignatureType.RSA_SHA256).outputStream(
            new FileOutputStream(fSigned)).sourceForOutput(
            new FileInputStream(fSign)).insertXpath("/*[1]").keyInfoBuilder(
            TestUtil.createKeyInfo());
        builder.build().signContents();
        //End Signing
		shredder.shred(fSigned, handler);
		List<? extends IEntity<?>> entities = handler.getEntities();
		XMLResource res = null;
		Map<String,Map<String,String>> namespaces = new HashMap<String,Map<String,String>>();
		for (IEntity<?> ie : entities) {
			try {
				res = (XMLResource) col.createResource(null, "XMLResource");
				XmlOptions xo = new XmlOptions();
				xo.setSaveOuter();
				XmlCursor xc = ie.getCursor();
				TokenType tt = xc.toNextToken();
				Map<String,String> localNamespaces = new HashMap<String,String>();
				while( tt == TokenType.ATTR || tt == TokenType.NAMESPACE ) {
					if( tt == TokenType.NAMESPACE ) {
						QName q = xc.getName();
						localNamespaces.put(q.getLocalPart(), q.getNamespaceURI());
					}
					tt = xc.toNextToken();
				}
				xc.toBookmark(ie.getBookmark());
				res.setContent(xc.getObject().xmlText(xo));
				col.storeResource(res);
				resId = res.getId();
				namespaces.put(resId, localNamespaces);
				xc.removeXml();
				xc.beginElement("xinclude", "gov:nist:scap:content-repo");
				xc.insertAttributeWithValue("resource-id",resId);
				System.out.println(resId + " entity("+ie.getRelationships().size()+"): "+ie.getCursor().getName());

			} finally {
				// dont forget to cleanup
				if (res != null) {
					try {
						((EXistResource) res).freeResources();
					} catch (XMLDBException xe) {
						xe.printStackTrace();
					}
				}
			}
		}
		
		File outputFile = new File("test-out.xml");
		OutputStream os = new BufferedOutputStream(new FileOutputStream("test-out.xml"));
		os.write(("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n").getBytes());
        res = (XMLResource)col.getResource(resId);
        res.getContentAsSAX(new TestContentHandler(col, namespaces, res.getId(), os));
        os.flush();
        os.close();

        // Validate signature
        ValidateSigConfig vsc =
            new ValidateSigConfig.Builder().content(new FileInputStream(outputFile)).build();
        List<ISignatureValidationResult> list =
            XMLValidator.validateContent(vsc);
        for (ISignatureValidationResult i : list) {
            Assert.assertTrue(i.isSignatureValid());
            for (IReferenceValidationResult irvr : i.getSignatureReferenceResults()) {
                Assert.assertTrue(outputFile.getName(), irvr.isReferenceDigestValid());
            }
            if (i.getSignatureReferenceResults().size() == 0) {
                Assert.fail("No references found");
            }
        }
        if (list.size() == 0) {
            Assert.fail("No signatures found");
        }


	}

	private static Collection getOrCreateCollection(String collectionUri)
			throws XMLDBException {
		return getOrCreateCollection(collectionUri, 0);
	}

	private static Collection getOrCreateCollection(String collectionUri,
			int pathSegmentOffset) throws XMLDBException {

		Collection col = DatabaseManager.getCollection(URI + collectionUri,
				"admin", "");
		if (col == null) {
			if (collectionUri.startsWith("/")) {
				collectionUri = collectionUri.substring(1);
			}

			String pathSegments[] = collectionUri.split("/");
			if (pathSegments.length > 0) {

				StringBuilder path = new StringBuilder();
				for (int i = 0; i <= pathSegmentOffset; i++) {
					path.append("/" + pathSegments[i]);
				}

				Collection start = DatabaseManager.getCollection(URI + path);
				if (start == null) {
					// collection does not exist, so create
					String parentPath = path
							.substring(0, path.lastIndexOf("/"));
					Collection parent = DatabaseManager.getCollection(URI
							+ parentPath);
					CollectionManagementService mgt = (CollectionManagementService) parent
							.getService("CollectionManagementService", "1.0");
					col = mgt.createCollection(pathSegments[pathSegmentOffset]);
					col.close();
					parent.close();
				} else {
					start.close();
				}
			}
			return getOrCreateCollection(collectionUri, ++pathSegmentOffset);
		} else {
			return col;
		}
	}

}
