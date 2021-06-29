package com.bingis_khan.bobicell;

import java.awt.Color;
import java.awt.image.BufferedImage;

// Class representing each state.
// Each state has a unique object,
// so '==' can be used to check for equality.
public class State {
	private static final int DEFAULT_WIDTH = 16, DEFAULT_HEIGHT = 16;
	
	
	final String name;
	BufferedImage image;
	
	State(String name) {
		this.name = name;
	}
	
	
	public void assignColor(Color c) {
		var colorBox = new BufferedImage(DEFAULT_WIDTH, DEFAULT_HEIGHT, BufferedImage.TYPE_3BYTE_BGR);
		
		var g = colorBox.createGraphics();
		g.setColor(c);
		g.fillRect(0,  0, colorBox.getWidth(), colorBox.getHeight());
		g.dispose();
		
		image = colorBox;
	}
	
	
	@Override
	public String toString() {
		return name;
	}
}
