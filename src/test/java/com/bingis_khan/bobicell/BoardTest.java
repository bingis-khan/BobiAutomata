package com.bingis_khan.bobicell;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;

import org.junit.jupiter.api.*;

@DisplayName("Board")
public class BoardTest {
	Board b;
	Ruleset rs;
	State s1 = new State("S1"), s2 = new State("S2");
	
	@Test
	@DisplayName("calling emptyBoard() should fill the board with a state.")
	public void callingEmptyBoardShouldFillTheBoardWithAState() {
		b = Board.emptyBoard(10, 10, null, s1, s1);
		b.forEachCell((s) -> assertEquals(s, s1));
	}
	
	@Nested
	@DisplayName("when calling update()")
	class WhenCallingUpdate {
		
		@Test
		@DisplayName("should have updated each state.")
		public void shouldComputeNextState() {
			rs = new Ruleset(Arrays.asList(s1, s2), Arrays.asList(new Rule(null, s2)), null);
			b = Board.emptyBoard(10, 10, rs, s1, s1);
			
			b.update(); // Update step should change all s1 to s2.
			
			b.forEachCell((s) -> assertEquals(s, s2));
		}
		
		@Test
		@DisplayName("should not modify the original grid while being updated.")
		public void shouldNotModifyOriginalGrid() {
			Condition a = (b, x, y) -> {
				b.forEachCell((s) -> assertEquals(s, s1));
				return true;
			};
			
			rs = new Ruleset(Arrays.asList(s1, s2), Arrays.asList(new Rule(a, s1)), null);
			
			b = Board.emptyBoard(10, 10, rs, s1, s1);
			
			b.update();
		}
	}

}
