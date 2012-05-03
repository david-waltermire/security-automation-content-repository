package gov.nist.sparql.builder;

import java.util.List;

public interface IProperty {
	IVerb getVerb();
	List<IObject> getObjects();
}
