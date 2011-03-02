/*******************************************************************************
 * The MIT License
 * 
 * Copyright (c) 2011 David Waltermire
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 ******************************************************************************/
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
