import java.util.concurrent.CopyOnWriteArrayList;
import javax.swing.JOptionPane;

//Purpose of class: to hold snake variables and do snake functions

public class Snake {
	public static final String DOWN = "down";
	public static final String UP = "up";
	public static final String RIGHT = "right";
	public static final String LEFT = "left";
    
    private String snakeDirection = DOWN;
    
    //These arrays are parallel
    private CopyOnWriteArrayList<Integer> snakeX = new CopyOnWriteArrayList<Integer>();
    private CopyOnWriteArrayList<Integer> snakeY = new CopyOnWriteArrayList<Integer>();
    
    //When snake is created, add one cell to body at x, y
    public Snake(int x, int y) {
    	snakeX.add(x);
    	snakeY.add(y);
    }
    
    public CopyOnWriteArrayList<Integer> getSnakeXs() {
    	return snakeX;
    }
    
    public CopyOnWriteArrayList<Integer> getSnakeYs() {
    	return snakeY;
    }
    
    //Should get passed a static direction variable
    public void setDirection(String direction) {
    	//Snake cannot run into itself by going the opposite direction it is currently in
    	if (!(direction.equals(DOWN) && snakeDirection.equals(UP)) && !(direction.equals(UP) && snakeDirection.equals(DOWN))
    			&& !(direction.equals(LEFT) && snakeDirection.equals(RIGHT)) && !(direction.equals(RIGHT) && snakeDirection.equals(LEFT))) {
    		snakeDirection = direction;
    	}
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
    	putSnakeInBounds();
    	
    	//TODO: put this into the Main class and implement multiplayer stuff
    	
    	//Check to see if the snake ran into itself
    	if (!Main.multiPlayer) {
    		int headX = snakeX.get(0);
        	int headY = snakeY.get(0);
        	
        	//Check if the snakes head is inside any of the other cells, the loop starts at 1 because 0 is the head
        	for (int i = 1; i < snakeX.size(); i++) {
        		int currentX = snakeX.get(i);
        		int currentY = snakeY.get(i);
        		
        		if (headX == currentX && headY == currentY) {
        			JOptionPane.showMessageDialog(null, "You lose!\nYou scored " + (snakeX.size()-1) + " points.");
        			System.exit(0);
        		}
        	}
    	}
    	
    }
    
    //This will put the snake back in map if it goes off screen, it will spawn at other side
    private void putSnakeInBounds() {
    	for (int i = 0; i < snakeX.size(); i++) {
    		//Put x in bounds of game
    		int x = snakeX.get(i);
    		
    		if (x >= Main.WINDOW_WIDTH)
    			x %= Main.WINDOW_WIDTH;
        	else if (x < 0)
        		x = Main.WINDOW_WIDTH;
    		
    		snakeX.set(i, x);
    		
    		int y = snakeY.get(i);
    		
    		//Put y in bounds of game
    		if (y >= Main.WINDOW_HEIGHT)
        		y %= Main.WINDOW_HEIGHT;
        	else if (y < 0)
        		y = Main.WINDOW_HEIGHT;
    		
    		snakeY.set(i, y);
    	}
    }
    
    //Adds cell to back of snake
    public void addCell(int amount) {
    	int backX = snakeX.get(snakeX.size()-1);
    	int backY = snakeY.get(snakeY.size()-1);
    	
    	for (int i = 0; i < amount; i++) {
    		snakeX.add(backX);
        	snakeY.add(backY);
    	}
    }
    
    public void removeCell(int amount) {
    	if (snakeX.size() - amount >= 1) {
    		snakeX.subList(snakeX.size() - amount, snakeX.size()).clear();
    		snakeY.subList(snakeY.size() - amount, snakeY.size()).clear();
    	}
    }
}