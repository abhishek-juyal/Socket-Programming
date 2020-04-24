
import java.net.*;
import java.io.*;

/**
 * This program demonstrates a client socket application that connects to a web
 * server and send a HTTP request.
 *
 * @author abhishek
 */
public class HttpClient {

	/**
	 * main method to connect to the serve using socket
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		/*
		 * take the 4 arguments i.e server,path, port and method
		 */
		if (args.length < 4) {
			System.out.println("Arguments should be server,path, port and method.");
			System.exit(0);
		}
		// variables from arguments
		String server = args[0];
		String port = args[1];
		String path = args[2];
		String method = args[3];

		System.out.println("Loading contents of URL: " + server);

		try {
			// Connect to the server
			Socket socket = new Socket(server, Integer.valueOf(port));

			// Create input and output streams to read from and write to the server
			PrintStream out = new PrintStream(socket.getOutputStream());
			// Follow the HTTP protocol of GET <path> HTTP/1.0 followed by an empty line
			out.println(method + " " + path + " HTTP/1.0");
			out.println();
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

			// Read data from the server until we finish reading the document
			String line = in.readLine();
			while (line != null) {
				System.out.println(line);
				line = in.readLine();
			}

			// Close our streams
			in.close();
			out.close();
			socket.close();
		} catch (Exception e) {
			// print any exception caught while connecting
			e.printStackTrace();
		}
	}
}
