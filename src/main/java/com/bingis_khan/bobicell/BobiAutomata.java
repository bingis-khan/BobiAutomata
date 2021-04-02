package com.bingis_khan.bobicell;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Random;

public class BobiAutomata {
	public static void main(String[] args) {
		BufferedImage white = new BufferedImage(16, 16, BufferedImage.TYPE_INT_RGB), 
				black = new BufferedImage(16, 16, BufferedImage.TYPE_INT_RGB),
				fuck = new BufferedImage(16, 16, BufferedImage.TYPE_INT_RGB);
		
		Graphics gWhite = white.getGraphics();
		gWhite.setColor(Color.RED);
		gWhite.fillRect(0, 0, 16, 16);
		gWhite.dispose();
		
		Graphics gBlack = black.getGraphics();
		gBlack.setColor(Color.BLUE);
		gBlack.fillRect(0, 0, 16, 16);
		gBlack.dispose();
		
		Graphics gFuck = fuck.getGraphics();
		gFuck.setColor(Color.LIGHT_GRAY);
		gFuck.fillRect(0, 0, 16, 16);
		gFuck.dispose();
		
		State s1 = new State("S1", white),
				s2 = new State("S2", black),
				s3 = new State("S3", fuck);
		
		final Random r = new Random();
		Ruleset rs = new Ruleset() {
			@Override
			State nextState(Board b, int x, int y) {
				if (r.nextInt(1000000) == 0) {
					return s2;
				}
				
				if (b.getState(x, y) == s2)
					return s3;
				
				if (b.getState(x, y) == s3)
					return s1;
				
				if (b.getState(x, y) == s1 && (b.getState(x, y-1) == s2 || b.getState(x, y+1) == s2 
						|| b.getState(x+1, y) == s2 || b.getState(x-1, y) == s2))
					return s2;
				
				return s1;
			}
		};
		
		Board board = Board.emptyBoard(200, 200, rs, s1);
		Display d = new Display(800, 600, board);
		d.run();
	}
}
