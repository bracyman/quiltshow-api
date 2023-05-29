package org.eihq.quiltshow.service;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eihq.quiltshow.exception.NotFoundException;
import org.eihq.quiltshow.model.Quilt;
import org.eihq.quiltshow.model.Report;
import org.eihq.quiltshow.model.Report.ReportCategory;
import org.eihq.quiltshow.model.ReportResult;
import org.eihq.quiltshow.reports.CheckInOutReport;
import org.eihq.quiltshow.reports.PaymentStatusReport;
import org.eihq.quiltshow.reports.StatisticsStatusReport;
import org.eihq.quiltshow.repository.PersonRepository;
import org.eihq.quiltshow.repository.QuiltRepository;
import org.eihq.quiltshow.repository.QuiltSearchBuilder;
import org.eihq.quiltshow.repository.ReportRepository;
import org.eihq.quiltshow.repository.ShowRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ReportService {

	@Autowired
	ReportRepository reportRepository;

	@Autowired
	PaymentService paymentService;

	@Autowired
	ShowRepository showRepository;

	@Autowired
	QuiltRepository quiltRepository;

	@Autowired
	PersonRepository personRepository;

	@Autowired
	QuiltSearchBuilder quiltSearchBuilder;
	

	

	public List<ReportCategory> getReportCategories() {
		return Arrays.asList(ReportCategory.values());
	}


	/**
	 * Returns all reports that can be run
	 * @return
	 */
	public List<Report> getReports() {
		List<Report> reports = new LinkedList<>(reportRepository.findAll());
		reports.add(PaymentStatusReport.instance());
		reports.add(new StatisticsStatusReport());
		reports.add(new CheckInOutReport());
		return reports;
	}


	/**
	 * Returns the specified report, or throws a not found exception if no matching report
	 * @param id
	 * @return
	 */
	public Report getReport(Long id) {
		if(id == null) {
			return null;
		}

		if(id.equals(PaymentStatusReport.ID)) {
			return PaymentStatusReport.instance();
		}

		if(id.equals(StatisticsStatusReport.ID)) {
			return new StatisticsStatusReport();
		}

		if(id.equals(CheckInOutReport.ID)) {
			return new CheckInOutReport();
		}

		return reportRepository.findById(id).orElseThrow(() -> new NotFoundException("Report", id));
	}


	/**
	 * Returns all reports that are flagged as favorites
	 * @return
	 */
	public List<Report> getFavoriteReports() {
		return reportRepository.findByFavorite(Boolean.TRUE);
	}


	/**
	 * Returns all reports that are flagged as favorites
	 * @return
	 */
	public List<Report> getReportsInCategory(ReportCategory reportCategory) {
		return reportRepository.findByReportCategory(reportCategory);
	}

	/**
	 * Creates or updates the specified report
	 * @param report
	 * @return
	 */
	public Report save(Report report) {
		log.info("Saving report [{}]", report.getName());
		return reportRepository.save(report);
	}


	/**
	 * Deletes the specified report
	 * @param id
	 */
	public void delete(Long id) {
		reportRepository.deleteById(id);
	}

	/**
	 * Executes a report and returns the result
	 * @param id
	 * @return
	 */
	public ReportResult runReport(Long id) {
		Report report = getReport(id);

		if(report.getId().equals(PaymentStatusReport.ID)) {
			return ((PaymentStatusReport)report).run(paymentService, personRepository);
		}

		if(report.getId().equals(StatisticsStatusReport.ID)) {
			return new StatisticsStatusReport().run(quiltRepository, showRepository);
		}

		if(report.getId().equals(CheckInOutReport.ID)) {
			return new CheckInOutReport().run(quiltRepository, showRepository);
		}

		return runReport(report);
	}

	public ReportResult runReport(Report report) {
		ReportResult result = new ReportResult();
		result.setReport(report);

		// if the report counts quilts, run the count search
		if(report.getFields().contains("count")) {
			List<Object[]> results = quiltSearchBuilder.buildCountSearch(report).getResultList();
			
			List<Map<String, Object>> mappedResults = new LinkedList<>();
			results.forEach(r -> {
				Map<String, Object> mappedResult = new HashMap<>();
				for(int i=0; i < report.getFields().size(); i++) {
					mappedResult.put(report.getFields().get(i), r[i]);
				}
				mappedResults.add(mappedResult);
			});
			
			result.setResults(mappedResults);
		}
		// otherwise run a quilt search
		else {
			List<Quilt> quilts = quiltSearchBuilder.buildSearch(report).getResultList();
	
			List<Map<String, Object>> searchResults = new LinkedList<>();
			quilts.forEach(q -> {
				Map<String, Object> searchResult = new HashMap<>();
				report.getFields().forEach(f -> {
					try {
						if(f.equalsIgnoreCase("perimeter")) {
							searchResult.put("width", q.getWidth());
							searchResult.put("length", q.getLength());
						}
						else {
							Field field = Quilt.class.getDeclaredField(f);
							field.setAccessible(true);
							searchResult.put(f, field.get(q));
						}
					} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
						searchResult.put(f, null);
					}
				});
				
				// include the grouping field for grouping reports
				if(report.getGroupField() != null) {
					try {
						Field field = Quilt.class.getDeclaredField(report.getGroupField());
						field.setAccessible(true);
						searchResult.put(report.getGroupField(), field.get(q));
					} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
						searchResult.put(report.getGroupField(), null);
					}
				}
				searchResults.add(searchResult);
			});
			
			
			result.setResults(searchResults);
		}

		return result;	
	}

}
