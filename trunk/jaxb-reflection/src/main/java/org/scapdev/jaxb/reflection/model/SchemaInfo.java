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
package org.scapdev.jaxb.reflection.model;

import java.net.URI;
import java.net.URL;

/**
 * This class descibes the properties associated with an XML schema 
 * @author David Waltermire
 *
 */
public class SchemaInfo {
	private final URI systemId;
	private URL schemaLocation;
	private String version;
	private String prefix;

	public SchemaInfo(URI systemId) {
		this.systemId = systemId;
	}

	/**
	 * Retrieves the system identifier (or namespace) of the XML schema
	 * @return a URI representing the XML schema's namespace
	 */
	public URI getSystemId() {
		return systemId;
	}

	/**
	 * Retrieves the location of the XML schema
	 * @return the location URL or <code>null</code> if this information is unavailable
	 */
	public URL getSchemaLocation() {
		return schemaLocation;
	}

	void setSchemaLocation(URL schemaLocation) {
		this.schemaLocation = schemaLocation;
	}

	/**
	 * Retrieves the version of the XML schema
	 * @return the schema version or <code>null</code> if this information is unavailable
	 */
	public String getVersion() {
		return version;
	}

	void setVersion(String version) {
		this.version = version;
	}

	/**
	 * Retrieves the suggested prefix of the XML schema
	 * @return the schema prefix or <code>null</code> if this information is unavailable
	 */
	public String getPrefix() {
		return prefix;
	}

	void setPrefix(String prefix) {
		this.prefix = prefix;
	}
}
