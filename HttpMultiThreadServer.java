import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

import org.apache.commons.io.FileUtils;

/**
 * This program creates a server and responds to the request to this server.
 *
 * @author abhishek
 */
public class HttpMultiThreadServer implements Runnable {
	Socket csocket;
	private static final String WEB_ROOT = "C:\\Users\\Admin\\Desktop";

	HttpMultiThreadServer(Socket csocket) {
		this.csocket = csocket;
	}

	/**
	 * main method to process multiple requests and span a new thread for a new
	 * request
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String args[]) throws Exception {
		// Get the port to listen on
		int port = Integer.parseInt(args[0]);
		// Create a ServerSocket to listen on that port.
		ServerSocket ss = new ServerSocket(port);
		System.out.println("Listening");
		// Now enter an infinite loop, waiting for & handling connections.
		while (true) {
			// Wait for a client to connect. The method will block;
			// when it returns the socket will be connected to the client
			Socket client = ss.accept();
			System.out.println("Connected");
			new Thread(new HttpMultiThreadServer(client)).start();
		}
	}

	/**
	 * This method run per thread for the client server request handling
	 */
	public void run() {
		try {
			// Get input and output streams to talk to the client
			BufferedReader in = new BufferedReader(new InputStreamReader(csocket.getInputStream()));
			PrintWriter out = new PrintWriter(csocket.getOutputStream());
			/*
			 * read the input lines from client and perform respective action based on the
			 * request
			 */
			String line;
			while ((line = in.readLine()) != null) {
				// return if all the lines are read
				if (line.length() == 0)
					break;
				else if (line.contains("GET") && line.contains("/index.html")) {
					/*
					 * get the respective file requested by the client i.e index.html
					 */
					File file = new File(WEB_ROOT, "/index.html");
					// send HTTP Headers
					out.println("HTTP/1.1 200 OK");
					out.println("Date: " + new Date());
					out.println("Content-length: " + file.length());
					out.println(); // blank line between headers and content
					out.flush(); // flush character output stream buffer
					out.write(FileUtils.readFileToString(file, "UTF-8"), 0, ((int) file.length()));
				} else if (line.contains("PUT")) {
					/*
					 * put the respective file at a location on local
					 */
					String[] split = line.split(" ");
					File source = new File(split[1]);
					File dest = new File(WEB_ROOT, "clientFile.html");
					try {
						// copy the file to local storage
						FileUtils.copyFile(source, dest);
					} catch (IOException e) {
						e.printStackTrace();
					}
					// send HTTP Headers
					out.println("HTTP/1.1 200 OK");
					out.println("Date: " + new Date());
					out.println(); // blank line between headers and content
					out.flush(); // flush character output stream buffer
				} else {
					out.print(line + "\r\n");
				}
			}
			// closing the input and output streams
			out.close(); // Flush and close the output stream
			in.close(); // Close the input stream
			csocket.close(); // Close the socket itself
		} // If anything goes wrong, print an error message
		catch (Exception e) {
			System.err.println(e);
			System.err.println("Usage: java HttpMirror <port>");
		}
	}
}
