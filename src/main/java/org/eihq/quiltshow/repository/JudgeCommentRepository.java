package org.eihq.quiltshow.repository;

import java.util.List;

import org.eihq.quiltshow.model.JudgeComment;
import org.eihq.quiltshow.model.Quilt;
import org.eihq.quiltshow.model.Report;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JudgeCommentRepository extends JpaRepository<JudgeComment, Long> {

	List<Report> findByQuilt(Quilt quilt);
    
}
