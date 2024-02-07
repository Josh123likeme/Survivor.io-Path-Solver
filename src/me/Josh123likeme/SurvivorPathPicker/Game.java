package me.Josh123likeme.SurvivorPathPicker;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;

import me.Josh123likeme.SurvivorPathPicker.InputListener.*;

public class Game extends Canvas implements Runnable {

	public static final int INITIAL_WIDTH = 400, INITIAL_HEIGHT = 400;
	
	private Thread thread;
	private boolean running = false;
	
	public MouseWitness mouseWitness;
	public KeyboardWitness keyboardWitness;
	
	private double deltaFrame;
	private int fps;
	
	private Tile[][] board;
	private final int BOARD_WIDTH = 7;
	private final int BOARD_HEIGHT = 9;
	private final int NUMBER_OF_STEPS = 15;
	
	private final int START_X = 3;
	private final int START_Y = 4;
	
	private double tileSize = 150;
	private int tileBorderWidth = 2;
	
	private Solver solver;
	
	public Game() {
		
		board = new Tile[9][7];
		
		for (int y = 0; y < board.length; y++) {
			
			for (int x = 0; x < board[y].length; x++) {
				
				board[y][x] = Tile.EMPTY;
				
			}
			
		}
		
		solver = new Solver();
		
		initInputs();
		
		new Window(INITIAL_WIDTH, INITIAL_HEIGHT, "Survivor.io Path Picker", this);
		
	}
	
	private void initInputs() {
		
		mouseWitness = new MouseWitness();
		keyboardWitness = new KeyboardWitness();
		
		addMouseListener(mouseWitness);
		addMouseMotionListener(mouseWitness);
		addKeyListener(keyboardWitness);
		
		requestFocus();
		
	}
	
	public synchronized void start() {
		
		thread = new Thread(this);
		thread.start();
		running = true;
		
	}
	
	public synchronized void stop() {
		
		try {
			
			thread.join();
			running = false;
		}
		
		catch(Exception e) {e.printStackTrace();}
		
	}
	
	public void run() {
		
		double targetfps = 60d;
		long targetDeltaFrame = Math.round((1d / targetfps) * 1000000000);
		long lastSecond = System.nanoTime();
		int frames = 0;
		
		long lastFrame = System.nanoTime();
		
		while (running) {
			
			frames++;
			
			if (lastSecond + 1000000000 < System.nanoTime()) {
				
				fps = frames;
				
				frames = 0;
				
				lastSecond = System.nanoTime();
				
				targetDeltaFrame = Math.round((1d / targetfps) * 1000000000);
				
			}
			
			//starting to push frame
			
			long nextTime = System.nanoTime() + targetDeltaFrame;
			
			deltaFrame = ((double) (System.nanoTime() - lastFrame)) / 1000000000;
			
			lastFrame = System.nanoTime();
			
			preFrame();
			
			paint();
			
			//finished pushing frame
			
			keyboardWitness.purgeTypedKeys();
			mouseWitness.purgeClickedButtons();
			
			while (nextTime > System.nanoTime());
			
		}
		
		stop();
		
	}
	
	private void preFrame() {
		
		//resize tile size based on window size
		tileSize = getWidth() / board[0].length < getHeight() / board.length ? getWidth() / board[0].length : getHeight() / board.length;
		
		//clicked
		if (mouseWitness.isLeftClicked()) {
			
			int x = (int) (mouseWitness.getMouseX() / tileSize);
			int y = (int) (mouseWitness.getMouseY() / tileSize);
			
			//not on board
			if (x > board[0].length - 1 || y > board.length - 1) {
				
				//start button
				if (x == board[0].length && y == 0 && !solver.isRunning()) {
					
					System.out.println("starting");
					
					double[][] boardWithWeights = new double[board.length][board[0].length];
					
					for (int iy = 0; iy < board.length; iy++) {
						
						for (int ix = 0; ix < board[iy].length; ix++) {
							
							boardWithWeights[iy][ix] = board[iy][ix].weight;
							
						}
						
					}
					
					solver.findbest(boardWithWeights, NUMBER_OF_STEPS, START_X, START_Y);
					
					System.out.println("best weight: " + solver.getBestWeight());
					
				}
				
			}
			//on board, but not on start position, as start position cannot have weight
			else if (!(x == START_X && y == START_Y)){
				
				int currentTileIndex = 0;
				
				for (int i = 0; i < Tile.values().length; i++) {
					
					if (board[y][x].equals(Tile.values()[i])) {
						
						currentTileIndex = i;
						break;
						
					}
					
				}
				
				board[y][x] = Tile.values()[(currentTileIndex + 1) % Tile.values().length];
				
			}
			
		}
		
	}

