package org.eihq.quiltshow.exception;

/**
 * An exception thrown when no show is flagged as active, and an operation is attempted that requires an active show
 * @author bracyman
 *
 */
public class NoActiveShowException extends Exception {

	private static final long serialVersionUID = 4458718232379065645L;
	
	public NoActiveShowException() {
		super("There is currently no active show");
	}

}
