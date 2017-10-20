package bashtalkclient.core;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.DefaultCaret;


public class BashTalkClient {

    private String host;
    private final int PORT = 9898;
    private String username;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private JFrame frame = new JFrame("Capitalize Client");
    private JTextField dataField = new JTextField(40);
    private JTextArea messageArea = new JTextArea(8, 60);

    public BashTalkClient() {

        // Layout GUI
        messageArea.setEditable(false);
        frame.getContentPane().add(dataField, "South");
        frame.getContentPane().add(new JScrollPane(messageArea), "Center");
        DefaultCaret caret = (DefaultCaret)messageArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        
        // Listen for enter in the text area (demo only until integrated with @patel760)
        dataField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String data = dataField.getText();
                if (data.indexOf("$$exit$$") != -1) {
                    try {
                        socket.close();
                    } catch (Exception err) {
                        messageArea.append("Could not close socket.\n");
                    }
                    System.exit(0);
                }
                
                out.println(username + ": " + data);
                dataField.setText("");
            }
        });
    }

    
    public void connectToServer() throws IOException {

        // Get the server address and from a dialog box.
        //TODO: Needs Regex from @patel760
        host = JOptionPane.showInputDialog(frame, "Enter IP Address of the Server:", "Welcome to BashTalk", JOptionPane.QUESTION_MESSAGE);
        if (host == null) {
            System.exit(0);
        }
        
        // Make connection and initialize streams
        socket = new Socket(host, PORT);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
        
        // Prompt for username until server validates
        while (true) {
            // "Please enter a valid username: "
            String response = in.readLine();
            messageArea.append(response + "\n");
            
            username = JOptionPane.showInputDialog(frame, "Enter username for " + host + "[" + PORT + "]:", "BashTalk Server " + host + "[" + PORT + "]", JOptionPane.QUESTION_MESSAGE);
            if (username == null) {
                System.exit(0);
            }
            out.println(username);
            
            response = in.readLine();
            
            // Username has been accepted and server join is successful
            if (response.equals("Username approved. Welcome.")) {
                messageArea.setText("");
                messageArea.append(response + "\n");
                
                // Receive cached messages
                while (!response.equals("-- End of Message History --")) {
                    response = in.readLine();
                    messageArea.append(response + "\n");
                }
                break;
            } else {
                JOptionPane.showMessageDialog(frame, response);
            }
        }

        // Listen for messages, filter special commands, and append to display
        while (true) {
            String incoming = in.readLine();
            if (incoming == null || incoming.indexOf("$$shutdown$$") != -1) {
                messageArea.setText("");
                System.exit(0);
                break;
            } else if (incoming.indexOf("$$clear$$")  != -1) {
                messageArea.setText("");
                continue;
            }
            messageArea.append(incoming + "\n");
        }
    }

    public static void main(String[] args) throws Exception {
        BashTalkClient client = new BashTalkClient();
        client.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        client.frame.pack();
        client.frame.setVisible(true);
        client.connectToServer();
    }
}
