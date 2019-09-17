import java.awt.Color;
import java.awt.Point;
import java.io.Serializable;
import java.util.ArrayList;

public class Snake implements Comparable<Snake>, Serializable {
	private static final long serialVersionUID = -7436136006855017951L;
	
	public static final String DOWN = "down";
	public static final String UP = "up";
	public static final String RIGHT = "right";
	public static final String LEFT = "left";
	public static int winWidth, winHeight;
    
    private String snakeDirection = DOWN;
    private Color color = new Color(0, 255, 0);
    private int timeToLive = 10;
    
    //These arrays are parallel
    private ArrayList<Integer> snakeX = new ArrayList<Integer>();
    private ArrayList<Integer> snakeY = new ArrayList<Integer>();
    
    private String name = "NAME";
    
    //When snake is created, add one cell to body at x, y
    public Snake(int x, int y) {
    	snakeX.add(x);
    	snakeY.add(y);
    }
    
    public ArrayList<Integer> getSnakeXs() {
    	return snakeX;
    }
    
    public ArrayList<Integer> getSnakeYs() {
    	return snakeY;
    }
    
    //Should get passed a static direction variable
    public void setDirection(String direction) {
		//Snake cannot run into itself by going the opposite direction it is currently in
		if (getLength() != 1) {
			if (!(!(direction.equals(DOWN) && snakeDirection.equals(UP)) && !(direction.equals(UP) && snakeDirection.equals(DOWN))
					&& !(direction.equals(LEFT) && snakeDirection.equals(RIGHT)) && !(direction.equals(RIGHT) && snakeDirection.equals(LEFT)))) {
				return;
			}
		}

		snakeDirection = direction;
    }
    
    public String getDirection() { return snakeDirection; }
    
    public void moveSnake(int amount) {
    	int frontX = snakeX.get(0);
    	int frontY = snakeY.get(0);
    	
    	
    	//To move the snake: insert a new cell to front, remove the last index of the list of cells

    	//Based off the direction, add a new x and y
    	if (snakeDirection.equals(DOWN)) {
    		snakeX.add(0, frontX);
    		snakeY.add(0, frontY + amount);
    	}
    	else if (snakeDirection.equals(UP)) {
    		snakeX.add(0, frontX);
    		snakeY.add(0, frontY - amount);
    	}
    	else if (snakeDirection.equals(RIGHT)) {
    		snakeX.add(0, frontX + amount);
    		snakeY.add(0, frontY);
    	}
    	else if (snakeDirection.equals(LEFT)) {
    		snakeX.add(0, frontX - amount);
    		snakeY.add(0, frontY);
    	}
    	//Removes last elements in x and y lists
    	snakeX.remove(snakeX.size()-1);
    	snakeY.remove(snakeY.size()-1);

    	//If a snake cell goes off screen, put it on the other side
    	putSnakeInBounds(winWidth, winHeight);
    }
    
    //This will put the snake back in map if it goes off screen, it will spawn at other side
    private void putSnakeInBounds(int width, int height) {
    	for (int i = 0; i < snakeX.size(); i++) {
    		//Put x in bounds of game
    		int x = snakeX.get(i);
    		
    		if (x >= width)
    			x %= width;
        	else if (x < 0)
        		x = width;
    		
    		snakeX.set(i, x);
    		
    		int y = snakeY.get(i);
    		
    		//Put y in bounds of game
    		if (y >= height)
        		y %= height;
        	else if (y < 0)
        		y = height;
    		
    		snakeY.set(i, y);
    	}
    }
    
    //Adds cell to back of snake
    public void addCell(int amount) {
    	int backX = snakeX.get(snakeX.size()-1);
    	int backY = snakeY.get(snakeY.size()-1);
    	
    	for (int i = 0; i < amount; i++) {
    		//Add cells off screen
    		snakeX.add(backX);
        	snakeY.add(backY);
    	}
    }
    
    public void removeCells(int n) {
    	if (getLength() - n > 0) {
    		snakeX = new ArrayList<Integer>(snakeX.subList(0, snakeX.size() - n));
        	snakeY = new ArrayList<Integer>(snakeY.subList(0, snakeY.size() - n)); 
    	}	
    }
    
    public void setName(String s) {
    	name = s;
    }
    
    public String getName() {
    	return name;
    }

    public Point getHead() {
    	return new Point(snakeX.get(0), snakeY.get(0));
	}

    public int getLength() {
    	return snakeX.size();
	}
    
    public int getTimeToLive() {
    	return timeToLive;
    }
    
    public void setTimeToLive(int n) {
    	timeToLive = n;
    }
    
    public void setColor(Color color) {
    	this.color = color;
    }
    
    public Color getColor() {
    	return color;
    }
    
    @Override
    public int compareTo(Snake otherSnake) {
    	return otherSnake.getLength() - getLength();
    }
}