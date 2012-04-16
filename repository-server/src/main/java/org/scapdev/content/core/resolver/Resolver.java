package org.scapdev.content.core.resolver;

import gov.nist.scap.content.model.IKey;
import gov.nist.scap.content.model.IKeyedEntity;
import gov.nist.scap.content.model.IVersion;
import gov.nist.scap.content.model.definitions.ProcessingException;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.scapdev.content.core.persistence.IEntityFilter;
import org.scapdev.content.core.resolver.retriever.IRetriever;

public class Resolver implements IResolver {
	private final List<IRetriever> retrievers = new LinkedList<IRetriever>();

	public Resolver() {
	}

	public Resolver(Resolver resolver) {
		this.retrievers.addAll(resolver.getRetievers());
	}

	protected List<? extends IRetriever> getRetievers() {
		return Collections.unmodifiableList(retrievers);
	}

	public void addRetriever(IRetriever retriever) {
		this.retrievers.add(retriever);
	}

	public Collection<? extends IKeyedEntity<?>> resolve(IKey key,
			IVersion version, IEntityFilter<IKeyedEntity<?>> filter) throws ProcessingException {

		Collection<? extends IKeyedEntity<?>> retval = null;
		for (IRetriever retriever : retrievers) {
			retval = retriever.retrieve(key, version, filter);
			if (retval != null) {
				break;
			}
		}

		if (retval == null || retval.isEmpty()) {
			retval = Collections.emptyList();
		} else {
			retval = Collections.unmodifiableCollection(retval);
		}
		return retval;
	}
}
