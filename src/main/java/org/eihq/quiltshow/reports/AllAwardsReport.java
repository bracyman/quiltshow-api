package org.eihq.quiltshow.reports;

import java.util.Arrays;
import java.util.List;

import org.eihq.quiltshow.model.Report;
import org.eihq.quiltshow.model.ReportResult;
import org.eihq.quiltshow.model.SearchField;
import org.eihq.quiltshow.model.SearchField.MatchType;
import org.eihq.quiltshow.repository.QuiltSearchBuilder;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AllAwardsReport extends Report {

	public static final Long ID = -80l;
	
	
	public AllAwardsReport() {
		
	}


	@Override
	public Long getId() {
		return ID;
	}

	@Override
	public String getReportName() {
		return "Show Awards";
	}


	@Override
	public ReportCategory getReportCategory() {
		return ReportCategory.AWARDS;
	}
	
	@Override
	public String getFormat() {
		return "all-awards";
	}
	
	@Override
	public Boolean getFavorite() {
		return true;
	}

	@Override
	public String getReportDescription() {
		return "All Quilt Show Awards";
	}


	@Override
	public List<String> getFields() {
		return Arrays.asList("name", "number","judged","groupSize","category","tags", "hangingLocation");
	}

	
	
	public ReportResult run(QuiltSearchBuilder quiltSearchBuilder) {
		log.debug("Starting Check In/Out Report...");
		
		ReportResult result = new ReportResult(this, quiltSearchBuilder.executeQueryReport(this));		
		return result;
	}

	
}

