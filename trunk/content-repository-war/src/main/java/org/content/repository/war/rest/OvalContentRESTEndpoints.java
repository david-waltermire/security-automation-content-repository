package org.content.repository.war.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/oval")
public class OvalContentRESTEndpoints {

	@Path("/get/definitions")
	@GET
	@Produces("text/plain")
	public String getDefinitionsList() {
		String ret = "GetDefinitions";

		// TODO: make call to RepositorySingleton

		return ret;
	}

	@Path("/get/definitions")
	@GET
	@Produces("text/plain")
	public String loadOvalDocument() {
		String ret = "LoadContent";

		// TODO: make call to RepositorySingleton

		return ret;
	}

	// TODO: Flesh out more rest endpoints
}
