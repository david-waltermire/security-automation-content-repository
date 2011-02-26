package org.content.repository.war.rest.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlType
@XmlRootElement(name = "fileEntry")
@XmlAccessorType(XmlAccessType.FIELD)
public class UploadedFileResponseEntry
{
	private String filename;
	@XmlAttribute
	private int entitiesProcessed;
	@XmlAttribute
	private int relationshipsProcessed;
	
	// required by jaxb
	public UploadedFileResponseEntry()
	{
		
	}
	
	public UploadedFileResponseEntry(String filename, int entities, int relationships)
	{
		this.filename = filename;
		this.entitiesProcessed = entities;
		this.relationshipsProcessed = relationships;
	}
}
