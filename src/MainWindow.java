//This class was made from the help of the Window Builder plug in for Eclipse
//The purpose of this class is to be an intuitive GUI for choosing to host, join or play single player

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JSlider;
import java.awt.Font;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

@SuppressWarnings("serial")
public class MainWindow extends JFrame {

	private JPanel contentPane;
	private JTextField txtJoinIP;
	private JTextField txtHostPort;
	private JTextField txtJoinPort;
	private JSlider slider;
	private JLabel lblColor;
	private boolean hostingPressedOnce = false;
	private JTextField txtNameBox;
	private JSpinner spNumPlayers;
	private JSpinner spNumApples;
	private JSpinner spApplePoints;
	private JButton btnHost;
	private JSpinner spSecondsInRound;
	
	public MainWindow() {
		setTitle("Jake's Snake Game");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 531, 357);
		contentPane = new JPanel();
		contentPane.setLayout(null);
		setContentPane(contentPane);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		
		JButton btnJoin = new JButton("Join");
		btnJoin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					//Set variables for multiplayer
					Main.multiPlayer = true;
					Main.hosting = false;
					Main.PORT = Integer.parseInt(txtJoinPort.getText());
					Main.UDP_PORT = Main.PORT;
					Main.ip = txtJoinIP.getText();

					//Initialize streams

					Main.connectionSocket = new Socket(Main.ip, Main.PORT);
					Main.br = new BufferedReader(new InputStreamReader(Main.connectionSocket.getInputStream()));
					Main.pw = new PrintWriter(Main.connectionSocket.getOutputStream(), true);
					
					//Replace : beacuse of server parsing
					Main.pw.println("NAME:" + txtNameBox.getText().replace(":", ""));
					
					Color color = slider.getBackground();//new Color(Color.HSBtoRGB((float)slider.getValue()/100, 1, 1));
					Main.pw.println("COLOR:" + color.getRed() + " " + color.getGreen() + " " + color.getBlue());

					Main.clientSocket = new DatagramSocket();

					Main.startGame();

