package com.bingis_khan.bobicell;

import static com.bingis_khan.bobicell.TokenType.EOF;

public class BobiAutomata {
	public static void main(String[] args) {
		
		var sc = new Scanner("States\r\n" + 
				"    OFF, DYING, ON\r\n" + 
				"    \r\n" + 
				"Rules (Moore)\r\n" + 
				"    ON -> DYING  -- Colon optional with simple state transitions.\r\n" + 
				"    DYING -> OFF\r\n" + 
				"    sum of ON is 2 -> ON\r\n" + 
				"    -> OFF", new ErrorReporter());
		
		Token token;
		while ((token = sc.nextToken()).type != EOF) {
			System.out.println(token.type + ": " + token.literal);
		}
		
		/*
		Board board = Board.emptyBoard(200, 200, rs, s1);
		Display d = new Display(800, 600, board);
		d.run();
		*/
	}
}
