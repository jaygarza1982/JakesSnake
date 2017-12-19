//Name: Jake Garza
//Date: 6/24/17
//Program made to be like the classic arcade snake game

import java.awt.Canvas;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

public class Main {
	public static MainWindow startWindow;

	public static int fps = 0;
	public static int frames = 0;
	public static long totalTime = 0;

	public static long currentTime = System.currentTimeMillis();
	public static long lastTime = currentTime;

	public static long totalFrames = 0;

	public static JFrame frame;
	//Target FPS is 13 because we want it to be arcade like
	public static final long targetFPS = 13;
	public static BufferStrategy bs;

	//Window variables
	public static final int WINDOW_WIDTH = 700;
	public static final int WINDOW_HEIGHT = 700;
	public static final int CELLSIZE = 20; //Needs to be multiple from WINDOW_WIDTH and WINDOW_HEIGHT

	//Player 1 snake
	public static Snake player1Snake = null;
	
	//Apples arraylist, how many apples in game, how much they give you
	public static CopyOnWriteArrayList<Apple> apples = new CopyOnWriteArrayList<Apple>();
	public static int applesNum = 1;
	public static int applePoints = 5;

	//Image of apple
	public static BufferedImage appleImg = getApple();

	//Networking variables and multiplayer variables
	public static ServerSocket server = null;
	public static Socket connectionSocket = null;
	public static int PORT = 9876;
	public static int UDP_PORT = PORT;
	public static BufferedReader br = null;
	public static PrintWriter pw = null;
	public static DatagramSocket serverSocket = null;
	public static DatagramSocket clientSocket = null;
	public static final int BUFFER_SIZE = 4096;
	public static byte[] receiveData = new byte[BUFFER_SIZE];
	public static byte[] sendData = new byte[BUFFER_SIZE];
	public static String ip = null;
	public static boolean multiPlayer = false;
	public static boolean hosting = false;
	public static int maxPlayers = 4;

	//Current seconds of server, seconds for each round until reset
	public static int secondsInRound = 0;
	public static int currentSeconds = 0;

	//: Because we strip that from the name when we receive a set name command, so it cannot be this
	public static String winnerName = ":";

	//Variables for server to set own player stats
	public static String hostingName = null;
	public static Color hostingColor = null;

	//List that holds all players and their snakes
	public static CopyOnWriteArrayList<PlayerHandler> players = new CopyOnWriteArrayList<PlayerHandler>();

	//TODO: Test more and fix, comment and organize code

	public static void startGame() {
		currentSeconds = secondsInRound;

		//Add random apples
		for (int i = 0; i < applesNum; i++) {
			Apple apple = new Apple();
			apple.randomizeCords();
			apples.add(apple);
		}

		players.add(new PlayerHandler(new Snake(0, 0), new Color(0, 255, 0)));

		if (hosting || !multiPlayer) {
			players.get(0).setPlayerName(hostingName);
			players.get(0).setColor(hostingColor);
		}

		player1Snake = players.get(0).getSnake();

		//Window setup
		frame = new JFrame();
		frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT+CELLSIZE); //Window height plus cell size to accommodate for a row of cells unseen
		frame.setResizable(false);
		frame.getContentPane().setBackground(Color.BLACK);
		frame.setTitle("Snake Game");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.setVisible(true);
		Canvas canvas = new Canvas();
		canvas.setFocusable(false);
		frame.add(canvas);
		canvas.createBufferStrategy(2);
		bs = canvas.getBufferStrategy();

		//spawnRandApple();

