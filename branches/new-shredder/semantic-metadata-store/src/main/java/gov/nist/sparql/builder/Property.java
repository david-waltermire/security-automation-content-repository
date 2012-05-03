package gov.nist.sparql.builder;

import java.util.Arrays;
import java.util.List;

public class Property implements IProperty {
	public static Property property(IVerb verb, IObject... objects) {
		return new Property(verb, objects);
	}

	private final IVerb verb;
	private final List<IObject> objects;

	public Property(IVerb verb, IObject...objects) {
		this(verb, Arrays.asList(objects));
	}

	public Property(IVerb verb, List<IObject> objects) {
		this.verb = verb;
		this.objects = objects;
	}

	@Override
	public IVerb getVerb() {
		return verb;
	}

	@Override
	public List<IObject> getObjects() {
		return objects;
	}

}
