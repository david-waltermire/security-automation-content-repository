package org.content.repository.config.validation;

import org.content.repository.config.ValueChecker;

/**
 * Provides basic non-null and non-zero-length checking for a string.
 * 
 * @author ssill2
 */
public class SimpleStringValueChecker implements ValueChecker {

	@Override
	public boolean checkValue(String value) 
	{
		boolean ret = false;
		if(value == null)
		{
			return ret;
		}
		
		if(value.trim().length() == 0)
		{
			return ret;
		}
		
		// TODO: maybe put some more validity checking here.
		
		ret = true;
		
		return ret;
	}
}
