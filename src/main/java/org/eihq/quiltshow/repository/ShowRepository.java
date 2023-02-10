package org.eihq.quiltshow.repository;

import java.util.Date;
import java.util.List;

import org.eihq.quiltshow.model.Show;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShowRepository extends JpaRepository<Show, Long> {
 
	public List<Show> findByActiveTrueOrderByEndDateDesc();

	public List<Show> findByEndDateBeforeOrderByEndDateDesc(Date date);
}
