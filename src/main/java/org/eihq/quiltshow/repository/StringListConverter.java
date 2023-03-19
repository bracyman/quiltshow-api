package org.eihq.quiltshow.repository;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.AttributeConverter;

public class StringListConverter implements AttributeConverter<List<String>, String> {

	@Override
	public String convertToDatabaseColumn(List<String> attribute) {
		if(attribute == null) {
			return null;
		}
		return attribute.stream().collect(Collectors.joining(","));
	}

	@Override
	public List<String> convertToEntityAttribute(String dbData) {
		if(dbData == null) {
			return null;
		}
		return Arrays.asList(dbData.split(","));
	}

}
