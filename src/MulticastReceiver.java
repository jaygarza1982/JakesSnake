//Purpose of class is to receive a packet from the MulticastPublisher

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import javax.swing.DefaultListModel;

public class MulticastReceiver implements Runnable {
    private MulticastSocket socket = null;
    private byte[] buf = new byte[256];
    private InetAddress group;
    private DefaultListModel<String> model;
    
    public MulticastReceiver(String group, int port, DefaultListModel<String> model) throws Exception {
    	socket = new MulticastSocket(port);
    	this.group = InetAddress.getByName(group);
        socket.joinGroup(this.group);
        this.model = model;
    }

    @Override
    public void run() {
    	try {
    		DatagramPacket packet = new DatagramPacket(buf, buf.length);
    		socket.receive(packet);
    		String received = new String(packet.getData(), 0, packet.getLength());

    		String toAdd = packet.getAddress() + " " + received;
    		if (!model.contains(toAdd)) {
    			model.addElement(toAdd);
    		}
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }
}