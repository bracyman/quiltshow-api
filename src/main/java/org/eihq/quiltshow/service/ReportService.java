package org.eihq.quiltshow.service;

import java.util.Arrays;
import java.util.List;

import org.eihq.quiltshow.exception.NotFoundException;
import org.eihq.quiltshow.model.Report;
import org.eihq.quiltshow.model.Report.ReportCategory;
import org.eihq.quiltshow.repository.ReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ReportService {
	
	@Autowired
	ReportRepository reportRepository;

	
	public List<ReportCategory> getReportCategories() {
		return Arrays.asList(ReportCategory.values());
	}
	
	
	/**
	 * Returns all reports that can be run
	 * @return
	 */
	public List<Report> getReports() {
		return reportRepository.findAll();
	}
	
	
	/**
	 * Returns the specified report, or throws a not found exception if no matching report
	 * @param id
	 * @return
	 */
	public Report getReport(Long id) {
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
}
