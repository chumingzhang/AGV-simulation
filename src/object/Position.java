package object;

import canvas.Main;
import canvas.MyMap;

public class Position {
	private int x, y;
	
	public Position(int row, int col) {
		setX(row);
		setY(col);
	}
	
	public Position(Position p) {
		setX(p.getX());
		setY(p.getY());
	}
	
	public Position getNearRoad() {
		int nx=x/Main.sideLen;
		int ny=y/Main.sideLen;
		if (MyMap.reachable(ny,nx-1)) return new Position(nx-1,ny);
		if (MyMap.reachable(ny,nx+1)) return new Position(nx+1,ny);
		if (MyMap.reachable(ny-1,nx)) return new Position(nx,ny-1);
		if (MyMap.reachable(ny+1,nx)) return new Position(nx,ny+1);
		return new Position(nx,ny);
	}

	public int getX() {
		return x;
	}
	
	public void setX(int row) {
		x = row;
	}
	
	public int getY() {
		return y;
	}
	
	public void setY(int col) {
		y = col;
	}
	
	public void setPos(int row, int col) {
		x = row;
		y = col;
	}
	
	public boolean equal(Position p2) {
		if(x == p2.getX() && y == p2.getY())
			return true;
		return false;
	}
}
