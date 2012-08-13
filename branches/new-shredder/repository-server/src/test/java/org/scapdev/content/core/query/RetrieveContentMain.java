package org.scapdev.content.core.query;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

public class RetrieveContentMain {

	private static final String ENDPOINT = "http://localhost:8080/repository-server/ws/";
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		URLConnection connection = new URL(ENDPOINT + "retrieve").openConnection();
		connection.setDoOutput(true);
		connection.setDoInput(true);
		connection.setRequestProperty("Content-Type", "application/json");
		
		InputStream is = SendContentMain.class.getResourceAsStream("/retrieve.json");
		OutputStream os = connection.getOutputStream();
		byte[] b = new byte[1024 * 10];
		int length;
		while( (length = is.read(b)) >= 0 ) {
			os.write(b, 0, length);
		}
		os.flush();
		os.close();

		for( Map.Entry<String, List<String>> e : connection.getHeaderFields().entrySet() ) {
			System.out.println("Header: " + e.getKey());
			for( String s : e.getValue() ) {
				System.out.println("  " + s);
			}
		}
		BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		String line;
		System.out.println("\n");
		while( (line = br.readLine()) != null ) {
			System.out.println(line);
		}

	}

}