		//Main game loop
		Thread gameThread = new Thread(new Runnable() {
			public void run() {
				while (true) {
					//If the milliseconds in the target frames / second have passed, do this
					try {
						if (System.currentTimeMillis() - lastTime >= (1000/targetFPS)) {
							//System.out.println(true);

							lastTime = currentTime;
							currentTime = System.currentTimeMillis();

							totalTime += currentTime - lastTime;

							//When a second passes
							if (totalTime > 1000) {
								totalTime -= 1000;
								fps = frames;
								frames = 0;
							}
							frames++;

							//If we are in multiplayer and are not hosting, we coordinates from the server
							if (multiPlayer && !hosting) {
								//Get info from server
								String serverMessage = "";

								String toSend = "getinfo";

								sendData = toSend.getBytes();
								DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName(ip), UDP_PORT);
								clientSocket.send(sendPacket);

								DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
								clientSocket.receive(receivePacket);

								String receiveStr = new String(receivePacket.getData(), 0, receivePacket.getLength(), "UTF-8");//new String(Arrays.toString(receivePacket.getData()));//new String(receivePacket.getData()).trim();


								serverMessage = receiveStr;

								//System.out.println(receiveStr);

								String serverMessages[] = serverMessage.split("\n");

								//Clear the players that have snakes
								players.clear();

								for (String message : serverMessages) {

									//Try catch because UDP packet received might be messed up
									try {
										message = message.trim();

										if (message.contains("APPLES:")) {

											message = message.replace("APPLES:", "");
											apples.clear();

											String appleCords[] = message.split(" ");
											for (int i = 0; i < appleCords.length; i++) {
												String strX = appleCords[i].split(",")[0];
												String strY = appleCords[i].split(",")[1];

												int x = Integer.parseInt(strX);
												int y = Integer.parseInt(strY);

												apples.add(new Apple(x, y));
											}
										}
										else if (message.contains("WINNER:")) {
											message = message.replace("WINNER:", "");

											winnerName = message;
										}
										else if (message.contains("TIME:")) {
											message = message.replace("TIME:", "");
											
											currentSeconds = Integer.parseInt(message);
										}
										else if (!message.isEmpty()) {
											String name = message.split(":")[0];
											//System.out.println(name);
											message = message.replace(name + ":", "");

											//System.out.println(message);
											//Get color
											int r = Integer.parseInt(message.split(" ")[0]);
											int g = Integer.parseInt(message.split(" ")[1]);
											int b = Integer.parseInt(message.split(" ")[2]);
											
											//System.out.println(r + ", " + g + ", " + b);

											Snake snake = new Snake(0, 0);
											snake.getSnakeXs().clear();
											snake.getSnakeYs().clear();

											CopyOnWriteArrayList<Integer> snakeXs = snake.getSnakeXs();//new ArrayList<Integer>();
											CopyOnWriteArrayList<Integer> snakeYs = snake.getSnakeYs();//new ArrayList<Integer>();

											//Split by space
											String cords[] = message.split(" ");

											//Ignore first 4 because they are RGB values for coloring
											for (int i = 3; i < cords.length; i++) {
												String currentCord = cords[i];

												int x = Integer.parseInt(currentCord.split(",")[0]);
												int y = Integer.parseInt(currentCord.split(",")[1]);

												snakeXs.add(x);
												snakeYs.add(y);
											}

											//PlayerHandler class holds snakes
											PlayerHandler player = new PlayerHandler(snake, new Color(r, g, b));
											player.setPlayerName(name);

											players.add(player);

										}
									} catch (Exception e) {
										e.printStackTrace();
									}	
								}
							}  		
							//If we are hosting, manage game
							else if (hosting || !multiPlayer && !winnerName.equals(":")) {
								//Do something with the serverTime
								if (totalFrames % targetFPS == 0) {
									currentSeconds--;
									//System.out.println(currentSeconds);
								}

								//Check if client ran into itself
								//If there isn't a winner
								if (winnerName.equals(":")) {
									for (int i = 0; i < players.size(); i++) {
										Snake currentSnake = players.get(i).getSnake();

										CopyOnWriteArrayList<Integer> snakeX = currentSnake.getSnakeXs();
										CopyOnWriteArrayList<Integer> snakeY = currentSnake.getSnakeYs();

										int headX = snakeX.get(0);
										int headY = snakeY.get(0);

										//Loop through body, if it's head touches body, remove 5 cells
										for (int j = 1; j < snakeX.size(); j++) {
											int currentX = snakeX.get(j);
											int currentY = snakeY.get(j);

											if (headX == currentX && headY == currentY) {
												currentSnake.removeCell(5);
											}
										}

										//If a player collides with an apple, randomize the apple cords and give them point
										int frontX = currentSnake.getSnakeXs().get(0);
										int frontY = currentSnake.getSnakeYs().get(0);
										for (int j = 0; j < apples.size(); j++) {
											Apple apple = apples.get(j);
											if (frontX == apple.getX() && frontY == apple.getY()) {
												apple.randomizeCords();
												currentSnake.addCell(applePoints);
											}
										}

										//If a player cuts someone off
										for (int j = 0; j < players.size(); j++) {
											//If we are not on the same player
											if (i != j) {
												Snake snake1 = players.get(i).getSnake();
												Snake snake2 = players.get(j).getSnake();

												//Check to see if snake1's head is in snake2's body, if so, remove 5 of it's cells
												headX = snake1.getSnakeXs().get(0);
												headY = snake1.getSnakeYs().get(0);

												for (int k = 1; k < snake2.getSnakeXs().size(); k++) {
													int currentX = snake2.getSnakeXs().get(k);
													int currentY = snake2.getSnakeYs().get(k);

													if (headX == currentX && headY == currentY) {
														snake1.removeCell(5);
													}
												}
											}
										}

										//Move players
										currentSnake.moveSnake(CELLSIZE);
									}
								}
							}

							Graphics g = bs.getDrawGraphics();

							//Clear last frame
							g.setColor(Color.BLACK);
							g.fillRect(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT+CELLSIZE);		



							//Draw cells
							g.setColor(new Color(100, 100, 100));
							for (int y = 0; y < WINDOW_WIDTH; y += CELLSIZE) {
								g.drawLine(0, y, WINDOW_HEIGHT, y);
							}
							for (int x = 0; x < WINDOW_WIDTH; x += CELLSIZE) {
								g.drawLine(x, 0, x, WINDOW_HEIGHT);
							}


							//Draw snake
							g.setColor(new Color(0, 255, 0));

							//Draw player snakes in players
							for (int i = 0; i < players.size(); i++) {
								PlayerHandler currentPlayer = players.get(i);

								Snake s = currentPlayer.getSnake();
								CopyOnWriteArrayList<Integer> snakeXs = s.getSnakeXs();
								CopyOnWriteArrayList<Integer> snakeYs = s.getSnakeYs();

								for (int j = 0; j < snakeXs.size(); j++) {
									int x = snakeXs.get(j);
									int y = snakeYs.get(j);
									//System.out.println("CLIENT: " + x + ", " + y);
									g.setColor(currentPlayer.getColor());
									g.drawRect(x, y, CELLSIZE, CELLSIZE);
								}

								//Draw first character of player name in head
								int headX = snakeXs.get(0);
								int headY = snakeYs.get(0);

								FontMetrics font = g.getFontMetrics();
								String firstLetter = ("" + currentPlayer.getPlayerName().charAt(0)).toUpperCase();
								int strWidth = font.stringWidth(firstLetter);
								int strHeight = font.getAscent();

								//Draw first letter in center of head
								g.drawString(firstLetter, headX + CELLSIZE/2 - (strWidth/2), headY + CELLSIZE/2 + (strHeight/2));
							}

							//Draw apples
							for (int i = 0; i < apples.size(); i++) {
								Apple apple = apples.get(i);
								g.drawImage(appleImg, apple.getX(), apple.getY(), null);
							}
							
							//Draw IP and score
							g.setColor(Color.WHITE);
							if (hosting) {
								g.drawString("Local server IP: " + InetAddress.getLocalHost().getHostAddress(), 5, 15);
							}
							else {
								g.drawString("FPS: " + fps, 5, 15);
							}
							
							g.drawString("Time: " + currentSeconds, 5, 15*2);


							//Draw player scores
							for (int i = 0; i < players.size(); i++) {
								PlayerHandler currentPlayer = players.get(i);
								Snake currentSnake = currentPlayer.getSnake();
								int score = currentSnake.getSnakeXs().size()-1;

								g.setColor(currentPlayer.getColor());
								g.drawString(currentPlayer.getPlayerName() + ": " + score, 5, 15*(i+3));
							}

							if (!winnerName.equals(":")) {
								g.clearRect(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);

								FontMetrics font = g.getFontMetrics();

								String playerWinsText = Main.winnerName + " wins!";

								int strWidth = font.stringWidth(playerWinsText);
								int strHeight = font.getAscent();

								g.drawString(playerWinsText, (WINDOW_WIDTH/2) + CELLSIZE/2 - (strWidth/2), (WINDOW_HEIGHT/2) + CELLSIZE/2 + (strHeight/2));
							}

							g.dispose();
							bs.show();

							totalFrames++;

							//If the currentServerTime is <=0, reset it
							//Wait 5 seconds before reseting
							if (currentSeconds <= -5) {
								currentSeconds = secondsInRound;
								Main.winnerName = ":";

								//Reset players
								for (int i = 0; i < players.size(); i++) {
									PlayerHandler currentPlayer = players.get(i);

									//Reset players, put at 0, 0 and clear the body
									currentPlayer.reset();
								}
							}

							//if the server time is <= 0, announce who won
							if (currentSeconds <= 0) {
								String winnerName = "";
								int winnerScore = -1;

								//Find the player with the most score
								for (int i = 0; i < players.size(); i++) {
									PlayerHandler currentPlayer = players.get(i);
									Snake currentSnake = currentPlayer.getSnake();
									int score = currentSnake.getSnakeXs().size();

									if (score > winnerScore) {
										winnerScore = score;
										winnerName = currentPlayer.getPlayerName();
									}
								}


								Main.winnerName = winnerName;
							}

							//Reset time
							lastTime = System.currentTimeMillis();
						}

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}});

		//Start main game thread
		gameThread.start();

		//If we are hosting, start a thread to get input from the client
		if (hosting) {
			Thread clientUDPInputThread = new Thread(new Runnable() {
				public void run() {
					try {
						while (true) {
							try {
								DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
								serverSocket.receive(receivePacket);
								//TODO: think about if there is already max players
								String receiveStr = new String(receivePacket.getData()).trim();
								if (receiveStr.equals("getinfo")) {
									String toSendStr = null;

									StringBuilder playerCords = new StringBuilder("");
									//Loop through players and get snake within
									for (int i = 0; i < players.size(); i++) {
										PlayerHandler currentPlayer = players.get(i);

										Snake currentSnake = currentPlayer.getSnake();
										CopyOnWriteArrayList<Integer> snakeXs = currentSnake.getSnakeXs();
										CopyOnWriteArrayList<Integer> snakeYs = currentSnake.getSnakeYs();

										Color color = currentPlayer.getColor();
										int r = color.getRed();
										int g = color.getGreen();
										int b = color.getBlue();

										playerCords.append("\n" + currentPlayer.getPlayerName() + ":" + r + " " + g + " " + b + " ");

										for (int j = 0; j < snakeXs.size(); j++) {
											try {
												int x = snakeXs.get(j);
												int y = snakeYs.get(j);

												playerCords.append(x + "," + y + " ");
											} catch (Exception e2) {
												e2.printStackTrace();
											}
										}
									}

									StringBuffer sbApples = new StringBuffer("\nAPPLES:");
									for (int i = 0; i < apples.size(); i++) {
										Apple apple = apples.get(i);
										sbApples.append(apple.getX() + "," + apple.getY() + " ");
									}



									//Send players, apples, and if anyone has won
									toSendStr = playerCords.toString()
											+ sbApples.toString() + "\nTIME:" + currentSeconds + "\nWINNER:" + winnerName;


									sendData = toSendStr.getBytes();

									DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, receivePacket.getAddress(), receivePacket.getPort());
									serverSocket.send(sendPacket);
								}
							} catch (Exception e) {
								System.out.println("Data race error will be ignored");
								e.printStackTrace();
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			clientUDPInputThread.start();
		}


		frame.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				char key = (e.getKeyChar()+"").toLowerCase().charAt(0); //Convert input to lower case

				//If we are hosting or not in multiplayer
				if (hosting || !multiPlayer) {
					if (key == 'w' || e.getKeyCode() == KeyEvent.VK_UP) player1Snake.setDirection(Snake.UP);
					else if (key == 's' || e.getKeyCode() == KeyEvent.VK_DOWN) player1Snake.setDirection(Snake.DOWN);
					else if (key == 'a' || e.getKeyCode() == KeyEvent.VK_LEFT) player1Snake.setDirection(Snake.LEFT);
					else if (key == 'd' || e.getKeyCode() == KeyEvent.VK_RIGHT) player1Snake.setDirection(Snake.RIGHT);
				}
				else if (multiPlayer && !hosting) {
					//Send movement command to the server
					if (key == 'w' || e.getKeyCode() == KeyEvent.VK_UP) {
						pw.println(Snake.UP);
					}
					else if (key == 's' || e.getKeyCode() == KeyEvent.VK_DOWN) {
						pw.println(Snake.DOWN);
					}
					else if (key == 'a' || e.getKeyCode() == KeyEvent.VK_LEFT) { 
						pw.println(Snake.LEFT);
					}
					else if (key == 'd' || e.getKeyCode() == KeyEvent.VK_RIGHT) {
						pw.println(Snake.RIGHT);
					}
				}

			}
		});

		frame.requestFocus();

	}

	public static void main(String args[]) {
		//Make the window look like the rest of the OS
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {}

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					startWindow = new MainWindow();
					startWindow.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	//This method returns the apple image stored in the resources file
	public static BufferedImage getApple() {
		URL locationOfAppleImage = Main.class.getClass().getResource("/res/Apple.png");
		BufferedImage appleImg = null;
		
		try {
			appleImg = ImageIO.read(locationOfAppleImage);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
		}
		
		return appleImg;
	}
}