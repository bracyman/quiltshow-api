package org.eihq.quiltshow.exception;

import java.util.List;
import java.util.stream.Collectors;

/**
 * An exception thrown when attempting to delete an object that is currently being used by another object
 * Ex. Cannot delete a category while at least one quilt is assigned to that category
 * @author bracyman
 *
 */
public class CannotDeleteInUseException extends Exception {

	private static final long serialVersionUID = -3439968488168986636L;
	
	public CannotDeleteInUseException(String objectName, List<String> inUseBy) {
		super("Cannot delete %s: currently in use by at least one %s".formatted(
				objectName,
				inUseBy.stream().collect(Collectors.joining(", "))));
	}
}
