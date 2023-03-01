package org.eihq.quiltshow.model;

import java.util.List;

import lombok.Data;

@Data
public class ReportResult {
	
	Report report;
	
	List<Quilt> results;
	
	boolean demo = false;

}
