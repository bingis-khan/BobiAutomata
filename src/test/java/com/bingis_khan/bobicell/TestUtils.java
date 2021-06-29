package com.bingis_khan.bobicell;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;


public class TestUtils {
	public static final ErrorReporter FAIL_ERROR_REPORTER = new ErrorReporter() {
		
		@Override
		public void error(String type, String errorMessage, String line, int from, int to) {
			super.error(type, errorMessage, line, from, to);
			fail("Error occurred.");
		}

		@Override
		public void error(String type, String errorMessage) {
			super.error(type, errorMessage);
			fail("Error occurred.");
		}
	};
	
	public static String loadResource(String path) {
		try {
			var loaded = new String(Files.readAllBytes(Paths.get("src/test/resources/" + path)));
			return loaded;
		} catch (IOException e) {
			fail("IOException thrown: " + e);
			return "";
		}
	}
}