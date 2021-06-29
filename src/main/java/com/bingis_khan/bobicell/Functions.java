package com.bingis_khan.bobicell;

import static com.bingis_khan.bobicell.TokenType.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.BiFunction;
import java.util.function.Function;

/*
 *  Right now it's pretty basic. What I want to do with it in the future:
 *   extract "is" from functions: requires change in parsing. (ie. sum of NUMBER | is | greater than 3)
 *   optional arguments.
 *   Finite-state machine - overlapping functions = more freedom.
 */
class Functions {
	private final Map<String, CustomFunction> functions = new HashMap<>();

	class FunctionBuilder {
		private final Functions caller;
		private final String name;

		private final List<Function<Parser, Object>> structure = new ArrayList<>();

		FunctionBuilder(Functions caller, String name) {
			this.caller = caller;
			this.name = name;
		}

		FunctionBuilder then(String identifier) {
			structure.add((p) -> {
				var token = p.consumeIdentifier("\"" + identifier + "\" expected.");

				if (!token.literal.equals(identifier))
					p.error("\"" + identifier + "\" expected.", token);

				return null;
			});

			return this;
		}

		FunctionBuilder argument(TokenType type) {
			structure.add((p) -> {
				var lit = p.consume(type, "\"" + type + "\" expected.").literal;

				switch (type) {
				case STATE:
					return p.state(lit);
				case NUMBER:
					return Integer.parseInt(lit);
				default:
					assert false : "Unhandled type.";
					return null;
				}
			});

			return this;
		}

		FunctionBuilder is() {
			structure.add((p) -> {
				p.consume(IS, "\"is\" expected.");
				return null;
			});

			return this;
		}

		Functions whichExecutesAs(BiFunction<List<Object>, Neighborhood, Condition> pr) {
			var f = new CustomFunction(structure, pr);

			caller.addFunction(name, f);
			return caller;
		}
	}

	private class CustomFunction {
		private final List<Function<Parser, Object>> structure;
		private final BiFunction<List<Object>, Neighborhood, Condition> compiler;

		CustomFunction(List<Function<Parser, Object>> structure,
				BiFunction<List<Object>, Neighborhood, Condition> compiler) {
			this.structure = structure;
			this.compiler = compiler;
		}

		Condition compile(Parser parser) {
			var args = new ArrayList<Object>();
			structure.forEach((pred) -> {
				var t = pred.apply(parser);

				if (t != null)
					args.add(t);
			});

			return compiler.apply(args, parser.getNeighborhood());
		}
	}

	public FunctionBuilder newFunction(String name) {
		return new FunctionBuilder(this, name);
	}

	public Condition compile(Parser parser) {
		var ident = parser.consumeIdentifier("Expect function name.");

		if (!functions.containsKey(ident.literal))
			parser.error("Function with this name does not exist.", ident);

		return functions.get(ident.literal).compile(parser);
	}

	private void addFunction(String name, CustomFunction f) {
		functions.put(name, f);
	}

	public static Functions all() {
		final var r = new Random();
		
		return new Functions().newFunction("sum").then("of").argument(STATE).is().argument(NUMBER)
				.whichExecutesAs((args, nh) -> {
					var state = (State) args.get(0);
					var num = (int) args.get(1);

					switch (nh) {
					case MOORE:
						return new Condition() {
							private int sum(Board b, int x, int y) {
								var sum = 0;

								for (var xx = x - 1; xx <= x + 1; xx++)
									for (var yy = y - 1; yy <= y + 1; yy++)
										if (b.getState(xx, yy) == state && !(x == xx && y == yy))
											sum++;

								return sum;
							}

							@Override
							public boolean evaluate(Board b, int x, int y) {
								return sum(b, x, y) == num;
							}

						};
					case VON_NEUMANN:
						return new Condition() {

							@Override
							public boolean evaluate(Board b, int x, int y) {
								var sum = 0;

								if (b.getState(x - 1, y) == state)
									sum++;
								if (b.getState(x + 1, y) == state)
									sum++;
								if (b.getState(x, y - 1) == state)
									sum++;
								if (b.getState(x, y + 1) == state)
									sum++;

								return sum == num;
							}

						};
					default:
						assert false : "This means that there is an unhandled neighborhood (" + nh + ").";
					}
					return null;
				})

				.newFunction("north").is().argument(STATE).whichExecutesAs((args, nh) -> {
					var state = (State) args.get(0);

					return (b, x, y) -> b.getState(x, y - 1) == state;
				})

				.newFunction("south").is().argument(STATE).whichExecutesAs((args, nh) -> {
					var state = (State) args.get(0);

					return (b, x, y) -> b.getState(x, y + 1) == state;
				})

				.newFunction("west").is().argument(STATE).whichExecutesAs((args, nh) -> {
					var state = (State) args.get(0);

					return (b, x, y) -> b.getState(x - 1, y) == state;
				})

				.newFunction("east").is().argument(STATE).whichExecutesAs((args, nh) -> {
					var state = (State) args.get(0);

					return (b, x, y) -> b.getState(x + 1, y) == state;
				})
				
				.newFunction("otherwise").whichExecutesAs((args, nh) -> ((b, x, y) -> true))
				
				.newFunction("random").argument(NUMBER).then("in").argument(NUMBER).then("chance")
					.whichExecutesAs((args, nh) -> {
						var chance = (int)args.get(0);
						var all = (int)args.get(1);
						
						return (b, x, y) -> r.nextInt(all) < chance;
					});
	}
}
