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
package org.scapdev.content.core;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.JAXBException;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;

import org.apache.commons.lang.time.StopWatch;
import org.apache.log4j.Logger;
import org.scapdev.content.core.query.QueryResult;
import org.scapdev.content.core.writer.InstanceWriter;
import org.scapdev.content.model.Entity;
import org.scapdev.content.model.Key;
import org.scapdev.content.model.Relationship;
import org.scapdev.content.model.processor.ImportData;
import org.scapdev.content.model.processor.ImportException;
import org.scapdev.content.model.processor.Importer;


/**
 * Hello world!
 *
 */
public class App {
	private static final Logger log = Logger.getLogger(App.class);

    public static void main( String[] args ) throws IOException, JAXBException, ImportException, ClassNotFoundException, XMLStreamException, FactoryConfigurationError {
    	ContentRepository repository = null;
    	{
	    	StopWatch watch = new StopWatch();
	    	watch.start();
	    	repository = new ContentRepository();
	    	watch.stop();
	    	
	    	log.info("Repository startup: "+watch.toString());
    	}

    	Importer importer = repository.getJaxbEntityProcessor().newImporter();
    	{
    		File file = new File("target/content/com.redhat.rhsa-all.xml");
    		importFile(file, importer);
    	}

    	{
    		File file = new File("target/content/USGCB-Major-Version-1.1.0.0/Win7/USGCB-Windows-7-oval.xml");
    		importFile(file, importer);
    	}

    	{
    		File file = new File("target/content/nvdcve-2.0-2011.xml");
    		importFile(file, importer);
    	}

    	InstanceWriter writer = repository.newInstanceWriter();
    	{
	    	StopWatch watch = new StopWatch();
	    	watch.start();
	    	LinkedHashMap<String, String> fields = new LinkedHashMap<String, String>();
	    	fields.put("urn:scap-content:field:org.mitre.oval:definition", "oval:gov.nist.usgcb.windowsseven:def:2");
	    	Key key = new Key("urn:scap-content:key:org.mitre.oval:definition", fields);
	    	QueryResult result = repository.query(key, true);
	    	for (Entity entity : result.getEntities().values()) {
	    		log.info("Retrieved entity: "+entity.getKey());
	    	}
	    	watch.stop();
	    	log.info("Definition query: "+watch.toString());
	
			Writer stringWriter = new StringWriter();
	    	writer.write(result, stringWriter);
	    	stringWriter.flush();
			log.info(stringWriter.toString());
    	}

    	{
	    	StopWatch watch = new StopWatch();
	    	watch.start();

	    	List<String> cces = new LinkedList<String>();
	    	cces.add("CCE-9345-0");
	    	cces.add("CCE-8414-5");

	    	QueryResult result = repository.query("urn:scap-content:external-identifier:org.mitre:cce-5", cces, Collections.singleton("urn:scap-content:entity:org.mitre.oval:definition"), true);
	    	for (Entity entity : result.getEntities().values()) {
	    		log.info("Retrieved entity: "+entity.getKey());
	    	}
	    	watch.stop();
	    	log.info("Definition query: "+watch.toString());
	
			Writer stringWriter = new StringWriter();
	    	writer.write(result, stringWriter);
	    	stringWriter.flush();
			log.info(stringWriter.toString());
    	}
    	repository.shutdown();
    }

	private static void importFile(File file, Importer importer) {
		log.info("importing: "+file);
    	StopWatch watch = new StopWatch();
    	watch.start();
    	ImportData data = importer.read(file);
    	watch.stop();
    	log.info("Entities processed: "+data.getEntities().size());
    	int relationships = 0;
    	for (Entity entity : data.getEntities()) {
    		for (@SuppressWarnings("unused") Relationship relationship : entity.getRelationships()) {
    			++relationships;
    		}
    	}
    	log.info("Relationships processed: "+relationships);
    	log.info("Import timing: "+watch.toString());
	}
}