	private void paint() {
	
		BufferStrategy bufferStrategy = this.getBufferStrategy();
		if(bufferStrategy == null) {
			this.createBufferStrategy(3);
			return;
		}
		
		Graphics graphics = bufferStrategy.getDrawGraphics();
		
		//basic black background to stop flashing
		graphics.setColor(Color.black); 
		graphics.fillRect(0, 0, getWidth(), getHeight());
		
		//put rendering stuff here
		
		for (int y = 0; y < board.length; y++) {
			
			for (int x = 0; x < board[y].length; x++) {
				
				graphics.setColor(new Color(150,150,150));
				
				graphics.fillRect((int) (x * tileSize), (int) (y * tileSize), (int) (tileSize), (int) (tileSize));
				
				switch (board[y][x]) {
				
				case EMPTY:
					graphics.setColor(new Color(200,200,200));
					break;
					
				case GREEN:
					graphics.setColor(new Color(50,255,50));
					break;
				
				case PURPLE:
					graphics.setColor(new Color(255,0,200));
					break;
					
				case RED:
					graphics.setColor(new Color(255,50,50));
					break;
				}
				
				if (x == START_X && y == START_Y) graphics.setColor(new Color(255,127,50));
				
				graphics.fillRect((int) (x * tileSize + tileBorderWidth),
								  (int) (y * tileSize + tileBorderWidth),
								  (int) (tileSize - 2 * tileBorderWidth),
								  (int) (tileSize - 2 * tileBorderWidth));
				
			}
			
		}
		
		graphics.setColor(new Color(150,150,150));
		
		graphics.fillRect((int) (board[0].length * tileSize), 0, (int) (tileSize), (int) (tileSize));
		
		graphics.setColor(new Color(200,200,200));
		
		graphics.fillRect((int) (board[0].length * tileSize + tileBorderWidth),
						  (int) (tileBorderWidth),
						  (int) (tileSize - 2 * tileBorderWidth),
						  (int) (tileSize - 2 * tileBorderWidth));
		
		if (solver.isRunning()) graphics.setColor(new Color(0,150,0));
		else graphics.setColor(new Color(0,255,0));
		
		graphics.fillPolygon(
		   new int[] {
				(int) (board[0].length * tileSize + tileBorderWidth),
				(int) (board[0].length * tileSize + tileBorderWidth),
				(int) ((board[0].length + 1) * tileSize - 2 * tileBorderWidth)
		}, new int[] {
				(int) (tileBorderWidth),
				(int) (tileSize - 2 * tileBorderWidth),
				(int) (tileSize / 2)
		}, 3);
		
		//draw path
		if (solver.getBestDirection() != null) {
			
			graphics.setColor(new Color(0,0,0));
			
			int currX = START_X;
			int currY = START_Y;
			
			for (int i = 0; i < solver.getBestDirection().length; i++) {
				
				graphics.drawLine((int) ((currX + 0.5) * tileSize),
						          (int) ((currY + 0.5) * tileSize),
								  (int) ((currX + solver.getBestDirection()[i].dx + 0.5) * tileSize),
								  (int) ((currY + solver.getBestDirection()[i].dy + 0.5) * tileSize));
				
				currX += solver.getBestDirection()[i].dx;
				currY += solver.getBestDirection()[i].dy;
				
			}
			
		}
		
		//this pushes the graphics to the window
		bufferStrategy.show();
		
	}
	
	public double getDeltaFrame() {
		
		return deltaFrame;
		
	}
	
	private enum Tile {
		
		EMPTY(0),
		GREEN(5),
		PURPLE(10),
		RED(25),
		
		;
		
		public double weight;
		
		Tile(double weight) {
			
			this.weight = weight;
			
		}
		
	}
	
}
