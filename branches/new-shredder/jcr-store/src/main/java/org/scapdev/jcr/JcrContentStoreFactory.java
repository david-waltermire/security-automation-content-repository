package org.scapdev.jcr;

import org.scapdev.content.core.persistence.hybrid.ContentStore;
import org.scapdev.content.core.persistence.hybrid.ContentStoreFactory;

public class JcrContentStoreFactory implements ContentStoreFactory {
	
	private static JcrContentStore INSTANCE = null;

	@Override
	public ContentStore newContentStore() {
		if (INSTANCE == null) {
			INSTANCE = new JcrContentStore();			
		}
		return INSTANCE;
	}
	
	
}
