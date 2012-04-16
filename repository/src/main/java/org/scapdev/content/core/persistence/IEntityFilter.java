package org.scapdev.content.core.persistence;

import gov.nist.scap.content.model.IEntity;

import java.util.Collection;

public interface IEntityFilter<ENTITY extends IEntity<?>> {
	Collection<ENTITY> filter(Collection<? extends ENTITY> entities);
}
