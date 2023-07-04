package org.eihq.quiltshow.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eihq.quiltshow.repository.QuiltSearchResult;

import lombok.Data;

@Data
public class ReportResult {
	
	Report report;
	
	List<Map<String,Object>> results;
	
	boolean demo = false;

	public ReportResult() {
		// nothing to do
	}

	public ReportResult(Report report, List<QuiltSearchResult> results) {
		this.report = report;
		this.results = results.stream().map(r -> {
			Map<String,Object> m = new HashMap<>();
			m.put("quilt", r.getQuilt());
			m.put("hangingLocation", r.getHangingLocation());
			m.put("count", r.getCount());
			return m;
		}).collect(Collectors.toList());
		
	}
}
