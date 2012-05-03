package gov.nist.sparql.builder;

import java.net.URI;

public interface IPrefix {
	String getLabel();
	URI getUri();
	IResource resource(String string);
}
