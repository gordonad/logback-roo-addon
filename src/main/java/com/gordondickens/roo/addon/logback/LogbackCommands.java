package com.gordondickens.roo.addon.logback;

import java.util.logging.Logger;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.springframework.roo.shell.CliAvailabilityIndicator;
import org.springframework.roo.shell.CliCommand;
import org.springframework.roo.shell.CommandMarker;

/**
 * Commands for the Logback commands at the Roo Shell. The command class is
 * registered by the Roo shell following an automatic classpath scan.
 * 
 * @author Gordon Dickens
 * @since 1.1
 */
@Component
@Service
public class LogbackCommands implements CommandMarker {
	private static Logger logger = Logger.getLogger(LogbackCommands.class
			.getName());
	@Reference
	private LogbackOperations operations;

	@CliAvailabilityIndicator("logback setup")
	public boolean isPropertyAvailable() {
		return true;
		// TODO Add logic for option --overwrite (default to true), if set, then
		// check for existing config
		// return operations.isCommandAvailable(); // it's safe to always see
		// the properties we expose
	}

	/*
	 * TODO Configure to accept parameter --overwrite (default true) TODO
	 * Configure to accept parameter --level (default ?)
	 * 
	 * @CliCommand(value="logging setup",
	 * help="Configure logging in your project") public void
	 * configureLogging(@CliOption(key={"","level"}, mandatory=true,
	 * help="The log level to configure") LogLevel logLevel,
	 * 
	 * @CliOption(key="package", mandatory=false,
	 * help="The package to append the logging level to (all by default)")
	 * LoggerPackage loggerAppender) {
	 * 
	 * loggingOperations.configureLogging(logLevel, loggerAppender==null ?
	 * LoggerPackage.ROOT : loggerAppender); }
	 */
	@CliCommand(value = "logback setup", help = "Setup Logback addon")
	public void setup() {
		operations.setup();
	}
}