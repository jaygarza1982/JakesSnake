import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.Stack;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.JCheckBox;

public class Main extends JFrame {
	private static final long serialVersionUID = 6361064515177470778L;
	
	private JPanel contentPane;
	private JTextField txtIP;
	Main main;
	private JTextField txtName;
	private JTextField txtPort;
	private JLabel lblColor;
	private JList<String> list;
	private JSlider slider;
	private JTextField txtServerName;
	private JButton btnJoin;
	private JCheckBox chckbxFullScreen;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Main frame = new Main();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Main() {
		main = this;
		setTitle("Jake's Snake");

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 672, 418);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		final GameManager gameManager = new GameManager(700, 700, 20);
		final BufferedImage gameImage = new BufferedImage(gameManager.winWidth, gameManager.winHeight, BufferedImage.TYPE_4BYTE_ABGR);
		JButton btnHost = new JButton("Host");
		btnHost.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				int port = Integer.parseInt(txtPort.getText());
				
				
				Snake hostSnake = gameManager.getSnakes().get("host");
				hostSnake.setName(txtName.getText());
				hostSnake.setColor(lblColor.getForeground());
				
				GameWindow gameWindow = new GameWindow(gameManager, chckbxFullScreen.isSelected());
				long FPS = 1000 / 15;

				gameWindow.addKeyListener(new MovementKeys(gameManager.getSnakes().get("host")));

				ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
				GameRenderer gameRenderer = new GameRenderer(gameImage, gameWindow, gameManager);
				
				GameLoop gameLoop = new GameLoop(gameRenderer, gameManager);
				ses.scheduleAtFixedRate(gameLoop, 0, FPS, TimeUnit.MILLISECONDS);

				ScheduledExecutorService UDPServerThread = Executors.newSingleThreadScheduledExecutor();
				UDPServer server = new UDPServer(port, gameManager);
				UDPServerThread.scheduleAtFixedRate(server, 0, FPS/4, TimeUnit.MILLISECONDS);
				
				try {
					ScheduledExecutorService UDPMulitCastThread = Executors.newSingleThreadScheduledExecutor();
					MulticastPublisher multicastPublisher = new MulticastPublisher("224.0.2.60", 4446, txtServerName.getText());
					UDPMulitCastThread.scheduleAtFixedRate(multicastPublisher, 0, 1500, TimeUnit.MILLISECONDS);
				} catch (Exception e) {
					e.printStackTrace();
				}

