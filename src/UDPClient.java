import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Stack;

public class UDPClient implements Runnable {
    String ip;
    int port;
    Stack<GameManager> gameManagerStack;
    ClientMessage clientMessage;
    DatagramSocket clientSocket;

    public UDPClient(String ip, int port, Stack<GameManager> gameManagerStack, ClientMessage clientMessage) {
        this.ip = ip;
        this.port = port;
        this.gameManagerStack = gameManagerStack;
        this.clientMessage = clientMessage;
        try {
			clientSocket = new DatagramSocket();
		} catch (SocketException e) {
			e.printStackTrace();
		}
    }

    @Override
    public void run() {
    	String message = "";
    	
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(clientMessage);
            byte sendData[] = baos.toByteArray();

            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName(ip), port);
            clientSocket.send(sendPacket);

            byte receiveData[] = new byte[1024*20];
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            clientSocket.receive(receivePacket);

            byte trimmed[] = Arrays.copyOf(receivePacket.getData(), receivePacket.getLength());
            message = new String(trimmed);
            ByteArrayInputStream bais = new ByteArrayInputStream(trimmed);
            ObjectInputStream ois = new ObjectInputStream(bais);
            gameManagerStack.push((GameManager) ois.readObject());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(message);
        }
    }
}
