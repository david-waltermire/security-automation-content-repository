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

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.apache.log4j.Logger;
import org.scapdev.content.core.query.EntityStatistic;

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
	private int other;
			
	// required by jaxb
	public RepositoryStatisticsXMLResponse()
	{
		
	}
	
	public void setCount(String key, EntityStatistic stat)
	{
		if(key.equals("urn:scap-content:entity:org.mitre.oval:test"))
		{
			ovalTests += stat.getCount();
		}
		else if(key.equals("urn:scap-content:entity:org.mitre.oval:definition"))
		{
			ovalDefinitions += stat.getCount();
		}
		else if(key.equals("urn:scap-content:entity:org.mitre.oval:object"))
		{
			ovalObjects += stat.getCount();
		}
		else if(key.equals("urn:scap-content:entity:org.mitre.oval:state"))
		{
			ovalObjects += stat.getCount();
		}
		else if(key.equals("urn:scap-content:entity:org.mitre.oval:variable"))
		{
			ovalObjects += stat.getCount();
		}
		else
		{
			LOG.info("Adding unhandled count for key " + key);
			other += stat.getCount();
		}

	}
	
}
