package org.eihq.quiltshow.model;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.Convert;

import org.eihq.quiltshow.repository.StringListConverter;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
public class SearchField implements Serializable {
	
	private static final long serialVersionUID = 1L;

	public enum MatchType {		
		CONTAINS("contains"),
		EQUALS("equals"),
		GREATER_THAN("gt"),
		LESS_THAN("lt"),
		ONE_OF("oneOf"),
		ALL_OF("allOf"),
		BETWEEN("between");
		
		public String value;
		
		MatchType(String value) {
			this.value = value;
		}
		
		public static MatchType from(String str) {
			for (MatchType m : values()) {
		        if (m.value.equalsIgnoreCase(str)) {
		            return m;
		        }
		    }
		    return null;
		}
	}

	
	String matches;
	MatchType matchType;
	
    @Convert(converter = StringListConverter.class)
	List<String> categories;

		
	@JsonIgnore
	public boolean isEmpty() {
		return !StringUtils.hasText(getMatches());
	}
	
	@JsonIgnore
	public Integer getMatchesInt() {
		if(getMatches() == null) {
			return null;
		}
		return Integer.valueOf(getMatches());
	}
	
	@JsonIgnore
	public Double getMatchesNumber() {
		if(getMatches() == null) {
			return null;
		}
		return Double.valueOf(getMatches());
	}
	
	@JsonIgnore
	public Boolean getMatchesBoolean() {
		if(getMatches() == null) {
			return null;
		}
		return "true".equalsIgnoreCase(getMatches());
	}
	
	@JsonIgnore
	public Double getMatchesRangeMin() {
		if(getMatches() == null) {
			return null;
		}
		double first = Double.valueOf(getMatches().split(",")[0]);
		double last = Double.valueOf(getMatches().split(",")[1]);
		
		return Math.min(first, last);
	}
	
	@JsonIgnore
	public Double getMatchesRangeMax() {
		if(getMatches() == null) {
			return null;
		}
		double first = Double.valueOf(getMatches().split(",")[0]);
		double last = Double.valueOf(getMatches().split(",")[1]);
		
		return Math.max(first, last);
	}
	
	@JsonIgnore
	public List<String> getMatchesStringList() {
		if(getMatches() == null) {
			return null;
		}
		return Arrays.asList(getMatches().split(","));
	}
	
	@JsonIgnore
	public List<Integer> getMatchesIntegerList() {
		if(getMatches() == null) {
			return null;
		}
		return Stream.of(getMatches().split(",")).map(m -> Integer.valueOf(m)).collect(Collectors.toList());
	}	
	
	@JsonIgnore
	public List<Long> getMatchesLongList() {
		if(getMatches() == null) {
			return null;
		}
		return Stream.of(getMatches().split(",")).map(m -> Long.valueOf(m)).collect(Collectors.toList());
	}
}
