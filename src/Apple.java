import java.awt.Point;
import java.io.Serializable;

public class Apple implements Serializable {
	private static final long serialVersionUID = -7335256724830797233L;
	
	int x, y;

    public Apple() {
        x = 0;
        y = 0;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Point getPos() {
        return new Point(x, y);
    }

    public void setPos(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
