package com.bingis_khan.bobicell;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.*;

@DisplayName("Board")
public class BoardTest {
	Board b;
	Ruleset rs;
	State s1 = new State("S1", null), s2 = new State("S2", null);
	
	@DisplayName("calling emptyBoard() should fill the board with a state.")
	public void callingEmptyBoardShouldFillTheBoardWithAState() {
		b = Board.emptyBoard(10, 10, null, s1);
		b.forEachCell((b) -> assertEquals(b.getState(0, 0), s1));
	}
	
	@Nested
	@DisplayName("when calling update()")
	class WhenCallingUpdate {
		
		@Test
		@DisplayName("should have updated each state.")
		public void shouldComputeNextState() {
			rs = new Ruleset() {
				@Override
				State nextState(Board b) {
					return s2;
				}
			};
			b = Board.emptyBoard(10, 10, rs, s1);
			
			b.update(); // Update step should change all s1 to s2.
			
			b.forEachCell((b) -> assertEquals(b.getState(0, 0), s2));
		}
		
		@Test
		@DisplayName("should not modify the original grid while being updated. (checked in Moore nb.)")
		public void shouldNotModifyOriginalGrid() {
			rs = new Ruleset() {
				@Override
				State nextState(Board b) {
					for (int dx = -1; dx <= 1; dx++)
						for (int dy = -1; dy <= 1; dy++)
							assertEquals(b.getState(dx, dy), s1, "Expected: " + s1 + "; Was: " + b.getState(0, 0));
					
					return s2;
				}
			};
			
			b = Board.emptyBoard(10, 10, rs, s1);
			
			b.update();
		}
	}

}
