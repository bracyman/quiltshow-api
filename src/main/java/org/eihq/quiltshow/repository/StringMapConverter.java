package org.eihq.quiltshow.repository;

import java.util.Map;

import javax.persistence.AttributeConverter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;


@Slf4j
public class StringMapConverter<K,V> implements AttributeConverter<Map<K,V>, String> {

	ObjectMapper mapper = new ObjectMapper();
	
	@Override
	public String convertToDatabaseColumn(Map<K,V> attribute) {
		if(attribute == null) {
			return null;
		}
		
		try {
			return mapper.writeValueAsString(attribute);
		} catch (JsonProcessingException e) {
			log.error("Failed to convert map tp database value", e);
			return null;
		}
	}

	@Override
	public Map<K,V> convertToEntityAttribute(String dbData) {
		if(dbData == null) {
			return null;
		}
		
		try {
			Map<K,V> map = mapper.readValue(dbData, Map.class);
			return map;
		} catch (JsonProcessingException e) {
			log.error("Failed to read map from database value: " + dbData, e);
			return null;
		}
	}

}
