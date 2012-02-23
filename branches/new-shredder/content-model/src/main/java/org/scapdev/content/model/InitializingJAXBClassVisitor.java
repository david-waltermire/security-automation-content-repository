/**
 * 
 */
package org.scapdev.content.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.scapdev.jaxb.reflection.model.JAXBClass;
import org.scapdev.jaxb.reflection.model.JAXBModel;
import org.scapdev.jaxb.reflection.model.JAXBProperty;
import org.scapdev.jaxb.reflection.model.visitor.DefaultModelVisitor;
import org.scapdev.jaxb.reflection.model.visitor.JAXBClassVisitor;

class InitializingJAXBClassVisitor implements JAXBClassVisitor {
	private final JAXBModel model;
	private final Map<String, BindingInfo<org.scapdev.content.annotation.Entity>> entities;
	private final Map<String, BindingInfo<org.scapdev.content.annotation.SchemaDocument>> documents;
	private final Map<String, KeyBindingInfo> keys;
	private final Map<String, KeyRefBindingInfo> keyRefs;
	private final Map<String, IndirectBindingInfo> indirects;

	InitializingJAXBClassVisitor(JAXBModel model) {
		this.model = model;
		entities = new HashMap<String, BindingInfo<org.scapdev.content.annotation.Entity>>();
		documents = new HashMap<String, BindingInfo<org.scapdev.content.annotation.SchemaDocument>>();
		keys = new HashMap<String, KeyBindingInfo>();
		keyRefs = new HashMap<String, KeyRefBindingInfo>();
		indirects = new HashMap<String, IndirectBindingInfo>();
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

	public IndirectBindingInfo getIndirectBindingInfo(String id) {
		return indirects.get(id);
	}

	/**
	 * Visit each JAXBClass and identify each annotation type if they exist.
	 */
	@Override
	public void visit(JAXBClass jaxbClass) {
		Class<?> clazz = jaxbClass.getType();
		org.scapdev.content.annotation.Entity entity = clazz.getAnnotation(org.scapdev.content.annotation.Entity.class);
		if (entity != null) {
			String id = entity.id();
			
			BindingInfo<org.scapdev.content.annotation.Entity> bindingInfo = new DefaultBindingInfo<org.scapdev.content.annotation.Entity>(id, entity, jaxbClass);
			assert(!entities.containsKey(id));
			entities.put(id, bindingInfo);
		}
		
		org.scapdev.content.annotation.SchemaDocument document = clazz.getAnnotation(org.scapdev.content.annotation.SchemaDocument.class);
		if (document != null) {
			String id = document.id();
			BindingInfo<org.scapdev.content.annotation.SchemaDocument> bindingInfo = new DefaultBindingInfo<org.scapdev.content.annotation.SchemaDocument>(id, document, jaxbClass);
			assert(!documents.containsKey(id));
			documents.put(id, bindingInfo);
		}

		org.scapdev.content.annotation.Key key = clazz.getAnnotation(org.scapdev.content.annotation.Key.class);
		if (key != null) {
			String id = key.id();

			KeyIdentifyingPropertyPathModelVisitor visitor = new KeyIdentifyingPropertyPathModelVisitor(key, jaxbClass, model);
			visitor.visit();
			Map<String, List<JAXBProperty>> propertyMap = visitor.getPropertyMap();

			KeyBindingInfo bindingInfo = new KeyBindingInfoImpl(id, key, propertyMap, jaxbClass);
			assert(!keys.containsKey(id));
			keys.put(id, bindingInfo);
		}

		org.scapdev.content.annotation.KeyRef keyRef = clazz.getAnnotation(org.scapdev.content.annotation.KeyRef.class);
		if (keyRef != null) {
			String id = keyRef.id();

			KeyRefIdentifyingPropertyPathModelVisitor visitor = new KeyRefIdentifyingPropertyPathModelVisitor(keyRef, jaxbClass, model);
			visitor.visit();
			Map<String, List<JAXBProperty>> propertyMap = visitor.getPropertyMap();

			KeyRefBindingInfo bindingInfo = new KeyRefBindingInfoImpl(id, keyRef, propertyMap, jaxbClass);
			assert(!keyRefs.containsKey(id));
			keyRefs.put(id, bindingInfo);
		}

		org.scapdev.content.annotation.Indirect indirect = clazz.getAnnotation(org.scapdev.content.annotation.Indirect.class);
		if (indirect != null) {
			String id = indirect.id();

			IndirectRelationshipIdentifyingPropertyPathModelVisitor visitor = new IndirectRelationshipIdentifyingPropertyPathModelVisitor(indirect, model);
			visitor.visit(jaxbClass);

			IndirectBindingInfo bindingInfo = new IndirectBindingInfoImpl(id, indirect, visitor, jaxbClass);
			assert(!keyRefs.containsKey(id));
			indirects.put(id, bindingInfo);
		}

		// Iterate over class properties to find additional annotations
		InternalModelVisitor visitor = new InternalModelVisitor(jaxbClass, model);
		visitor.visit(jaxbClass);
	}

	private class InternalModelVisitor extends DefaultModelVisitor {
		private final JAXBClass jaxbClass;

		public InternalModelVisitor(JAXBClass jaxbClass, JAXBModel model) {
			super(model);
			this.jaxbClass = jaxbClass;
		}

		/**
		 * @return the jaxbClass
		 */
		public JAXBClass getJaxbClass() {
			return jaxbClass;
		}

		@Override
		public boolean beforeJAXBClass(JAXBClass jaxbClass) {
			// Limit visitor to this class only
			if (!getJaxbClass().equals(jaxbClass)) {
				return false;
			}
			// Process properties on this JAXB class
			return true;
		}

		@Override
		public boolean beforeJAXBProperty(JAXBProperty property) {
			org.scapdev.content.annotation.Indirect indirect = property.getAnnotation(org.scapdev.content.annotation.Indirect.class);
			if (indirect != null) {
				String id = indirect.id();

				IndirectRelationshipIdentifyingPropertyPathModelVisitor visitor = new IndirectRelationshipIdentifyingPropertyPathModelVisitor(indirect, model);
				visitor.visit(property);

				IndirectBindingInfo bindingInfo = new IndirectBindingInfoImpl(id, indirect, visitor, property);
				assert(!keyRefs.containsKey(id));
				indirects.put(id, bindingInfo);
			}

			return false;
		}

		
	}
}