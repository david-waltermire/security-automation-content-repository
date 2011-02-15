package org.content.repository.war.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.jboss.seam.annotations.Name;

@Name("ovalContentRestEndpoints")
@Path("/oval")
public class OvalContentRESTEndpoints 
{
	@Path("/definitions")
	@GET
	@Produces("text/plain")
	public String getDefinitionsList()
	{
		String ret = "hi!";
		
		return ret;
	}
}
