package org.eihq.quiltshow.exception;

public class NotFoundException extends RuntimeException {
	private static final long serialVersionUID = 5034739557486417209L;

	public NotFoundException(String message) {
		super(message);
	}

	public NotFoundException(String objectType, String id) {
		super(String.format("%s [%s] cannot be located", objectType, id));
	}

	public NotFoundException(String objectType, int id) {
		super(String.format("%s [%d] cannot be located", objectType, id));
	}

	public NotFoundException(String objectType, long id) {
		super(String.format("%s [%d] cannot be located", objectType, id));
	}

	public NotFoundException(String objectType, Object id) {
		super(String.format("%s [%s] cannot be located", objectType, id.toString()));
	}

}
