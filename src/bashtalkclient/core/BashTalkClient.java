package bashtalkclient.core;

import java.io.*;
import java.net.*;
import java.time.*;
import java.time.format.*;
import java.util.*;

/**
 * Abstract implementation of the BashTalk client.
 * 
 * @version 1.0.0
 */
public abstract class BashTalkClient {
	
	protected static final String DEFAULT_HOST = "127.0.0.1";
	protected static final int DEFAULT_PORT = 9898;
	
	protected String host;
	protected int port;
	protected Socket socket;
	
	protected String username;
	
	protected BufferedReader inStream;
	protected PrintWriter outStream;
	
	protected boolean running = true;
	
	/**
	 * Default constructor to initialize fields.
	 * Uses localhost (127.0.0.1), port 9898, and a blank username.
	 */
	public BashTalkClient()
	{
		this(DEFAULT_HOST, DEFAULT_PORT, "");
	}
	
	/**
	 * Constructor to initialize fields.
	 * 
	 * @param host
	 *            - IP address of server
	 * @param port
	 *            - Port of server
	 * @param username
	 *            - Username of client
	 */
	public BashTalkClient(String host, int port, String username)
	{
		this.host = host;
		this.port = port;
		this.username = username;
	}
	
	/**
	 * Parse the command line arguments to determine whether the user wants
	 * to connect using a terminal or the GUI.
	 * 
	 * @param args
	 *            - JVM arguments
	 * @return BashTalkClient instance
	 */
	public static BashTalkClient parseArgs(String[] args)
	{
		
		if (args.length == 0)
			return new ClientUIMode();
		
		if (args.length == 1 && args[0].equals("-t")) // User has provided neither IP address nor port (prompts user for IP address and port)
		{
			
			Scanner input = new Scanner(System.in);
			
			System.out.print("Enter the server's IP address: ");
			String host = input.nextLine();
			System.out.print("Enter the server's port: ");
			int port = input.nextInt();
			
			input.close();
			return new TerminalClient(host, port);
			
		}
		else if (args.length == 2 && args[0].equals("-t")) // User provided IP address but not port (uses default port: 9898)
			return new TerminalClient(args[1], DEFAULT_PORT);
		else if (args.length == 3 && args[0].equals("-t")) // User provided both the IP address and port
			return new TerminalClient(args[1], Integer.parseInt(args[2]));
		else
		{
			
			System.out.println("Usage: -t [ip] [port]");
			Runtime.getRuntime().exit(-1);
			
		}
		return null;
	}
	
	/**
	 * Connect to the server using the given host and port.
	 * The connection times out after 2.5 seconds.
	 */
	public void connect()
	{
		
		try
		{
			
			socket = new Socket();
			socket.connect(new InetSocketAddress(host, port), 2500);
			
			inStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			outStream = new PrintWriter(socket.getOutputStream(), true);
			
			// Checks for username validity and prints cached message history
			while (running)
			{
				
				clearOutput();
				
				// Username prompt or max clients reached warning
				String response = inStream.readLine();
				
				// Server cannot accept any more clients
				if (response.equals("Maximum number of clients reached."))
				{
					alertMessage("Maximum number of clients reached. Please try again later.");
					disconnect();
				}
				
				// Use received instructions from the server to prompt the user for a username
				// if they don't already have one
				if (username.equals(""))
					username = getLocalInput(response);
				
				// Send username to server
				outStream.println(username);
				
				// Wait for valid username response
				response = inStream.readLine();
				
				if (response.equals("Username approved. Welcome."))
				{
					
					clearOutput();
					displayMessage(response);
					displayMessage("");
					
					while (!response.equals("-- End of Message History --"))
					{
						
						response = inStream.readLine();
						displayMessage(response);
						
					}
					
					displayMessage("");
					
					// Break out of username error trap
					break;
					
				}
				else
				{
					
					// Show invalid username error from server
					alertMessage(response);
					
					username = getLocalInput("Please enter a valid username: ");
					
					if (username == null || username.length() == 0)
						disconnect();
					
				}
				
			}
			
			new Thread("ListenMessage") {
				
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
			
			new Thread("ListenLocalInput") {
				
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
		catch (Exception err) // TODO: Handle error (output to file?)
		{
			
			String message = "";
			if (err.getMessage().indexOf("refused") != -1)
				message = "Connection refused: Please try again later!";
			else if (err.getMessage().indexOf("reset") != -1)
				message = "Connection reset: The server closed the connection!";
			else if (err.getMessage().indexOf("timed") != -1)
				message = "Connection timed out: Please enter the correct IP and Port";
			else
				message = "Oops... Should have handled errors better.";
			
			alertMessage(message);
			
		}
		
	}
	
	/**
	 * Disconnects from the server and kills the client.
	 */
	public void disconnect()
	{
		
		try
		{
			
			running = false;
			
			inStream.close();
			outStream.flush();
			outStream.close();
			socket.close();
			
			displayMessage("Disconnected from server.");
			Runtime.getRuntime().exit(0);
			
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Relays formatted message to server.
	 * 
	 * @param msg
	 *            - Message to be sent to the server
	 */
	public void sendMessage(String msg)
	{
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm");
		
		outStream.println("[" + dtf.format(LocalDateTime.now()) + "] <" + username + "> " + msg);
	}
	
	/**
	 * Listens to all messages from the server and print them to local client.
	 */
	public void listenMessage()
	{
		
		while (running)
		{
			
			String incoming = null;
			try
			{
				incoming = inStream.readLine();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			
			if (incoming == null)
				disconnect();
			else if (incoming.equals("shutdown"))
			{
				alertMessage("Server is shutting down...");
				disconnect();
			}
			else if (incoming.equals("banned"))
			{
				alertMessage("An administrator banned you from the server.");
				disconnect();
			}
			else
				displayMessage(incoming);
			
		}
		
	}
	
	/**
	 * Listen for input from the client.
	 */
	protected abstract void listenLocalInput();
	
	/**
	 * Get input from the client.
	 * 
	 * @param prompt
	 *            - Prompt message displayed to user
	 * @return User's response
	 */
	protected abstract String getLocalInput(String prompt);
	
	/**
	 * Show text in the implemented output of the client class.
	 * 
	 * @param msg
	 *            - Text to be shown in the client
	 */
	protected abstract void displayMessage(String msg);
	
	/**
	 * Display alert message in the implemented error output of the client class.
	 * 
	 * @param alert
	 *            - Alert message to be shown to user
	 * @deprecated
	 */
	protected abstract void alertMessage(String alert);
	
	/**
	 * Clears the output defined in the client class.
	 */
	protected abstract void clearOutput();
	
	public static void main(String[] args)
	{
		parseArgs(args);
	}
	
}