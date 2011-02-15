/**
 * 
 */
package org.scapdev.content.model.jaxb;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.scapdev.jaxb.reflection.model.JAXBClass;
import org.scapdev.jaxb.reflection.model.JAXBProperty;
import org.scapdev.jaxb.reflection.model.jaxb.JAXBModel;
import org.scapdev.jaxb.reflection.model.visitor.JAXBClassVisitor;

class InitializingJAXBClassVisitor implements JAXBClassVisitor {
	private final JAXBModel model;
	private final Map<String, BindingInfo<org.scapdev.content.annotation.Entity>> entities;
	private final Map<String, BindingInfo<org.scapdev.content.annotation.SchemaDocument>> documents;
	private final Map<String, KeyBindingInfo> keys;
	private final Map<String, KeyRefBindingInfo> keyRefs;

	InitializingJAXBClassVisitor(JAXBModel model) {
		this.model = model;
		entities = new HashMap<String, BindingInfo<org.scapdev.content.annotation.Entity>>();
		documents = new HashMap<String, BindingInfo<org.scapdev.content.annotation.SchemaDocument>>();
		keys = new HashMap<String, KeyBindingInfo>();
		keyRefs = new HashMap<String, KeyRefBindingInfo>();
	}

	protected BindingInfo<org.scapdev.content.annotation.Entity> getEntityBindingInfo(String id) {
		return entities.get(id);
	}

	protected BindingInfo<org.scapdev.content.annotation.SchemaDocument> getDocumentBindingInfo(String id) {
		return documents.get(id);
	}

	protected KeyBindingInfo getKeyBindingInfo(String id) {
		return keys.get(id);
	}

	protected KeyRefBindingInfo getKeyRefBindingInfo(String id) {
		return keyRefs.get(id);
	}

	protected BindingInfo<org.scapdev.content.annotation.Field> getFieldBindingInfo(String id) {
		return null;
	}

	/**
	 * Visit each JAXBClass and identify each annotation type if they exist.
	 */
	@Override
	public void visit(JAXBClass typeInfo) {
		Class<?> clazz = typeInfo.getType();
		org.scapdev.content.annotation.Entity entity = clazz.getAnnotation(org.scapdev.content.annotation.Entity.class);
		if (entity != null) {
			String id = entity.id();
			
			BindingInfo<org.scapdev.content.annotation.Entity> bindingInfo = new DefaultBindingInfo<org.scapdev.content.annotation.Entity>(id, entity, typeInfo);
			assert(!entities.containsKey(id));
			entities.put(id, bindingInfo);
		}
		
		org.scapdev.content.annotation.SchemaDocument document = clazz.getAnnotation(org.scapdev.content.annotation.SchemaDocument.class);
		if (document != null) {
			String id = document.id();
			BindingInfo<org.scapdev.content.annotation.SchemaDocument> bindingInfo = new DefaultBindingInfo<org.scapdev.content.annotation.SchemaDocument>(id, document, typeInfo);
			assert(!documents.containsKey(id));
			documents.put(id, bindingInfo);
		}

		org.scapdev.content.annotation.Key key = clazz.getAnnotation(org.scapdev.content.annotation.Key.class);
		if (key != null) {
			String id = key.id();

			KeyIdentifyingPropertyPathModelVisitor keyIdVisitor = new KeyIdentifyingPropertyPathModelVisitor(key, typeInfo, model);
			keyIdVisitor.visit();
			Map<String, List<JAXBProperty>> propertyMap = keyIdVisitor.getPropertyMap();

			KeyBindingInfo bindingInfo = new KeyBindingInfoImpl(id, key, propertyMap, typeInfo);
			assert(!keys.containsKey(id));
			keys.put(id, bindingInfo);
		}

		org.scapdev.content.annotation.KeyRef keyRef = clazz.getAnnotation(org.scapdev.content.annotation.KeyRef.class);
		if (keyRef != null) {
			String id = keyRef.id();

			KeyRefIdentifyingPropertyPathModelVisitor keyRefIdVisitor = new KeyRefIdentifyingPropertyPathModelVisitor(keyRef, typeInfo, model);
			keyRefIdVisitor.visit();
			Map<String, List<JAXBProperty>> propertyMap = keyRefIdVisitor.getPropertyMap();

			KeyRefBindingInfo bindingInfo = new KeyRefBindingInfoImpl(id, keyRef, propertyMap, typeInfo);
			assert(!keyRefs.containsKey(id));
			keyRefs.put(id, bindingInfo);
		}
	}
}