package org.content.repository.war.rest;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang.time.StopWatch;
import org.apache.log4j.Logger;
import org.content.repository.config.ConfigProperties;
import org.content.repository.config.RepositoryConfiguration;
import org.content.repository.util.WarUtil;
import org.content.repository.war.rest.response.FileUploadXMLResponse;
import org.scapdev.content.model.Entity;
import org.scapdev.content.model.Relationship;
import org.scapdev.content.model.processor.ImportData;
import org.scapdev.content.model.processor.Importer;

@Path("/upload")
public class ContentUploadEndpoints {

	private static Logger LOG = Logger.getLogger(ContentUploadEndpoints.class);
	
	@POST
	@Produces("text/xml")
	@Path("/content")
	public FileUploadXMLResponse uploadContent(@Context HttpServletRequest request) {
		LOG.info("processing loadContentRequest");
		
		FileUploadXMLResponse ret = new FileUploadXMLResponse();

		String tmpDirName = RepositoryConfiguration.INSTANCE
				.getProperty(ConfigProperties.TEMP_DIRECTORY);
		File tmpDir = new File(tmpDirName);
		File tmpSubDir = null;

		try {
			try {
				tmpSubDir = WarUtil.createTempDir("scapRepoFile", "upload", tmpDir);
			} catch (IOException ioe) {
				ret.setCause(ioe);
				return ret;
			}

			if (ServletFileUpload.isMultipartContent(request)) {
				FileItemFactory factory = new DiskFileItemFactory();
				ServletFileUpload upload = new ServletFileUpload(factory);
				List<FileItem> items = null;

				try {
					items = upload.parseRequest(request);
				} catch (FileUploadException fue) {
					// TODO: is the temp dir removed?
					ret.setCause(fue);
					return ret;
				}

				if (items != null) {
					Iterator<FileItem> itr = items.iterator();
					while (itr.hasNext()) {
						FileItem item = itr.next();
						
						if(!item.isFormField() && item.getSize() > 0)
						{
							String filename = item.getName();
														
							String fileSeparator = null;
							
							int loc = -1;
							loc = filename.indexOf("/");
							
							if(loc > -1)
							{
								fileSeparator = "/";
							}
							
							if(fileSeparator == null)
							{
								loc = filename.indexOf("\\");
								if(loc > -1)
								{
									fileSeparator = "\\";
								}
							}
							
							int lastfs = -1;
							
							if(fileSeparator != null)
							{
								lastfs = filename.lastIndexOf(fileSeparator);								
							}
							
							String fnOnly = filename.substring(lastfs + 1);

							try
							{
								item.write(new File(tmpSubDir.getAbsolutePath() + File.separator + fnOnly));
							}
							catch(Exception e)
							{
								ret.setCause(e);
								return ret;
							}
							
							Importer importer = RepositoryConfiguration.INSTANCE.getRepo().getJaxbEntityProcessor().newImporter();
							
							// assuming no exception occurred, try to load the file into the repository
							File[] uploadedfiles = tmpSubDir.listFiles();
							if(uploadedfiles != null && uploadedfiles.length > 0)
							{
								for(int x = 0; x < uploadedfiles.length; x++)
								{
									File f = uploadedfiles[x];
									
									LOG.info("importing file " + f.getAbsolutePath());
									StopWatch timer = new StopWatch();
									timer.start();
									ImportData data = importer.read(f);
									timer.stop();
									int entitiesProcessed = data.getEntities().size();
									int relationshipsProcessed = 0;
									
									if(entitiesProcessed > 0)
									{
										List<Entity> entities = data.getEntities();
										for(Iterator<Entity> eItr = entities.iterator(); eItr.hasNext();)
										{
											Entity e = eItr.next();
											
											Collection<Relationship> relationships = e.getRelationships();
											if(relationships != null)
											{
												relationshipsProcessed = relationships.size();
											}
										}
									}
									LOG.info("Entities processed: " + entitiesProcessed);
									LOG.info("Relationships processed: " + relationshipsProcessed);
									
									LOG.info("Import timing for " + f.getAbsolutePath() + ": " + timer.toString());	
									ret.addUploadedFilename(f.getName(), entitiesProcessed, relationshipsProcessed);
								}
							}
						}
					}
				}
			}
		} finally {

			if(tmpSubDir != null)
			{
				// attempt to delete the temp directory we created.
				WarUtil.delete(tmpSubDir);
			}
		}

		return ret;
	}
}
