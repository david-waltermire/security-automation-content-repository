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
package org.scapdev.content.core.writer.jaxb;
//
//import java.io.IOException;
//import java.io.OutputStream;
//import java.io.OutputStreamWriter;
//import java.io.Writer;
//import java.util.List;
//import java.util.Map;
//
//import javax.xml.bind.JAXBException;
//import javax.xml.bind.Marshaller;
//
//import org.scapdev.content.core.model.Key;
//import org.scapdev.content.core.model.MutableSCAPModel;
//import org.scapdev.content.core.model.context.instance.FragmentHandle;
//import org.scapdev.content.core.query.QueryResult;
//import org.scapdev.content.core.writer.InstanceWriter;
//
//public class JAXBInstanceWriter implements InstanceWriter {
//	@SuppressWarnings("unused")
//	private final MutableSCAPModel model;
//	private final Marshaller marshaller;
//	private final Writer writer;
//
//	public JAXBInstanceWriter(MutableSCAPModel model, OutputStream os) throws JAXBException {
//		this.model = model;
//		marshaller = model.getJAXBContext().createMarshaller();
//		writer = new OutputStreamWriter(os);
//	}
//
//	public void write(QueryResult queryResult) throws IOException {
//		QueryResult result = queryResult;
//		Map<Key, FragmentHandle> handles = result.getFragmentHandles();
//		// Generate Documents
//		List<DocumentWriter> documents = generateDocuments(handles);
//		for (DocumentWriter writer : documents) {
//			writer.write(marshaller);
//		}
//		try {
//			for (FragmentHandle handle : handles.values()) {
//				marshaller.marshal(handle.getInstance(), writer);
//			}
//		} catch (JAXBException e) {
//			throw new IOException(e);
//		}
//	}
//
//	private List<DocumentWriter> generateDocuments(Map<Key, FragmentHandle> handles) {
//		for (@SuppressWarnings("unused") FragmentHandle handle : handles.values()) {
////			handle.getFragment().
//		}
//		return null;
//	}
//}
