//Purpose of class is to be a UDP packet publisher, MulticastReceiver receives the packet

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class MulticastPublisher implements Runnable {
    private DatagramSocket socket;
    private InetAddress group;
    private byte[] buf;
    private int port;

    public MulticastPublisher(String group, int port, String multicastMessage) throws Exception {
        socket = new DatagramSocket();
        this.port = port;
        this.group = InetAddress.getByName(group);
        buf = multicastMessage.getBytes();
    }
    
    @Override
    public void run() {
    	try {
			DatagramPacket packet = new DatagramPacket(buf, buf.length, group, port);
			socket.send(packet);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
}