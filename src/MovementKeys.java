import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class MovementKeys implements KeyListener {
    public Snake snake;

    public MovementKeys(Snake snake) {
        this.snake = snake;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        char key = (e.getKeyChar() + "").toLowerCase().charAt(0); //Convert input to lower case
        if (key == 'w') snake.setDirection(Snake.UP);
        else if (key == 's') snake.setDirection(Snake.DOWN);
        else if (key == 'a') snake.setDirection(Snake.LEFT);
        else if (key == 'd') snake.setDirection(Snake.RIGHT);
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}
}
