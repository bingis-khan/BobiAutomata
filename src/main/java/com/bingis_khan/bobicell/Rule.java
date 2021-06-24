package com.bingis_khan.bobicell;

public class Rule {
	private final Condition condition;
	final State next;
	
	
	
	public Rule(Condition condition, State next) {
		this.condition = condition;
		this.next = next;
	}
	
	
	public boolean checkCondition(Board b, int x, int y) {
		return condition == null ? true : condition.evaluate(b, x, y);
	}
	
	
	@Override
	public String toString() {
		return "Rule(" + condition + ", " + next + ")";
	}
}
