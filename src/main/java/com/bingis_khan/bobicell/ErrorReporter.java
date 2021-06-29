package com.bingis_khan.bobicell;

public class ErrorReporter {
	private boolean hadError;

	public void error(String type, String errorMessage, Token token) {
		
		error(type, errorMessage, token.line, token.from, token.to);
	}

	public void error(String type, String errorMessage, String line, int from, int to) {
		System.err.println("[" + type + "] " + errorMessage);
		System.err.println("Here: " + line);
		System.err.println("\t on position " + from + ":" + to);
		hadError = true;
	}

	public void error(String type, String errorMessage) {
		System.err.println("[" + type + "] " + errorMessage);
		hadError = true;
	}
	
	public boolean hadError() {
		return hadError;
	}
}
