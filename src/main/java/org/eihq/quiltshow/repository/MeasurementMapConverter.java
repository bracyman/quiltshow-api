package org.eihq.quiltshow.repository;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.AttributeConverter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;


@Slf4j
public class MeasurementMapConverter implements AttributeConverter<Map<String,Double>, String> {

	ObjectMapper mapper = new ObjectMapper();
	
	@Override
	public String convertToDatabaseColumn(Map<String,Double> attribute) {
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
	public Map<String,Double> convertToEntityAttribute(String dbData) {
		if(dbData == null) {
			return null;
		}
		
		try {
			TypeReference<HashMap<String, Double>> doubleTypeReference = new TypeReference<HashMap<String, Double>>() { };
			Map<String, Double> map = mapper.readValue(dbData, doubleTypeReference);
			return map;
		} catch (JsonProcessingException e) {
			log.error("Failed to read map from database value: " + dbData, e);
			return null;
		}
	}
	
	public static void main(String args[]) {
		MeasurementMapConverter converter = new MeasurementMapConverter();
		
		Map<String, Double> input = new HashMap<String, Double>();
		input.put("length", 43.2);
		input.put("width", 12.0);
		input.put("height", null);
		
		String json = converter.convertToDatabaseColumn(input);
		System.out.println(String.format("ToString: %s", json));
		
		Map<String,Double> deserialized = converter.convertToEntityAttribute(json);
		deserialized.keySet().forEach(k -> System.out.println(String.format("%s: %f", k, deserialized.get(k))));
	}

}
