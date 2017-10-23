package bashtalkclient.ui;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.*;

import java.io.*;

import bashtalkclient.core.*;

public class ChatUI extends JFrame 
{
	
	// Initialize final variabless
	private static final int WIDTH = 89;
	private static final int HEIGHT = 49;

	// Instantiate major JPanels and Windows
	private Window window;
	private JPanel contentPane, inputPanel;
	
	// Instantiate JComponents and other variables
	private Font font;
	private JTextArea terminal, input;
	private JLabel tag;
	public String username;

	// Instance of a client
	private BashTalkClient client;

	public ChatUI(String usr, BashTalkClient client) 
	{
		// Format username input
		username = "<" + usr + "> ";
		
		// Calculates scaling unique to each resolution screen
		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		int uiscale = (int) Math.round((gd.getDisplayMode().getHeight() / 100.0) + 0.5);
		int scale = (gd.getDisplayMode().getHeight() + gd.getDisplayMode().getWidth()) / 200;
		
		// Initialize client and window
		this.client = client;
		window = this;
		
		// Sets up intial text and accessibility features
		setTitle("BashTalk");
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResolution(uiscale, scale);
		setLocationRelativeTo(null);

		// Initializes the login components
		initComponents();
		setContentPane(contentPane);

		// Runs the KeyListener to check if Enter is pressed
		try {
			keyPress();
		} catch (Exception e) {
			System.out.println("IO Error");
		}

	}

	/* Sets the resolution of the UI */
	public void setResolution(int uiscale, int scale) 
	{
		setSize(WIDTH * uiscale, HEIGHT * uiscale);
		font = new Font("Consolas", Font.PLAIN, scale);
	}

	/* Initializes all JComponents to displayed to the client */
	private void initComponents() 
	{
		// Creates the program icon and sets it to the JFrame
		ImageIcon img = new ImageIcon("src/bashTalk.png");
		this.setIconImage(img.getImage());
		
		// Creates a panel for the whole JFrame
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
		
		// Settings for the username tag
		tag = new JLabel(username);
		tag.setFont(font);
		tag.setOpaque(true);
		tag.setBackground(Color.BLACK);
		tag.setForeground(Color.GREEN);
		tag.setBorder(BorderFactory.createLineBorder(Color.WHITE));

		// Settings for the input text area
		input = new JTextArea(2, 50);
		input.setFont(font);
		input.setBackground(Color.BLACK);
		input.setForeground(Color.WHITE);
		input.setCaretColor(Color.WHITE);
		input.setLineWrap(true);
		input.setWrapStyleWord(true);
		input.setCaret(new MyCaret());

		// Settings for the inputPanel that holds the username tag and the input textArea
		inputPanel = new JPanel(new BorderLayout());
		inputPanel.setOpaque(true);
		inputPanel.setBackground(Color.black);
		inputPanel.add(tag, BorderLayout.BEFORE_LINE_BEGINS);
		inputPanel.add(new JScrollPane(input), BorderLayout.CENTER);

		// Settings for the terminal textArea
		terminal = new JTextArea(29, 131);
		terminal.setFont(font);
		terminal.setBackground(Color.BLUE);
		terminal.setForeground(Color.GREEN);
		terminal.setEditable(false);
		
		// Sets the scrollbar automatically to the bottom of the terminal textArea
		DefaultCaret c = (DefaultCaret) terminal.getCaret();
		c.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

		// Settings for the terminalPanel that holds the terminal textArea
		JPanel tpan = new JPanel(new BorderLayout());
		tpan.add(new JScrollPane(terminal), BorderLayout.CENTER);

		// ContentPane holds the terminal Panel and the inputPanel
		contentPane.add(tpan, "Center");
		contentPane.add(inputPanel, "South");

	}

	/* Appends the provided message to the terminal screen */
	public void addMessage(String msg)
	{
		terminal.setEditable(true);
		terminal.append(msg + "\n");
		terminal.setEditable(false);
	}

	/* Clears the terminal screen */
	public void clear()
	{
		terminal.setEditable(true);
		terminal.setText("");
		terminal.setEditable(false);
	}

	/*
	 * Checks if Enter is pressed and runs pre-set commands or broadcasts the
	 * message to other clients
	 */
	public void keyPress() throws IOException 
	{
		// Checks for keyPress in the input textArea
		input.addKeyListener(new KeyListener() {
			String pass = "";
			boolean flag = false;
			@Override
			public void keyPressed(KeyEvent e) {
				
				// Protects the input so that it is not stolen by wavering eyes
				passwordProtected(e);
				
				if (e.isShiftDown() && e.getKeyCode() == KeyEvent.VK_ENTER)
					input.append("\n");
				else if (e.getKeyCode() == KeyEvent.VK_ENTER) 
				{
					// Forwards the message to the server when enter is pressed
					if (input.getText().equals("/clear"))
						clear();
					else if(flag)
					{
						// Sends the stored input value rather than the face value which are stars
						client.sendMessage(username + pass);
						flag = false;
						pass="";
					}
					else
						client.sendMessage(username + input.getText());
					
					// Flags the process that the input needs to be password protected
					if(input.getText().equals("/clear_cache"))
						flag = true;
					input.setText(null);
				}
			}

			@Override
			public void keyTyped(KeyEvent e) {}

			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER && !e.isShiftDown())
					input.setText(null);
				
			}
			
			/*Protects the input textfield by converting the characters to stars and still keeping its typed value*/
			private void passwordProtected(KeyEvent e)
			{
				int lastChar = input.getText().length() - 1;
				if(flag && lastChar != -1)
				{
					if(e.getKeyCode() == KeyEvent.VK_BACK_SPACE)
						pass = pass.substring(0, lastChar-1);
					else
						pass += input.getText().charAt(lastChar);
					input.setText(input.getText().substring(0, lastChar) + "*");
				}
			}
		});
	}

	/* Creates a custom caret that gives a more terminal feel */
	public class MyCaret extends DefaultCaret {
		private java.awt.Image img = null;

		/* Initializes the new caret by setting its image and its blink rate */
		public MyCaret() {
			setBlinkRate(500);
			try {
				img = new ImageIcon("src/cursor.png").getImage();
			} catch (Exception e) {
				System.out.println("Cursor image not found!");
			}
		}

		/* Removes the current instance of the caret and repaints it to the terminal */
		@Override
		protected synchronized void damage(Rectangle r) {
			if (r == null)
				return;

			FontMetrics fm = getComponent().getFontMetrics(getComponent().getFont());
			x = r.x;
			y = r.y;
			width = tag.getFont().getSize() / 2;
			height = tag.getFont().getSize();
			repaint(); // calls getComponent().repaint(x, y, width, height)
		}

		/* Paints the caret image to the same location every blink interval */
		@Override
		public void paint(Graphics g) {
			JTextComponent comp = getComponent();
			if (comp == null)
				return;

			int dot = getDot();
			Rectangle r = null;
			try {
				r = comp.modelToView(dot);
			} catch (BadLocationException e) {
				return;
			}
			if (r == null)
				return;

			// If the location changes remove the current instance and repaint the caret
			// symbol to its new location
			if ((x != r.x) || (y != r.y)) {
				repaint(); // erase previous location of caret
				damage(r);
			}

			// Draws the caret on the frame
			if (isVisible())
				g.drawImage(img, x, y + 2, width, height, Color.white, window);
		}
	}
}