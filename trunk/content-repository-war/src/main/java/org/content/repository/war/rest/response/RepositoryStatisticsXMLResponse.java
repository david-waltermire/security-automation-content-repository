package org.content.repository.war.rest.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlType
@XmlRootElement(name = "fileEntry")
@XmlAccessorType(XmlAccessType.FIELD)
public class RepositoryStatisticsXMLResponse
{
	private String filename;
	private int entitiesProcessed;
	private int relationshipsProcessed;
	
	// required by jaxb
	public RepositoryStatisticsXMLResponse()
	{
		
	}
	
	public RepositoryStatisticsXMLResponse(String filename, int entities, int relationships)
	{
		this.filename = filename;
		this.entitiesProcessed = entities;
		this.relationshipsProcessed = relationships;
	}
}
