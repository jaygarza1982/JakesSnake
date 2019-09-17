import java.awt.Color;
import java.io.Serializable;

public class ClientMessage implements Serializable {
	private static final long serialVersionUID = 6659707237430763392L;
	
	String name = "NAME", cookie, direction = Snake.DOWN;
	Color color;
	public ClientMessage(String name, Color color) {
		this.name = name;
		this.color = color;
		cookie = Math.random()+"";
	}
}
