package bashtalkserver.core;

import java.awt.*;
import java.io.*;
import java.math.*;
import java.net.*;
import java.security.*;
import java.time.*;
import java.time.format.*;
import java.util.*;

import javax.swing.*;

/**
 * Implementation of BashTalkServer with both terminal and GUI capabilities.
 * 
 * @version 1.0.0
 */
public class BashTalkServer {
	
	private static final int DEFAULT_PORT = 9898;
	
	private String host;
	private int port;
	private int clientNumber;
	private boolean useTerminal;
	private String hashedPassword;
	private ArrayList<Client> clients;
	private ArrayList<String> messageCache;
	private final int MAX_CLIENTS = 50;
	private final int MAX_CACHE_SIZE = 100;
	private final String HELP_TEXT = "\n\tClear terminal: /clear" + "\n\tExit terminal: /exit" + "\n\tClear Cache (superuser): /clear_cache" + "\n\tUsers online: /users" + "\n\tPrivate Message: /pmsg <user> <message>" + "\n\tMute: /mute\n\tUnmute: /unmute" + "\n\tBan (superuser): /ban <user>";
	
	/**
	 * Construct BashTalkServer object with given port and password.
	 * 
	 * @param port
	 *            - Port on which to listen for connections
	 * @param plainTxtPassword
	 *            - Administrator password for server
	 */
	public BashTalkServer(int port, String plainTxtPassword)
	{
		this.port = port;
		hashedPassword = hashString(plainTxtPassword);
		
		host = getExternalIp();
		clients = new ArrayList<Client>();
		messageCache = new ArrayList<String>();
		
		clientNumber = 0;
	}
	
	/**
	 * Construct BashTalkServer object with useTerminal option.
	 * 
	 * @param useTerminal
	 *            - Option to use terminal or GUI (true: terminal / false: GUI)
	 */
	public BashTalkServer(boolean useTerminal)
	{
		this(DEFAULT_PORT, "");
		this.useTerminal = useTerminal;
	}
	
	/**
	 * Construct BashTalkServer object with default port and empty string password.
	 */
	public BashTalkServer()
	{
		this(DEFAULT_PORT, "");
	}
	
