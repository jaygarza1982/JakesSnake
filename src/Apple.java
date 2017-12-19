//Purpose of class is to hold x and y cords

public class Apple {
	private int x = 0, y = 0;
	
	public Apple() {}
	
	public Apple(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	private int genRand(int min, int max) {
		int rang = (max - min) + 1;
		return (int)(Math.random() * rang) + min;
	}
	
	public void randomizeCords() {
		//Generate random x and y, get nearest multiple of CELLSIZE
		x = genRand(0, Main.WINDOW_WIDTH - Main.CELLSIZE);
		x = (int)Math.ceil(x / Main.CELLSIZE) * Main.CELLSIZE;

		y = genRand(0, Main.WINDOW_HEIGHT - Main.CELLSIZE);
		y = (int)Math.ceil(y / Main.CELLSIZE) * Main.CELLSIZE;
	}
}