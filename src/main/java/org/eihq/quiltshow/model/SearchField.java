package org.eihq.quiltshow.model;

import java.util.List;
import java.util.stream.Stream;

import org.springframework.util.StringUtils;

import lombok.Data;

@Data
public class SearchField {
	
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

	
	public boolean isEmpty() {
		return !StringUtils.hasText(getMatches());
	}
	
	public Integer getMatchesInt() {
		if(getMatches() == null) {
			return null;
		}
		return Integer.valueOf(getMatches());
	}
	
	public Double getMatchesNumber() {
		if(getMatches() == null) {
			return null;
		}
		return Double.valueOf(getMatches());
	}
	
	public Boolean getMatchesBoolean() {
		if(getMatches() == null) {
			return null;
		}
		return "true".equalsIgnoreCase(getMatches());
	}
	
	public Double getMatchesRangeMin() {
		if(getMatches() == null) {
			return null;
		}
		double first = Double.valueOf(getMatches().split(",")[0]);
		double last = Double.valueOf(getMatches().split(",")[1]);
		
		return Math.min(first, last);
	}
	
	public Double getMatchesRangeMax() {
		if(getMatches() == null) {
			return null;
		}
		double first = Double.valueOf(getMatches().split(",")[0]);
		double last = Double.valueOf(getMatches().split(",")[1]);
		
		return Math.max(first, last);
	}
	
	public List<String> getMatchesStringList() {
		if(getMatches() == null) {
			return null;
		}
		return List.of(getMatches().split(","));
	}
	
	public List<Integer> getMatchesIntegerList() {
		if(getMatches() == null) {
			return null;
		}
		return Stream.of(getMatches().split(",")).map(m -> Integer.valueOf(m)).toList();
	}	
	
	public List<Long> getMatchesLongList() {
		if(getMatches() == null) {
			return null;
		}
		return Stream.of(getMatches().split(",")).map(m -> Long.valueOf(m)).toList();
	}
}
