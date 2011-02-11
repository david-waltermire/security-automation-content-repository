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

import java.io.IOException;

import javax.xml.bind.JAXBException;

import org.scapdev.content.core.database.AbstractContentDatabase;
import org.scapdev.content.core.database.memory.MemoryResidentContentDatabase;
import org.scapdev.content.model.jaxb.JAXBMetadataModel;
import org.scapdev.content.model.jaxb.MetadataModelFactory;
import org.scapdev.content.model.processor.jaxb.JAXBEntityProcessor;

public class ContentRepository {
	private final AbstractContentDatabase contentDatabase;
	private final JAXBMetadataModel model;
	private final JAXBEntityProcessor processor;
//	private final ProcessingFactory processingFactory;
//	private final InstanceWriterFactory instanceWriterFactory;
//	private final Resolver resolver;
//	private final QueryProcessor queryProcessor;


	public ContentRepository(ClassLoader classLoader) throws IOException, JAXBException {
		contentDatabase = new MemoryResidentContentDatabase();
		model = MetadataModelFactory.newInstance();
		processor = new JAXBEntityProcessor(this, model);
//		processingFactory = new JAXBProcessingFactory(contentDatabase, model);
//		instanceWriterFactory = new JAXBInstanceWriterFactory(model);
//		resolver = new LocalResolver(contentDatabase);
//		queryProcessor = new DefaultQueryProcessor(resolver);
	}

	public JAXBMetadataModel getMetadataModel() {
		return model;
	}
//
//	public void retrieveContentFragment(Key key, OutputStream os) throws IOException {
//		QueryResult queryResult = queryProcessor.query(new SimpleQuery(key));
//		InstanceWriter writer = instanceWriterFactory.newInstanceWriter(os);
//		writer.write(queryResult);
//	}

	public JAXBEntityProcessor getProcessor() {
		return processor;
	}

	public void shutdown() {
		processor.shutdown();
	}
}
