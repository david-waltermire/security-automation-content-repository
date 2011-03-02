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
package org.content.repository.war.rest.response;

import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlType
@XmlRootElement(name = "fileUploadResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class FileUploadXMLResponse extends SimpleXMLResponse {
	
	@XmlAttribute
	private int processed = 0;
	
	// will actually be used
	// when response is serialized by jaxb
	@SuppressWarnings("unused")
	@XmlElement(name = "fileEntry")
	private List<UploadedFileResponseEntry> filesUploaded = new LinkedList<UploadedFileResponseEntry>();

	// required by jaxb
	public FileUploadXMLResponse()
	{	
		super();
	}
	
	public FileUploadXMLResponse(Throwable cause)
	{
		super(cause);
	}
	 
	public void addUploadedFilename(String fn, int entitiesProcessed, int relationshipsProcessed)
	{
		filesUploaded.add(new UploadedFileResponseEntry(fn, entitiesProcessed, relationshipsProcessed));
		processed++;
	}	
}
