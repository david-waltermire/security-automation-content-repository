package org.content.repository.war.rest;

import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlType
@XmlRootElement(name = "fileUploadResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class FileUploadXMLResponse extends SimpleXMLResponse {
		
	// will actually be used
	// when response is serialized by jaxb
	@SuppressWarnings("unused")
	@XmlElement(name = "uploadedFile")
	private List<String> filesUploaded = new LinkedList<String>();

	// required by jaxb
	public FileUploadXMLResponse()
	{	
		super();
	}
	
	public FileUploadXMLResponse(Throwable cause)
	{
		super(cause);
	}
	 
	public void addUploadedFilename(String fn)
	{
		filesUploaded.add(fn);
	}	
}
