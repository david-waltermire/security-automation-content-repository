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

import javax.xml.bind.JAXBException;

import org.apache.commons.lang.time.StopWatch;
import org.scapdev.content.model.processor.ImportException;
import org.scapdev.content.model.processor.Importer;
import org.scapdev.content.model.processor.jaxb.ImportData;


/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws IOException, JAXBException, ImportException
    {
    	StopWatch watch = new StopWatch();
    	watch.start();
    	ContentRepository repository = new ContentRepository(App.class.getClassLoader());
    	watch.stop();
    	System.out.println("Repository startup: "+watch.toString());

    	
    	Importer importer = repository.getProcessor().newImporter();
    	watch.reset();
    	watch.start();
    	ImportData data = importer.read(new File("target/content/com.redhat.rhsa-all.xml"));
    	watch.stop();
    	System.out.println("Redhat import: "+watch.toString());

//    	for (Entity<Object> entity : data.getEntities()) {
//    		System.out.println("Entity: "+entity.getEntityInfo().getId());
//    		for (Relationship<Object, ?> relationship : entity.getRelationships()) {
//        		System.out.println("  Relationship: "+relationship.getRelationshipInfo().getId());
//    		}
//    	}
    	watch.reset();
    	watch.start();
    	ImportData data2 = importer.read(new File("target/content/USGCB-Major-Version-1.1.0.0/Win7/USGCB-Windows-7-oval.xml"));
    	watch.stop();
    	System.out.println("USGCB Win7 import: "+watch.toString());

//    	for (Entity<Object> entity : data2.getEntities()) {
//    		System.out.println("Entity: "+entity.getEntityInfo().getId());
//    		for (Relationship<Object, ?> relationship : entity.getRelationships()) {
//        		System.out.println("  Relationship: "+relationship.getRelationshipInfo().getId());
//    		}
//    	}
    	repository.shutdown();
    }
}
