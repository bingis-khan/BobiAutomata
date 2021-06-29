package com.bingis_khan.bobicell;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static com.bingis_khan.bobicell.TokenType.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


@DisplayName("Scanner")
public class ScannerTest {
	private static List<Token> getTokens(Scanner sc) {
		var tokens = new ArrayList<Token>();
		
		Token token;
		do {
			token = sc.nextToken();
			tokens.add(token);
		} while (token.type != EOF);
		
		return tokens;
	}
	
	private static boolean tokensEqual(List<Token> t1, List<Token> t2) {
		if (t1.size() != t2.size())
			return false;
		
		var i1 = t1.listIterator();
		var i2 = t2.listIterator();
		while (i1.hasNext() && i2.hasNext())
			if (i1.next().type != i2.next().type)
				return false;
		
		return true;
	}
	
	// Placeholder method. We'll only be checking token types (because it's easier and faster).
	private static Token token(TokenType type) {
		return new Token(type, "", "", 0, 1);
	}

	private final String s1 = "States\n\tOFF, ON, FUCK";
	@Test
	@DisplayName("scans properly \"" + s1 + "\".")
	public void scansProperlyString1() {
		var sc = new Scanner(s1, TestUtils.FAIL_ERROR_REPORTER);
		var tokens = getTokens(sc);
		
		assertTrue(tokensEqual(tokens, Arrays.asList(token(SECTION), token(NEWLINE), 
				token(STATE), token(COMMA), token(STATE), token(COMMA), token(STATE), token(EOF))));
	}
	
	private final String s2 = "ON:sum->OFF";
	@Test
	@DisplayName("scans properly \"" + s2 + "\".")
	public void scansProperlyString2() {
		var sc = new Scanner(s2, TestUtils.FAIL_ERROR_REPORTER);
		var tokens = getTokens(sc);
		
		assertTrue(tokensEqual(tokens, Arrays.asList(token(STATE), token(COLON), token(IDENTIFIER),
				token(RIGHT_ARROW), token(STATE), token(EOF))));
	}
	
	private final String s3 = "ON -> OFF  -- this is a comment.\n FUCK->OFF";
	@Test
	@DisplayName("ignores comments like \"" + s3 + "\".")
	public void ignoresComments() {
		var sc = new Scanner(s3, TestUtils.FAIL_ERROR_REPORTER);
		var tokens = getTokens(sc);
		
		assertTrue(tokensEqual(tokens, Arrays.asList(token(STATE), token(RIGHT_ARROW), token(STATE), token(NEWLINE),
				token(STATE), token(RIGHT_ARROW), token(STATE), token(EOF))));
	}
}
