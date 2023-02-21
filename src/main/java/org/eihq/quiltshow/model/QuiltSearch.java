package org.eihq.quiltshow.model;

import java.util.LinkedList;
import java.util.List;

import lombok.Data;


@Data
public class QuiltSearch {

	
	private SearchField number = null;

	private SearchField name = null;

	private SearchField description = null;

	private SearchField category = null;

	private SearchField tags = null;

	private SearchField length = null;
	
	private SearchField width = null;

	private SearchField firstShow = null;
	
	private SearchField judged = null;

	private SearchField mainColor = null;

	private SearchField hangingPreference = null;

	private SearchField designSourceTypes = null;

	private SearchField designSourceName = null;

	private SearchField enteredBy = null;

	private SearchField groupSize = null;

	private SearchField additionalQuilters = null;

	private List<String> fields = new LinkedList<>();	
	
	private List<String> order = new LinkedList<>();	

}
