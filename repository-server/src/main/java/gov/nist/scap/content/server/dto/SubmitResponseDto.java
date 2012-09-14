package gov.nist.scap.content.server.dto;

import java.net.URI;
import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;

public class SubmitResponseDto {
	
	@JsonProperty("status")
	private STATUS status;
	@JsonProperty("new-entity")
	private URI newEntity;
	@JsonProperty("entity-statuses")
	private List<SubmitEntityResponseDto> entityList;
	
	public static enum STATUS {
		SUCCESS, FAILED;
	}
	
	
	public STATUS getStatus() {
		return status;
	}


	public void setStatus(STATUS status) {
		this.status = status;
	}


	public URI getNewEntity() {
		return newEntity;
	}


	public void setNewEntity(URI newEntity) {
		this.newEntity = newEntity;
	}


	public List<SubmitEntityResponseDto> getEntityList() {
		return entityList;
	}


	public void setEntityList(List<SubmitEntityResponseDto> entityList) {
		this.entityList = entityList;
	}


	public static class SubmitEntityResponseDto {
		@JsonProperty("key-uri")
		private String keyUri;
		@JsonProperty("fields")
		private List<Field> fields;
		@JsonProperty("version")
		private String version;
		@JsonProperty("status")
		private STATUS status;

		public List<Field> getFields() {
			return fields;
		}

		public void setFields(List<Field> fields) {
			this.fields = fields;
		}

		public STATUS getStatus() {
			return status;
		}

		public void setStatus(STATUS status) {
			this.status = status;
		}

		public String getKeyUri() {
			return keyUri;
		}

		public void setKeyUri(String keyUri) {
			this.keyUri = keyUri;
		}

		public String getVersion() {
			return version;
		}

		public void setVersion(String version) {
			this.version = version;
		}

		public static enum STATUS {
			IDENTICAL, SIMILAR, DIFFERENT;
		}

		public static class Field {
			@JsonProperty("name")
			private String name;
			@JsonProperty("value")
			private String value;

			public String getName() {
				return name;
			}

			public void setName(String name) {
				this.name = name;
			}

			public String getValue() {
				return value;
			}

			public void setValue(String value) {
				this.value = value;
			}

		}
	}
}
