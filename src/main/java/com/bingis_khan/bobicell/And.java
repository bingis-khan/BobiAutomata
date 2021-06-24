package com.bingis_khan.bobicell;

import java.util.List;

public class And implements Condition {
	private final List<Condition> conditions;
	
	
	public And(List<Condition> conditions) {
		this.conditions = conditions;
	}
	
	
	@Override
	public boolean evaluate(Board b, int x, int y) {
		for (var condition : conditions)
			if (!condition.evaluate(b, x, y))
				return false;
			
		return true;
	}
	
	
	@Override
	public String toString() {
		return "And(" + conditions + ")";
	}

}