				main.dispose();
			}
		});
		
		btnJoin = new JButton("Join");
		btnJoin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				GameManager gameManager = new GameManager(700, 700, 20);
				GameWindow gameWindow = new GameWindow(gameManager, chckbxFullScreen.isSelected());
				long FPS = 1000 / 15;

				String ip = txtIP.getText();
				int port = Integer.parseInt(txtPort.getText());

				ClientMessage clientMessage = new ClientMessage(txtName.getText(), lblColor.getForeground());
				gameWindow.addKeyListener(new ClientKeyListener(clientMessage));

				ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
				GameRenderer gameRenderer = new GameRenderer(gameImage, gameWindow, gameManager);
				Stack<GameManager> gameManagerStack = new Stack<>();
				GameLoop gameLoop = new GameLoop(gameRenderer, gameManager, gameManagerStack);
				ses.scheduleAtFixedRate(gameLoop, 0, FPS, TimeUnit.MILLISECONDS);

				ScheduledExecutorService UDPClientThread = Executors.newSingleThreadScheduledExecutor();
				UDPClient client = new UDPClient(ip, port, gameManagerStack, clientMessage);
				UDPClientThread.scheduleAtFixedRate(client, 0, FPS, TimeUnit.MILLISECONDS);
				
				main.dispose();
			}
		});

		DefaultListModel<String> model = new DefaultListModel<String>();
		list = new JList<String>(model);
		list.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent evt) {
		        if (evt.getClickCount() == 2) {
		        	String ip = list.getSelectedValue().split(" ")[0].substring(1);
		        	
		        	txtIP.setText(ip);
		        	btnJoin.doClick();
		        }
			}
		});
		
		//Set up the multicast listener
		try {
			ScheduledExecutorService UDPMulitCastThread = Executors.newSingleThreadScheduledExecutor();
			MulticastReceiver multicastPublisher = new MulticastReceiver("224.0.2.60", 4446, model);
			UDPMulitCastThread.scheduleAtFixedRate(multicastPublisher, 0, 1500, TimeUnit.MILLISECONDS);
		} catch (Exception e) {
			e.printStackTrace();
		}
		

		JLabel lblOpenServers = new JLabel("Open Servers: ");

		txtIP = new JTextField();
		txtIP.setColumns(10);
		
		JLabel lblName = new JLabel("Name:");
		
		txtName = new JTextField();
		txtName.setText("Player " + Math.round(Math.random()*99));
		txtName.setColumns(10);
		
		txtPort = new JTextField();
		txtPort.setText("9876");
		txtPort.setColumns(10);
		
		JLabel lblPort = new JLabel("Port:");
		
		JLabel lblServerName = new JLabel("Server name:");
		
		txtServerName = new JTextField();
		txtServerName.setText("Server " + Math.round(Math.random()*99));
		txtServerName.setColumns(10);
		
		lblColor = new JLabel("Color: ");
		
		slider = new JSlider();
		slider.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				int c = Color.HSBtoRGB((float)slider.getValue()/100, 1, 1);
				
				//Prevent darker colors
				lblColor.setForeground(new Color(c).brighter());
			}
		});
		
		slider.setValue((int)(Math.random()*99));
		
		lblColor.setVerticalAlignment(SwingConstants.BOTTOM);
		
		chckbxFullScreen = new JCheckBox("Full screen");
		chckbxFullScreen.setSelected(true);
		
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addComponent(btnJoin, GroupLayout.PREFERRED_SIZE, 111, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(txtIP, GroupLayout.PREFERRED_SIZE, 140, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED, 293, Short.MAX_VALUE)
					.addComponent(btnHost, GroupLayout.PREFERRED_SIZE, 112, GroupLayout.PREFERRED_SIZE))
				.addComponent(lblOpenServers)
				.addComponent(list, GroupLayout.DEFAULT_SIZE, 662, Short.MAX_VALUE)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(lblName)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(txtName, GroupLayout.PREFERRED_SIZE, 133, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED, 263, Short.MAX_VALUE)
							.addComponent(lblPort)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(txtPort, GroupLayout.PREFERRED_SIZE, 146, GroupLayout.PREFERRED_SIZE))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(slider, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED, 222, Short.MAX_VALUE)
							.addComponent(lblServerName)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(txtServerName, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
					.addContainerGap())
				.addGroup(gl_contentPane.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblColor)
					.addContainerGap(606, Short.MAX_VALUE))
				.addGroup(Alignment.TRAILING, gl_contentPane.createSequentialGroup()
					.addContainerGap(528, Short.MAX_VALUE)
					.addComponent(chckbxFullScreen)
					.addContainerGap())
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addComponent(lblOpenServers)
					.addGap(9)
					.addComponent(list, GroupLayout.DEFAULT_SIZE, 184, Short.MAX_VALUE)
					.addGap(18)
					.addComponent(chckbxFullScreen)
					.addGap(14)
					.addComponent(lblColor)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(slider, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addGap(18)
							.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
								.addComponent(lblName)
								.addComponent(txtName, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
							.addGap(18))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
								.addComponent(txtServerName, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(lblServerName))
							.addGap(16)
							.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
								.addComponent(txtPort, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(lblPort))
							.addPreferredGap(ComponentPlacement.RELATED)))
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnJoin)
						.addComponent(txtIP, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnHost)))
		);
		contentPane.setLayout(gl_contentPane);
	}
}
