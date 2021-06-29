package com.bingis_khan.bobicell;

import java.util.function.BiFunction;
import java.util.function.Consumer;

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
	private final State border;
	
	// Constructors will be named static functions
	// for different initial configurations.
	private Board(int width, int height, Ruleset ruleset, State border) {
		currentStates = new State[width][height];
		nextStates = new State[width][height];
		
		this.width = width;
		this.height = height;
		
		this.ruleset = ruleset;
		
		this.border = border;
	}
	
	// Initializes an empty board.
	static Board emptyBoard(int width, int height, Ruleset rs, final State empty, final State border) {
		Board board = new Board(width, height, rs, border);
		board.updateEachCell((x, y) -> empty);
		
		return board;
	}
	
	// Computes the next state for each tile.
	void update() {
		updateEachCell((x, y) -> ruleset.nextState(this, x, y));
	}
	
	private void updateEachCell(BiFunction<Integer, Integer, State> update) {
		for (int x = 0; x < width; x++)
			for (int y = 0; y < height; y++)
				nextStates[x][y] = update.apply(x, y);
		
		swap();
	}
	
	// Generic function for iterating over all cells.
	void forEachCell(Consumer<State> f) {
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
		//	^^^^^^^^^^^^ It's obsolete! ^^^^^^^^^^^^^^^^^^^^^^^^^^^
		// I originally chose instance variables. But, it actually caused some weird ass bugs
		// like weird nextStates when stepping through simulation. I don't know why the bug happened.
		// But I sure do know now which 'type' of variable I'll use.
		for (int x = 0; x < width; x++)
			for (int y = 0; y < height; y++)
				f.accept(currentStates[x][y]);
		
	}
	
	State getState(int x, int y) {
		// Check if out of bounds.
		if (x < 0 || y < 0 || x >= width || y >= height)
			return border;
		
		return currentStates[x][y];
	}
	
	void setCurrentState(State s, int x, int y) {
		currentStates[x][y] = s;
	}
	
	// Buffer swap.
	private void swap() {
		var t = currentStates;
		currentStates = nextStates;
		
		// We do actually need to swap them, despite only reading one.
		// If we did only 'current = next', it'd be the same array, 
		// so writing to one would modify the other. REAL BAD.
		nextStates = t;
	}
	
	int getWidth() {
		return width;
	}
	
	int getHeight() {
		return height;
	}
}
