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
public class QuiltIdSlipReport extends Report {

	public static final Long ID = -40l;
	
	
	public QuiltIdSlipReport() {
		
	}


	@Override
	public Long getId() {
		return ID;
	}

	@Override
	public String getReportName() {
		return "Quilt ID Slips";
	}


	@Override
	public ReportCategory getReportCategory() {
		return ReportCategory.SHOW;
	}
	
	@Override
	public String getFormat() {
		return "IdSlip";
	}


	@Override
	public String getReportDescription() {
		return "ID Slips to attach to quilts during the show";
	}


	@Override
	public List<String> getFields() {
		return Arrays.asList("name", "number","judged","width","length","enteredBy","presidentsChallenge","tags");
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

