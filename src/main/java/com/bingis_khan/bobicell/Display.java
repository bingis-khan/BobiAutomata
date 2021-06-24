package com.bingis_khan.bobicell;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;
import java.util.function.Consumer;

import javax.swing.JFrame;

public class Display {
	// Size of cell grid.
	private int hTiles, vTiles;
	
	/* Window / Frame */
	private final JFrame frame;
	private final Canvas canvas;
	
	// BufferStrategy for rendering.
	BufferStrategy bs;
	
	
	// Simulation configuration.
	private final Board board;
	
	// Exit value.
	private boolean stop = false;
	
	private boolean paused = false;
	
	// Amount of time the automata updates per second.
	private int updatesPerSecond = 1000;
	
	private final static String WINDOW_NAME = "Bobi Automaton Simulator";
	
	
	public Display(final int initialWidth, final int initialHeight, 
			Board board) {
		assert hTiles > 0 : "Number of horizontal tiles must be greater than 0.";
		assert vTiles > 0 : "Number of vertical tiles must be greater than 0.";
		
		this.board = board;
		
		this.hTiles = board.getWidth(); // Number of tiles in each dimension.
		this.vTiles = board.getHeight();
		
		// Stop flickering while resizing.
		Toolkit.getDefaultToolkit().setDynamicLayout(false);
		
		// Initialize window.
		canvas = makeCanvas(initialWidth, initialHeight);
		frame = makeFrame(canvas, initialWidth, initialHeight, WINDOW_NAME);
	}
	
	public void run() {
		double timePerTick = 1_000_000_000 / updatesPerSecond;
		double delta = 0;
		long now;
		long lastTime = System.nanoTime();
		long timer = 0;
		int ticks = 0;
		
		while(!stop) {
			now = System.nanoTime();
			delta+= (now - lastTime) / timePerTick;
			timer += now - lastTime;
			lastTime = now;
			if(delta >= 1 && !paused) {
				board.update();
				
				ticks++;
				delta--;
			}
			if(timer >= 1000000000) {
				System.out.println(ticks);
				ticks = 0;
				timer = 0;
			}
			render();
		}
	}
	
	/**
	 * Method for rendering the contents of the display.
	 */
	public void render() {
		bs = canvas.getBufferStrategy();
		if (bs == null) {
			canvas.createBufferStrategy(3);
			return;
		}
		
		Graphics g = bs.getDrawGraphics();
		
		drawScreen(g);
		
		bs.show();
		g.dispose();
	}
	
	private void drawScreen(Graphics g) {
		int screenWidth = canvas.getWidth();
		int screenHeight = canvas.getHeight();
		
		// How we doin' it:
		// Tile width = screen width / hTiles
		// Probably won't be fit whole screen.
		// Also, if tile size is the same, looks bad. (see: my previous simulator)
		// So, what we do is make tile size variable.
		// floor(screen width/hTiles) <= tile size <= floor(screen width/hTiles) + 1
		// Or, because we're working on integers, it's either
		//  floor(screen width/hTiles) or floor(screen width/hTiles) + 1.
		// And its always closer to one value than the other.
		// ----------------------------------
		// A'ight. So that's what I came up with:
		// Precalculate xDif and yDif.
		// dif = size / tiles - floor(size / tiles)
		// Have xAll and yAll.
		// For each tile, (1D for the sake of example) draw(c, floor + round(all))
		// And all - round(all). Would be the same with branching like: all >= 0.5 etc.
		// And add it to remaining. Then repeat. Uhh... you'll see.
		
		final int ftWidth = screenWidth / hTiles, ftHeight = screenHeight / vTiles;
		final double xDif = (screenWidth / (double)hTiles) - ftWidth, 
					yDif = (screenHeight / (double)vTiles) - ftHeight;
		
		// Lambdas can't have no permanent variables.
		board.forEachCell(new Consumer<State>() {
			double xAll = 0, yAll = yDif; // Different to xAll. I want to keep all vertical stuff in one if,
										  // that's why the order is a bit weird.
			int xFilled = 0, yFilled = 0;
			
			@Override
			public void accept(State s) {
				xAll += xDif;
				
				assert xAll < 1 : "xAll must be greater than 1.";
				
				// round (0.5 + x) = 1 for x > 0. So, after the subtraction later,
				// all = -0.5 + x (where x > 0)
				assert xAll > -0.5 : "xAll must be greater or equal to -0.5.";
				assert yAll < 1 : "yAll must be greater than 1.";
				assert yAll > -0.5 : "yAll must be greater or equal to -0.5.";
				
				// Decide whether to add the extra part.
				final int xExtra = (int)Math.round(xAll),
						  yExtra = (int)Math.round(yAll);
				
				// Actually draw. Huh.
				g.drawImage(s.image, xFilled, yFilled, ftWidth + xExtra, ftHeight + yExtra, null);
				
				// Update the amount of filled screen.
				xFilled += ftWidth + xExtra;
				
				// And update accumulated "accumulated diffs".
				xAll -= xExtra;
				
				// Check if end of screen, then reset horizontal and 'increment' vertical.
				if (xFilled > screenWidth - ftWidth) {
					yFilled += ftHeight + yExtra;
					yAll -= yExtra;
					
					// See yAll declaration.
					yAll += yDif;
					
					xFilled = 0;
					xAll = 0;
				}
				
				// TODO: This algorithm can sometimes leave bottom pixels empty. Fix that.
				// Also, somehow rightmost seem ok?
			}
			
		});
	}
	
	private static Canvas makeCanvas(int width, int height) {
		Canvas canvas = new Canvas();
		Dimension dim = new Dimension(width, height);
		canvas.setPreferredSize(dim);
		canvas.setFocusable(false);
		canvas.setIgnoreRepaint(true);
		return canvas;
	}
	
	private JFrame makeFrame(Canvas c, int width, int height, String name) {
		JFrame frame = new JFrame(name);
		frame.setSize(width, height);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(true);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		
		// Quick key listener for basing control.
		frame.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {}

			@Override
			public void keyPressed(KeyEvent e) {}
			
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyChar() == ' ')
					paused = !paused;
				
				if (e.getKeyChar() == 's')
					synchronized(board) {
						board.update();
					}
				
			}
			
		});
		
		frame.add(c);
		frame.pack();
		return frame;
	}
}
