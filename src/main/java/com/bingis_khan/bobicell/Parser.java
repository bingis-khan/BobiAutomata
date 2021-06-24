package com.bingis_khan.bobicell;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.bingis_khan.bobicell.TokenType.*;

class Parser {
	private static class ParseError extends RuntimeException {};
	
	private final Scanner sc;
	private final Functions functions;
	private final Map<String, State> states = new HashMap<>();
	private final Map<String, Runnable> sections = new HashMap<>();
	
	
	private Token current, previous;
	
	private final ErrorReporter er;
	
	
	public Parser(Scanner sc, Functions functions, ErrorReporter er) {
		this.sc = sc;
		this.er = er;
		this.functions = functions;
		
		sections.put("States", this::stateDef);
		sections.put("Rules", this::ruleDef);
		sections.put("Defaults", this::defaultsDef);
		sections.put("Graphics", this::graphicsDef);

		advance();
	}
	
	
	Ruleset parse(Scanner sc) {
		automatonDefinition();
		return null;
	}
	
	
	private void automatonDefinition() {
		skipNewlines();
		while (!match(EOF)) {
			consume(SECTION, "Expect section name.");
			
			var sectionName = previous.literal;
			if (!sections.containsKey(sectionName))
				error(String.format("Section '%s' does not exist.", sectionName));
			
			var sectionParsing = sections.get(sectionName);
			if (sectionParsing == null)
				error(String.format("Section '%s' was already defined.", sectionName));
			
			sectionParsing.run();
			sections.put(sectionName, null);	// Define section. Section can only be defined once.
		}
	}
	
	
	private void stateDef() {
		consume(NEWLINE, "A section declaration starts with a newline after the name.");
		
		do {
			consume(STATE, "Expect a state declaration in 'States' section.");
			
			var stateName = previous.literal;
			State state = new State(stateName);
			states.put(stateName, state);
		} while (match(COMMA));
		
		consume(NEWLINE, "A newline must follow a 'States' definition.");
	}
	
	
	private void ruleDef() {
		consume(LEFT_PAREN, "Expect '(' to declare neigborhood type.");
		consume(IDENTIFIER, "Expect neighborhood type - either Moore or Neumann");

		var neigborhood = previous.literal; // Maybe check here if neighborhood exists?
		consume(RIGHT_PAREN, "Expect ')' after neighborhood declaration.");
		consume(NEWLINE, "Expect newline after 'Rules' declaration.");
		
		
		skipNewlines();
		while (!match(SECTION)) {
			rule(neigborhood);
			skipNewlines();
		}
	}
	
	
	private Rule rule(String neighborhood) {
		var conditions = new ArrayList<Condition>();
		if (match(STATE)) {
			var prerequisites = new ArrayList<State>();
			prerequisites.add(state(previous.literal));
			
			while (match(COMMA)) {
				consume(STATE, "Expect state.");
				prerequisites.add(state(previous.literal));
			}
			
			// We can do 'if not match(RIGHT_ARROW)'
			consume(COLON, "Expect ':'.");
			
			conditions.add(new Prerequisite(prerequisites));
		}
		
		if (!match(RIGHT_ARROW)) {
			do {
				conditions.add(functions.call(this));
			} while (match(AND));
		}
		
		// We want to reduce the amount of nested conditions.
		Condition condition = null;
		if (conditions.size() > 1)
			new And(conditions);
		else
			conditions.get(0);
		
		consume(RIGHT_ARROW, "Expect '->'.");
		consume(STATE, "Expect state.");
		
		State state = state(previous.literal);
		
		consume(NEWLINE, "Expect newline after the end of this statement.");
		
		return new Rule(condition, state);
	}
	
	
	private State state(String stateName) {
		if (!states.containsKey(stateName))
			error(String.format("State '%s' does not exist.", stateName));
		
		return states.get(stateName);
	}
	
	
	/////////////////////
	// Helper methods. //
	/////////////////////
	
	private boolean match(TokenType type) {
		if (current.type != type)
			return false;
		
		advance();
		return true;
	}
	
	
	private void consume(TokenType type, String message) {
		if (current.type == type) {
			advance();
			return;
		}
		
		error(message);
	}
	
	
	private void advance() {
		previous = current;
		current = sc.nextToken();
	}
	
	
	private void error(String message) {
		er.error("Parsing", message, current);
		throw new ParseError();
	}
	
	
	private void skipNewlines() {
		while (current.type == NEWLINE) advance();
	}
}
