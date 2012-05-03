package org.scapdev.content.core.query.json;

import java.io.IOException;
import java.io.StringWriter;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JSONSerializer<T> {
	private final ObjectMapper mapper;

	public JSONSerializer() {
		mapper = new InternalObjectMapper();
		mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
//		mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
	}

	public String serialize(T obj) throws IOException {
		StringWriter writer = new StringWriter();

//		mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
//		mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
//		mapper.enable(SerializationFeature.WRAP_ROOT_VALUE);

		mapper.writeValue(writer, obj);

		return writer.toString();
	}

	public T deserialize(String json, Class<T> clazz) throws JsonParseException, JsonMappingException, IOException {
//		mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
//		mapper.enable(DeserializationFeature.UNWRAP_ROOT_VALUE);
		return mapper.readValue(json, clazz);
	}

	private static class InternalObjectMapper extends ObjectMapper {

		@Override
		public boolean canDeserialize(JavaType type) {
			return super.canDeserialize(type);
		}
	}
}
