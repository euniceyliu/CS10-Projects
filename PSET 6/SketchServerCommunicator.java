import java.awt.*;
import java.io.*;
import java.net.Socket;

/**
 * Handles communication between the server and one client, for SketchServer
 * @author Kevine Twagizihirwe, Eunice Liu, CS10, 22W
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Fall 2012; revised Winter 2014 to separate SketchServerCommunicator
 */
public class SketchServerCommunicator extends Thread {
	private Socket sock;                    // to talk with client
	private BufferedReader in;                // from client
	private PrintWriter out;                // to client
	private SketchServer server;            // handling communication for

	public SketchServerCommunicator(Socket sock, SketchServer server) {
		this.sock = sock;
		this.server = server;
	}

	/**
	 * Sends a message to the client
	 * @param msg
	 */
	public void send(String msg) {
		out.println(msg);
	}

	/**
	 * Keeps listening for and handling (your code) messages from the client
	 */
	public void run() {
		try {
			System.out.println("someone connected");

			// Communication channel
			in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			out = new PrintWriter(sock.getOutputStream(), true);

			// Tell the client the current state of the world
			// TODO: YOUR CODE HERE
			// Inform the client about the shapes accessed from the map in Sketch
			for (Integer i : server.getSketch().accessShapeMap().keySet()) {
				send("add " + server.getSketch().accessShapeMap().get(i));
			}

			// Keep getting and handling messages from the client
			// TODO: YOUR CODE HERE
			String clientMessage;
			while ((clientMessage = in.readLine()) != null) {
				server.broadcast(clientMessage);

				// Update the Sketch
				handleMessage messageHandler = new handleMessage(clientMessage, server.getSketch());
				String currRequest = messageHandler.getRequest();

				// How to deal with the messages -- add or recolor or move or delete
				switch (currRequest) {
					case "add" -> messageHandler.handleAdd();
					case "recolor" -> messageHandler.handleRecolor();
					case "move" -> messageHandler.handleMove();
					case "delete" -> messageHandler.handleDelete();
				}

			}
			// Clean up -- note that also remove self from server's list, so it doesn't broadcast here
			server.removeCommunicator(this);
			out.close();
			in.close();
			sock.close();
		}
		// Throw an exception about where the errors happened
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}