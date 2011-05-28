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

import javax.xml.stream.XMLStreamException;

import org.apache.commons.lang.time.StopWatch;
import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.scapdev.content.core.query.Query;
import org.scapdev.content.core.query.QueryResult;
import org.scapdev.content.core.writer.InstanceWriter;
import org.scapdev.content.model.Entity;
import org.scapdev.content.model.Relationship;
import org.scapdev.content.model.processor.ImportData;
import org.scapdev.content.model.processor.Importer;

public class ContentRepositoryTestBase {
	private static final Logger log = Logger.getLogger(ContentRepositoryTestBase.class);
	protected static ContentRepository repository;
	protected static Importer importer;
	protected static InstanceWriter writer;

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
    	StopWatch watch = new StopWatch();
    	watch.start();
		repository.shutdown();
    	watch.stop();
    	log.info("Repository shutdown: "+watch.toString());
	}

	protected void importFile(File file) {
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

//		Set<String> statIds = new HashSet<String>();
//		statIds.add("urn:scap-content:entity:org.mitre.oval:definition");
//		statIds.add("urn:scap-content:entity:org.mitre.oval:variable");
//		statIds.add("urn:scap-content:entity:org.mitre.oval:test");
//		statIds.add("urn:scap-content:entity:org.mitre.oval:object");
//		statIds.add("urn:scap-content:entity:org.mitre.oval:state");
//		statIds.add("urn:scap-content:entity:gov.nist.scap:vulnerability-0.4-vuln");
//		Map<String, ? extends EntityStatistic> stats = repository.getContentPersistenceManager().getEntityStatistics(statIds, repository.getMetadataModel());
//		printStatInfo(stats);
//    	repository.queryStatistics(entityInfoIds)
	}

	protected <RESULT extends QueryResult> void writeQuery(Query<RESULT> query) throws XMLStreamException, IOException {
    	StopWatch watch = new StopWatch();
    	watch.start();

    	QueryResult result = repository.query(query);
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

//	@SuppressWarnings("unused")
//	protected void printStatInfo(Map<String, ? extends EntityStatistic> stats){
//		log.info("printing entity stats: ");
//		for (Map.Entry<String, ? extends EntityStatistic> entry : stats.entrySet()){
//			log.info("Entity Key: " + entry.getKey());
//			EntityStatistic stat = entry.getValue();
//			log.info("Entity count: " + stat.getCount());
//			log.info("printing entity relationship stats: ");
//			for (Map.Entry<String, ? extends RelationshipStatistic> relStatEntry : stat.getRelationshipInfoStatistics().entrySet()){
//				log.info("Relationship Key: " + relStatEntry.getKey());
//				RelationshipStatistic relStat = relStatEntry.getValue();
//				log.info("Relationship count: "+ relStat.getCount());
//			}
//		}
//	}
}
