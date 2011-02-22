package org.content.repository.config;

import java.io.File;

import org.content.repository.config.validation.SimpleStringValueChecker;

/**
 * Possible properties.  Default values are provided and also an implementation of class that can
 * check that the value supplied for the given property is valid.
 *  
 * @author ssill2
 */
public enum ConfigProperties {

	TEMP_DIRECTORY("repository.temp.directory", "repo" + File.separator + "temp", new SimpleStringValueChecker()),
	REPOSITORY_CONTENT_DIRECTORY("repository.content.directory", "repo" + File.separator + "content", new SimpleStringValueChecker()),
	REPOSITORY_METADATA_DIRECTORY("repository.metadata.directory", "repo" + File.separator + "metadata", new SimpleStringValueChecker());
	
	private String propertyName;
	private String defaultValue;
	private ValueChecker valueChecker;
	
	private ConfigProperties(String propName, String defaultVal, ValueChecker valChecker)
	{
		this.propertyName = propName;
		defaultValue = defaultVal;
		this.valueChecker = valChecker;
	}
	
	public String getDefaultValue()
	{
		return defaultValue;
	}
	
	public String getPropertyName()
	{
		return propertyName;
	}
	
	public ValueChecker getValueChecker()
	{
		return valueChecker;
	}
}
