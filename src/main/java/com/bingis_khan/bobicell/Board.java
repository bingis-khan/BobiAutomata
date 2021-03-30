package com.bingis_khan.bobicell;

import java.util.function.Consumer;
import java.util.function.Function;

/*
 *  Core of the simulation. 
 *  Given a Ruleset, computes the next states of the automaton.
 */
class Board {
	// Determines how next states are computed.
	private final Ruleset ruleset;
	
	// We'll be using two buffers for this task.
	private State[][] currentStates;	// Current iteration. Only reads.
	private State[][] nextStates;		// Next iteration. Only writes.
	
	// A bit of redundancy for clarity.
	private final int width, height;
	
	// Default state, when OOB and Board is not toroidal. 
	private final State defaultState;
	
	// Constructors will be named static functions
	// for different initial configurations.
	private Board(int width, int height, Ruleset ruleset, State defaultState) {
		currentStates = new State[width][height];
		nextStates = new State[width][height];
		
		this.width = width;
		this.height = height;
		
		this.ruleset = ruleset;
		
		this.defaultState = defaultState;
	}
	
	// Initializes an empty board.
	static Board emptyBoard(int width, int height, Ruleset rs, final State empty) {
		Board board = new Board(width, height, rs, empty);
		board.computeNextState((b) -> empty);
		
		return board;
	}
	
	// Computes the next state for each tile.
	void update() {
		computeNextState(ruleset::nextState);
	}
	
	// Updates each tile accordingly and swaps the buffer.
	private void computeNextState(Function<Board, State> f) {
		forEachCell((b) -> {
			nextStates[x][y] = f.apply(b);
		});
		
		swap();
	}
	
	// Generic function for iterating over all cells.
	private int x, y;	// Uses these instance variables.
	void forEachCell(Consumer<Board> f) {
		// A bit unfinished...
		// What I mean is that x, y should be instance variables
		// if we're going to pass the whole Board object.
		// Decide: should x, y be local and passed to a BiConsumer, whatever,
		//  In theory faster, but more inconvenient
		//    we'd have to do something like getState(x+dx, y+dy).
		// Or: should x, y be instance variables and the board will be passed.
		//  In theory slower, but we could have methods like:
		//    current, left, right, up, down, etc. - alongside getState(dx, dy)
		// 	  and only ONE argument passed.
		// According to this [i lost it :3] SO post, there IS a difference, 
		//  but a really small one. 
		for (x = 0; x < width; x++)
			for (y = 0; y < height; y++)
				f.accept(this);
		
	}
	
	State getState(int dx, int dy) {
		assert x < width && y < height : "getState called outside of any forEach loop.";
		
		int sx = x + dx,
			sy = y + dy;
		
		// Check if out of bounds.
		if (sx < 0 || sy < 0 || sx >= width || sy >= height)
			return defaultState;
		
		return currentStates[sx][sy];
	}
	
	// Buffer swap.
	private void swap() {
		State[][] t = currentStates;
		currentStates = nextStates;
		
		// We do actually need to swap them, despite only reading one.
		// If we did only 'current = next', it'd be the same array, 
		// so writing to one would modify the other. REAL BAD.
		nextStates = t;
	}
}
