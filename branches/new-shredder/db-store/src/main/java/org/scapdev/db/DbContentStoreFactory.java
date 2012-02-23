package org.scapdev.db;

import org.scapdev.content.core.persistence.hybrid.ContentStore;
import org.scapdev.content.core.persistence.hybrid.ContentStoreFactory;

	
public class DbContentStoreFactory implements ContentStoreFactory {
	
	private static DbContentStore INSTANCE = null;

	public ContentStore newContentStore() {
		if (INSTANCE == null) {
			INSTANCE = new DbContentStore();
		}
		return INSTANCE;
	}

}

