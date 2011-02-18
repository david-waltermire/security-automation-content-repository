/*******************************************************************************
 * The MIT License
 * 
 * Copyright (c) 2011 davidwal
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
package org.scapdev.content.core.writer;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.Marshaller;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.log4j.Logger;
import org.codehaus.stax2.XMLOutputFactory2;
import org.scapdev.content.core.query.QueryResult;
import org.scapdev.content.model.DocumentInfo;
import org.scapdev.content.model.Entity;
import org.scapdev.content.model.Key;
import org.scapdev.content.model.MetadataModel;
import org.scapdev.content.util.XmlStreamWriterNamespaceFilter;

public class DefaultInstanceWriter implements InstanceWriter {
	private static final Logger log = Logger.getLogger(DefaultInstanceWriter.class);
	private final Marshaller marshaller;
	private final MetadataModel model;

	public DefaultInstanceWriter(Marshaller marshaller, MetadataModel model) {
		this.marshaller = marshaller;
		this.model = model;
	}

	@Override
	public void write(QueryResult queryResult) throws IOException, XMLStreamException, FactoryConfigurationError {
//		XMLOutputFactory factory = XMLOutputFactory.newFactory();
		XMLOutputFactory2 factory = (XMLOutputFactory2)XMLOutputFactory2.newInstance();
		factory.configureForRobustness();
		factory.setProperty(XMLOutputFactory2.IS_REPAIRING_NAMESPACES, Boolean.TRUE);
		factory.setProperty(XMLOutputFactory2.P_AUTOMATIC_NS_PREFIX, Boolean.TRUE);
		factory.setProperty(XMLOutputFactory2.XSP_NAMESPACE_AWARE, Boolean.TRUE);

		for (Map.Entry<DocumentInfo, DocumentData> entry : generateDocumentEntityMap(queryResult).entrySet()) {
			log.info("writing document: "+entry.getKey().getId());
			DocumentData documentData = entry.getValue();

			//			XMLStreamWriter writer = factory.createXMLStreamWriter(new FileOutputStream(new File("test.xml")));
			Writer stringWriter = new StringWriter();
			XMLStreamWriter writer = factory.createXMLStreamWriter(stringWriter);
			writer = new XmlStreamWriterNamespaceFilter(writer);
			for (Map.Entry<String, String> namespaceEntry : model.getNamespaceToPrefixMap().entrySet()) {
				writer.setPrefix(namespaceEntry.getValue(), namespaceEntry.getKey());
			}
			new DocumentWriter(documentData, writer, marshaller).write();
			writer.flush();
			log.info(stringWriter.toString());
		}
	}

	private static Map<DocumentInfo, DocumentData> generateDocumentEntityMap(QueryResult queryResult) {
		Map<DocumentInfo, DocumentData> result = new HashMap<DocumentInfo, DocumentData>();

		for (Map.Entry<Key, Entity> entry : queryResult.getEntities().entrySet()) {
			Entity entity = entry.getValue();
			Collection<DocumentInfo> documentInfos = entity.getEntityInfo().getSchemaInfo().getDocumentInfos();

			if (documentInfos.size() == 1) {
				DocumentInfo documentInfo = documentInfos.iterator().next();

				DocumentData documentData;
				if (result.containsKey(documentInfo)) {
					documentData = result.get(documentInfo);
				} else {
					documentData = new DocumentData(documentInfo);
					result.put(documentInfo, documentData);
				}
				documentData.addEntity(entity);
			} else {
				// TODO: need to figure out how to handle multiple possible documents
				// TODO: perhaps provide a hint as to what to produce
				throw new UnsupportedOperationException();
			}
		}
		return result;
	}
}
