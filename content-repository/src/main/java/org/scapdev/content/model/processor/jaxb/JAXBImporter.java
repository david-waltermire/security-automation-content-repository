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
package org.scapdev.content.model.processor.jaxb;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;

import org.scapdev.content.core.persistence.ContentPersistenceManager;
import org.scapdev.content.model.MetadataModel;
import org.scapdev.content.model.processor.ImportException;
import org.scapdev.content.model.processor.Importer;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

public class JAXBImporter implements Importer {
	private final JAXBEntityProcessor jaxbEntityProcessor;
	

	JAXBImporter(JAXBEntityProcessor jaxbEntityProcessor) {
		this.jaxbEntityProcessor = jaxbEntityProcessor;
	}

	public MetadataModel getMetadataModel() {
		return jaxbEntityProcessor.getMetadataModel();
	}

	public JAXBEntityProcessor getEntityProcessor() {
		return jaxbEntityProcessor;
	}

	private Unmarshaller createUnmarshaller() throws JAXBException {
		return jaxbEntityProcessor.getMetadataModel().getJAXBContext().createUnmarshaller();
		
	}

	private ImportData importObject(Object obj) {
		return importInternal(obj);
	}

	private <T> ImportData importJAXBElement(JAXBElement<T> element) {
		return importInternal(element);
	}

	private ImportData importInternal(Object obj) {
		ImportVisitor visitor = new ImportVisitor(obj, jaxbEntityProcessor, jaxbEntityProcessor.getMetadataModel());
		ImportData data = visitor.importContent();
		data.validateRelationships();
		importData(data);
		return data;
	}

	private void importData(ImportData data) {
		ContentPersistenceManager manager = getEntityProcessor().getPersistenceManager();
		manager.storeEntities(data.getEntities(), getEntityProcessor().getMetadataModel());
//		for (Entity entity : data.getEntities()) {
//			manager.storeEntity(entity, getEntityProcessor().getMetadataModel());
//		}
	}

	@Override
	public ImportData read(File file) throws ImportException {
		Object obj;
		try {
			Unmarshaller unmarshaller = createUnmarshaller();
			obj = unmarshaller.unmarshal(file);
		} catch (JAXBException e) {
			throw new ImportException(e);
		}
		return importObject(obj);
	}

	@Override
	public ImportData read(InputStream is) throws ImportException {
		Object obj;
		try {
			Unmarshaller unmarshaller = createUnmarshaller();
			obj = unmarshaller.unmarshal(is);
		} catch (JAXBException e) {
			throw new ImportException(e);
		}
		return importObject(obj);
	}

	@Override
	public ImportData read(Reader reader) throws ImportException {
		Object obj;
		try {
			Unmarshaller unmarshaller = createUnmarshaller();
			obj = unmarshaller.unmarshal(reader);
		} catch (JAXBException e) {
			throw new ImportException(e);
		}
		return importObject(obj);
	}

	@Override
	public ImportData read(URL url) throws ImportException {
		Object obj;
		try {
			Unmarshaller unmarshaller = createUnmarshaller();
			obj = unmarshaller.unmarshal(url);
		} catch (JAXBException e) {
			throw new ImportException(e);
		}
		return importObject(obj);
	}

	@Override
	public ImportData read(InputSource is) throws ImportException {
		Object obj;
		try {
			Unmarshaller unmarshaller = createUnmarshaller();
			obj = unmarshaller.unmarshal(is);
		} catch (JAXBException e) {
			throw new ImportException(e);
		}
		return importObject(obj);
	}

	@Override
	public ImportData read(Node node) throws ImportException {
		Object obj;
		try {
			Unmarshaller unmarshaller = createUnmarshaller();
			obj = unmarshaller.unmarshal(node);
		} catch (JAXBException e) {
			throw new ImportException(e);
		}
		return importObject(obj);
	}

	@Override
	public <T> ImportData read(Node node, Class<T> declaredType) throws ImportException {
		JAXBElement<T> element;
		try {
			Unmarshaller unmarshaller = createUnmarshaller();
			element = unmarshaller.unmarshal(node, declaredType);
		} catch (JAXBException e) {
			throw new ImportException(e);
		}
		return importJAXBElement(element);
	}

	@Override
	public ImportData read(Source source) throws ImportException {
		Object obj;
		try {
			Unmarshaller unmarshaller = createUnmarshaller();
			obj = unmarshaller.unmarshal(source);
		} catch (JAXBException e) {
			throw new ImportException(e);
		}
		return importObject(obj);
	}

	@Override
	public <T> ImportData read(Source source, Class<T> declaredType) throws ImportException {
		JAXBElement<T> element;
		try {
			Unmarshaller unmarshaller = createUnmarshaller();
			element = unmarshaller.unmarshal(source, declaredType);
		} catch (JAXBException e) {
			throw new ImportException(e);
		}
		return importJAXBElement(element);
	}

	@Override
	public ImportData read(XMLStreamReader reader) throws ImportException {
		Object obj;
		try {
			Unmarshaller unmarshaller = createUnmarshaller();
			obj = unmarshaller.unmarshal(reader);
		} catch (JAXBException e) {
			throw new ImportException(e);
		}
		return importObject(obj);
	}

	@Override
	public <T> ImportData read(XMLStreamReader reader, Class<T> declaredType) throws ImportException {
		JAXBElement<T> element;
		try {
			Unmarshaller unmarshaller = createUnmarshaller();
			element = unmarshaller.unmarshal(reader, declaredType);
		} catch (JAXBException e) {
			throw new ImportException(e);
		}
		return importJAXBElement(element);
	}

	@Override
	public ImportData read(XMLEventReader reader) throws ImportException {
		Object obj;
		try {
			Unmarshaller unmarshaller = createUnmarshaller();
			obj = unmarshaller.unmarshal(reader);
		} catch (JAXBException e) {
			throw new ImportException(e);
		}
		return importObject(obj);
	}

	@Override
	public <T> ImportData read(XMLEventReader reader, Class<T> declaredType) throws ImportException {
		JAXBElement<T> element;
		try {
			Unmarshaller unmarshaller = createUnmarshaller();
			element = unmarshaller.unmarshal(reader, declaredType);
		} catch (JAXBException e) {
			throw new ImportException(e);
		}
		return importJAXBElement(element);
	}
}
