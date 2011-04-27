package org.scapdev.db;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Logger;
import org.scapdev.content.core.persistence.hybrid.AbstractContentRetriever;
import org.scapdev.content.core.persistence.hybrid.ContentRetriever;
import org.scapdev.content.core.persistence.hybrid.ContentStore;
import org.scapdev.content.model.Entity;
import org.scapdev.content.model.MetadataModel;
import net.sourceforge.jtds.jdbc.Driver;
//import com.microsoft.jdbc.sqlserver.SQLServerDriver;


public class DbContentStore implements ContentStore {
	
	private static final Properties env = new Properties();
	private static final Logger LOG = Logger.getLogger(DbContentStore.class);
	private java.sql.Driver driver =  new net.sourceforge.jtds.jdbc.Driver();
	private Connection conn = null;
	
	static {
		try {
//			Class.forName("com.microsoft.jdbc.sqlserver.SQLServerDriver");
			Class.forName("net.sourceforge.jtds.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			System.out.println("Can't load MS SQL Server JDBC Driver com.microsoft.jdbc.sqlserver.SQLServerDriver");
			e.printStackTrace();
		}
	}
	
	public DbContentStore() {
		try {
//			conn = DriverManager.getConnection("jdbc:microsoft:sqlserver://localhost:1433", "sa", "adminadmin");
			String urlString = "jdbc:jtds:sqlserver://localhost:2301/SACR_DB";
			System.out.println("Trying connection URL: " + urlString);
			conn = DriverManager.getConnection(urlString, "sa", "adminadmin");
		} catch (SQLException e) {
			System.out.println("Can't create database connection");
			e.printStackTrace();
		}
	}

	public JAXBElement<Object> getContent(String contentId, MetadataModel model) {
		JAXBElement<Object> result = null;
		String content = null;
		try {
			long id = Long.parseLong(contentId);
			PreparedStatement ps = conn.prepareStatement("select content from SACR_ENTITY where id = ?");
			ps.setLong(1, id);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				content = rs.getString(1);
				if (content != null) {
					ByteArrayInputStream bais = new ByteArrayInputStream(content.getBytes());
					Unmarshaller unmarshaller = model.getJAXBContext().createUnmarshaller();
					@SuppressWarnings("unchecked")
					JAXBElement<Object> unmarshalled = (JAXBElement<Object>) unmarshaller.unmarshal(bais);
					result = unmarshalled;
				} else {
					throw new IllegalStateException("CONTENT ID NOT FOUND: " + contentId);
				}
			}
			
		} catch (Exception e) {
			LOG.error("Error getting content for id: " + contentId,e);
		}
		return result;
	}

	public Map<String, Entity> persist(List<? extends Entity> entities,
			MetadataModel model) {
		Map<String, Entity> result = new HashMap<String, Entity>();
		for (Entity entity : entities) {
			JAXBElement<Object> element = entity.getObject();
			String contentId = null;
			String content = null;
			try {
				Marshaller marshaller = model.getJAXBContext().createMarshaller();
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				marshaller.marshal(element, baos);
				content = new String(baos.toByteArray());
			} catch (Exception e) {
				String message = "Error marshalling content " + entity.getKey().getId() + " to be persisted";
				LOG.error(message, e);
				throw new IllegalStateException(message, e);
			}
			try {
				conn.setAutoCommit(false);
				PreparedStatement ps = conn.prepareStatement("insert into SACR_ENTITY (content) values (?)", Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, content);
				ps.executeUpdate();
				ResultSet rs = ps.getGeneratedKeys();
				while (rs.next()) {
					long id = rs.getLong(1);
					contentId = Long.toString(id);
				}
				rs.close();
				result.put(contentId, entity);
				conn.commit();
				conn.setAutoCommit(true);
			} catch (Exception e) {
				String message = "Error inserting content " + entity.getKey().getId() + " into database";
				LOG.error(message, e);
				throw new IllegalStateException(message, e);
			}
		}
		return result;
	}

	public ContentRetriever getContentRetriever(String contentId,
			MetadataModel model) {
		return new InternalContentRetriever(contentId, model);
	}

	public void shutdown() {
		try {
			conn.close();
		} catch (SQLException e) {
		}
	}
	
	private class InternalContentRetriever extends AbstractContentRetriever<Object> {

		public InternalContentRetriever(String contentId, MetadataModel model) {
			super(contentId, model);
		}

		@Override
		protected JAXBElement<Object> getContentInternal(String contentId, MetadataModel model) {
			return DbContentStore.this.getContent(contentId, model);
		}
	}
	
	public static void main(String[] args) {
		DbContentStore store = new DbContentStore();
		System.out.println(store.driver.getClass());
	}

}
