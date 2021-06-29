package com.bingis_khan.bobicell;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


@DisplayName("Parser")
public class ParserTest {
	
	@Test
	@DisplayName("successfully parses Game of Life.")
	public void successfullyParsesGameOfLife() {
		var source = TestUtils.loadResource("gol.ca");
		var sc = new Scanner(source, TestUtils.FAIL_ERROR_REPORTER);
		var ps = new Parser(sc, Functions.all(), TestUtils.FAIL_ERROR_REPORTER);
		
		ps.parse();
	}
	
	@Test
	@DisplayName("successfully parses Seeds.")
	public void successfullyParsesSeeds() {
		var source = TestUtils.loadResource("seeds.ca");
		var sc = new Scanner(source, TestUtils.FAIL_ERROR_REPORTER);
		var ps = new Parser(sc, Functions.all(), TestUtils.FAIL_ERROR_REPORTER);
		
		ps.parse();
	}
	
	@Test
	@DisplayName("successfully parses Brian's Brain.")
	public void successfullyParsesBriansBrain() {
		var source = TestUtils.loadResource("briansbrain.ca");
		var sc = new Scanner(source, TestUtils.FAIL_ERROR_REPORTER);
		var ps = new Parser(sc, Functions.all(), TestUtils.FAIL_ERROR_REPORTER);
		
		ps.parse();
	}
}
