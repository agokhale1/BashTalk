package bashtalkclient.ui;

import java.awt.*;

import bashtalkclient.core.*;

import java.awt.event.*;
import java.util.regex.*;

import javax.swing.*;
import javax.swing.border.*;

public class LoginUI extends JFrame {

    private static final long serialVersionUID = -2816500385275412556L;

    private static final int WIDTH = 26;
    private static final int HEIGHT = 36;
    private static final Pattern PATTERN = Pattern
            .compile("^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");

    private Window window;
    private JPanel contentPane, btnPane;

    private Font font;
    private JLabel usernameLbl, addressLbl, portLbl;
    private JTextField username, address, port;
    private JButton confirm, cancel;

    private BashTalkClient client;

    public LoginUI(BashTalkClient client) {

        // Calculates scaling unique to each resolution screen
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        int uiscale = (int) Math.round((gd.getDisplayMode().getHeight() / 100.0) + 0.5);
        int scale = (gd.getDisplayMode().getHeight() + gd.getDisplayMode().getWidth()) / 200;

        window = this;
        this.client = client;

        // Sets up intial text and accessibility features
        setTitle("Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResolution(uiscale, scale);
        setUIHints(scale);
        setResizable(false);
        setLocationRelativeTo(null);

        // Listens for escape key to quit the login
        KeyboardFocusManager kfm = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        kfm.addKeyEventDispatcher(new KeyEventDispatcher() {

            @Override
            public boolean dispatchKeyEvent(KeyEvent e) {

                if (e.getID() == KeyEvent.KEY_PRESSED && e.getKeyCode() == KeyEvent.VK_ESCAPE)
                    dispatchEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSING));
                return false;
            }
        });

        // Initializes the login components
        initComponents();
        setContentPane(contentPane);

        // Aligns all the JComponents
        contentPane.add(Box.createVerticalStrut(scale * 3));
        contentPane.add(usernameLbl);
        contentPane.add(username);
        contentPane.add(Box.createVerticalStrut(scale * 3));
        contentPane.add(addressLbl);
        contentPane.add(address);
        contentPane.add(Box.createVerticalStrut(scale * 3));
        contentPane.add(portLbl);
        contentPane.add(port);

        btnPane.add(confirm);
        btnPane.add(Box.createHorizontalStrut(scale));
        btnPane.add(cancel);
        btnPane.setAlignmentX(Component.CENTER_ALIGNMENT);

