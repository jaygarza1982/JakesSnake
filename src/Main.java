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
import java.util.concurrent.CopyOnWriteArrayList;
import javax.swing.JFrame;
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

	//A note to anyone who reads this method
	//This method creates an apple image without reading any files
	//I would never do this in a serious project, I just thought it would
	//be cool if I could actually do it this way, It draws an image pixel by pixel

	public static BufferedImage getApple() {
		BufferedImage bi = new BufferedImage(30, 30, BufferedImage.TYPE_4BYTE_ABGR);
		bi.setRGB(0, 0, 0);
		bi.setRGB(0, 1, 0);
		bi.setRGB(0, 2, 0);
		bi.setRGB(0, 3, 0);
		bi.setRGB(0, 4, 0);
		bi.setRGB(0, 5, 0);
		bi.setRGB(0, 6, 0);
		bi.setRGB(0, 7, 0);
		bi.setRGB(0, 8, 0);
		bi.setRGB(0, 9, 0);
		bi.setRGB(0, 10, 0);
		bi.setRGB(0, 11, 0);
		bi.setRGB(0, 12, 0);
		bi.setRGB(0, 13, 0);
		bi.setRGB(0, 14, 0);
		bi.setRGB(0, 15, 0);
		bi.setRGB(0, 16, 0);
		bi.setRGB(0, 17, 0);
		bi.setRGB(0, 18, 0);
		bi.setRGB(0, 19, 0);
		bi.setRGB(1, 0, 0);
		bi.setRGB(1, 1, 0);
		bi.setRGB(1, 2, 0);
		bi.setRGB(1, 3, 0);
		bi.setRGB(1, 4, 0);
		bi.setRGB(1, 5, 0);
		bi.setRGB(1, 6, 0);
		bi.setRGB(1, 7, 282005520);
		bi.setRGB(1, 8, 1472861975);
		bi.setRGB(1, 9, 1925520150);
		bi.setRGB(1, 10, 1958681878);
		bi.setRGB(1, 11, 1673076501);
		bi.setRGB(1, 12, 750002961);
		bi.setRGB(1, 13, 97268480);
		bi.setRGB(1, 14, 0);
		bi.setRGB(1, 15, 0);
		bi.setRGB(1, 16, 0);
		bi.setRGB(1, 17, 0);
		bi.setRGB(1, 18, 0);
		bi.setRGB(1, 19, 0);
		bi.setRGB(2, 0, 0);
		bi.setRGB(2, 1, 0);
		bi.setRGB(2, 2, 0);
		bi.setRGB(2, 3, 0);
		bi.setRGB(2, 4, 0);
		bi.setRGB(2, 5, 50266112);
		bi.setRGB(2, 6, 1875973399);
		bi.setRGB(2, 7, -405924329);
		bi.setRGB(2, 8, -3663850);
		bi.setRGB(2, 9, -3925482);
		bi.setRGB(2, 10, -4317675);
		bi.setRGB(2, 11, -4644843);
		bi.setRGB(2, 12, -5037292);
		bi.setRGB(2, 13, -475060972);
		bi.setRGB(2, 14, 2108237586);
		bi.setRGB(2, 15, 799222550);
		bi.setRGB(2, 16, 0);
		bi.setRGB(2, 17, 0);
		bi.setRGB(2, 18, 0);
		bi.setRGB(2, 19, 0);
		bi.setRGB(3, 0, 0);
		bi.setRGB(3, 1, 219459860);
		bi.setRGB(3, 2, 16842496);
		bi.setRGB(3, 3, 0);
		bi.setRGB(3, 4, 0);
		bi.setRGB(3, 5, 819269653);
		bi.setRGB(3, 6, -271444969);
		bi.setRGB(3, 7, -3336681);
		bi.setRGB(3, 8, -3729130);
		bi.setRGB(3, 9, -3990762);
		bi.setRGB(3, 10, -4383211);
		bi.setRGB(3, 11, -4710379);
		bi.setRGB(3, 12, -5102828);
		bi.setRGB(3, 13, -5364460);
		bi.setRGB(3, 14, -72865773);
		bi.setRGB(3, 15, -677238253);
		bi.setRGB(3, 16, 1402809874);
		bi.setRGB(3, 17, 93926144);
		bi.setRGB(3, 18, 0);
		bi.setRGB(3, 19, 0);
		bi.setRGB(4, 0, 337290778);
		bi.setRGB(4, 1, 1897964825);
		bi.setRGB(4, 2, 1394581529);
		bi.setRGB(4, 3, 103524907);
		bi.setRGB(4, 4, 0);
		bi.setRGB(4, 5, -1898573289);
		bi.setRGB(4, 6, -3075049);
		bi.setRGB(4, 7, -3402218);
		bi.setRGB(4, 8, -3794666);
		bi.setRGB(4, 9, -4121579);
		bi.setRGB(4, 10, -4514027);
		bi.setRGB(4, 11, -4775660);
		bi.setRGB(4, 12, -5168108);
		bi.setRGB(4, 13, -5495277);
		bi.setRGB(4, 14, -5887725);
		bi.setRGB(4, 15, -6149357);
		bi.setRGB(4, 16, -140759534);
		bi.setRGB(4, 17, -1818676975);
		bi.setRGB(4, 18, 109062912);
		bi.setRGB(4, 19, 0);
		bi.setRGB(5, 0, 673226522);
		bi.setRGB(5, 1, -1742364899);
		bi.setRGB(5, 2, -1725589220);
		bi.setRGB(5, 3, 673618208);
		bi.setRGB(5, 4, 148840480);
		bi.setRGB(5, 5, -1328278761);
		bi.setRGB(5, 6, -3140329);
		bi.setRGB(5, 7, -3467498);
		bi.setRGB(5, 8, -3859946);
		bi.setRGB(5, 9, -4121579);
		bi.setRGB(5, 10, -4514027);
		bi.setRGB(5, 11, -4841196);
		bi.setRGB(5, 12, -5233644);
		bi.setRGB(5, 13, -5495277);
		bi.setRGB(5, 14, -5953261);
		bi.setRGB(5, 15, -6214894);
		bi.setRGB(5, 16, -6607342);
		bi.setRGB(5, 17, -107597807);
		bi.setRGB(5, 18, 1569797136);
		bi.setRGB(5, 19, 33488896);
		bi.setRGB(6, 0, 824874783);
		bi.setRGB(6, 1, -1272212191);
		bi.setRGB(6, 2, -852651486);
		bi.setRGB(6, 3, 1915718948);
		bi.setRGB(6, 4, 50266112);
		bi.setRGB(6, 5, -1697508841);
		bi.setRGB(6, 6, -3271401);
		bi.setRGB(6, 7, -3533034);
		bi.setRGB(6, 8, -3925482);
		bi.setRGB(6, 9, -4252651);
		bi.setRGB(6, 10, -4645099);
		bi.setRGB(6, 11, -4906732);
		bi.setRGB(6, 12, -5298924);
		bi.setRGB(6, 13, -5626093);
		bi.setRGB(6, 14, -6018541);
		bi.setRGB(6, 15, -6280174);
		bi.setRGB(6, 16, -6672622);
		bi.setRGB(6, 17, -6999791);
		bi.setRGB(6, 18, -762236143);
		bi.setRGB(6, 19, 680276755);
		bi.setRGB(7, 0, 657362983);
		bi.setRGB(7, 1, -1188066011);
		bi.setRGB(7, 2, -416118234);
		bi.setRGB(7, 3, -1154185688);
		bi.setRGB(7, 4, 0);
		bi.setRGB(7, 5, 1758467094);
		bi.setRGB(7, 6, -3271145);
		bi.setRGB(7, 7, -3598314);
		bi.setRGB(7, 8, -3990762);
		bi.setRGB(7, 9, -4252395);
		bi.setRGB(7, 10, -4710379);
		bi.setRGB(7, 11, -4972012);
		bi.setRGB(7, 12, -5364460);
		bi.setRGB(7, 13, -5691629);
		bi.setRGB(7, 14, -6084077);
		bi.setRGB(7, 15, -6345710);
		bi.setRGB(7, 16, -6738158);
		bi.setRGB(7, 17, -7065327);
		bi.setRGB(7, 18, -175229935);
		bi.setRGB(7, 19, 1267414545);
		bi.setRGB(8, 0, 87267635);
		bi.setRGB(8, 1, 2100659497);
		bi.setRGB(8, 2, -46628053);
		bi.setRGB(8, 3, -164068565);
		bi.setRGB(8, 4, 87267635);
		bi.setRGB(8, 5, 1053955353);
		bi.setRGB(8, 6, -3402217);
		bi.setRGB(8, 7, -3663850);
		bi.setRGB(8, 8, -4056042);
		bi.setRGB(8, 9, -4383211);
		bi.setRGB(8, 10, -4775659);
		bi.setRGB(8, 11, -5037292);
		bi.setRGB(8, 12, -5429740);
		bi.setRGB(8, 13, -5756909);
		bi.setRGB(8, 14, -6149357);
		bi.setRGB(8, 15, -6410990);
		bi.setRGB(8, 16, -6803438);
		bi.setRGB(8, 17, -7130607);
		bi.setRGB(8, 18, -41077487);
		bi.setRGB(8, 19, 1468871954);
		bi.setRGB(9, 0, 0);
		bi.setRGB(9, 1, 338919462);
		bi.setRGB(9, 2, -1304919509);
		bi.setRGB(9, 3, -96959701);
		bi.setRGB(9, 4, 440759837);
		bi.setRGB(9, 5, 1571760400);
		bi.setRGB(9, 6, -3467497);
		bi.setRGB(9, 7, -3729130);
		bi.setRGB(9, 8, -4121578);
		bi.setRGB(9, 9, -4448747);
		bi.setRGB(9, 10, -4841195);
		bi.setRGB(9, 11, -5102828);
		bi.setRGB(9, 12, -5495276);
		bi.setRGB(9, 13, -5822445);
		bi.setRGB(9, 14, -6214893);
		bi.setRGB(9, 15, -6476526);
		bi.setRGB(9, 16, -6868974);
		bi.setRGB(9, 17, -7196143);
		bi.setRGB(9, 18, -343067375);
		bi.setRGB(9, 19, 1099773712);
		bi.setRGB(10, 0, 208284416);
		bi.setRGB(10, 1, 946086912);
		bi.setRGB(10, 2, 929443333);
		bi.setRGB(10, 3, 2018989845);
		bi.setRGB(10, 4, -2089994236);
		bi.setRGB(10, 5, -1734921971);
		bi.setRGB(10, 6, -3533034);
		bi.setRGB(10, 7, -3794666);
		bi.setRGB(10, 8, -4187115);
		bi.setRGB(10, 9, -4514027);
		bi.setRGB(10, 10, -4906476);
		bi.setRGB(10, 11, -5168108);
		bi.setRGB(10, 12, -5560557);
		bi.setRGB(10, 13, -5887725);
		bi.setRGB(10, 14, -6280173);
		bi.setRGB(10, 15, -6607342);
		bi.setRGB(10, 16, -6999790);
		bi.setRGB(10, 17, -7261423);
		bi.setRGB(10, 18, -477219311);
		bi.setRGB(10, 19, 965424658);
		bi.setRGB(11, 0, 476324864);
		bi.setRGB(11, 1, -1167842816);
		bi.setRGB(11, 2, 1768302336);
		bi.setRGB(11, 3, 795291909);
		bi.setRGB(11, 4, 242038528);
		bi.setRGB(11, 5, 1305286423);
		bi.setRGB(11, 6, -3598314);
		bi.setRGB(11, 7, -3859946);
		bi.setRGB(11, 8, -4252395);
		bi.setRGB(11, 9, -4579563);
		bi.setRGB(11, 10, -4972012);
		bi.setRGB(11, 11, -5233644);
		bi.setRGB(11, 12, -5626093);
		bi.setRGB(11, 13, -5953261);
		bi.setRGB(11, 14, -6345710);
		bi.setRGB(11, 15, -6607342);
		bi.setRGB(11, 16, -6999791);
		bi.setRGB(11, 17, -7326959);
		bi.setRGB(11, 18, -74697199);
		bi.setRGB(11, 19, 1619866896);
		bi.setRGB(12, 0, 0);
		bi.setRGB(12, 1, 711009280);
		bi.setRGB(12, 2, 55902208);
		bi.setRGB(12, 3, 0);
		bi.setRGB(12, 4, 0);
		bi.setRGB(12, 5, 2043417879);
		bi.setRGB(12, 6, -3663850);
		bi.setRGB(12, 7, -3925482);
		bi.setRGB(12, 8, -4317675);
		bi.setRGB(12, 9, -4644843);
		bi.setRGB(12, 10, -5037292);
		bi.setRGB(12, 11, -5364460);
		bi.setRGB(12, 12, -5756909);
		bi.setRGB(12, 13, -6018541);
		bi.setRGB(12, 14, -6410990);
		bi.setRGB(12, 15, -6738158);
		bi.setRGB(12, 16, -7130607);
		bi.setRGB(12, 17, -7392239);
		bi.setRGB(12, 18, -24365551);
		bi.setRGB(12, 19, 1821062673);
		bi.setRGB(13, 0, 0);
		bi.setRGB(13, 1, 0);
		bi.setRGB(13, 2, 0);
		bi.setRGB(13, 3, 0);
		bi.setRGB(13, 4, 114633515);
		bi.setRGB(13, 5, -1463085289);
		bi.setRGB(13, 6, -3729130);
		bi.setRGB(13, 7, -3990762);
		bi.setRGB(13, 8, -4383211);
		bi.setRGB(13, 9, -4710379);
		bi.setRGB(13, 10, -5102828);
		bi.setRGB(13, 11, -5364460);
		bi.setRGB(13, 12, -5822445);
		bi.setRGB(13, 13, -6084077);
		bi.setRGB(13, 14, -6476526);
		bi.setRGB(13, 15, -6738158);
		bi.setRGB(13, 16, -7196143);
		bi.setRGB(13, 17, -7457775);
		bi.setRGB(13, 18, -259246575);
		bi.setRGB(13, 19, 1250768657);
		bi.setRGB(14, 0, 0);
		bi.setRGB(14, 1, 0);
		bi.setRGB(14, 2, 0);
		bi.setRGB(14, 3, 0);
		bi.setRGB(14, 4, 215291157);
		bi.setRGB(14, 5, -1228204522);
		bi.setRGB(14, 6, -3794410);
		bi.setRGB(14, 7, -4121579);
		bi.setRGB(14, 8, -4514027);
		bi.setRGB(14, 9, -4775660);
		bi.setRGB(14, 10, -5168108);
		bi.setRGB(14, 11, -5495277);
		bi.setRGB(14, 12, -5887725);
		bi.setRGB(14, 13, -6149357);
		bi.setRGB(14, 14, -6541806);
		bi.setRGB(14, 15, -6868975);
		bi.setRGB(14, 16, -7261423);
		bi.setRGB(14, 17, -7523055);
		bi.setRGB(14, 18, -1433651695);
		bi.setRGB(14, 19, 327562765);
		bi.setRGB(15, 0, 0);
		bi.setRGB(15, 1, 0);
		bi.setRGB(15, 2, 0);
		bi.setRGB(15, 3, 0);
		bi.setRGB(15, 4, 61472768);
		bi.setRGB(15, 5, -1765205994);
		bi.setRGB(15, 6, -3859946);
		bi.setRGB(15, 7, -4121579);
		bi.setRGB(15, 8, -4514027);
		bi.setRGB(15, 9, -4841196);
		bi.setRGB(15, 10, -5233644);
		bi.setRGB(15, 11, -5560813);
		bi.setRGB(15, 12, -5953261);
		bi.setRGB(15, 13, -6214894);
		bi.setRGB(15, 14, -6607342);
		bi.setRGB(15, 15, -6934511);
		bi.setRGB(15, 16, -7326959);
		bi.setRGB(15, 17, -326355695);
		bi.setRGB(15, 18, 814363920);
		bi.setRGB(15, 19, 0);
		bi.setRGB(16, 0, 0);
		bi.setRGB(16, 1, 0);
		bi.setRGB(16, 2, 0);
		bi.setRGB(16, 3, 0);
		bi.setRGB(16, 4, 0);
		bi.setRGB(16, 5, 818551573);
		bi.setRGB(16, 6, -272360938);
		bi.setRGB(16, 7, -4252651);
		bi.setRGB(16, 8, -4645099);
		bi.setRGB(16, 9, -4906476);
		bi.setRGB(16, 10, -5298924);
		bi.setRGB(16, 11, -5626093);
		bi.setRGB(16, 12, -6018541);
		bi.setRGB(16, 13, -6280174);
		bi.setRGB(16, 14, -6672622);
		bi.setRGB(16, 15, -107728623);
		bi.setRGB(16, 16, -1349504239);
		bi.setRGB(16, 17, 814691600);
		bi.setRGB(16, 18, 0);
		bi.setRGB(16, 19, 0);
		bi.setRGB(17, 0, 0);
		bi.setRGB(17, 1, 0);
		bi.setRGB(17, 2, 0);
		bi.setRGB(17, 3, 0);
		bi.setRGB(17, 4, 0);
		bi.setRGB(17, 5, 33488896);
		bi.setRGB(17, 6, 1740708630);
		bi.setRGB(17, 7, -490857195);
		bi.setRGB(17, 8, -4710379);
		bi.setRGB(17, 9, -4972012);
		bi.setRGB(17, 10, -5364460);
		bi.setRGB(17, 11, -5691629);
		bi.setRGB(17, 12, -6084077);
		bi.setRGB(17, 13, -6345710);
		bi.setRGB(17, 14, -845598959);
		bi.setRGB(17, 15, 1872048144);
		bi.setRGB(17, 16, 412431637);
		bi.setRGB(17, 17, 0);
		bi.setRGB(17, 18, 0);
		bi.setRGB(17, 19, 0);
		bi.setRGB(18, 0, 0);
		bi.setRGB(18, 1, 0);
		bi.setRGB(18, 2, 0);
		bi.setRGB(18, 3, 0);
		bi.setRGB(18, 4, 0);
		bi.setRGB(18, 5, 0);
		bi.setRGB(18, 6, 0);
		bi.setRGB(18, 7, 280961040);
		bi.setRGB(18, 8, 1605836821);
		bi.setRGB(18, 9, -2018302955);
		bi.setRGB(18, 10, -1850988781);
		bi.setRGB(18, 11, -1918359532);
		bi.setRGB(18, 12, 1705257748);
		bi.setRGB(18, 13, 882715668);
		bi.setRGB(18, 14, 0);
		bi.setRGB(18, 15, 0);
		bi.setRGB(18, 16, 0);
		bi.setRGB(18, 17, 0);
		bi.setRGB(18, 18, 0);
		bi.setRGB(18, 19, 0);
		bi.setRGB(19, 0, 0);
		bi.setRGB(19, 1, 0);
		bi.setRGB(19, 2, 0);
		bi.setRGB(19, 3, 0);
		bi.setRGB(19, 4, 0);
		bi.setRGB(19, 5, 0);
		bi.setRGB(19, 6, 0);
		bi.setRGB(19, 7, 0);
		bi.setRGB(19, 8, 0);
		bi.setRGB(19, 9, 0);
		bi.setRGB(19, 10, 0);
		bi.setRGB(19, 11, 0);
		bi.setRGB(19, 12, 0);
		bi.setRGB(19, 13, 0);
		bi.setRGB(19, 14, 0);
		bi.setRGB(19, 15, 0);
		bi.setRGB(19, 16, 0);
		bi.setRGB(19, 17, 0);
		bi.setRGB(19, 18, 0);
		bi.setRGB(19, 19, 0);
		return bi;
	}
}