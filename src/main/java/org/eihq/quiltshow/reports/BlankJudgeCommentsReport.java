package org.eihq.quiltshow.reports;

import java.util.Arrays;
import java.util.List;

import org.eihq.quiltshow.model.Report;
import org.eihq.quiltshow.model.ReportResult;
import org.eihq.quiltshow.model.SearchField;
import org.eihq.quiltshow.model.SearchField.MatchType;
import org.eihq.quiltshow.repository.QuiltSearchBuilder;
import org.eihq.quiltshow.repository.QuiltSearchResult;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BlankJudgeCommentsReport extends Report {

	public static final Long ID = -75l;
	
	
	public BlankJudgeCommentsReport() {
		
	}


	@Override
	public Long getId() {
		return ID;
	}

	@Override
	public String getReportName() {
		return "Blank Judges Comments Form";
	}


	@Override
	public ReportCategory getReportCategory() {
		return ReportCategory.AWARDS;
	}
	
	@Override
	public String getFormat() {
		return "judge-remarks";
	}
	
	@Override
	public Boolean getFavorite() {
		return true;
	}

	@Override
	public String getReportDescription() {
		return "Blank Judge Comments Form";
	}


	@Override
	public List<String> getFields() {
		return Arrays.asList("name", "number","judged","groupSize","category","tags", "hangingLocation");
	}

	@Override
	public List<String> getSortOrder() {
		return Arrays.asList("number");
	}
	
	
	public ReportResult run(QuiltSearchBuilder quiltSearchBuilder) {
		log.debug("Starting Judges Comments Report...");
		
		ReportResult result = new ReportResult(this, Arrays.asList(new QuiltSearchResult(null)));		
		return result;
	}

	
}