        contentPane.add(Box.createVerticalStrut(scale * 3));
        contentPane.add(btnPane);

    }

    /* Sets the resolution of the UI */
    private void setResolution(int uiscale, int scale) {
        setSize(WIDTH * uiscale, HEIGHT * uiscale);
        font = new Font("Consolas", Font.PLAIN, scale);
    }

    /* Set the UI to the correct font based on the scale */
    private void setUIHints(int scale) {
        UIManager.put("OptionPane.messageFont", new Font("Consolas", Font.PLAIN, scale * 2));
        UIManager.put("OptionPane.buttonFont", new Font("Consolas", Font.BOLD, scale * 2));
    }

    /* Initializes all JComponents to displayed to the client */
    private void initComponents() {
        // Creates the program icon and sets it to the JFrame
        ImageIcon img = new ImageIcon("src/bashTalk.png");
        this.setIconImage(img.getImage());

        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));

        // Creates a panel for all buttons
        btnPane = new JPanel();

        // Creates the labels for the username, ip and port
        usernameLbl = new JLabel("Enter a Username");
        usernameLbl.setFont(font);
        addressLbl = new JLabel("Enter an IP Address");
        addressLbl.setFont(font);
        portLbl = new JLabel("Enter a Port");
        portLbl.setFont(font);

        // Creates the text field for username input and validates the username
        username = new JTextField(26);
        username.setFont(font);
        username.addFocusListener(new FocusListener() {

            @Override
            public void focusLost(FocusEvent e) {

                if (!username.getText().equals("")) {

                    if (!validateUsername(username.getText())) {
                        JOptionPane.showMessageDialog(getParent(), "Input a valid username!", "Error",
                                JOptionPane.ERROR_MESSAGE);
                        username.setForeground(Color.RED);
                    } else {
                        username.setForeground(Color.GREEN);
                    }

                }

            }

            @Override
            public void focusGained(FocusEvent e) {
                username.setForeground(Color.BLACK);
            }

        });

        // Creates the text field for the ip address and validates ip address
        address = new JTextField(26);
        address.setFont(font);
        address.addFocusListener(new FocusListener() {

            @Override
            public void focusLost(FocusEvent e) {

                if (!address.getText().equals("")) {

                    if (!validateIP(address.getText())) {
                        JOptionPane.showMessageDialog(getParent(), "Input a valid IP address!", "Error",
                                JOptionPane.ERROR_MESSAGE);
                        address.setForeground(Color.RED);
                    } else
                        address.setForeground(Color.GREEN);
                }

            }

            @Override
            public void focusGained(FocusEvent e) {
                address.setForeground(Color.BLACK);
            }

        });

        // Creates a text field for the port number and validates the port number
        port = new JTextField(5);
        port.setFont(font);
        port.addKeyListener(new KeyListener() {
        	
        	@Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER && validateIP(address.getText()) && validateUsername(username.getText()) && !validatePort(port.getText())) 
                	createConnection();
            }

            @Override
            public void keyTyped(KeyEvent e) {}

            @Override
            public void keyReleased(KeyEvent e) {}
        });
        port.addFocusListener(new FocusListener() {

            @Override
            public void focusLost(FocusEvent e) {

                if (!port.getText().equals("")) {

                    if (validatePort(port.getText())) {
                        JOptionPane.showMessageDialog(getParent(), "Input a valid port!", "Error",
                                JOptionPane.ERROR_MESSAGE);
                        port.setForeground(Color.RED);
                    } else {
                        port.setForeground(Color.GREEN);
                    }
                }

            }

            @Override
            public void focusGained(FocusEvent e) {
                port.setForeground(Color.BLACK);
            }

        });

        // Creates the Confirm button and connects to client onClick
        confirm = new JButton("Confirm");
        confirm.setFont(font);
        confirm.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (validateIP(address.getText()) && validateUsername(username.getText()) && !validatePort(port.getText())) 
                    createConnection();
            }
        });

        // Creates the cancel button and closes the LoginUI onClick
        cancel = new JButton("Cancel");
        cancel.setFont(font);
        cancel.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                window.dispose();
            }
        });

        // Aligns the JComponents to their desired positions
        usernameLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        addressLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        portLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        username.setAlignmentX(Component.CENTER_ALIGNMENT);
        username.setMaximumSize(username.getPreferredSize());

        address.setAlignmentX(Component.CENTER_ALIGNMENT);
        address.setMaximumSize(address.getPreferredSize());

        port.setAlignmentX(Component.CENTER_ALIGNMENT);
        port.setMaximumSize(port.getPreferredSize());

        cancel.setPreferredSize(confirm.getPreferredSize());

    }
    
    /*Attempts the connection between the client and server else throws the */
    public void createConnection()
    {
    	try {
            client.setCredentials(address.getText(), port.getText(), username.getText());
            client.connectToServer();
            window.dispose();
        } catch (Exception err) {
        	String message = "";
        	if(err.getMessage().indexOf("refused") != -1)
        		message = "Connection refused: Please try again later!";
        	else if(err.getMessage().indexOf("reset") != -1)
        		message = "Connection reset: The server closed the connection!";
        	else
        		message = "Connection timed out: Please enter the correct IP and Port";
        	JOptionPane.showMessageDialog(window, message, "Connection Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /* Validates the IP Address to its correct format */
    private boolean validateIP(String ip) {
        return PATTERN.matcher(ip).matches();
    }

    /* Validates username excluding all special characters */
    private boolean validateUsername(String username) {
        return username.matches("^[a-zA-Z0-9]*$");
    }

    /* Validates the port number by accepting only numbers */
    private boolean validatePort(String port) {
        return !port.matches("[0-9]+");
    }
}