package org.content.repository.servlet.startup;

import org.scapdev.content.core.ContentRepository;

public enum RepositorySingleton 
{
	INSTANCE;
	
	private ContentRepository repo;
	
	private RepositorySingleton()
	{
		try
		{
			repo = new ContentRepository(ContentRepositoryStartupServlet.class.getClassLoader());			
		}
		catch(Exception e)
		{
			
		}
	}
	
	public void loadLocalContent()
	{
		
	}
}
