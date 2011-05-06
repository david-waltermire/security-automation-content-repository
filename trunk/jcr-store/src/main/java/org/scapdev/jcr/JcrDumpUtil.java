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
package org.scapdev.jcr;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.ValueFormatException;
import javax.jcr.nodetype.NodeType;
import javax.jcr.version.Version;
import javax.jcr.version.VersionHistory;
import javax.jcr.version.VersionIterator;

import org.apache.log4j.Logger;


public class JcrDumpUtil {
	
	private static final Logger LOG = Logger.getLogger(JcrDumpUtil.class);

	private Repository repo;
	private Session session;

	public static final String TYPE_NAME_ENTITIES = "Entities";
	public static final String TYPE_NAME_ENTITY = "Entity";
	public static final String TYPE_NAME_CONTENT = "Content";
	
//	public static void main(String[] args) throws Exception {
//		JcrContentStoreFactory factory = new JcrContentStoreFactory();
//		JcrContentStore contentStore = (JcrContentStore) factory.newContentStore();
//		
//		JcrDumpUtil jcrDumpUtil = new JcrDumpUtil(contentStore.getRepository(), contentStore.getSession());
//		System.out.println(jcrDumpUtil.dumpContentListing());
//	}
	
	public JcrDumpUtil(Repository repo, Session session) {
		this.repo = repo;
		this.session = session;
	}
	
	public String dumpContentListing() throws Exception
	{
		StringBuilder sb = new StringBuilder();

		dumpRepo(session, session.getRootNode(), sb);
		
		session.logout();
		return sb.toString();
	}
	
	private void dumpRepo(Session s, Node node, StringBuilder sb) throws RepositoryException
	{
		if(node.getName().equals("jcr:system"))
		{
			return;
		}
		String eol = System.getProperty("line.separator");
		
		String indentString = indent(node.getDepth());
		sb.append(node.getPath() + eol);
		sb.append("identifier: " + node.getIdentifier() + eol);
		
		// show any properties
		PropertyIterator pi = node.getProperties();
		if(pi.hasNext())
		{
			sb.append(indentString + "Properties:" + eol);
			while(pi.hasNext())
			{
				Property p = pi.nextProperty();
				String pname = p.getName();
				String pvalue = "";
				try
				{
					if(p.getValues() != null )
					{
						Value[] values = p.getValues();
						for(int x = 0; x < values.length; x++)
						{
							Value val = values[x];
							pvalue += val.getString() + ((x == values.length - 1) ? "" : ", ");
						}					
					}
				}
				catch(ValueFormatException vfe)
				{
					// property must be a single value and not an array
					pvalue = p.getValue().getString();
				}
				
				sb.append(indentString + "   " + pname + " = " + pvalue + eol);
			}
		}
		
		// show any version history
		// look at revision history on the node
		NodeType[] mixinTypes = node.getMixinNodeTypes();
		boolean versionable = false;
		for(int x = 0; x < mixinTypes.length;x++)
		{
			NodeType nt = mixinTypes[x];
			if(nt.getName().equals("mix:versionable"))
			{
				versionable = true;
				break;
			}
		}
		
		if(versionable)
		{
			VersionHistory vhist = s.getWorkspace().getVersionManager().getVersionHistory(node.getPath());
			VersionIterator vItr = vhist.getAllVersions();
			if(vItr.hasNext())
			{
				sb.append(indentString + "Revisions:" + eol);
				while(vItr.hasNext())
				{
					Version v = vItr.nextVersion();
//					sb.append(indentString + "   " + v.getCreated().getTime().toString() + v.getIdentifier() eol);
					dumpRepo(s, v, sb);
				}
			}
		}

		// get any children
		NodeIterator ni = node.getNodes();
		if(ni.hasNext())
		{
			while(ni.hasNext())
			{
				Node child = ni.nextNode();
				dumpRepo(s, child, sb);
			}
		}
	}
	
	private String indent(int depth)
	{
		StringBuilder sb = new StringBuilder();
		
		for(int x = 0; x < depth; x++)
		{
			sb.append("   ");
		}
		
		return sb.toString();
	}
}
