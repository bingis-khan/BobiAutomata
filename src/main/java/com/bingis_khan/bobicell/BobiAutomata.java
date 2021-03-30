package com.bingis_khan.bobicell;

public class BobiAutomata {
	public static void main(String[] args) {
		State s1 = new State("S1", null),
				s2 = new State("S2", null);
		Ruleset rs = new Ruleset() {
			@Override
			State nextState(Board b) {
				return s2;
			}
		};
		
		Board board = Board.emptyBoard(10, 1, rs, s1);
		
		board.forEachCell((b) -> System.out.print(b.getState(0, 0) + " "));
		board.update();
		System.out.println();
		board.forEachCell((b) -> System.out.print(b.getState(0, 0) + " "));
	}
}
