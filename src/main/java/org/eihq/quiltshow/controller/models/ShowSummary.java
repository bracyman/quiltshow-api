package org.eihq.quiltshow.controller.models;

import java.util.Date;

import org.eihq.quiltshow.model.Show;

import lombok.Data;

@Data
public class ShowSummary {
	
	public static ShowSummary from(Show show) {
		ShowSummary s = new ShowSummary();
		
		s.setId(show.getId());
		s.setName(show.getName());
		s.setDescription(show.getDescription());
		s.setStartDate(show.getStartDate());
		s.setEndDate(show.getEndDate());
		s.setActive(show.isActive());
		
		return s;
	}
	
	Long id;
	String name;
	String description;
	
	Date startDate;
	Date endDate;
	
	boolean active;
}
