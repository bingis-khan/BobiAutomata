package com.bingis_khan.bobicell;



public class ErrorReporter {
	
	public void error(String type, String errorMessage, Token token) {
		error(type, errorMessage, token.line, token.from, token.to);
	}
	
	
	public void error(String type, String errorMessage, String line, int from, int to) {
		System.err.println("[" + type + "] " + errorMessage);
		System.err.println("Here: " + line);
		System.err.println("\t on position " + from + ":" + to);
	}
}
