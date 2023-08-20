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
public class AllJudgeCommentsReport extends Report {

	public static final Long ID = -70l;
	
	
	public AllJudgeCommentsReport() {
		setJudged(new SearchField("true", MatchType.EQUALS, null));
	}


	@Override
	public Long getId() {
		return ID;
	}

	@Override
	public String getReportName() {
		return "All Judges Comments";
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
		return "All Judges Comments";
	}


	@Override
	public List<String> getFields() {
		return Arrays.asList("name", "number","judged","groupSize","category","tags", "hangingLocation");
	}

	@Override
	public List<String> getSortOrder() {
		return Arrays.asList("category", "number");
	}
	
	
	public ReportResult run(QuiltSearchBuilder quiltSearchBuilder) {
		log.debug("Starting Judges Comments Report...");
		
		ReportResult result = new ReportResult(this, quiltSearchBuilder.executeQueryReport(this));		
		return result;
	}

	
}

