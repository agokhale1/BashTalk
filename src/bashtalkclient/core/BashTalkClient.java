package bashtalkclient.core;

import bashtalkclient.ui.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.swing.JOptionPane;

public class BashTalkClient {

    private String host;
    private int port;
    private String username;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    public void setCredentials(String address, String port, String username) {
        // Set the server address, port, and username
        this.host = address;
        this.port = Integer.parseInt(port);
        this.username = username;
    }

    public void connectToServer() throws IOException {
        // Make connection and initialize streams
        socket = new Socket(this.host, this.port); // TODO: Catch socket fail!
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);

        ChatUI chatWindow = new ChatUI(this.username, this);
        chatWindow.setVisible(true);

        // Checks for username validity and prints cached message history
        while (true) {
            
            chatWindow.clear();
            
            // "Please enter a valid username: "
            String response = in.readLine();

            chatWindow.addMessage(response);

            // Send username to server
            out.println(this.username);

            // Wait for valid username response
            response = in.readLine();

            // Username has been accepted and server join is successful
            if (response.equals("Username approved. Welcome.")) {
                // Clear terminal
                chatWindow.clear();
                chatWindow.addMessage(response);
                chatWindow.addMessage("");

                // Receive all cached messages
                while (!response.equals("-- End of Message History --")) {
                    // Receive single message and append to terminal
                    response = in.readLine();
                    chatWindow.addMessage(response);
                }
                
                chatWindow.addMessage("");
                
                // Break out of username error trap
                break;

            } else {

                // Show error from server
                JOptionPane.showMessageDialog(chatWindow, response);

                // Request new username
                // TODO: validate and use different UI
                this.username = JOptionPane.showInputDialog(chatWindow, "Enter a username: ");

                // User hit cancel
                if (this.username == null)
                    System.exit(0);
            }
        }

        // Generates a new thread so that the client can simultaneously listen to
        // incoming messages
        new Thread() {
            public void run() {
                try {
                    listenMessage(chatWindow);
                } catch (Exception e) {
                    System.out.println("Error listening to messages!");
                }
            }
        }.start();
    }

    /* Sends the message to the server to be broadcasted to every client */
    public void sendMessage(String msg) {
    	DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm");
    	LocalDateTime now = LocalDateTime.now();

        out.println("[" + dtf.format(now) + "] " + msg);
    }

    /* Listens to all message changes in the server and prints to local client */
    public void listenMessage(ChatUI chatWindow) throws IOException {
        // Listen for messages and append to display
        while (true) {
            // Wait for a message
            String incoming = in.readLine();

            // Server has closed socket
            if (incoming == null) {
                // Clear terminal and exit
                chatWindow.clear();
                System.exit(0);
                break;
            }

            // Append the message to the terminal
            chatWindow.addMessage(incoming);
        }
    }

    /* Runs the bash talk client system */
    public static void main(String[] args) throws Exception {

        BashTalkClient client = new BashTalkClient();
        LoginUI login = new LoginUI(client); // LoginUI calls client.connectToServer when ready
        login.setVisible(true);

    }
}
