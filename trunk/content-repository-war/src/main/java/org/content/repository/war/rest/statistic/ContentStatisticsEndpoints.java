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
package org.content.repository.war.rest.statistic;

import java.util.Map;
import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.apache.log4j.Logger;
import org.content.repository.config.RepositoryConfiguration;
import org.scapdev.content.core.ContentRepository;
import org.scapdev.content.core.query.EntityStatistic;
import org.scapdev.content.model.MetadataModel;

@Path("/statistics")
public class ContentStatisticsEndpoints {

	private static Logger LOG = Logger.getLogger(ContentStatisticsEndpoints.class);

	@GET
	@Produces("text/xml")
	@Path("/global")
	public RepositoryStatisticsXMLResponse getGlobalStatistics() 
	{
		LOG.info("Processing request for uploadContent()");
		RepositoryStatisticsXMLResponse ret = new RepositoryStatisticsXMLResponse();

		try 
		{
			ContentRepository cr = RepositoryConfiguration.INSTANCE.getRepo();
			MetadataModel mm = cr.getMetadataModel();
			
			Set<String> entityInfoIds = mm.getEntityInfoIds();
			Map<String, ? extends EntityStatistic> stats = cr.queryStatistics(entityInfoIds);
			for (EntityStatistic stat : stats.values()) {
				ret.add(stat);
			}
		} 
		finally
		{
		}

		return ret;
	}
}
