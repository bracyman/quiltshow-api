package org.eihq.quiltshow.reports;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eihq.quiltshow.model.Report;
import org.eihq.quiltshow.model.ReportResult;
import org.eihq.quiltshow.repository.QuiltRepository;
import org.eihq.quiltshow.repository.QuiltSearchBuilder;
import org.eihq.quiltshow.repository.ShowRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CheckInOutReport extends Report {

	public static final Long ID = -30l;
	
	
	public CheckInOutReport() {
		
	}


	@Override
	public Long getId() {
		return ID;
	}

	@Override
	public String getReportName() {
		return "Check In/Out";
	}


	@Override
	public ReportCategory getReportCategory() {
		return ReportCategory.SHOW;
	}
	
	@Override
	public String getFormat() {
		return "CheckInOut";
	}


	@Override
	public String getReportDescription() {
		return "Forms to check quilts in and out of the show";
	}


	@Override
	public List<String> getFields() {
		return Arrays.asList("name", "number","judged","groupSize","category","tags", "hangingLocation");
	}

	@Override
	public List<String> getSortOrder() {
		return Arrays.asList("enteredBy", "name");
	}
	
	
	public ReportResult run(QuiltSearchBuilder quiltSearchBuilder) {
		log.debug("Starting Check In/Out Report...");
		
		ReportResult result = new ReportResult(this, quiltSearchBuilder.executeQueryReport(this));		
		return result;
	}

	
}

