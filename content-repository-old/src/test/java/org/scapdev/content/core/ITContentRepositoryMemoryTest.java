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

import org.apache.commons.lang.time.StopWatch;
import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.scapdev.content.core.persistence.ContentPersistenceManager;
import org.scapdev.content.core.persistence.hybrid.MemoryResidentHybridContentPersistenceManager;

public class ITContentRepositoryMemoryTest extends ProcessContentTestBase {
	private static final Logger log = Logger.getLogger(ITContentRepositoryMemoryTest.class);

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
    	StopWatch watch = new StopWatch();
    	watch.start();
    	repository = new ContentRepository();
		ContentPersistenceManager persistenceManager = new MemoryResidentHybridContentPersistenceManager(); 
		repository.setContentPersistenceManager(persistenceManager);
    	watch.stop();
    	log.info("Repository startup: "+watch.toString());

    	importer = repository.getJaxbEntityProcessor().newImporter();
    	writer = repository.newInstanceWriter();
	}
}
