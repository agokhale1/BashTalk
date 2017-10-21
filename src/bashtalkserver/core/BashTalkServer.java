package bashtalkserver.core;

import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;

public class BashTalkServer {

    private static final int PORT = 9898;
    private static ArrayList<Client> clients = new ArrayList<Client>();
    private static ArrayList<String> messageCache = new ArrayList<String>();
    private static final int MAX_CLIENTS = 50;

    public static void main(String[] args) throws Exception {
        final String HOST = getIp();
        int clientNumber = 0;

        System.out.print("\033[H\033[2J"); // "clear" command
        System.out.println("-- BashTalk Server " + HOST + "[" + PORT + "] --");

        ServerSocket listener = new ServerSocket(PORT);
        try {
            while (true) {
                if (clients.size() < MAX_CLIENTS) {
                    clients.add(new Client(clientNumber++, listener.accept()));
                    clients.get(clients.size() - 1).start();
                } else {
                    Client temp = new Client(clientNumber + 1, listener.accept());
                    temp.directMsg("Maximum number of clients reached.");
                    temp.close(false);
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

        public void run() {
            try {
                // Initialize streams
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                // Handle username
                while (true) {
                    this.directMsg("Please enter a valid username: ");
                    String username = in.readLine();

                    if (username.length() > 0) {
                        this.directMsg("Username approved. Welcome.");
                        log(username + " has joined the server as client #" + this.clientNumber + ".");
                        this.username = username;
                        break;
                    }
                }

                // Send cached messages
                for (String msg : messageCache) {
                    this.directMsg(msg);
                }
                this.directMsg("-- End of Message History --");
                broadcastMsg(username + " has joined the server.");

                // Wait for messages from client
                while (true) {
                    String msg = in.readLine();

                    if (msg.equals("/exit")) {
                        clients.remove(this);
                        this.close(true);
                        break;
                    } else if (msg.equals("/clear_cache")) {
                        messageCache.clear();
                        continue;
                    }
                    
                    broadcastMsg(msg);
                    messageCache.add(msg);
                }
            } catch (IOException e) {
                log("Error handling client #" + this.clientNumber + ": " + e);
            } finally {
                this.close(true);
            }
        }

        public void directMsg(String msg) {
            this.out.println(msg);
        }

        public void close(boolean notify) {
            try {
                this.in.close();
                this.out.close();
                this.socket.close();
                if (notify) {
                    broadcastMsg(this.username + " has left the server.");
                }
            } catch (Exception e) {
                log("Error closing socket #" + this.clientNumber + ": " + e);
            }
        }

        private void log(String msg) {
            System.out.println(msg);
        }
    }

    private static void broadcastMsg(String msg) {
        for (Client client : clients) {
            try {
                client.directMsg(msg);
            } catch (Exception e) {
                System.out.println("Error sending message \"" + msg + "\": " + e);
            }
        }
    }

    public static String getIp() throws Exception {
        URL AWSCheck = new URL("http://checkip.amazonaws.com");
        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(AWSCheck.openStream()));
            return in.readLine();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
