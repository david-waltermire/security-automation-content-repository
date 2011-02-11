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
package org.scapdev.content.core.reader.jaxb;
//
//import java.net.URL;
//import java.util.LinkedList;
//import java.util.List;
//import java.util.Map;
//
//import org.scapdev.content.core.model.AbstractContextDocumentWalker;
//import org.scapdev.content.core.model.Key;
//import org.scapdev.content.core.model.SCAPModel;
//import org.scapdev.content.core.model.context.Fragment;
//import org.scapdev.content.core.model.context.instance.CompositeReference;
//import org.scapdev.content.core.model.context.instance.EntityHandle;
//import org.scapdev.content.core.model.context.instance.JAXBFragmentHandle;
//import org.scapdev.content.core.model.context.instance.KeyReference;
//import org.scapdev.content.core.reader.ComponentIndex;
//
//public class JAXBComponentIndex implements ComponentIndex {
//	private final URL componentURL;
//	private final SCAPModel model;
//	private Object rootDocumentNode;
//	private List<EntityHandle> fragmentHandles = new LinkedList<EntityHandle>();
//
//	public JAXBComponentIndex(URL componentURL, SCAPModel model) {
//		this.componentURL = componentURL;
//		this.model = model;
//	}
//
//	public URL getComponentURL() {
//		return componentURL;
//	}
//
//	public Object getRootDocumentNode() {
//		return rootDocumentNode;
//	}
//
//	public void setRootDocumentNode(Object node) {
//		this.rootDocumentNode = node;
//	}
//
//	public List<EntityHandle> getFragmentHandles() {
//		return fragmentHandles;
//	}
//
//	protected void addFragmentHandle(EntityHandle handle) {
//		fragmentHandles.add(handle);
//	}
//
//	public void index() {
//		// TODO: process documents
//		new IndexingDocumentWalker(model).walk(this.getRootDocumentNode());
//	}
//
//	class IndexingDocumentWalker extends AbstractContextDocumentWalker {
//
//		public IndexingDocumentWalker(SCAPModel model) {
//			super(model);
//		}
//
//		@Override
//		protected void handleFragment(Object instance, Fragment fragment,
//				Key key, Map<Key, KeyReference> keyReferences,
//				Map<Key, CompositeReference> compositeReferences) {
//			EntityHandle handle = new JAXBFragmentHandle(instance, fragment, key, keyReferences, compositeReferences);
//			addFragmentHandle(handle);
//		}
//	}
//}
