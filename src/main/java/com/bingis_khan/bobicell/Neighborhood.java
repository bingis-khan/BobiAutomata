package com.bingis_khan.bobicell;

public enum Neighborhood {
	MOORE, VON_NEUMANN;
	
	public static Neighborhood convertNeighborhood(String raw) {
		switch (raw) {
		case "Moore":
			return MOORE;
			
		case "VonNeumann":
			return VON_NEUMANN;
			
		default:
			return null;
		}
	}
}
