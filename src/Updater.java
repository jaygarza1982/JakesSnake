//Class is used for checking for updates for the program

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.security.MessageDigest;

public class Updater {
	//Returns the exact location to running jar file
	private static String currentPath() throws Exception {
		return Main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
	}
	
	//Method downloads file
	public static void downloadFile(String url, String savePath) throws Exception {
        URL website = new URL(url);
        //Readable byte channel to read bytes from given url
        ReadableByteChannel rbc = Channels.newChannel(website.openStream());
        //Output stream to write bytes to file
        FileOutputStream fos = new FileOutputStream(savePath);
        //Transfer all bytes to the files readable byte channel
        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        //Close streams
        fos.close();
        rbc.close();
    }
	
	//Function returns hex string of a sha1 from a URL
	private static String SHA1(URL url) throws Exception {
        //New message digest for sha1
        MessageDigest md = MessageDigest.getInstance("SHA1");
        //Input stream to read files
        InputStream is = url.openStream();
        byte[] dataBytes = new byte[1024];

        int nread = 0;

        //is.read returns the number of bytes read, if it is -1, the end of the file has been reached
        while ((nread = is.read(dataBytes)) != -1) {
            //Update bytes of our hash
            md.update(dataBytes, 0, nread);
        }
        
        //Close stream
        is.close();

        //Do final calculation
        byte[] mdbytes = md.digest();

        //Convert the byte to hex format
        StringBuffer sb = new StringBuffer("");
        for (int i = 0; i < mdbytes.length; i++)
        	sb.append(String.format("%X", mdbytes[i]));

        return sb.toString();
	}

	//Function returns hex string of a sha1 from a file
	private static String SHA1(String filePath) throws Exception {
		//New message digest for sha1
		MessageDigest md = MessageDigest.getInstance("SHA1");
		//Input stream to read files
		InputStream is = new FileInputStream(filePath); //new URL(filePath).openStream();
		byte[] dataBytes = new byte[1024];

		int nread = 0;

		//is.read returns the number of bytes read, if it is -1, the end of the file has been reached
		while ((nread = is.read(dataBytes)) != -1) {
			//Update bytes of our hash
			md.update(dataBytes, 0, nread);
		}

		//Close stream
		is.close();

		//Do final calculation
		byte[] mdbytes = md.digest();

		//Convert the byte to hex format
		StringBuffer sb = new StringBuffer("");
		for (int i = 0; i < mdbytes.length; i++)
			sb.append(String.format("%X", mdbytes[i]));

		return sb.toString();
	}

	//Returns true if the updated SHA and running SHA DO NOT equal each other
	public static boolean isUpdate() throws Exception {
		String fileOnServerSHA = SHA1(new URL("http://www.motths.net/Snake.jar"));
		String runningFileSHA = SHA1(currentPath());
		
		return !fileOnServerSHA.equals(runningFileSHA);
	}
}