package bashtalkclient.ui;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.regex.*;

import javax.swing.*;
import javax.swing.border.*;

import bashtalkclient.core.*;

public class LoginUI extends JFrame {
	
	private static final long serialVersionUID = -2816500385275412556L;
	
	// Initialize final variables
	private static final int WIDTH = 26;
	private static final int HEIGHT = 36;
	private static final Pattern PATTERN = Pattern.compile("^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");
	
	// Instantiate major JPanels and Windows
	private Window window;
	private JPanel contentPane, btnPane;
	
	// Instantiate JComponents
	private Font font;
	private JLabel usernameLbl, addressLbl, portLbl;
	private JTextField username, address, port;
	private JButton confirm, cancel;
	
	// Instance of the client
	private BashTalkClient client;
	
	public LoginUI(BashTalkClient client)
	{
		// Calculates scaling unique to each screen resolution
		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		int uiscale = (int) Math.round(gd.getDisplayMode().getHeight() / 100.0 + 0.5);
		int scale = (gd.getDisplayMode().getHeight() + gd.getDisplayMode().getWidth()) / 200;
		
		// Initialize client and window
		this.window = this;
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
			public boolean dispatchKeyEvent(KeyEvent e)
			{
				
				if (e.getID() == KeyEvent.KEY_PRESSED && e.getKeyCode() == KeyEvent.VK_ESCAPE)
					dispatchEvent(new WindowEvent(LoginUI.this.window, WindowEvent.WINDOW_CLOSING));
				return false;
			}
		});
		
		// Initializes the login components
		initComponents();
		setContentPane(this.contentPane);
		
		// Aligns all the JComponents and adds it to their respective JPanels
		this.contentPane.add(Box.createVerticalStrut(scale * 3));
		this.contentPane.add(this.usernameLbl);
		this.contentPane.add(this.username);
		this.contentPane.add(Box.createVerticalStrut(scale * 3));
		this.contentPane.add(this.addressLbl);
		this.contentPane.add(this.address);
		this.contentPane.add(Box.createVerticalStrut(scale * 3));
		this.contentPane.add(this.portLbl);
		this.contentPane.add(this.port);
		
		this.btnPane.add(this.confirm);
		this.btnPane.add(Box.createHorizontalStrut(scale));
		this.btnPane.add(this.cancel);
		this.btnPane.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		this.contentPane.add(Box.createVerticalStrut(scale * 3));
		this.contentPane.add(this.btnPane);
		
	}
	
	/* Sets the resolution of the UI */
	private void setResolution(int uiscale, int scale)
	{
		setSize(WIDTH * uiscale, HEIGHT * uiscale);
		this.font = new Font("Consolas", Font.PLAIN, scale);
	}
	
	/* Set the UI to the correct font based on the scale */
	private void setUIHints(int scale)
	{
		UIManager.put("OptionPane.messageFont", new Font("Consolas", Font.PLAIN, scale * 2));
		UIManager.put("OptionPane.buttonFont", new Font("Consolas", Font.BOLD, scale * 2));
	}
	
	/* Initializes all JComponents to displayed to the client */
	private void initComponents()
	{
		// Creates the program icon and sets it to the JFrame
		ArrayList<Image> iconImgs = new ArrayList<Image>();
		
		for(int i = 512; i >= 16; i /= 2)
			iconImgs.add(new ImageIcon(getClass().getResource("/icon_" + i + "x" + i + ".png")).getImage());
		
		setIconImages(iconImgs);
		
		// Creates a panel for the whole JFrame
		this.contentPane = new JPanel();
		this.contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		this.contentPane.setLayout(new BoxLayout(this.contentPane, BoxLayout.Y_AXIS));
		
		// Creates a panel for all buttons
		this.btnPane = new JPanel();
		
		// Creates the labels for the username, ip and port
		this.usernameLbl = new JLabel("Enter a Username");
		this.usernameLbl.setFont(this.font);
		this.addressLbl = new JLabel("Enter an IP Address");
		this.addressLbl.setFont(this.font);
		this.portLbl = new JLabel("Enter a Port");
		this.portLbl.setFont(this.font);
		
		// Creates the text field for username input and validates the username
		this.username = new JTextField(26);
		this.username.setFont(this.font);
		this.username.addFocusListener(new FocusListener() {
			
			@Override
			public void focusLost(FocusEvent e)
			{
				
				if (!LoginUI.this.username.getText().equals(""))
					if (!validateUsername(LoginUI.this.username.getText()))
					{
						JOptionPane.showMessageDialog(getParent(), "Input a valid username!", "Error", JOptionPane.ERROR_MESSAGE);
						LoginUI.this.username.setForeground(Color.RED);
					}
					else
						LoginUI.this.username.setForeground(Color.GREEN);
					
			}
			
			@Override
			public void focusGained(FocusEvent e)
			{
				LoginUI.this.username.setForeground(Color.BLACK);
			}
			
		});
		
		// Creates the text field for the ip address and validates ip address
		this.address = new JTextField(26);
		this.address.setFont(this.font);
		this.address.addFocusListener(new FocusListener() {
			
			@Override
			public void focusLost(FocusEvent e)
			{
				
				if (!LoginUI.this.address.getText().equals(""))
					if (!validateIP(LoginUI.this.address.getText()))
					{
						JOptionPane.showMessageDialog(getParent(), "Input a valid IP address!", "Error", JOptionPane.ERROR_MESSAGE);
						LoginUI.this.address.setForeground(Color.RED);
					}
					else
						LoginUI.this.address.setForeground(Color.GREEN);
					
			}
			
			@Override
			public void focusGained(FocusEvent e)
			{
				LoginUI.this.address.setForeground(Color.BLACK);
			}
			
		});
		
		// Creates a text field for the port number, validates the port number and connects to client on ENTER keypress
		this.port = new JTextField(5);
		this.port.setFont(this.font);
		this.port.addKeyListener(new KeyListener() {
			
			@Override
			public void keyPressed(KeyEvent e)
			{
				if (e.getKeyCode() == KeyEvent.VK_ENTER && validateIP(LoginUI.this.address.getText()) && validateUsername(LoginUI.this.username.getText()) && !validatePort(LoginUI.this.port.getText()))
					createConnection();
			}
			
			@Override
			public void keyTyped(KeyEvent e)
			{}
			
			@Override
			public void keyReleased(KeyEvent e)
			{}
		});
		this.port.addFocusListener(new FocusListener() {
			
			@Override
			public void focusLost(FocusEvent e)
			{
				
				if (!LoginUI.this.port.getText().equals(""))
					if (validatePort(LoginUI.this.port.getText()))
					{
						JOptionPane.showMessageDialog(getParent(), "Input a valid port!", "Error", JOptionPane.ERROR_MESSAGE);
						LoginUI.this.port.setForeground(Color.RED);
					}
					else
						LoginUI.this.port.setForeground(Color.GREEN);
					
			}
			
			@Override
			public void focusGained(FocusEvent e)
			{
				LoginUI.this.port.setForeground(Color.BLACK);
			}
			
		});
		
		// Creates the Confirm button and connects to client onClick
		this.confirm = new JButton("Confirm");
		this.confirm.setFont(this.font);
		this.confirm.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e)
			{
				if (validateIP(LoginUI.this.address.getText()) && validateUsername(LoginUI.this.username.getText()) && !validatePort(LoginUI.this.port.getText()))
					createConnection();
			}
		});
		
		// Creates the cancel button and closes the LoginUI onClick
		this.cancel = new JButton("Cancel");
		this.cancel.setFont(this.font);
		this.cancel.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e)
			{
				LoginUI.this.window.dispose();
			}
		});
		
		// Aligns the JComponents to their desired positions
		this.usernameLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
		this.addressLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
		this.portLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		this.username.setAlignmentX(Component.CENTER_ALIGNMENT);
		this.username.setMaximumSize(this.username.getPreferredSize());
		
		this.address.setAlignmentX(Component.CENTER_ALIGNMENT);
		this.address.setMaximumSize(this.address.getPreferredSize());
		
		this.port.setAlignmentX(Component.CENTER_ALIGNMENT);
		this.port.setMaximumSize(this.port.getPreferredSize());
		
		this.cancel.setPreferredSize(this.confirm.getPreferredSize());
		
	}
	
	/* Attempts the connection between the client and server else throws its respective exception */
	public void createConnection()
	{
		try
		{
			this.client.setCredentials(this.address.getText(), this.port.getText(), this.username.getText());
			this.client.connectToServer();
			this.window.dispose();
		}
		catch (Exception err)
		{
			String message = "";
			if (err.getMessage().indexOf("refused") != -1)
				message = "Connection refused: Please try again later!";
			else if (err.getMessage().indexOf("reset") != -1)
				message = "Connection reset: The server closed the connection!";
			else
				message = "Connection timed out: Please enter the correct IP and Port";
			JOptionPane.showMessageDialog(this.window, message, "Connection Error", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	/* Validates the IP Address to its correct format */
	private boolean validateIP(String ip)
	{
		return PATTERN.matcher(ip).matches();
	}
	
	/* Validates username excluding all special characters */
	private boolean validateUsername(String username)
	{
		return username.matches("^[a-zA-Z0-9]*$");
	}
	
	/* Validates the port number by accepting only numbers */
	private boolean validatePort(String port)
	{
		return !port.matches("[0-9]+");
	}
}