package org.content.repository.war.rest.oval;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/content/oval")
public class OVALEndpointProviderImpl implements IOVALEndpointProvider {

	private static Logger LOG = LoggerFactory.getLogger(OVALEndpointProviderImpl.class);
	
	@Path("/get/definitions")
	@GET
	@Produces("text/plain")
	public String getDefinitionsList() {
		String ret = "GetDefinitions";
		LOG.info("Entering getDefinitionsList()");
		
		// TODO: make call to Repository
		return ret;
	}
}
