package gov.nist.scap.content.model.definitions;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class DefaultSchema implements ISchemaDefinition {
	private final String id;
	private final URI namespace;
	private final Map<String, IVersioningMethodDefinition> versioningMethods = new HashMap<String, IVersioningMethodDefinition>();
	private final Map<String, IPropertyDefinition> properties = new HashMap<String, IPropertyDefinition>();

	public DefaultSchema(String id, URI namespace) {
		this.id = id;
		this.namespace = namespace;
	}

	public String getId() {
		return id;
	}

	/**
	 * @return the namespace
	 */
	public URI getNamespace() {
		return namespace;
	}

	public IVersioningMethodDefinition getVersioningMethodById(String id) {
		return versioningMethods.get(id);
	}

	public void addVersioningMethodDefinition(IVersioningMethodDefinition definition) {
		versioningMethods.put(definition.getId(), definition);
	}

	public IPropertyDefinition getPropertyDefinitionById(String id) {
		return properties.get(id);
	}

	public void addProperty(DefaultPropertyDefinition definition) {
		properties.put(definition.getId(), definition);
	}
}
