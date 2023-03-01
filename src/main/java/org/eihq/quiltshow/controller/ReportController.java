package org.eihq.quiltshow.controller;

import java.util.List;

import org.eihq.quiltshow.model.Quilt;
import org.eihq.quiltshow.model.Report;
import org.eihq.quiltshow.model.Report.ReportCategory;
import org.eihq.quiltshow.model.ReportResult;
import org.eihq.quiltshow.repository.QuiltSearchBuilder;
import org.eihq.quiltshow.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins="*")
@RestController
@RequestMapping("/reports")
public class ReportController {

	@Autowired
	ReportService reportService;
	
	@Autowired
	QuiltSearchBuilder quiltSearchBuilder;
	
	
	@GetMapping
	public List<Report> getAllReports(Authentication auth) {
		List<Report> reports = reportService.getReports();
		return reports;
	}
	
	@GetMapping("/favorites")
	public List<Report> getFavoriteReports() {
		return reportService.getFavoriteReports();
	}
	
	@GetMapping("/report/{reportId}")
	public Report getReport(@PathVariable Long reportId) {
		Report report = reportService.getReport(reportId);
		return report;
	}
	
	@PostMapping("/run")
	public ReportResult runReport(@RequestBody Report report) {
		List<Quilt> quilts = quiltSearchBuilder.buildSearch(report).getResultList();
		
		ReportResult result = new ReportResult();
		result.setReport(report);
		result.setResults(quilts);
		
		return result;
	}
	
	@GetMapping("/report/{reportId}/run")
	public ReportResult runReport(@PathVariable Long reportId) {
		Report report = reportService.getReport(reportId);
		List<Quilt> quilts = quiltSearchBuilder.buildSearch(report).getResultList();
		
		ReportResult result = new ReportResult();
		result.setReport(report);
		result.setResults(quilts);
		
		return result;
	}
	
	@GetMapping("/categories")
	public List<ReportCategory> getReportCategories() {
		return reportService.getReportCategories();
	}
	
	@GetMapping("/categories/{category}")
	public List<Report> getReportsInCategory(@PathVariable String category) {
		ReportCategory reportCategory = ReportCategory.valueOf(category);
		return reportService.getReportsInCategory(reportCategory);
	}
	
	@PostMapping("/report")
	public Report saveOrUpdateReport(@RequestBody Report report) {
		return reportService.save(report);
	}
	
	@DeleteMapping("/report/{reportId}")
	public void deleteReport(@PathVariable long reportId) {
		reportService.delete(reportId);
	}
}
