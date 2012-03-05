package gov.nist.scap.content.shredder.rules.xmlbeans;
/**
 * License Agreement
 *
 * This software was developed at the National Institute of Standards and
 * Technology (NIST) by employees of the Federal Government in the course of
 * their official duties. Pursuant to Title 17 Section 105 of the United States
 * Code, this software is not subject to copyright protection and is in the
 * public domain. NIST assumes no responsibility whatsoever for use by other
 * parties of its source code or open source server, and makes no guarantees,
 * expressed or implied, about its quality, reliability, or any other
 * characteristic.
 */


import gov.nist.scap.signature.KeyInfoBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.LinkedList;
import java.util.List;

import org.junit.Ignore;

/**
 * A utility class for getting set-up test parameters
 */
@Ignore("Utility Class - has no tests")
public class TestUtil {
    private static final String ALIAS = "test";
    private static final String PASSWORD = "123456";

    /** holds the keystore instance */
    private static KeyStore keystore;

    public static KeyStore getKeystore() throws Exception {
        if (keystore == null) {
            KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
            File file = new File("test.jks");
            ks.load(new FileInputStream(file), PASSWORD.toCharArray());
            keystore = ks;
        }
        return keystore;
    }

    public static PublicKey getPublicKey() throws Exception {
        return getKeystore().getCertificate(ALIAS).getPublicKey();
    }

    public static PrivateKey getPrivateKey() throws Exception {
        return (PrivateKey)getKeystore().getKey(ALIAS, PASSWORD.toCharArray());
    }

    public static List<InputStream> getCertificateList() throws FileNotFoundException {
        List<InputStream> list = new LinkedList<InputStream>();
        list.add(new FileInputStream(new File("verisign-cert.cer")));
        return list;
    }
    
    public static KeyInfoBuilder createKeyInfo() throws Exception {
        KeyInfoBuilder keyInfo = new KeyInfoBuilder()
	    	.privateKey(TestUtil.getPrivateKey())
	    	.publicKey(TestUtil.getPublicKey())
	    	.certificate(TestUtil.getCertificateList());
        return keyInfo;
    }


}
