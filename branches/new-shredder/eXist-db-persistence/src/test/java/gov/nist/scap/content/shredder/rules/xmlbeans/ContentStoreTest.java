package gov.nist.scap.content.shredder.rules.xmlbeans;

import gov.nist.scap.content.exist.ExistDBContentStore;
import gov.nist.scap.content.model.IEntity;
import gov.nist.scap.content.model.definitions.RuleDefinitions;
import gov.nist.scap.content.shredder.parser.ContentShredder;
import gov.nist.scap.content.shredder.parser.DataExtractingContentHandler;
import gov.nist.scap.signature.XMLValidator;
import gov.nist.scap.signature.config.ValidateSigConfig;
import gov.nist.scap.signature.model.IReferenceValidationResult;
import gov.nist.scap.signature.model.ISignatureValidationResult;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.apache.xmlbeans.XmlCursor;
import org.junit.Test;
import org.scapdev.content.core.persistence.hybrid.ContentStore;

/**
 * Smoke test that the content store works
 * @author Adam Halbardier
 *
 */
public class ContentStoreTest {

    private static final String ALIAS = "test";
    private static final String PASSWORD = "123456";

    /**
     * smoke test that exist-db content store works
     * @throws Exception general error
     */
    @Test
    public void testContentStore() throws Exception {

        String testFile = "scap-data-stream-multi-signatures.xml";

        validateSignatures(this.getClass().getResourceAsStream("/" + testFile));

        XmlbeansRules xmlbeansRules =
            new XmlbeansRules(this.getClass().getResourceAsStream(
                "/test-rules.xml"));

        RuleDefinitions rules = xmlbeansRules.getRuleDefinitions();
        ContentShredder shredder = new ContentShredder(rules);
        DataExtractingContentHandler handler =
            new DataExtractingContentHandler();
        shredder.shred(
            this.getClass().getResourceAsStream("/" + testFile),
            handler);
        Collection<? extends IEntity<?>> entities = handler.getEntities();

        ContentStore cs = new ExistDBContentStore();
        Map<String, IEntity<?>> result = cs.persist(entities);
        IEntity<?> ie = null;
        for (IEntity<?> iee : entities) {
            ie = iee;
        }
        for (String s : result.keySet()) {
            if (result.get(s) == ie) {
                XmlCursor xml = cs.getContentRetriever(s).getCursor();
                File fOut =
                    new File(getOutputDirectory(), "exist-out-" + testFile);
                OutputStream fos =
                    new BufferedOutputStream(new FileOutputStream(fOut));
                fos.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n".getBytes("UTF-8"));
                fos.write(xml.xmlText().getBytes("UTF-8"));
                fos.flush();
                fos.close();

                validateSignatures(new BufferedInputStream(new FileInputStream(fOut)));

            }
        }

    }

    private void validateSignatures(InputStream is) throws Exception {
        ValidateSigConfig config =
                new ValidateSigConfig.Builder().content(
                    new BufferedInputStream(is)).trustedPublicKey(
                    getPublicKey()).build();

            List<ISignatureValidationResult> listSVR =
                XMLValidator.validateContent(config);
            if( listSVR.size() == 0 ) {
                Assert.fail("Did not find a signature in the content");
            }
            for (ISignatureValidationResult svr : listSVR) {
                Assert.assertTrue(svr.isSignatureValid());
                for (IReferenceValidationResult rvr : svr.getSignatureReferenceResults()) {
                    Assert.assertTrue(rvr.isReferenceDigestValid());
                }
                for (List<IReferenceValidationResult> list : svr.getManifestsReferenceResults()) {
                    for (IReferenceValidationResult rvr : list) {
                        Assert.assertTrue(rvr.isReferenceDigestValid());
                    }
                }
            }

    }

    private File getOutputDirectory() {
        File file = new File("target\\test-results", this.getClass().getName());
        file.mkdirs();
        return file;
    }

    private PublicKey getPublicKey()
            throws KeyStoreException, NoSuchAlgorithmException,
            CertificateException, IOException {
        return getKeystore().getCertificate(ALIAS).getPublicKey();
    }

    private KeyStore getKeystore()
            throws KeyStoreException, NoSuchAlgorithmException,
            CertificateException, IOException {
        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        ks.load(
            this.getClass().getResourceAsStream("/test.jks"),
            PASSWORD.toCharArray());
        return ks;
    }
}
