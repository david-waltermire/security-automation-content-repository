package org.content.repository.util;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class that holds useful static methods.
 * 
 * @author ssill2
 */
public final class WarUtil {
	private static Logger LOG = LoggerFactory.getLogger(WarUtil.class);
	
	private WarUtil()
	{
		throw new IllegalStateException("This class is not to be instantiated");
	}
	
	/**
	 * Since java has no method to create a temp directory, like File.createTempFile does for files, 
	 * we'll provide a method here.
	 * 
	 * @param prefix
	 * @param suffix
	 * @param parentDir
	 * 
	 * @return File The newly created temp directory
	 */
	public static File createTempDir(String prefix, String suffix, File parentDir) throws IOException
	{
		File ret = null;
		
		// first create a temp file using builtin method.
		ret = File.createTempFile(prefix, suffix, parentDir);
		
		// delete the newly created file
		ret.delete();
		
		// now create a directory with the same name as the file we just deleted
		ret.mkdirs();
		
		return ret;
	}
	
	/**
	 * Delete a file or directory(recursively)
	 * 
	 * @param fileOrDir
	 */
	public static void delete(File fileOrDir)
	{
		if(fileOrDir == null)
		{
			return;
		}
		
		if(fileOrDir.isFile())
		{
			// try to delete as a normal file
			if(!fileOrDir.delete())
			{
				throw new IllegalStateException("Unable to delete file " + fileOrDir.getAbsolutePath());
			}
			else
			{
				LOG.info("Successfully deleted file " + fileOrDir.getAbsolutePath());
			}
		}
		else if(fileOrDir.isDirectory())
		{
			File[] contents = fileOrDir.listFiles();
			
			if(contents != null && contents.length > 0)
			{
				for(int x = 0; x < contents.length; x++)
				{
					File entry = contents[x];
					
					delete(entry);
				}
			}

			if(!fileOrDir.delete())
			{
				throw new IllegalStateException("Unable to delete directory " + fileOrDir.getAbsolutePath());
			}
			else
			{
				LOG.info("Successfully deleted directory " + fileOrDir.getAbsolutePath());
			}
		}
	}
}
