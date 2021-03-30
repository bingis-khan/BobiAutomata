package com.bingis_khan.bobicell;

import java.awt.image.BufferedImage;

// Class representing each state.
// Each state has a unique object,
// so '==' can be used to check for equality.
public class State {
	final String name;
	final BufferedImage image;
	
	State(String name, BufferedImage image) {
		this.name = name;
		this.image = image;
	}
	
	@Override
	public String toString() {
		return name;
	}
}
