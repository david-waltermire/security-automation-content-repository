package org.content.repository.config;

import java.io.File;
import java.util.Properties;

import org.scapdev.content.core.ContentRepository;
import org.scapdev.content.core.persistence.hybrid.HybridContentPersistenceManager;
import org.scapdev.content.core.persistence.hybrid.MemoryResidentHybridContentPersistenceManager;

/**
 * A single place to access configuration settings for the repository and hold on to
 * the underlying ContentRepository instance.
 * 
 * @author ssill2
 */
public enum RepositoryConfiguration {

	INSTANCE;
	
	private Properties props = new Properties();
	private ContentRepository repo;

	private RepositoryConfiguration()
	{
		init();
	}
	
	private void init()
	{
		initSuppliedAndDefaultProperties();
		initTempDirectory();
		initContentRepository();
	}

	/**
	 * Make sure that temporary directory exists
	 */
	private void initTempDirectory()
	{
		String tempDirPath = getProperty(ConfigProperties.TEMP_DIRECTORY);
		
		File tmpDir = new File(tempDirPath);
		
		if(!tmpDir.exists())
		{
			if(!tmpDir.mkdirs())
			{
				throw new IllegalStateException("Unable to create temp directory " + tmpDir.getAbsolutePath());
			}
		}
	}
	
	private void initContentRepository()
	{
		try
		{
			repo = new ContentRepository();
			HybridContentPersistenceManager manager = new MemoryResidentHybridContentPersistenceManager();
			repo.setContentPersistenceManager(manager);
		}
		catch(Exception e)
		{
			throw new IllegalStateException("Error trying to initialize ContentRepository!", e);
		}		
	}
	
	/**
	 * Initialize supplied properties and defaults.
	 */
	private void initSuppliedAndDefaultProperties()
	{
		ConfigProperties[] possibleProps = ConfigProperties.values();
		
		for(int x = 0; x < possibleProps.length; x++)
		{
			ConfigProperties prop = possibleProps[x];
			
			String suppliedValue = System.getProperty(prop.getPropertyName(), prop.getDefaultValue());
			
			setProperty(prop, suppliedValue);
		}
	}

	/**
	 * Set a given property.  The property name will be checked against the list of
	 * possible config properties and it's value will be checked for null.  In addition
	 * the value will be checked that it's valid for the given property.
	 * 
	 * @param prop A valid value from config properties enum.
	 * @param value A value to be set for the given property
	 * 
	 * @throws IllegalArgumentException
	 */
	public void setProperty(ConfigProperties prop, String value) throws IllegalArgumentException
	{
		if(prop == null)
		{
			throw new IllegalArgumentException("Property must be a value in " + ConfigProperties.class.getName());
		}

		if(value == null)
		{
			throw new IllegalArgumentException("value must be non-null");
		}
		
		ValueChecker valChecker = prop.getValueChecker();
		
		if(!valChecker.checkValue(value))
		{
			throw new IllegalArgumentException("Supplied value is not valid input for property " + prop.getPropertyName() + ": " + value);
		}
		
		props.setProperty(prop.getPropertyName(), value);
	}
	
	/**
	 * Get the instance of the underlying ContentRepository.
	 * 
	 * @return ContentRepository The instance of the underlying ContentRepository.
	 */
	public ContentRepository getRepo()
	{
		return repo;
	}
	
	public String getProperty(ConfigProperties prop)
	{
		return props.getProperty(prop.getPropertyName());
	}
}

