package org.eihq.quiltshow.exception;

public class NotFoundException extends RuntimeException {
	private static final long serialVersionUID = 5034739557486417209L;

	public NotFoundException(String message) {
		super(message);
	}

	public NotFoundException(String objectType, String id) {
		super("%s [%s] cannot be located".formatted(objectType, id));
	}

	public NotFoundException(String objectType, int id) {
		super("%s [%d] cannot be located".formatted(objectType, id));
	}

	public NotFoundException(String objectType, long id) {
		super("%s [%d] cannot be located".formatted(objectType, id));
	}

	public NotFoundException(String objectType, Object id) {
		super("%s [%s] cannot be located".formatted(objectType, id.toString()));
	}

}
