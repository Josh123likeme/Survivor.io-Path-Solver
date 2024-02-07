package me.Josh123likeme.SurvivorPathPicker;

public enum Direction {
	
	UP(0,-1),
	RIGHT(1,0),
	DOWN(0,1),
	LEFT(-1,0),
	
	;
	
	int dx;
	int dy;
	
	Direction(int xChange, int yChange) {
		
		dx = xChange;
		dy = yChange;
		
	}
	
	public Direction getOpposite() {
		
		switch (this) {
		
		case UP: return DOWN;
		case RIGHT: return LEFT;
		case DOWN: return UP;
		case LEFT: return RIGHT;
		
		}
		
		return null;
		
	}
	
}
