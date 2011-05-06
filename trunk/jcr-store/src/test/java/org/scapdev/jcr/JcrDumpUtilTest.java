package org.scapdev.jcr;

import org.junit.Test;

public class JcrDumpUtilTest {
	
//	@Test
	public void testDumpUtl() throws Exception {
		
		System.setProperty(JcrContentStore.REPOSITORY_NAME_PROPERTY, JcrContentStore.JCR_REPO_NAME);
		System.setProperty(JcrContentStore.REPOSITORY_CONFIG_FILE_PROPERTY, "repo/repository.xml");
		System.setProperty(JcrContentStore.REPOSITORY_HOME_PROPERTY, "jcrHome");
		
		
		JcrContentStoreFactory factory = new JcrContentStoreFactory();
		JcrContentStore contentStore = (JcrContentStore) factory.newContentStore();
		try {
			JcrDumpUtil jcrDumpUtil = new JcrDumpUtil(contentStore.getRepository(), contentStore.getSession());
			System.out.println(jcrDumpUtil.dumpContentListing());
		} finally {
			contentStore.shutdown();
		}

	}

}
