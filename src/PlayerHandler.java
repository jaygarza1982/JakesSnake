//Purpose of class is to be a handler for incoming clients and to listen to them for movement commands
//This class also holds the snake object for each client

import java.awt.Color;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketException;

public class PlayerHandler extends Thread {
	private BufferedReader br = null;
	private Snake snake = null;
	private Color color = null;
	private int maxNameLength = 18;
	private String name = "NO NAME";
	
	public PlayerHandler(Socket socket, Snake snake, Color color) throws Exception {
		br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		this.snake = snake;
		this.color = color;
		
		//Start the thread of this
		this.start();
	}
	
	public PlayerHandler(Snake snake, Color color) {
		this.snake = snake;
		this.color = color;
	}
	
	public Snake getSnake() {
		return snake;
	}
	
	public Color getColor() {
		return color;
	}
	
	public void setColor(Color color) {
		this.color = color;
	}
	
	public String getPlayerName() {
		return name;
	}
	
	public void setPlayerName(String name) {
		if (name.length() > maxNameLength) {
			this.name = name.substring(0, maxNameLength);
		}
		else {
			this.name = name;
		}
	}
	
	public void reset() {
		//Clear X and Y array
		snake.getSnakeXs().clear();
		snake.getSnakeYs().clear();
		
		//Put at 0, 0
		snake.getSnakeXs().add(0);
		snake.getSnakeYs().add(0);
	}
	
	public void run() {
		while (true) {
			//If we aren't at max player count
			if (Main.players.size() <= Main.maxPlayers) {
				String input = null;
				try {
					//Get input from client
					input = br.readLine();
					
					//Check for commands from client
					if (input.contains("NAME:")) {
						input = input.replace("NAME:", "").replace(":", "");
						setPlayerName(input);
					}
					else if (input.contains("COLOR:")) {
						input = input.replace("COLOR:", "");

						int r = Integer.parseInt(input.split(" ")[0]);
						int g = Integer.parseInt(input.split(" ")[1]);
						int b = Integer.parseInt(input.split(" ")[2]);

						color = new Color(r, g, b);
					}
					else {
						snake.setDirection(input);
					}
				}
				catch (SocketException socketException) {
					//If a socket exception occurs, remove the player and stop the thread
					Main.players.remove(this);
					return;
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
			else {
				Main.players.remove(this);
				try { br.close(); } catch (Exception e) {}
				return;
			}
		}
	}
}