package org.eihq.quiltshow.repository;

import java.util.List;

import org.eihq.quiltshow.model.Report;
import org.eihq.quiltshow.model.Report.ReportCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {

	List<Report> findByFavorite(Boolean favorite);

	List<Report> findByReportCategory(ReportCategory reportCategory);
    
}
