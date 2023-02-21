package org.eihq.quiltshow.model;

import org.springframework.util.StringUtils;

public enum GroupSize {
	
	SOLO(false), DUET(true), GROUP(true);
	
	boolean multiperson;

	
	private GroupSize(boolean multiperson) {
		this.multiperson = multiperson;
	}
	
	public static GroupSize from(String str) {
		if(!StringUtils.hasText(str)) {
			return null;
		}
		return GroupSize.valueOf(str.toUpperCase());
	}
}
