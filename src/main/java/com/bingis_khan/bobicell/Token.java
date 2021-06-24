package com.bingis_khan.bobicell;


public class Token {
	public final TokenType type;
	public String literal, line;
	public int from, to;
	
	public Token(TokenType type, String literal, String line, int from, int to) {
		this.type = type;
		this.literal = literal;
		this.line = line;
		this.from = from;
		this.to = to;
	}
}