	/**
	 * Starts the BashTalkServer.
	 */
	public void startServer()
	{
		
		clearOutput();
		System.out.println("-- BashTalk Server --");
		System.out.println("Local: " + getLocalIp() + "[" + port + "]");
		System.out.println("External: " + host + "[" + port + "]");
		System.out.println("");
		
		setUIHints();
		
		// Prompt user to set admin password
		if (!useTerminal)
			setUIHints();
		
		String temp1, temp2;
		do
		{
			
			if (!useTerminal)
			{
				
				temp1 = JOptionPane.showInputDialog(null, "Enter new administrator password:");
				
				// User hit cancel
				if (temp1 == null)
					System.exit(0);
				
				temp2 = JOptionPane.showInputDialog(null, "Confirm password:");
				
				// User hit cancel
				if (temp2 == null)
					System.exit(0);
				
			}
			else
			{
				
				Scanner in = new Scanner(System.in);
				
				System.out.print("Enter new administrator password: ");
				temp1 = in.nextLine();
				
				System.out.print("Confirm password: ");
				temp2 = in.nextLine();
				
				in.close();
				
			}
			
			if (!temp1.equals(temp2))
				if (!useTerminal)
					JOptionPane.showMessageDialog(null, "Passwords did not match. Please try again.");
				else
					System.out.println("Passwords did not match. Please try again.");
				
		} while (!temp1.equals(temp2));
		
		// Store hashed user password
		hashedPassword = hashString(temp1);
		
		// Notify the user that the server has started
		if (!useTerminal)
			JOptionPane.showMessageDialog(null, "The BashTalk Server has started!");
		else
		{
			// Clear terminal or cmd screen
			clearOutput();
			System.out.println("-- BashTalk Server --");
			System.out.println("Local: " + getLocalIp() + "[" + port + "]");
			System.out.println("External: " + host + "[" + port + "]");
			System.out.println("");
		}
		
		ServerSocket listener = null;
		try
		{
			listener = new ServerSocket(port);
			
			while (true)
				if (clients.size() < MAX_CLIENTS)
				{
					clients.add(new Client(clientNumber++, listener.accept()));
					clients.get(clients.size() - 1).start();
				}
				else
				{
					Socket socket = listener.accept();
					PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
					out.println("Maximum number of clients reached.");
					// Client closes self
				}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			broadcastMsg("shutdown");
			try
			{
				listener.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Set the UI to the correct font based on the scale and the resolution
	 */
	private static void setUIHints()
	{
		// Calculates scaling unique to each screen resolution
		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		int scale = (gd.getDisplayMode().getHeight() + gd.getDisplayMode().getWidth()) / 200;
		
		UIManager.put("OptionPane.messageFont", new Font("Consolas", Font.PLAIN, (int) (scale * 1.5)));
		UIManager.put("OptionPane.buttonFont", new Font("Consolas", Font.BOLD, (int) (scale * 1.5)));
	}
	
	/**
	 * Client class used to store information for and communicate with each client that connects to the server.
	 */
	public class Client extends Thread {
		
		public int clientNumber;
		public String username;
		private BufferedReader in;
		private PrintWriter out;
		private Socket socket;
		private boolean muted;
		
		public Client(int clientNumber, Socket socket)
		{
			this.clientNumber = clientNumber;
			this.socket = socket;
			muted = false;
		}
		
		/**
		 * Returns the username of this client.
		 * 
		 * @return Username of client
		 */
		public String getUsername()
		{
			return username;
		}
		
		/**
		 * Returns the mute status of this client.
		 * 
		 * @return Mute status of client (true: muted / false: unmuted)
		 */
		public boolean getMuted()
		{
			return muted;
		}
		
		/**
		 * Sets the mute status of this client.
		 * 
		 * @param state
		 *            - Mute status of client (true: muted / false: unmuted)
		 */
		public void setMuted(boolean state)
		{
			muted = state;
		}
		
		@Override
		public void run()
		{
			try
			{
				// Initialize streams
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				out = new PrintWriter(socket.getOutputStream(), true);
				
				// Handle username
				while (true)
				{
					directMsg("Please enter a valid username: ");
					String tempUsername = in.readLine();
					
					// Check if username is already online
					boolean valid = true;
					for (Client c : clients)
						if (c.getUsername() != null && c.getUsername().equals(tempUsername))
						{
							valid = false;
							break;
						}
					
					// If the username is valid
					if (valid && tempUsername.length() > 0)
					{
						
						directMsg("Username approved. Welcome.");
						log(tempUsername + " has joined the server as client #" + clientNumber + ".");
						username = tempUsername;
						
						// Break out of error trap
						break;
						
					}
					else
						directMsg("Username already online. Please try again.");
				}
				
				// Send cached messages
				for (String msg : messageCache)
					directMsg(msg);
				directMsg("-- End of Message History --");
				
				// Notify group of join
				broadcastMsg(username + " has joined the server.");
				
				// Send the list of online users
				directMsg(getOnlineUsers());
				
				// Wait for messages from client
				while (true)
				{
					
					// Get the message that is sent to the server
					String msg = in.readLine();
					
					// Ignore empty message with no formatting, since reload speed can exceed KeyListener refresh speed
					if (msg.equals(""))
						continue;
					
					String command = extractMessageSegments(msg, 0)[3];
					
					if (!command.equals(""))
					{
						if (command.equals("/exit"))
						{
							// Close the connection for this client
							close(true);
							break;
						}
						else if (command.equals("/clear_cache"))
						{
							// Clear the server's cache of stored messages (requires password)
							// Messages on user's screens are preserved for now
							
							// Request, receieve, hash, and check password against stored hash
							if (promptAndValidatePassword())
							{
								messageCache.clear();
								serverMsg("Cache cleared.");
							}
							else
								serverMsg("Authentication failed.");
						}
						else if (command.equals("/pmsg"))
						{
							// Send a private message to another online user
							// Format: [time] <usr> /cmd <usr1> <msg>
							
							// Extract segments with one expected argument
							String[] segments = extractMessageSegments(msg, 1);
							
							// Check for errors from extraction
							
							if (segments.length == 1)
							{
								if (segments[0].equals("Too few arguments") || segments[0].equals("No command found"))
									directMsg("\nUsage: /pmsg <user> <message>");
								else
									directMsg("\nUnknown error extracting message segments.");
								
								continue;
							}
							
							// Get client by username
							Client c = getClient(segments[4]);
							
							if (c != null)
							{
								// Add "Private: " + timestamp + <sender@arg1> + message
								String pmsg = "Private: " + segments[0] + " <" + segments[1] + "@" + segments[4] + "> " + segments[2];
								
								// Send back to sender and receiver
								c.directMsg(pmsg);
								directMsg(pmsg);
								
							}
							else
								serverMsg("\"" + segments[4] + "\" is not online.");
							
						}
						else if (command.equals("/users"))
							// Return the list of users online
							directMsg(getOnlineUsers());
						else if (command.equals("/help"))
							// Prints all possible commands available
							directMsg(HELP_TEXT);
						else if (muted)
							serverMsg("You are currently muted.");
						else if (command.equals("/mute"))
						{
							
							// Mute the specified user
							
							// Extract segments with one expected argument
							String[] segments = extractMessageSegments(msg, 1);
							
							if (segments.length == 1)
							{
								if (segments[0].equals("Too few arguments") || segments[0].equals("No command found"))
									directMsg("\nUsage: /mute <user>");
								else
									directMsg("\nUnknown error extracting message segments.");
								
								continue;
							}
							
							// Get the targeted client
							Client c = getClient(segments[4]);
							
							if (c != null)
							{
								
								// Mute the user
								c.setMuted(true);
								
								// Notify the group
								broadcastMsg(getTimestamp() + " <" + getUsername() + "> muted <" + c.getUsername() + ">.");
								
							}
							else
								serverMsg("\"" + segments[4] + "\" is not online.");
							
						}
						else if (command.equals("/unmute"))
						{
							
							// Unmute the specified user
							
							// Extract segments with one expected argument
							String[] segments = extractMessageSegments(msg, 1);
							
							if (segments.length == 1)
							{
								if (segments[0].equals("Too few arguments") || segments[0].equals("No command found"))
									directMsg("\nUsage: /unmute <user>");
								else
									directMsg("\nUnknown error extracting message segments.");
								
								continue;
							}
							
							// Get the targeted client
							Client c = getClient(segments[4]);
							
							if (c != null)
							{
								
								// Unmute the user
								c.setMuted(false);
								
								// Notify the group
								broadcastMsg(getTimestamp() + " <" + getUsername() + "> unmuted <" + c.getUsername() + ">.");
								
							}
							else
								serverMsg("\"" + segments[4] + "\" is not online.");
							
						}
						else if (command.equals("/ban"))
						{
							
							// Ban the specified user from the server (requires password)
							
							// Extract segments with one expected argument
							String[] segments = extractMessageSegments(msg, 1);
							
							if (segments.length == 1)
							{
								if (segments[0].equals("Too few arguments") || segments[0].equals("No command found"))
									directMsg("\nUsage: /ban <user>");
								else
									directMsg("\nUnknown error extracting message segments.");
								
								continue;
							}
							
							// Get the targeted client
							Client c = getClient(segments[4]);
							
							if (promptAndValidatePassword() && c != null)
							{
								
								// Ban the user
								clients.remove(c);
								c.directMsg("banned"); // Trigger banned routine in client
								
								// Notify the group
								broadcastMsg(getTimestamp() + " <" + c.getUsername() + "> was banned from the server.");
								
							}
							else if (c == null)
								serverMsg("\"" + segments[4] + "\" is not online.");
							else
								serverMsg("Authentication failed.");
							
						}
						else
							// Command not found; Send help text
							directMsg("\tInvalid command: \"" + command + "\"\n" + HELP_TEXT);
					}
					else
					{
						// No special commands found. Broadcast the message (unless the user is muted)
						
						if (!muted)
						{
							broadcastMsg(msg);
							messageCache.add(msg);
						}
						else
							serverMsg("You are currently muted.");
						
						// Remove the oldest message if cache is full
						if (messageCache.size() > MAX_CACHE_SIZE)
							messageCache.remove(0);
					}
				}
			}
			catch (IOException e)
			{
				log("Error handling client #" + clientNumber + ": " + e);
				
				// If the client is still in the client list, notify the group that it logged off
				if (clients.contains(this))
					close(true);
				else
					close(false);
			}
		}
		
		/**
		 * Send a message to only this client.
		 * 
		 * @param msg
		 *            - Message to be sent
		 */
		public void directMsg(String msg)
		{
			out.println(msg);
		}
		
		/**
		 * Send a message with server formatting to only this client.
		 * 
		 * @param msg
		 *            - Message to be sent
		 */
		private void serverMsg(String msg)
		{
			directMsg(getTimestamp() + " <# server #> " + msg);
		}
		
		/**
		 * Prompt user for password, hash reply, and check against stored hash.
		 * 
		 * @return Passed (true) or failed (false) authentication
		 */
		private boolean promptAndValidatePassword()
		{
			
			// Prompt for user password
			serverMsg("Enter password: ");
			
			// Wait for user reply and extract password
			try
			{
				
				String[] segments = extractMessageSegments(in.readLine(), 0);
				String password = segments[2];
				return hashString(password).equals(hashedPassword);
				
			}
			catch (IOException e)
			{
				
				directMsg("Error handling password.");
				log("Error handling password for user: " + getUsername() + ".");
				return false;
				
			}
			
		}
		
		/**
		 * Close the connection to this client. Optionally notify all users that the client has left the server.
		 * 
		 * @param notify
		 *            - Option to notify all other clients
		 */
		private void close(boolean notify)
		{
			try
			{
				clients.remove(this);
				in.close();
				out.close();
				socket.close();
				log(username + " has left the server.");
				if (notify)
					broadcastMsg(username + " has left the server.");
			}
			catch (Exception e)
			{
				log("Error closing socket #" + clientNumber + ": " + e);
			}
		}
		
	}
	
	/**
	 * Log a message to the server's screen
	 */
	private void log(String msg)
	{
		System.out.println(getTimestamp() + " " + msg);
	}
	
	/**
	 * Clear the terminal or cmd screen
	 */
	private void clearOutput()
	{
		if (System.getProperty("os.name").toLowerCase().indexOf("win") != -1)
			// Clear command prompt
			try
			{
				new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
			}
			catch (Exception err)
			{
				err.printStackTrace();
				System.out.println("Error clearing screen. My bad.");
			}
		else
			// Clear terminal
			System.out.println("\033[H\033[2J");
	}
	
	/**
	 * Send a message to all clients in the client pool.
	 * 
	 * @param msg
	 *            - Message to be sent
	 */
	private void broadcastMsg(String msg)
	{
		for (Client client : clients)
			try
			{
				client.directMsg(msg);
			}
			catch (Exception e)
			{
				System.out.println("Error sending message \"" + msg + "\": " + e);
			}
	}
	
	/**
	 * Get a formatted string of online users.
	 * 
	 * @return List of online users
	 */
	private String getOnlineUsers()
	{
		String users = "\nOnline Users: [";
		for (Client c : clients)
			users += c.getUsername() + ", ";
		users = users.substring(0, users.length() - 2) + "]";
		return users;
	}
	
	/**
	 * Return the client with the specified username.
	 * 
	 * @param username
	 *            - username of desired client
	 * @return Client object with the specified name
	 */
	private Client getClient(String username)
	{
		for (Client c : clients)
			if (username.equals(c.getUsername()))
				return c;
			
		// Client was not found
		return null;
	}
	
	/**
	 * Hash a string using SHA-256.
	 * 
	 * @param str
	 *            - String to be hashed
	 * @return Hashed string
	 */
	private String hashString(String str)
	{
		try
		{
			MessageDigest msgDg = MessageDigest.getInstance("SHA-256");
			msgDg.update(str.getBytes());
			return String.format("%032x", new BigInteger(1, msgDg.digest()));
		}
		catch (Exception e)
		{
			System.out.println("Could not find proper hashing algorithim.");
			return null;
		}
	}
	
	/**
	 * Create a timestamp in [HH:mm] form.
	 * 
	 * @return String Timestamp in [HH:mm] form
	 */
	private String getTimestamp()
	{
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm");
		LocalDateTime now = LocalDateTime.now();
		return "[" + dtf.format(now) + "]";
	}
	
	/*
	 * Converts the segments of a client message into an array of Strings. The array
	 * is formatted as follows:
	 * [0] - timestamp
	 * [1] - sender
	 * [2] - message
	 * [3] - command (must have '/' as leading character) (optional)
	 * [4] - argument1 (optional)
	 * [5] - argument2 (optional)
	 * ...
	 * [3 + x] - argumentX
	 * 
	 * If too few arguments are provided, returns a String[] of length 1 with
	 * "Too few arguments." in the first position. If no command is found but
	 * numberOfArgs > 0, returns a String[] of length 1 with "No command found." in
	 * the first position.
	 * 
	 */
	private String[] extractMessageSegments(String msg, int numberOfArgs)
	{
		
		final int MIN_LEN = 3; // Minimum number of positions required for a message
		final int MSG_ARG_START = 3; // Position at which arguments start in msg
		final int SEG_ARG_START = MIN_LEN + 1; // Arguments start at MIN_LEN + 1 in fSegments because an additional
												// position is needed for command
		
		// Create array for final segments
		String[] fSegments = new String[SEG_ARG_START + numberOfArgs];
		
		// Split raw segments at each space
		String[] rSegments = msg.split(" ");
		
		// Check if there are less segments than needed
		// If there are too few, return the error
		// Handicap rSegments by 1 because fSegments always has a command field
		if (rSegments.length + 1 < fSegments.length && numberOfArgs > 0)
			return new String[] {"Too few arguments"};
		
		// Store the basic info
		fSegments[0] = rSegments[0]; // Timestamp
		fSegments[1] = rSegments[1].substring(1, rSegments[1].length() - 1); // Store sender without < and >
		fSegments[2] = ""; // Initialize the message segment so that it can be added to below
		fSegments[3] = rSegments.length > 2 && rSegments[2].indexOf("/") == 0 ? rSegments[2] : ""; // Store command if it begins with a /
		
		// Check if arguments were requested (implies a command), but no command was
		// found
		if (numberOfArgs > 0 && fSegments[3].length() == 0)
			return new String[] {"No command found"};
			
		// Collect arguments
		// Args fill [SEG_ARG_START] through [SEG_ARG_START + numberOfArgs - 1] in
		// fSegments
		// and [MSG_ARG_START] through [MSG_ARG_START + numberOfArgs - 1] in rSegments
		for (int i = 0; i < numberOfArgs; i++)
			fSegments[SEG_ARG_START + i] = rSegments[MSG_ARG_START + i];
			
		// Collect message segments and re-join with a space.
		// Message segments starts at MIN_LEN + fSegments[3].indexOf("/") +
		// numberOfArgs.
		// fSegments[3].indexOf("/") checks to see if a valid command was found.
		// If command is not found, indexOf will return -1.
		// This will shift msgStart back one position to include what would usually be a
		// command as part of the message.
		// If a command is found, indexOf will return 0, leaving the msgStart unaffected
		// and the command out of the message
		int msgStart = MIN_LEN + fSegments[3].indexOf("/") + numberOfArgs;
		for (int i = msgStart; i < rSegments.length; i++)
		{
			
			// Add a space for every word after the first
			if (i != msgStart)
				fSegments[2] += " ";
			
			// Add each word of the message
			fSegments[2] += rSegments[i];
			
		}
		
		return fSegments;
	}
	
	/**
	 * Get the local IP of the host device.
	 * 
	 * @return Local IP of host device
	 */
	public String getLocalIp()
	{
		try
		{
			InetAddress ipAddr = InetAddress.getLocalHost();
			return ipAddr.getHostAddress();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.exit(1);
			
			return null; // To make Eclipse shutup
		}
	}
	
	/**
	 * Get the external IP of the host device.
	 * 
	 * @return External IP of host device
	 */
	public String getExternalIp()
	{
		URL AWSCheck = null;
		try
		{
			AWSCheck = new URL("http://checkip.amazonaws.com");
		}
		catch (MalformedURLException e1)
		{
			e1.printStackTrace();
		}
		BufferedReader in = null;
		try
		{
			try
			{
				in = new BufferedReader(new InputStreamReader(AWSCheck.openStream()));
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			try
			{
				return in.readLine();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		finally
		{
			if (in != null)
				try
				{
					in.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
		}
		return null;
	}
	
	/**
	 * Parse command line arguments.
	 * 
	 * @param args
	 *            - Command line arguments
	 */
	public static BashTalkServer parseArgs(String[] args)
	{
		
		if (args.length > 0)
			if (args[0].equals("-t"))
				return new BashTalkServer(true);
			else
			{
				System.out.println("Valid options: -t");
				System.exit(0);
				return null;
			}
		else
			return new BashTalkServer(false);
		
	}
	
	/**
	 * Parse command line args and start the BashTalkServer.
	 * 
	 * @param args
	 *            - Command line arguments
	 */
	public static void main(String[] args)
	{
		BashTalkServer server = parseArgs(args);
		server.startServer();
	}
}
