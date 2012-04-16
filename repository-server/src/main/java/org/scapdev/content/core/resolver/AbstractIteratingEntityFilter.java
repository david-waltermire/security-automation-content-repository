package org.scapdev.content.core.resolver;

import gov.nist.scap.content.model.IEntity;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

import org.scapdev.content.core.persistence.IEntityFilter;

public abstract class AbstractIteratingEntityFilter<ENTITY extends IEntity<?>> implements IEntityFilter<ENTITY> {

	@Override
	public Collection<ENTITY> filter(
			Collection<? extends ENTITY> entities) {
		Collection<ENTITY> retval = new LinkedList<ENTITY>();
		
		for (ENTITY entity : entities) {
			if (!filter(entity)) {
				retval.add(entity);
			}
		}
		if (retval.isEmpty()) {
			retval = Collections.emptyList();
		} else {
			retval = Collections.unmodifiableCollection(retval);
		}
		return retval;
	}

	protected abstract <T extends IEntity<?>> boolean filter(T entity);
}
