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
