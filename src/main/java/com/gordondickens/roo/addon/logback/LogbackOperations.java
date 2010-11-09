package com.gordondickens.roo.addon.logback;

import org.springframework.roo.model.JavaType;

/**
 * Interface of commands that are available via the Roo shell.
 *
 * @since 1.1
 */
public interface LogbackOperations {

	boolean isCommandAvailable();

	void annotateType(JavaType type);
	
	void setup();
}