package org.content.repository.war.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.apache.log4j.Logger;
import org.content.repository.config.RepositoryConfiguration;
import org.content.repository.war.rest.response.RepositoryStatisticsXMLResponse;
import org.scapdev.content.core.ContentRepository;
import org.scapdev.content.model.Key;

@Path("/statistics")
public class ContentStatisticsEndpoints {

	private static Logger LOG = Logger.getLogger(ContentStatisticsEndpoints.class);

	@GET
	@Produces("text/xml")
	@Path("/global")
	public RepositoryStatisticsXMLResponse uploadContent() 
	{
		LOG.info("Processing request for uploadContent()");

		RepositoryStatisticsXMLResponse ret = new RepositoryStatisticsXMLResponse();

		try 
		{
			ContentRepository cr = RepositoryConfiguration.INSTANCE.getRepo();
			

		} 
		finally
		{
		}

		return ret;
	}
}
