package com.bingis_khan.bobicell;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;



public class BobiAutomata {
	private static final int WINDOW_WIDTH = 800, WINDOW_HEIGHT = 600;
	
	
	public static HashMap<String, Object> setupParameters(Ruleset rs) {
		var parameters = new HashMap<String, Object>();
		
		parameters.put("Border", rs.getState(0));
		parameters.put("Default", rs.getState(0));
		
		return parameters;
	}
	
	public static Board makeBoard(int width, int height, Ruleset rs, Map<String, Object> params) {
		return Board.emptyBoard(width, height, rs, (State)params.get("Default"), (State)params.get("Border"));
	}
	
	public static void usage() {
		System.err.println("Usage: [width] [height] [.ca file]");
	}
	
	public static void main(String[] args) {
		
		// Program arguments.
		if (args.length != 3) {
			usage();
			return;
		}
		
		int width, height;
		String source;
		try {
			width = Integer.parseInt(args[0]);
			height = Integer.parseInt(args[1]);
			source = Files.readString(Paths.get(args[2]));
		} catch (NumberFormatException e) {
			System.err.println("Width and height must both be positive integers.");
			usage();
			return;
			
		} catch (IOException e) {
			System.err.println("Error while opening file '" + args[2] + "'.");
			usage();
			return;
		}
		
		if (width < 1) {
			System.err.println("Width must be a positive integer.");
			usage();
			return;
		}
		
		if (height < 1) {
			System.err.println("Height must be a positive integer.");
			usage();
			return;
		}
		
		
		// Actual stuff begins.
		var er = new ErrorReporter();
		var sc = new Scanner(source, er);
		
		// Parsing (+ scanning).
		var ruleset = new Parser(sc, Functions.all(), er).parse();
		if (er.hadError())
			return;

		
		// Check if user-defined parameters are 'actual' parameters.
		var parameters = ruleset.getParameters();
		var defaults = setupParameters(ruleset);
		
		if (!parameters.keySet().containsAll(defaults.keySet())) {
			// If not:
			var unknown = parameters.keySet();
			unknown.removeAll(defaults.keySet());
			
			unknown.forEach((us) -> er.error("Parameters", String.format("Unknown parameter \"%s\".", us)));
		
			return;
		}
		
		// If they are, overwrite defaults with user-defined params.
		var merged = defaults;	// Name change.
		merged.putAll(parameters);
		
		var board = makeBoard(width, height, ruleset, merged);
		var display = new Display(WINDOW_WIDTH, WINDOW_HEIGHT, board, ruleset.getStates());
		
		display.run();
	}
}
