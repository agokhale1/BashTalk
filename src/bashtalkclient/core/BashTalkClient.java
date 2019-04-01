package bashtalkclient.core;

import java.io.*;
import java.net.*;
import java.time.*;
import java.time.format.*;
import java.util.*;

import bashtalkclient.ui.*;

public abstract class BashTalkClient {

	protected String host;
	protected int port;
	protected String username;
	protected Socket socket;
	protected BufferedReader in;
	protected PrintWriter out;

	/*
	 * Default constructor to initialize fields.
	 * Uses localhost, port 9898, and a blank username.
	 *
	 */
	public BashTalkClient()
	{
		this("127.0.0.1", "9898", "");
	}

	/*
	 * Constructor to initialize fields
	 *
	 */
	public BashTalkClient(String host, String port, String username)
	{
		this.host = host;
		this.port = Integer.parseInt(port);
		this.username = username;
	}

	/*
	 * Connect to the server
	 *
	 */
	public void connectToServer()
	{

		try
		{
			// Create socket and set timeout to 2.5 seconds
			this.socket = new Socket();
			this.socket.connect(new InetSocketAddress(this.host, this.port), 2500);

			// Create buffers for sending and receiving data from the server
			this.in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
			this.out = new PrintWriter(this.socket.getOutputStream(), true);

			// Checks for username validity and prints cached message history
			while (true)
			{

				// Clear screen between username prompts
				clearOutput();

				// Username prompt or max clients reached warning
				String response = this.in.readLine();

				// Server cannot accept any more clients
				if (response.equals("Maximum number of clients reached."))
				{
					alertMessage("Maximum number of clients reached. Please try again later.");
					this.in.close();
					this.out.close();
					this.socket.close();
					System.exit(0);
				}

				// Use received instructions from the server to prompt the user for a username
				// if they don't already have one
				if (this.username.equals(""))
					this.username = getInput(response);

				// Send username to server
				this.out.println(this.username);

				// Wait for valid username response
				response = this.in.readLine();

				// Username has been accepted and server join is successful
				if (response.equals("Username approved. Welcome."))
				{
					// Clear terminal
					clearOutput();
					appendMessage(response);
					appendMessage("");

					// Receive all cached messages
					while (!response.equals("-- End of Message History --"))
					{
						// Receive each message and append to terminal
						response = this.in.readLine();
						appendMessage(response);
					}

					appendMessage("");

					// Break out of username error trap
					break;

				}
				else
				{

					// Show invalid username error from server
					alertMessage(response);

					// Request new username
					this.username = getInput("Please enter a valid username: ");

					// User hit cancel or did not type anything
					if (this.username == null || this.username.length() == 0)
						System.exit(0);
				}
			}

			// Generates a new thread so that the client can simultaneously listen to
			// incoming messages
			new Thread() {

				@Override
				public void run()
				{
					try
					{
						listenMessage();
					}
					catch (Exception e)
					{
						alertMessage("Error listening to messages!");
					}
				}
			}.start();

			// Generate a new thread so that the client can simultaneously listen for local input from the user
			new Thread() {

				@Override
				public void run()
				{
					try
					{
						listenLocalInput();
					}
					catch (Exception e)
					{
						alertMessage("Error listening for local input!");
					}
				}
			}.start();
		}
		catch (Exception err)
		{
			String message = "";
			if (err.getMessage().indexOf("refused") != -1)
				message = "Connection refused: Please try again later!";
			else if (err.getMessage().indexOf("reset") != -1)
				message = "Connection reset: The server closed the connection!";
			else if (err.getMessage().indexOf("timed") != -1)
				message = "Connection timed out: Please enter the correct IP and Port";
			else
				message = "Unhandled error:\n" + err.getMessage();

			alertMessage(message);
		}
	}

	/*
	 * Send the message to the server.
	 *
	 */
	public void sendMessage(String msg)
	{
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm");
		LocalDateTime now = LocalDateTime.now();

		this.out.println("[" + dtf.format(now) + "] <" + username + "> " + msg);
	}

	/*
	 * Listens to all messages from the server and print them to local client.
	 *
	 */
	public void listenMessage() throws IOException
	{
		// Listen for messages and append to display
		while (true)
		{
			// Wait for a message
			String incoming = this.in.readLine();

			// Server has closed socket
			if (incoming == null)
			{
				// Clear terminal and exit
				clearOutput();
				System.exit(0);
				break;
			}

			// User has been banned from the server
			if (incoming.equals("banned"))
			{
				alertMessage("An administrator banned you from the server.");
				System.exit(0);
			}

			// Append the message to the terminal
			appendMessage(incoming);
		}
	}

	/*
	 * Begin listening for local input
	 *
	 */
	abstract void listenLocalInput();

	/*
	 * Get text from the user via whatever input the child class is using.
	 *
	 */
	abstract String getInput(String prompt);

	/*
	 * Add standard text to whatever output the child class is using.
	 *
	 */
	abstract void appendMessage(String msg);

	/*
	 * Send alert to whatever output the child class is using.
	 */
	abstract void alertMessage(String alert);

	/*
	 * Clears whatever output the child class is using.
	 *
	 */
	abstract void clearOutput();

	/*
	 * Runs the client system
	 *
	 */
	public static void main(String[] args) throws Exception
	{

		if (args.length > 0)
		{
			// Handle arguments and create a Terminal Mode client if needed
			if (args[0].equals("-t") && args.length == 1)
			{
				// Did not already provide address and port
				Scanner terminalIn = new Scanner(System.in);

				System.out.print("Enter the server's IP address: ");
				String host = terminalIn.nextLine();
				System.out.print("Enter the server's port: ");
				String port = terminalIn.nextLine();

				new ClientTerminalMode(host, port);
			}
			else if (args[0].equals("-t") && args.length == 2)
				// User provided address
				// Connect using default port
				new ClientTerminalMode(args[1], "9898");
			else if (args[0].equals("-t") && args.length == 3)
				// User provided port and address
				new ClientTerminalMode(args[1], args[2]);
			else
			{
				// Invalid arguments
				System.out.println("Usage: -t <ip> <port>");
				System.exit(0);
			}
		}
		else
		{
			// Create a UI Mode client
			LoginUI login = new LoginUI();
			login.setVisible(true);
		}
	}
}
