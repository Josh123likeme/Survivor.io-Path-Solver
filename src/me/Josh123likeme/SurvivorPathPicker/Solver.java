package me.Josh123likeme.SurvivorPathPicker;

public class Solver {
	
	private boolean running = false;
	
	private double bestWeight = -1;
	private Direction[] bestDirection = null;
	
	public void findbest(final double[][] board, final int steps, final int startX, final int startY) {
		
		running = true;
		
		double bestWeight = -1;
		int bestContender = 0;
		
		for (int i = 0; i < Math.pow(4, steps); i++) {
			
			Direction[] dirs = generateDirsFromNumber(i, steps);
			
			for (int j = 1; j < dirs.length; j++) {
				
				//path backtracks
				if (dirs[j].getOpposite().equals(dirs[j - 1])) continue;
				
			}
			
			double weight = generateWeightForPath(board, dirs, startX, startY);
			
			if (weight > bestWeight) {
				bestWeight = weight;
				bestContender = i;
			}
			
			if (i % ((int) (Math.pow(4, steps) / 100)) == 0) System.out.println(((double) i * 100 / Math.pow(4, steps)) + "% (" + i + "/" + Math.pow(4, steps) + ")");
			
		}
		
		running = false;
		
		this.bestWeight = bestWeight;
		this.bestDirection = generateDirsFromNumber(bestContender, steps);
		
		return;
		
	}
	
	private double generateWeightForPath(final double[][] board, final Direction[] direction, final int startX, final int startY) {
		
		double weight = 0;
		
		int[] xsBeenTo = new int[direction.length + 1];
		int[] ysBeenTo = new int[direction.length + 1];
		
		xsBeenTo[0] = startX;
		ysBeenTo[0] = startY;
		
		int currentX = startX;
		int currentY = startY;
		
		for (int i = 0; i < direction.length; i++) {
			
			Direction dir = direction[i];
			
			currentX += dir.dx;
			currentY += dir.dy;
			
			//check if already been in this square
			for (int j = 0; j < i + 1; j++) {
				
				//entered a square thats already been used, so invalid path
				if  (xsBeenTo[j] == currentX && ysBeenTo[j] == currentY) return -1;
				
			}
			
			//left the board
			if (currentX < 0 || currentX >= board[0].length || currentY < 0 || currentY >= board.length) return - 1;
			
			xsBeenTo[i + 1] = currentX;
			ysBeenTo[i + 1] = currentY;
			
			weight += board[currentY][currentX];
			
		}
		
		return weight;
		
	}
	
	private Direction[] generateDirsFromNumber(int num, int maxDirs) {
		
		Direction[] dirs = new Direction[maxDirs];
		
		for (int j = 0; j < maxDirs; j++) {
			
			dirs[j] = Direction.values()[num % 4];
			
			num = num / 4;
			
		}
		
		return dirs;
		
	}
	
	public boolean isRunning() {
		
		return running;
		
	}
	
	public double getBestWeight() {
		
		return bestWeight;
		
	}
	
	public Direction[] getBestDirection() {
		
		return bestDirection;
		
	}
	
}
