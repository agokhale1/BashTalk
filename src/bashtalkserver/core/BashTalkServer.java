package bashtalkserver.core;

import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BashTalkServer {

    private static final int PORT = 9898;
    private static ArrayList<Client> clients = new ArrayList<Client>();
    private static ArrayList<String> messageCache = new ArrayList<String>();
    private static final int MAX_CLIENTS = 50;

    public static void main(String[] args) throws Exception {
        final String HOST = getIp();
        int clientNumber = 0;

        // Clear Linux terminal screen
        System.out.print("\033[H\033[2J");
        System.out.println("-- BashTalk Server " + HOST + "[" + PORT + "] --");

        ServerSocket listener = new ServerSocket(PORT);
        try {
            while (true) {
                if (clients.size() < MAX_CLIENTS) {
                    clients.add(new Client(clientNumber++, listener.accept()));
                    clients.get(clients.size() - 1).start();
                } else {
                    Socket socket = listener.accept();
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                    out.println("Maximum number of clients reached.");
                    // Client closes self
                }
            }
        } finally {
            broadcastMsg("shutdown");
            listener.close();
        }
    }

    public static class Client extends Thread {
        public int clientNumber;
        public String username;
        private BufferedReader in;
        private PrintWriter out;
        private Socket socket;

        public Client(int clientNumber, Socket socket) {
            this.clientNumber = clientNumber;
            this.socket = socket;
        }
        
        public String getUsername() {
            return this.username;
        }

        public void run() 
        {
            try {
                // Initialize streams
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                // Handle username
                while (true) 
                {
                    this.directMsg("Please enter a valid username: ");
                    String tempUsername = in.readLine();
                    
                    // Check if username is already online
                    boolean valid = true;
                    for (Client c : clients) {
                        if (c.getUsername() != null && c.getUsername().equals(tempUsername))
                        {
                            valid = false;
                            break;
                        }
                    }

                    // If the username is valid
                    if (valid && tempUsername.length() > 0) {
                        
                        this.directMsg("Username approved. Welcome.");
                        log(tempUsername + " has joined the server as client #" + this.clientNumber + ".");
                        this.username = tempUsername;
                        
                        // Break out of error trap
                        break;
                        
                    } else {
                        this.directMsg("Username already online. Please try again.");
                    }
                }

                // Send cached messages
                for (String msg : messageCache) 
                {
                    this.directMsg(msg);
                }
                this.directMsg("-- End of Message History --");
                broadcastMsg(username + " has joined the server.");

                // Wait for messages from client
                while (true) 
                {
                	// Gets the message that is sent to the server
                    String msg = in.readLine();

                    if (msg.indexOf("/exit") != -1) {
                        
                        // Close the connection for this client
                        
                        clients.remove(this);
                        this.close(true);
                        break;
                        
                        
                    } else if (msg.indexOf("/clear_cache") != -1) {
                        
                        // Clear the server's cache of stored messages (requires password)
                        // Messages on user's screens are preserved for now
                        
                        // Prompt for user password
                        this.serverMsg("Enter password: ");
                        
                        // Wait for user reply and extract password
                        String[] segments = extractMessageSegments(in.readLine(), 0);
                        String password = segments[2];
                        
                        // Hash password and check against stored hash
                        if (hashString(password).equals("5f7d7fda54ac318dae8cd49ba5e6241b24d826daa71fd5607945457f34c21f4a")) {
                            
                            messageCache.clear();
                            this.serverMsg("Cache cleared.");

                            
                        } else {
                            this.serverMsg("Authentication failed.");

                        }
                        

                    } else if(msg.indexOf("/pmsg") != -1) {
                        
                        // Send a private message to another online user
                    	// Format: [time] <usr> /cmd <usr1> <msg>
                        
                        // Extract segments with one expected argument
                        String[] segments = extractMessageSegments(msg, 1);
                        
                        // Check for errors from extraction
                    	if(segments.length == 1)
                    	{
                    	    if (segments[0].equals("Too few arguments") || segments[0].equals("No command found"))
                    	        this.directMsg("\nUsage: /pmsg <user> <message>\n");
                    	    else
                    	        this.directMsg("\nUnknown error extracting message segments.\n");

                    	}
                    	
                    	boolean clientFound = false;
                    	for(Client c : clients)
                    	{
                    		if(c.getUsername().equals(segments[4]))
                    		{
                    		    // Add "Private: " + timestamp + <sender@arg1> + message
                    			String pmsg = "Private: " + segments[0] + " <" + segments[1] + "@" + segments[4] + "> " + segments[2];

                    			// Send back to sender and receiver
                        		c.directMsg("Private: " + msg);
                        		this.directMsg("Private: " + msg);

                        		clientFound = true;
                        		break;
                    		}
                    	}
                    	
                    	// Notify if recipient is not online
                    	if (!clientFound) {
                    	    this.serverMsg("\"" + segments[4] + "\" is not online.");
                    	}


                    } else {
                        
                        // No special commands were found. Broadcast the message
                    
                        broadcastMsg(msg);
                        messageCache.add(msg);
                    
                    }
                }
            } catch (IOException e) {
                log("Error handling client #" + this.clientNumber + ": " + e);
            }
        }

        /* Send a message to only this client. */
        public void directMsg(String msg) 
        {
            this.out.println(msg);
        }
        
        /* Send a message with server formatting to only this client. */
        private void serverMsg(String msg) 
        {
            this.directMsg(getTimestamp() + " <# server #> " + msg);
        }

        /* 
         * Close the connection to this client.
         * Optionally notify all users that the client has left the server.
         */
        public void close(boolean notify) 
        {
            try {
                this.in.close();
                this.out.close();
                this.socket.close();
                log(this.username + " has left the server.");
                if (notify)
                    broadcastMsg(this.username + " has left the server.");
            } catch (Exception e) {
                log("Error closing socket #" + this.clientNumber + ": " + e);
            }
        }

        private void log(String msg) 
        {
            System.out.println(msg);
        }
    }
    
    /* 

    /* Send a message to all clients in the client pool. */
    private static void broadcastMsg(String msg) 
    {
        for (Client client : clients) 
        {
            try {
                client.directMsg(msg);
            } catch (Exception e) {
                System.out.println("Error sending message \"" + msg + "\": " + e);
            }
        }
    }
    
    /* Hash a string using SHA-256. */
    public static String hashString(String str) 
    {
        try {
            MessageDigest msgDg = MessageDigest.getInstance("SHA-256");
            msgDg.update(str.getBytes());
            return String.format("%032x", new BigInteger(1, msgDg.digest()));
        } catch(Exception e) {
            System.out.println("Could not find proper hashing algorithim.");
            return null;
        }
    }
    
    /* Return a timestamp of form [HH:mm]. */
    public static String getTimestamp() 
    {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm");
        LocalDateTime now = LocalDateTime.now();
        return "[" + dtf.format(now) + "]";
    }
    
    /*
     * Converts the segments of a client message into an array of Strings.
     * The array is formatted as follows:
     * [0] - timestamp
     * [1] - sender
     * [2] - message
     * [3] - command (must have '/' as leading character) (optional)
     * [4] - argument1 (optional)
     * [5] - argument2 (optional)
     * ...
     * [3 + x] - argumentX 
     * 
     * If too few arguments are provided, returns a String[] of length 1 with "Too few arguments." in the first position.
     * If no command is found but numberOfArgs > 0, returns a String[] of length 1 with "No command found." in the first position.
     * 
     */
    public static String[] extractMessageSegments(String msg, int numberOfArgs) {
        
        final int MIN_LEN = 3; // Minimum number of positions required for a message
        final int MSG_ARG_START = 3; // Position at which arguments start in msg
        final int SEG_ARG_START = MIN_LEN + 1; // Arguments start at MIN_LEN + 1 in fSegments because an additional position is needed for command
        
        // Create array for final segments
        String[] fSegments = new String[SEG_ARG_START + numberOfArgs];
        
        // Format: [time] <usr> /cmd <usr1> <msg>
        
        // Split raw segments at each space
        String[] rSegments = msg.split(" ");
        
        // Check if there are less segments than needed
        // If there are too few, return the error
        // Handicap rSegments by 1 because fSegments always has a command field
        if(rSegments.length + 1 < fSegments.length)
        {
            return new String[] {"Too few arguments"};
        }
        
        // Store the basic info
        fSegments[0] = rSegments[0]; // Timestamp
        fSegments[1] = rSegments[1].substring(1,  rSegments[1].length() - 1); // Store sender without < and >
        fSegments[2] = ""; // Initialize the message segment so that it can be added to below
        fSegments[3] = (rSegments[2].indexOf("/") == 0) ? rSegments[2] : ""; // Store command if it begins with a /
        
        // Check if arguments were requested (implies a command), but no command was found
        if (numberOfArgs > 0 && fSegments[3].length() == 0) {
            return new String[] {"No command found"};
        }
        
        // Collect arguments
        // Args fill [SEG_ARG_START] through [SEG_ARG_START + numberOfArgs - 1] in fSegments 
        // and [MSG_ARG_START] through [MSG_ARG_START + numberOfArgs - 1] in rSegments
        for (int i = 0; i < numberOfArgs; i++) {
            fSegments[SEG_ARG_START + i] = rSegments[MSG_ARG_START + i];
        }
        
        // Collect message segments and re-join with a space.
        // Message segments starts at MIN_LEN + fSegments[3].indexOf("/") + numberOfArgs.
        // fSegments[3].indexOf("/") checks to see if a valid command was found.
        // If command is not found, indexOf will return -1.
        // This will shift msgStart back one position to include what would usually be a command as part of the message.
        // If a command is found, indexOf will return 0, leaving the msgStart unaffected and the command out of the message
        int msgStart = MIN_LEN + fSegments[3].indexOf("/") + numberOfArgs;
        for (int i = msgStart; i < rSegments.length; i++) {

            // Add a space for every word after the first
            if (i != msgStart) {
                fSegments[2] += " ";
            }
            
            // Add each word of the message
            fSegments[2] += rSegments[i];
            
        }
        
        return fSegments;
    }
    
    /* Get the external IP of the host device. */
    public static String getIp() throws Exception 
    {
        URL AWSCheck = new URL("http://checkip.amazonaws.com");
        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(AWSCheck.openStream()));
            return in.readLine();
        } finally {
            if (in != null) 
            {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
}
