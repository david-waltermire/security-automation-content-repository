package gov.nist.scap.content.server.dto;

import org.codehaus.jackson.annotate.JsonProperty;


public class RetrieveRequestDto {

	@JsonProperty("identifier")
	private ContentIdentiferDto identifier;
	@JsonProperty("metadata")
	private Boolean metadata;
	@JsonProperty("depth")
	private Integer depth;
	
	public ContentIdentiferDto getIdentifier() {
		return identifier;
	}
	public void setIdentifier(ContentIdentiferDto identifier) {
		this.identifier = identifier;
	}
	public Boolean getMetadata() {
		return metadata;
	}
	public void setMetadata(Boolean metadata) {
		this.metadata = metadata;
	}
	public Integer getDepth() {
		return depth;
	}
	public void setDepth(Integer depth) {
		this.depth = depth;
	}
	
	
}
