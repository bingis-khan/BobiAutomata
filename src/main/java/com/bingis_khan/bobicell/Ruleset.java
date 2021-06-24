package com.bingis_khan.bobicell;

import java.util.List;

class Ruleset {
	private final List<Rule> rules;
	
	
	Ruleset(List<Rule> rules) {
		this.rules = rules;
	}
	
	
	State nextState(Board b, int x, int y) {
		for (var rule : rules)
			if (rule.checkCondition(b, x, y))
				return rule.next;

		
		throw new RuntimeException("State is not covered in any of the rules.");
	}
}
