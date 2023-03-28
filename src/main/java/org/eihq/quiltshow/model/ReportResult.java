package org.eihq.quiltshow.model;

import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class ReportResult {
	
	Report report;
	
	List<Map<String,Object>> results;
	
	boolean demo = false;

}
