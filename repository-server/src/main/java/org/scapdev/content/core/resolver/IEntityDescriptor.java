package org.scapdev.content.core.resolver;

import gov.nist.scap.content.model.IEntity;

public interface IEntityDescriptor<ENTITY extends IEntity<?>> {
	ENTITY getEntity();
}
