package org.content.repository.servlet.startup;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Called to properly initialize the RepositorySingleton instance.
 * 
 * @author ssill2
 *
 * @see RepositorySingleton
 */
public class ContentRepositoryStartupServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	@Override
	protected void doGet(HttpServletRequest arg0, HttpServletResponse arg1)
			throws ServletException, IOException {
		initRepo();
	}

	/**
	 * Actually force the initialization of RepositorySingleton
	 */
	private void initRepo()
	{
		// force repository to be opened or created
		RepositorySingleton rs = RepositorySingleton.INSTANCE;
		
		System.out.println("StartupServlet finished initializing repository.");
	}
}
