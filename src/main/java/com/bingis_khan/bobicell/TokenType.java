package com.bingis_khan.bobicell;

public enum TokenType {
	// Normal tokens.
	COLON, RIGHT_ARROW, COMMA, LEFT_PAREN, RIGHT_PAREN,
	
	// Keywords.
	IS, AND,
	
	// Special.
	 NEWLINE, EOF,
	
	// Literal-using tokens.
	STATE,		// Uppercase.
	SECTION,	// First letter is uppercase.
	IDENTIFIER,	// Lowercase.
	NUMBER,		// Integer, actually.
	
	
	// Scanning trash.
	NO_TOKEN
}
