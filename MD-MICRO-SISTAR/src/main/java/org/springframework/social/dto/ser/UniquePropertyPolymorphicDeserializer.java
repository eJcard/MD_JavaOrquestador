package org.springframework.social.dto.ser;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Deserializes documents without a specific field designated for Polymorphic Type
 * identification, when the document contains a field registered to be unique to that type
 *
 * @author robin howlett (https://github.com/robinhowlett)
 */
public class UniquePropertyPolymorphicDeserializer<T> extends StdDeserializer<T> {

	private static final long serialVersionUID = 1L;
	
	// the registry of unique field names to Class types
	private final Map<String, Class<? extends T>> registry;
	
	public UniquePropertyPolymorphicDeserializer(Class<T> clazz) {
		super(clazz);
		registry = new HashMap<>();
	}
	
	public void register(String uniqueProperty, Class<? extends T> clazz) {
		registry.put(uniqueProperty, clazz);
	}

	/* (non-Javadoc)
	 * @see com.fasterxml.jackson.databind.JsonDeserializer#deserialize(com.fasterxml.jackson.core.JsonParser, com.fasterxml.jackson.databind.DeserializationContext)
	 */
	@Override
	public T deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
		Class<? extends T> clazz = null;
		
		ObjectMapper mapper = (ObjectMapper) jp.getCodec();  
		ObjectNode obj = mapper.readTree(jp);
		Iterator<Entry<String, JsonNode>> elementsIterator = obj.fields();
		
		while (elementsIterator.hasNext()) {  
			Entry<String, JsonNode> element = elementsIterator.next();  
			String name = element.getKey();  
			if (!name.equals("*") && registry.containsKey(name)) {  
				clazz = registry.get(name);  
				break;  
			}
		}

		if(registry.containsKey("*") && clazz == null){
			clazz = registry.get("*");
		}
		
		if (clazz == null) {
			ctxt.reportBadDefinition((JavaType)null, "No registered unique properties found for polymorphic deserialization");
		}
		// mapper.addMixIn(clazz, null);
		return mapper.readValue(obj.toString(), clazz);
	}
}