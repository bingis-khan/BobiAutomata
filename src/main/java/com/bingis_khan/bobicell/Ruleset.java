package com.bingis_khan.bobicell;

import java.util.List;
import java.util.Map;

class Ruleset {
	private final List<Rule> rules;
	private final List<State> states;
	private final Map<String, Object> params;
	
	
	Ruleset(List<State> states, List<Rule> rules, Map<String, Object> params) {
		this.states = states;
		this.rules = rules;
		this.params = params;
	}
	
	
	State nextState(Board b, int x, int y) {
		for (var rule : rules)
			if (rule.checkCondition(b, x, y))
				return rule.next;

		
		// Decided that 'no change' is a valid approach.
		// throw new RuntimeException("State is not covered in any of the rules.");
		return b.getState(x, y);
	}
	
	// I'm not sure if this is even logical, but storing state and parameters here should save us some headaches.
	State getState(int n) {
		return states.get(n);
	}
	
	// Warning: does not copy the list.
	List<State> getStates() {
		return states;
	}
	
	Map<String, Object> getParameters() {
		return params;
	}
}
