package org.eihq.quiltshow.repository;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.AttributeConverter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;


@Slf4j
public class StringMapConverter implements AttributeConverter<Map<String,String>, String> {

	ObjectMapper mapper = new ObjectMapper();
	
	@Override
	public String convertToDatabaseColumn(Map<String,String> attribute) {
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
	public Map<String,String> convertToEntityAttribute(String dbData) {
		if(dbData == null) {
			return null;
		}
		
		try {
			TypeReference<HashMap<String, String>> stringTypeReference = new TypeReference<HashMap<String, String>>() { };
			Map<String, String> map = mapper.readValue(dbData, stringTypeReference);
			return map;
		} catch (JsonProcessingException e) {
			log.error("Failed to read map from database value: " + dbData, e);
			return null;
		}
	}
}
