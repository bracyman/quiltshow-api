package org.eihq.quiltshow.exception;

public class PaymentException extends Exception {
	private static final long serialVersionUID = 5034739557486417209L;

	public PaymentException(String message) {
		super(message);
	}

	public PaymentException(String message, Throwable source) {
		super(message, source);
	}

	
}
