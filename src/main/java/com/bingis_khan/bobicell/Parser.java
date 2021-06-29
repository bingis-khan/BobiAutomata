package com.bingis_khan.bobicell;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import static com.bingis_khan.bobicell.TokenType.*;

class Parser {
	@SuppressWarnings("serial")
	private static class ParseError extends RuntimeException {
	};

	private final Scanner sc;
	private final Functions functions;
	private final List<State> states = new ArrayList<>();
	private final Map<String, Runnable> sections = new HashMap<>();
	private final Map<String, Object> parameters = new HashMap<>();
	private static final Map<String, Color> COLORS = new HashMap<>();

	static {
		COLORS.put("black", Color.BLACK);
		COLORS.put("white", Color.WHITE);
		COLORS.put("gray", Color.GRAY);
		COLORS.put("darkblue", Color.decode("#00008B"));
		COLORS.put("blue", Color.BLUE);
		COLORS.put("green", Color.GREEN);
		COLORS.put("darkgreen", Color.decode("#006400"));
		COLORS.put("lightblue", Color.decode("#ADD8E6"));
		COLORS.put("red", Color.RED);
		
		// Add the ability to define own colors.
		// (probably another token type, identify it with a '#' at the start.)
	}

	private Ruleset ruleset;
	private Neighborhood neighborhood;
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

	Ruleset parse() {
		automatonDefinition();

		if (sections.get("States") != null)
			error("'States' section not defined.");

		if (sections.get("'Rules'") != null)
			error("'Rules' section not defined.");

		return ruleset;
	}

	private void automatonDefinition() {
		skipNewlines();
		while (!match(EOF)) {
			try {
				consume(SECTION, "Expect section name.");

				var sectionName = previous.literal;
				if (!sections.containsKey(sectionName))
					throw error(String.format("Section '%s' does not exist.", sectionName));

				var sectionParsing = sections.get(sectionName);
				if (sectionParsing == null)
					throw error(String.format("Section '%s' was already defined.", sectionName));

				sectionParsing.run();
				sections.put(sectionName, null); // Define section. Section can only be defined once.
			} catch (ParseError e) {
				synchronize();
			}

			skipNewlines();
		}
	}

	private void stateDef() {
		consume(NEWLINE, "A section declaration starts with a newline after the name.");

		do {
			consume(STATE, "Expect a state declaration in 'States' section.");

			var stateName = previous.literal;
			State state = new State(stateName);
			states.add(state);
		} while (match(COMMA));

		consume(NEWLINE, "A newline must follow a 'States' definition.");
	}

	private void ruleDef() {
		consume(LEFT_PAREN, "Expect '(' to declare neigborhood type.");
		consume(IDENTIFIER, "Expect neighborhood type - either Moore or Neumann");

		var nh = Neighborhood.convertNeighborhood(previous.literal);
		if (nh == null)
			throw error("\"" + previous.literal + "\" neighborhood does not exist.", previous);

		neighborhood = nh;
		consume(RIGHT_PAREN, "Expect ')' after neighborhood declaration.");
		consume(NEWLINE, "Expect newline after 'Rules' declaration.");

		var rules = new ArrayList<Rule>();

		skipNewlines();
		while (!check(SECTION) && !check(EOF)) {
			rules.add(rule());
			skipNewlines();
		}

		ruleset = new Ruleset(states, rules, parameters);
	}

	private Rule rule() {
		var conditions = new ArrayList<Condition>();
		if (match(STATE)) {
			var prerequisites = new ArrayList<State>();
			prerequisites.add(state(previous.literal));

			while (match(COMMA)) {
				consume(STATE, "Expect state.");
				prerequisites.add(state(previous.literal));
			}

			// Colon is optional if all we have are preconditions.
			if (!check(RIGHT_ARROW))
				consume(COLON, "Expect ':'.");

			conditions.add(new Prerequisite(prerequisites));
		}

		if (!check(RIGHT_ARROW)) {
			do {
				conditions.add(functions.compile(this));
			} while (match(AND));
		}

		// We want to reduce the amount of nested conditions.
		Condition condition = null;
		if (conditions.size() > 1)
			condition = new And(conditions);
		else if (conditions.size() == 1)
			condition = conditions.get(0);

		consume(RIGHT_ARROW, "Expect '->'.");
		consume(STATE, "Expect state.");

		State state = state(previous.literal);

		if (!match(EOF))
			consume(NEWLINE, "Expect newline after the end of this statement.");

		return new Rule(condition, state);
	}

	State state(String stateName) {
		for (var state : states)
			if (state.name.equals(stateName))
				return state;
		
		throw error(String.format("State '%s' does not exist.", stateName));
	}

	private void defaultsDef() {
		consume(NEWLINE, "Section declaration must end with a newline.");

		skipNewlines();
		while (!check(SECTION) && !check(EOF)) {
			// Java has problems with tuples, so...
			definition((paramToken, state) -> {
				var param = paramToken.literal;

				parameters.put(param, state);
			});

			skipNewlines();
		}
	}

	private void definition(BiConsumer<Token, State> f) {
		consume(IDENTIFIER, "Expect identifier.");
		var name = previous;

		consume(IS, "Expect 'is'.");
		consume(STATE, "Expect state.");
		var state = state(previous.literal);

		if (!match(EOF))
			consume(NEWLINE, "Expect newline.");

		f.accept(name, state);
	}

	private void graphicsDef() {
		consume(NEWLINE, "Section declaration must end with a newline.");

		skipNewlines();
		while (!check(SECTION) && !check(EOF)) {
			// Java has problems with tuples, so...
			definition((colorToken, state) -> {
				var color = colorToken.literal;
				if (!COLORS.containsKey(color))
					throw error("Unknown color.", colorToken);

				state.assignColor(COLORS.get(color));
			});

			skipNewlines();
		}
	}

	private void synchronize() {
		while (!check(SECTION) && !check(EOF))
			advance();
	}

	/////////////////////
	// Helper methods. //
	/////////////////////

	private boolean match(TokenType type) {
		if (!check(type))
			return false;

		advance();
		return true;
	}

	Token consume(TokenType type, String message) {
		if (check(type)) {
			advance();
			return previous;
		}

		throw error(message);
	}

	Token consumeIdentifier(String message) {
		consume(IDENTIFIER, message);

		return previous;
	}

	private boolean check(TokenType type) {
		return current.type == type;
	}

	private void advance() {
		previous = current;
		current = sc.nextToken();
	}

	private ParseError error(String message) {
		return error(message, current);
	}

	ParseError error(String message, Token culprit) {
		er.error("Parsing", message, culprit);
		return new ParseError();
	}

	private void skipNewlines() {
		while (match(NEWLINE))
			;
	}

	Neighborhood getNeighborhood() {
		return neighborhood;
	}
}
