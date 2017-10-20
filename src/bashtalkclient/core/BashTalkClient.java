package bashtalkclient.core;

import bashtalkclient.ui.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.JOptionPane;


public class BashTalkClient {

    private String host;
    private int port = 9898;
    private String username;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    public void connectToServer(String address, String port, String username) throws IOException {

        // Set the server address, port, and username
        this.host = address;
        this.port = Integer.parseInt(port);
        this.username = username;
        
        // Make connection and initialize streams
        socket = new Socket(this.host, this.port); // TODO: Catch socket fail!
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
        
        ChatUI chatWindow = new ChatUI(this.username);
        chatWindow.setVisible(true);
        
        System.out.println("I'm here");
        chatWindow.addMessage("Helllo");
        
        // Prompt for username until server validates
        while (true) {
            
            // "Please enter a valid username: "
            String response = in.readLine();
            
            System.out.println(response);
            
            chatWindow.addMessage(response);
            
            // Send username to server
            out.println(this.username);
            
            // Wait for valid username response
            response = in.readLine();
            
            System.out.println(response);
            
            // Possibly set timeout?
            
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
                    
                    System.out.println(response);
                    
                }
                
                // Break out of username error trap
                break;
                
            } else {
                
                // Show error from server
                JOptionPane.showMessageDialog(chatWindow, response);
                
                // Request new username
                // TODO: validate and use different UI
                this.username = JOptionPane.showInputDialog(chatWindow, "Enter a username: ");
                
                // User hit cancel
                if (this.username == null) {
                    System.exit(0);
                }               
                
            }
        }
        
        System.out.println("Cleared cache");

        // Listen for messages and append to display
        while (true) {
            
            // Wait for a message
            String incoming = in.readLine();
            
            System.out.println("Cleared incoming");
            System.out.println(incoming);
            System.out.println(incoming == null);
            
            // Server has closed socket
            /*if (incoming == null) {
                
                // Clear terminal and exit
                chatWindow.clear();
                System.exit(0);
                break;
                
            }*/
            
            // Append the message
            chatWindow.addMessage(incoming);
            break;
            
        }
    }

    public static void main(String[] args) throws Exception {

        BashTalkClient client = new BashTalkClient();
        LoginUI login = new LoginUI(client); // LoginUI calls client.connectToServer when ready
        login.setVisible(true);

    }
}
