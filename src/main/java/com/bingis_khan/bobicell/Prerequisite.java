package com.bingis_khan.bobicell;

import java.util.List;

public class Prerequisite implements Condition {
	private final List<State> prerequisites;
	
	
	public Prerequisite(List<State> prerequisites) {
		this.prerequisites = prerequisites;
	}
	
	public boolean evaluate(Board b, int x, int y) {
		var current = b.getState(x, y);
		for (var state : prerequisites)
			if (current == state)
				return true;
		
		return false;
	}
}
