package org.content.repository.config;

/**
 * Implementations of this interface will be used to check a value before
 * setting it as a property in RepositoryConfiguration.
 * 
 * @author ssill2
 *
 */
public interface ValueChecker {

	/**
	 * Check the given value for validity.
	 * 
	 * @param value The string to be checked
	 * @return boolean True if valid, false otherwise.
	 */
	public boolean checkValue(String value);
}
