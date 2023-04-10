package org.eihq.quiltshow.model;

import java.util.LinkedList;
import java.util.List;

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.eihq.quiltshow.repository.StringListConverter;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "reports")
@Data
@NoArgsConstructor
public class Report {
	
	public static enum ReportCategory {
		SHOW("Show reports"),
		AWARDS("Award reports"),
		MISCELLANEOUS("Miscellaneous");
		
		String name;
		
		ReportCategory(String name) {
			this.name = name;
		}
	}
    
    @Id
    @GeneratedValue
    private Long id;

    private String reportName;
    private String reportDescription;
    
    ReportCategory reportCategory = ReportCategory.MISCELLANEOUS;
    Boolean favorite = Boolean.FALSE;
    private String format = null;
    private String groupField = null;
    

	private SearchField number = null;

	private SearchField name = null;

	private SearchField description = null;

	private SearchField category = null;

	private SearchField tags = null;

	private SearchField length = null;
	
	private SearchField width = null;

	private SearchField presidentsChallenge = null;

	private SearchField firstEntry = null;
	
	private SearchField judged = null;

	private SearchField mainColor = null;

	private SearchField hangingPreference = null;

	private SearchField designSourceTypes = null;

	private SearchField designSourceName = null;

	private SearchField enteredBy = null;

	private SearchField groupSize = null;

	private SearchField additionalQuilters = null;

    @Convert(converter = StringListConverter.class)
	private List<String> fields = new LinkedList<>();	
	
    @Convert(converter = StringListConverter.class)
	private List<String> sortOrder = new LinkedList<>();	
}
