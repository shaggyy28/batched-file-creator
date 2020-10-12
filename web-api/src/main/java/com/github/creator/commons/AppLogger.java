package com.github.creator.commons;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class AppLogger {

	private AppLogger() {
	}

	private static final Logger logger = LoggerFactory.getLogger(AppLogger.class);

	public static void info(String msg) {
		logger.info(msg);
	}

	public static void info(String format, Object... arguments) {
		logger.info(format, arguments);
	}

	public static void error(String msg, Throwable t) {
		logger.error(msg, t);
	}

}
