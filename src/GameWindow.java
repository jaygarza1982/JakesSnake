import java.awt.Color;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferStrategy;
import javax.swing.JFrame;

public class GameWindow extends JFrame {
	private static final long serialVersionUID = 8008568686871937758L;
	
	BufferStrategy bs;
	boolean isFullscreen = false;
	
	public GameWindow(GameManager gameManager, boolean fullscreen) {
		isFullscreen = fullscreen;
		setSize(gameManager.winWidth, gameManager.winHeight + gameManager.cellSize);

//		setResizable(false);
		setBackground(Color.BLACK);
		setTitle("Snake Game");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ESCAPE) 
					System.exit(0);
			}
		});
		
		if (fullscreen) {
			setUndecorated(true);
			setExtendedState(JFrame.MAXIMIZED_BOTH);
			setAlwaysOnTop(true);
		}
		
		setVisible(true);
		createBufferStrategy(2);
		bs = getBufferStrategy();
	}

}