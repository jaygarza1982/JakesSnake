import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class GameManager implements Serializable {
	private static final long serialVersionUID = 8604978283825666432L;

	transient int winWidth, winHeight, cellSize;

    Apple apple;

    public HashMap<String, Snake> snakes = new HashMap<String, Snake>();
    
    public GameManager(int winWidth, int winHeight, int cellSize) {
        this.winWidth = winWidth;
        this.winHeight = winHeight;
        this.cellSize = cellSize;

        Snake.winWidth = winWidth;
        Snake.winHeight = winHeight;
        
        snakes.put("host", new Snake(0, 0));
        apple = new Apple();
        spawnRandApple();
    }

    public int genRand(int min, int max) {
        int rang = (max - min) + 1;
        return (int) (Math.random() * rang) + min;
    }

    public void spawnRandApple() {
        int appleX = genRand(0, ((winHeight - cellSize) / cellSize)) * cellSize;
        int appleY = genRand(0, ((winHeight - cellSize) / cellSize)) * cellSize;

        apple.setPos(appleX, appleY);
    }

    public Apple getApple() {
        return apple;
    }

    public HashMap<String, Snake> getSnakes() {
        return snakes;
    }

    //This handles collisions and other things
    public void manage() {
//    	if (lock.tryLock()) {
//    		lock.lock();
    		ArrayList<String> deleteKeys = new ArrayList<String>();

    		//Host will never reach 0 TTL
    		snakes.get("host").setTimeToLive(10);

    		for (String key : snakes.keySet()) {
    			Snake currSnake = snakes.get(key);
    			for (String otherKey : snakes.keySet()) {
    				if (!otherKey.equals(key)) {
    					Snake otherSnake = snakes.get(otherKey);

    					//Check if currSnake is inside of the otherSnake
    					int headX = currSnake.getHead().x;
    					int headY = currSnake.getHead().y;

    					for (int i = 0; i < otherSnake.getLength(); i++) {
    						int bodyX = otherSnake.getSnakeXs().get(i);
    						int bodyY = otherSnake.getSnakeYs().get(i);

    						if (headX == bodyX && headY == bodyY) {
    							currSnake.removeCells(5);
    						}
    					}
    				}
    			}

    			currSnake.moveSnake(cellSize);

    			//Check to see if the snake ran into itself
    			int headX = currSnake.getHead().x;
    			int headY = currSnake.getHead().y;

    			//Check if the snakes head is inside any of the other cells, the loop starts at 1 because 0 is the head
    			for (int j = 1; j < currSnake.getSnakeXs().size(); j++) {
    				int currentX = currSnake.getSnakeXs().get(j);
    				int currentY = currSnake.getSnakeYs().get(j);

    				if (headX == currentX && headY == currentY) {
    					currSnake.removeCells(5);
    					//TODO: Do something here
    					//                    JOptionPane.showMessageDialog(null, "You lose!\nYou scored " + (currSnake.getSnakeXs().size() - 1) + " points.");
    					//                    System.exit(0);
    				}
    			}

    			if (currSnake.getHead().equals(apple.getPos())) {
    				currSnake.addCell(5);
    				spawnRandApple();
    			}

    			currSnake.setTimeToLive(currSnake.getTimeToLive()-1);

    			//If TTL is 0, mark for deletion
    			if (currSnake.getTimeToLive() == 0) {
    				deleteKeys.add(key);
    			}
    		}

    		//Delete inactive snakes
    		snakes.keySet().removeAll(deleteKeys);
    		
//    		lock.unlock();
//    	}
    }
}
