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

import org.scapdev.content.model.Entity;
import org.scapdev.content.model.Relationship;
import org.scapdev.content.model.RelationshipInfo;
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
    	ContentRepository repository = new ContentRepository(App.class.getClassLoader());
    	Importer importer = repository.getProcessor().newImporter();
    	ImportData data = importer.read(new File("content/fdcc/FDCC-Major-Version-1.2.0.0/winxp/fdcc-winxp-oval.xml"));
    	for (Entity<Object> entity : data.getEntities()) {
    		System.out.println("Entity: "+entity.getEntityInfo().getId());
    		for (Relationship<Object, ?> relationship : entity.getRelationships()) {
        		System.out.println("  Relationship: "+relationship.getRelationshipInfo().getId());
    		}
    	}
    	repository.shutdown();
    }
}
