package org.eihq.quiltshow.reports;

import java.util.Arrays;
import java.util.List;

import org.eihq.quiltshow.model.Report;
import org.eihq.quiltshow.model.ReportResult;
import org.eihq.quiltshow.repository.QuiltSearchBuilder;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HangingLabelsReport extends Report {

	public static final Long ID = -60l;
	
	
	public HangingLabelsReport() {
		
	}


	@Override
	public Long getId() {
		return ID;
	}

	@Override
	public String getReportName() {
		return "Hanging Tags";
	}


	@Override
	public ReportCategory getReportCategory() {
		return ReportCategory.MISCELLANEOUS;
	}
	
	@Override
	public String getFormat() {
		return "description-card";
	}
	
	@Override
	public Boolean getFavorite() {
		return true;
	}

	@Override
	public String getReportDescription() {
		return "Half page hanging tags";
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

