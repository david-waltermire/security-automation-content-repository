package gov.nist.scap.content.server.dto;

import java.net.URI;
import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;


public class ContentIdentiferDto {

	@JsonProperty("host")
	private String host;
	@JsonProperty("port")
	private Integer port;
	@JsonProperty("key-uri")
	private String keyUri;
	@JsonProperty("key-values")
	private List<String> keyValues;
	@JsonProperty("version")
	private String version;
	@JsonProperty("entity-id")
	private URI entityId;
	

	public URI getEntityId() {
		return entityId;
	}
	public void setEntityId(URI entityId) {
		this.entityId = entityId;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public Integer getPort() {
		return port;
	}
	public void setPort(Integer port) {
		this.port = port;
	}
	public String getKeyUri() {
		return keyUri;
	}
	public void setKeyUri(String keyUri) {
		this.keyUri = keyUri;
	}
	public List<String> getKeyValues() {
		return keyValues;
	}
	public void setKeyValues(List<String> keyValues) {
		this.keyValues = keyValues;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	
	
}
