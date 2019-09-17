import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class ClientKeyListener implements KeyListener {
	ClientMessage clientMessage;
	
	public ClientKeyListener(ClientMessage clientMessage) {
		this.clientMessage = clientMessage;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		//Convert input to lower case
		char key = (e.getKeyChar() + "").toLowerCase().charAt(0);
		
        if (key == 'w') clientMessage.direction = Snake.UP;
        else if (key == 's') clientMessage.direction = Snake.DOWN;
        else if (key == 'a') clientMessage.direction = Snake.LEFT;
        else if (key == 'd') clientMessage.direction = Snake.RIGHT;
	}

	@Override
	public void keyReleased(KeyEvent e) {}
	
	@Override
	public void keyTyped(KeyEvent e) {}
}
