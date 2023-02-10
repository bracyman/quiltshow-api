package org.eihq.quiltshow.model;

public enum GroupSize {
	
	SOLO(false), DUET(true), GROUP(true);
	
	boolean multiperson;

	
	private GroupSize(boolean multiperson) {
		this.multiperson = multiperson;
	}
}
