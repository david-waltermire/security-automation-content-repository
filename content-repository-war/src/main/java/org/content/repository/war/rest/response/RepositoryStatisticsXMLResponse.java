package org.content.repository.war.rest.response;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.apache.log4j.Logger;

@XmlType
@XmlRootElement(name = "repositoryStatistics")
@XmlAccessorType(XmlAccessType.FIELD)
public class RepositoryStatisticsXMLResponse
{
	@XmlTransient
	private static Logger LOG = Logger.getLogger(RepositoryStatisticsXMLResponse.class);
	
	private int ovalDefinitions;
	private int ovalTests;
	private int ovalObjects;
	private int ovalStates;
	private int ovalVariables;
		
	private HashMap<String, String> namespaceToPrefixMap = new HashMap<String, String>();
	
	// required by jaxb
	public RepositoryStatisticsXMLResponse()
	{
		
	}
	
	public void setNamespaceToPrefixMap(Map<String, String> namespaceToPrefixMap) {
		
		if(namespaceToPrefixMap == null)
		{
			return;
		}
		
		this.namespaceToPrefixMap.putAll(namespaceToPrefixMap);
	}	

	
}
