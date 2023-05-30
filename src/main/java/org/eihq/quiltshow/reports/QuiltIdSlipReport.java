package org.eihq.quiltshow.reports;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eihq.quiltshow.model.Report;
import org.eihq.quiltshow.model.ReportResult;
import org.eihq.quiltshow.repository.QuiltRepository;
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
	
	
	public ReportResult run(QuiltRepository quiltRepository, ShowRepository showRepository) {
		log.debug("Starting Statistics Report...");
		
		ReportResult result = new ReportResult();
		result.setDemo(false);
		result.setReport(this);
		
		List<Map<String,Object>> results = new LinkedList<>();
		
		quiltRepository.findAll().forEach(q -> {
			Map<String, Object> vals = new HashMap<>();
			vals.put("name", q.getName());
			vals.put("number", q.getNumber());
			vals.put("judged", q.getJudged());
			vals.put("width", q.getWidth());
			vals.put("length", q.getLength());
			vals.put("enteredBy", q.getEnteredBy());
			vals.put("presidentsChallenge", q.getPresidentsChallenge());
			vals.put("tags", q.getTags());
			
			results.add(vals);
		});
		
		result.setResults(results);		
		return result;
	}

	
}

