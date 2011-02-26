package org.content.repository.war.rest.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlType
@XmlRootElement(name = "repositoryStatistics")
@XmlAccessorType(XmlAccessType.FIELD)
public class RepositoryStatisticsXMLResponse
{
	private int ovalDefinitions;
	private int ovalTests;
	private int ovalObjects;
	private int ovalStates;
	private int ovalVariables;
		
	// required by jaxb
	public RepositoryStatisticsXMLResponse()
	{
		
	}	
}