					Main.startWindow.dispose();
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null, e.getMessage());
					e.printStackTrace();
				}
			}
		});
		btnJoin.setBounds(10, 76, 95, 23);
		contentPane.add(btnJoin);

		btnHost = new JButton("Host");
		btnHost.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				//If we have not pressed the button yet
				if (!hostingPressedOnce) {
					hostingPressedOnce = true;
					
					try {
						//Set variables for multiplayer
						Main.multiPlayer = true;
						Main.hosting = true;
						Main.PORT = Integer.parseInt(txtHostPort.getText());
						Main.UDP_PORT = Main.PORT;
						
						//Replace : beacuse of server parsing
						Main.hostingName = txtNameBox.getText().replace(":", "");
						
						Color color = slider.getBackground();//new Color(Color.HSBtoRGB((float)slider.getValue()/100, 1, 1));
						Main.hostingColor = color;
						
						//Number of apples to spawn in game
						Main.applesNum = (Integer)spNumApples.getValue();
						
						//Number of points given to players when they pickup apples
						Main.applePoints = (Integer)spApplePoints.getValue();
						
						//Number of seconds before the server determines a winner
						Main.secondsInRound = (Integer)spSecondsInRound.getValue();

						//Listen for client on chosen port
						Main.server = new ServerSocket(Main.PORT);
						Main.serverSocket = new DatagramSocket(Main.UDP_PORT);

						Thread listenThread = new Thread(new Runnable() {
							public void run() {
								while (true) {
									try {
										float randColor = (float)Math.random();
										Color color = new Color(Color.HSBtoRGB(randColor, 1, 1));
										Main.maxPlayers = (Integer)spNumPlayers.getValue();
										Main.players.add(new PlayerHandler(Main.server.accept(), new Snake(0, 0), color));
									} catch (Exception e) {
										e.printStackTrace();
										//Let user press again if something goes wrong
										hostingPressedOnce = false;
									}
								}
								//Main.connectionSocket = Main.server.accept();
								//Create new stream for reading
								//Main.br = new BufferedReader(new InputStreamReader(Main.connectionSocket.getInputStream()));
							}
						});
						listenThread.start();
						
						

						Main.startGame();

						Main.startWindow.dispose();
					} catch (Exception e) {
						JOptionPane.showMessageDialog(null, e.getMessage());
						e.printStackTrace();
					}
				}
			}
		});
		btnHost.setBounds(10, 135, 95, 23);
		contentPane.add(btnHost);
		
		JButton btnSinglePlayer = new JButton("Single Player");
		btnSinglePlayer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//Single player is hosting but max players of 1
				spNumPlayers.setValue(1);
				spSecondsInRound.setValue(Integer.MAX_VALUE);
				btnHost.doClick();
				Main.multiPlayer = false;
			}
		});
		btnSinglePlayer.setBounds(10, 11, 495, 23);
		contentPane.add(btnSinglePlayer);
		
		String IP = "";
		try {
			IP = InetAddress.getLocalHost().getHostAddress();
		} catch (Exception e) {}
		
		JLabel lblIP = new JLabel("Local IP: " + IP);
		lblIP.setBounds(376, 289, 129, 19);
		contentPane.add(lblIP);
		
		txtJoinIP = new JTextField();
		txtJoinIP.setText("localhost");
		txtJoinIP.setBounds(135, 77, 122, 20);
		contentPane.add(txtJoinIP);
		txtJoinIP.setColumns(10);
		
		txtHostPort = new JTextField();
		txtHostPort.setText("9876");
		txtHostPort.setBounds(168, 136, 86, 20);
		contentPane.add(txtHostPort);
		txtHostPort.setColumns(10);
		
		JLabel lblHostPort = new JLabel("Host Port: ");
		lblHostPort.setBounds(115, 139, 63, 14);
		contentPane.add(lblHostPort);
		
		JLabel lblJoinIP = new JLabel("IP:");
		lblJoinIP.setBounds(115, 80, 23, 14);
		contentPane.add(lblJoinIP);
		
		JLabel lblJoinPort = new JLabel("Port:");
		lblJoinPort.setBounds(267, 80, 39, 14);
		contentPane.add(lblJoinPort);
		
		txtJoinPort = new JTextField();
		txtJoinPort.setText("9876");
		txtJoinPort.setBounds(296, 77, 86, 20);
		contentPane.add(txtJoinPort);
		txtJoinPort.setColumns(10);
		
		JLabel lblName = new JLabel("Name: ");
		lblName.setBounds(20, 45, 39, 14);
		contentPane.add(lblName);
		
		txtNameBox = new JTextField();
		txtNameBox.setText("Player " + (int)(Math.random()*101));
		txtNameBox.setBounds(55, 42, 86, 20);
		contentPane.add(txtNameBox);
		txtNameBox.setColumns(10);
		
		lblColor = new JLabel("Color:");
		lblColor.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblColor.setBounds(10, 289, 46, 14);
		contentPane.add(lblColor);

		slider = new JSlider();
		//When value changes, change lblColor's foreground color
		slider.addChangeListener(new ChangeListener () {
			public void stateChanged(ChangeEvent e) {
				//Get object of what changed
				JSlider source = (JSlider)e.getSource();
				
				//We don't want dark colors
				Color color = new Color(Color.HSBtoRGB((float)source.getValue()/100, 1, 1)).brighter().brighter();
				source.setBackground(color);
				lblColor.setForeground(color);
			}
		});
		slider.setValue((int)(Math.random() * 101));
		slider.setBounds(55, 285, 200, 23);
		contentPane.add(slider);
		
		spNumPlayers = new JSpinner();
		spNumPlayers.setModel(new SpinnerNumberModel(new Integer(4), null, null, new Integer(1)));
		spNumPlayers.setBounds(330, 136, 54, 20);
		contentPane.add(spNumPlayers);
		
		JLabel lblNumberOfPlayers = new JLabel("Max Players:");
		lblNumberOfPlayers.setBounds(264, 139, 73, 14);
		contentPane.add(lblNumberOfPlayers);
		
		JButton btnCheckForUpdates = new JButton("Check for Updates");
		btnCheckForUpdates.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					if (Updater.isUpdate()) {
						//File chooser object with jar files as default file to save to
						JFileChooser jfc = new JFileChooser();
						jfc.setDialogTitle("Select a save loction for the updated version.");
						jfc.setFileFilter(new FileNameExtensionFilter("JAR Files", "jar"));
						jfc.showOpenDialog(null);

						File toSave = jfc.getSelectedFile();

						//If the name of the file dosen't have .jar at the end, add it to the file name
						if (!toSave.getName().toLowerCase().endsWith(".jar"))
							toSave = new File(toSave.getAbsolutePath() + ".jar");

						Updater.downloadFile("http://www.motths.net/Snake.jar", toSave.getAbsolutePath());
					}
					else {
						JOptionPane.showMessageDialog(null, "You are running the latest version.");
					}
				} catch (Exception e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(null, e.getMessage());
				}
			}
		});
		btnCheckForUpdates.setBounds(358, 255, 147, 23);
		contentPane.add(btnCheckForUpdates);
		
		JLabel lblNumberOfApples = new JLabel("Number of apples:");
		lblNumberOfApples.setBounds(10, 169, 95, 14);
		contentPane.add(lblNumberOfApples);
		
		spNumApples = new JSpinner();
		spNumApples.setModel(new SpinnerNumberModel(new Integer(3), null, null, new Integer(1)));
		spNumApples.setBounds(103, 166, 39, 20);
		contentPane.add(spNumApples);
		
		JLabel lblPointApplesGive = new JLabel("Points apples give you:");
		lblPointApplesGive.setBounds(10, 194, 115, 14);
		contentPane.add(lblPointApplesGive);
		
		spApplePoints = new JSpinner();
		spApplePoints.setModel(new SpinnerNumberModel(new Integer(5), null, null, new Integer(1)));
		spApplePoints.setBounds(123, 191, 35, 20);
		contentPane.add(spApplePoints);
		
		JLabel lblSecondsInRound = new JLabel("Seconds in round:");
		lblSecondsInRound.setBounds(152, 169, 95, 14);
		contentPane.add(lblSecondsInRound);
		
		spSecondsInRound = new JSpinner();
		spSecondsInRound.setModel(new SpinnerNumberModel(new Integer(120), null, null, new Integer(1)));
		spSecondsInRound.setBounds(241, 166, 54, 20);
		contentPane.add(spSecondsInRound);
	}
}
