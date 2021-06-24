package com.bingis_khan.bobicell;

import java.awt.image.BufferedImage;

// Class representing each state.
// Each state has a unique object,
// so '==' can be used to check for equality.
public class State {
	final String name;
	BufferedImage image;
	
	State(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return name;
	}
}
