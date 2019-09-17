import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Arrays;

public class UDPServer implements Runnable {
    private DatagramSocket serverSocket;
    private GameManager gameManager;
    private DatagramPacket receivePacket;
    
    public UDPServer(int port, GameManager gameManager) {
        try {
            this.gameManager = gameManager;
            serverSocket = new DatagramSocket(port);

            System.out.println("Server started on port " + port);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    @Override
    public void run() {
    	try {
    		byte buffer[] = new byte[1024*20];

    		receivePacket = new DatagramPacket(buffer, 0, buffer.length);

    		serverSocket.receive(receivePacket);
    		byte trimmed[] = Arrays.copyOf(receivePacket.getData(), receivePacket.getLength());
    		ByteArrayInputStream bais = new ByteArrayInputStream(trimmed);
    		ObjectInputStream ois = new ObjectInputStream(bais);
    		ClientMessage clientMessage = (ClientMessage) ois.readObject();

    		//If the player has not been added to the game yet
    		if (!gameManager.getSnakes().containsKey(clientMessage.cookie)) {
    			Snake snake = new Snake(0, 0);
    			snake.setName(clientMessage.name.length() > 20 ? clientMessage.name.substring(0, 20) : clientMessage.name);
    			snake.setColor(clientMessage.color);
    			gameManager.getSnakes().put(clientMessage.cookie, snake);
    		} else { 
    			Snake snake = gameManager.getSnakes().get(clientMessage.cookie);
    			snake.setDirection(clientMessage.direction);
    			snake.setTimeToLive(10);
    		}

    		//                String receivedMessage = new String(receivePacket.getData()).substring(0, receivePacket.getLength());

    		//                System.out.println(receivedMessage);

    		ByteArrayOutputStream baos = new ByteArrayOutputStream();
    		ObjectOutputStream oos = new ObjectOutputStream(baos);
    		oos.writeObject(gameManager);
//    		gameManager.notifyAll();

    		byte sendData[] = baos.toByteArray();
    		//                System.out.println("Sending: " + sendData.length);
    		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, receivePacket.getAddress(), receivePacket.getPort());
    		serverSocket.send(sendPacket);  
        } catch (Exception e) {
        	byte errorData[] = "There was an error".getBytes();
        	DatagramPacket errorPacket = new DatagramPacket(errorData, errorData.length, receivePacket.getAddress(), receivePacket.getPort());
        	try {
				serverSocket.send(errorPacket);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
            e.printStackTrace();
        }
        
        
    }
}
