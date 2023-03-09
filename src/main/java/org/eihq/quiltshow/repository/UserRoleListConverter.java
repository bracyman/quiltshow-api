package org.eihq.quiltshow.repository;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.AttributeConverter;

import org.eihq.quiltshow.configuration.UserRoles;

public class UserRoleListConverter implements AttributeConverter<List<UserRoles>, String> {

	@Override
	public String convertToDatabaseColumn(List<UserRoles> attribute) {
		if(attribute == null) {
			return null;
		}
		return attribute.stream().map(o -> o.toString()).collect(Collectors.joining(","));
	}

	@Override
	public List<UserRoles> convertToEntityAttribute(String dbData) {
		if(dbData == null) {
			return null;
		}
		return Stream.of(dbData.split(",")).map(s -> UserRoles.from(s)).toList();
	}

}
