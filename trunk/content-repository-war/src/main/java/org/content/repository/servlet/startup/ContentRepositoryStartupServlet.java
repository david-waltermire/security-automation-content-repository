package org.content.repository.servlet.startup;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

public class ContentRepositoryStartupServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static Logger LOG = Logger.getLogger(ContentRepositoryStartupServlet.class);
	
	@Override
	protected void doGet(HttpServletRequest arg0, HttpServletResponse arg1)
			throws ServletException, IOException {
		initRepo();
	}

	private void initRepo()
	{
		// force repository to be opened or created
		RepositorySingleton rs = RepositorySingleton.INSTANCE;
		
		LOG.info("StartupServlet finished initializing repository.");
	}
}
