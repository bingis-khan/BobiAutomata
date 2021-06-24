package com.bingis_khan.bobicell;

import static com.bingis_khan.bobicell.TokenType.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class Scanner {
	private final String source;
	private int current = 0, lineStart = 0, start = 0;
	private String currentLine;
	private static final Set<String> sections;
	
	static {
		// Technically, this is redundant,
		// as the same info (which is always up-to-date!)
		// is found in Parser class.
		sections = new HashSet<>();
		sections.add("States");
		sections.add("Rules");
		sections.add("Defaults");
		sections.add("Graphics");
	}
	
	private final ErrorReporter er;
	
	Scanner(String file, ErrorReporter er) {
		source = file;
		this.er = er;
		currentLine = scanLine();
	}
	
	
	Token nextToken() {
		Token token;
		do {
			start = current;
		} while ((token = scanToken()).type == NO_TOKEN);
		
		return token;
	}
	
	
	private Token scanToken() {
		if (isAtEnd())
			return token(EOF);
		
		char c = advance();
		switch (c) {
		case ':': return token(COLON);
		case ',': return token(COMMA);
		case '(': return token(LEFT_PAREN);
		case ')': return token(RIGHT_PAREN);
		case '-': 
			if (match('>'))
				return token(RIGHT_ARROW);
			else if (match('-')) {
				skipComment();
			} else
				error("'-' found. Did you mean to write '->' or comment ('--')?");
			
			break;
		
		case '\n': {
			var newline = token(NEWLINE);
			
			lineStart = current;
			currentLine = scanLine();
			
			return newline;
		}
		
		case ' ':                                    
    	case '\r':                                   
    	case '\t':                                   
    		// Ignore whitespace... for now.
    		// I might make whitespace required for different sections...
    		// but it would be hard(er) to scan (scan whitespace ONLY after newline? or ignore it in parsing?).
    		break;
		
		default: {
			if (isDigit(c))
				return number();
			
			if (!isAlpha(c)) {
				error("Weird-ass character spotted.");
				break;
			}
			
			
			var identifier = identifier();
			
			// Keywords.
			if (identifier.equals("is"))
				return token(IS);
			else if (identifier.equals("and"))
				return token(AND);
			
			// Section names.
			if (sections.contains(identifier))
				return token(SECTION, identifier);
				
			// States + identifiers (user defined).
			if (identifier.toUpperCase().equals(identifier))
				return token(STATE, identifier);
			else
				return token(IDENTIFIER, identifier);
		}
		
		}
		
		// No-op token to tell the calling function to keep scanning.
		return token(NO_TOKEN);
	}
	
	
	// Extracts as a string the current line we're on.
	private String scanLine() {
		var cur = lineStart;
		while (cur < source.length() && source.charAt(cur) != '\n')
			cur++;
		
		return source.substring(current, cur);
	}
	
	
	private void skipComment() {
		while (!isAtEnd() && source.charAt(current) != '\n')
			advance();
		
		return;
	}
	
	
	private void error(String errorMessage) {
		er.error("Scanning", errorMessage, currentLine,
				current - lineStart, start - lineStart);
	}
	
	
	private Token number() {
		while (isDigit(peek())) advance();
		
		return token(NUMBER, source.substring(start, current));
	}
	
	
	// Scanner functions copied from Lox's Scanner class.
	private String identifier() {
		while (isAlphaNumeric(peek())) advance();
		
		return source.substring(start, current);
	}
	
	
	private boolean isAtEnd() {
		return current >= source.length();
	}
	
	
	private char advance() {
		return source.charAt(current++);
	}
	
	
	private boolean match(char c) {
		if (isAtEnd()) return false;
		if (source.charAt(current) != c) return false;
		
		current++;
		return true;
	}
	
	
	private Token token(TokenType type) {
		return token(type, null);
	}
	
	
	private Token token(TokenType type, String literal) {
		return new Token(type, literal, currentLine,
				current - lineStart, start - lineStart);
	}
	
	
	private char peek() {
		if (isAtEnd()) return '\0';
		return source.charAt(current);
	}
	
	
	private boolean isAlpha(char c) {
		return (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || c == '_';
	}
	
	
	private boolean isDigit(char c) {
		return c >= '0' && c <= '9';
	}
	
	
	private boolean isAlphaNumeric(char c) {
		return isAlpha(c) || isDigit(c);
	}
}
