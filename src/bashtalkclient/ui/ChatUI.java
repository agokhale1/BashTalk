package bashtalkclient.ui;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.*;

import bashtalkclient.core.*;

public class ChatUI extends JFrame {

	private static final long serialVersionUID = 3246415652860859900L;

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
		this.username = "<" + usr + "> ";

		// Calculates scaling unique to each resolution screen
		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		int uiscale = (int) Math.round(gd.getDisplayMode().getHeight() / 100.0 + 0.5);
		int scale = (gd.getDisplayMode().getHeight() + gd.getDisplayMode().getWidth()) / 200;

		// Initialize client and window
		this.client = client;
		this.window = this;

		// Sets up intial text and accessibility features
		setTitle("BashTalk");
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResolution(uiscale, scale);
		setLocationRelativeTo(null);

		// Initializes the login components
		initComponents();
		setContentPane(this.contentPane);

		addWindowFocusListener(new WindowAdapter() {

			@Override
			public void windowGainedFocus(WindowEvent e)
			{
				input.requestFocusInWindow();
			}

		});

		// Runs the KeyListener to check if Enter is pressed
		try
		{
			keyPress();
		}
		catch (Exception e)
		{
			System.out.println("IO Error");
		}

	}

	/* Sets the resolution of the UI */
	public void setResolution(int uiscale, int scale)
	{
		setSize(WIDTH * uiscale, HEIGHT * uiscale);
		this.font = new Font("Consolas", Font.PLAIN, scale);
	}

	/* Initializes all JComponents to displayed to the client */
	private void initComponents()
	{
		// Creates the program icon and sets it to the JFrame
		// ArrayList<Image> iconImgs = new ArrayList<Image>();

		// for(int i = 512; i >= 16; i /= 2)
		// 	iconImgs.add(new ImageIcon(getClass().getResource("/icon_" + i + "x" + i + ".png")).getImage());

		// setIconImages(iconImgs);

		// Creates a panel for the whole JFrame
		this.contentPane = new JPanel();
		this.contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		this.contentPane.setLayout(new BoxLayout(this.contentPane, BoxLayout.Y_AXIS));

		// Settings for the username tag
		this.tag = new JLabel(this.username);
		this.tag.setFont(this.font);
		this.tag.setOpaque(true);
		this.tag.setBackground(Color.BLACK);
		this.tag.setForeground(Color.GREEN);
		this.tag.setBorder(BorderFactory.createLineBorder(Color.WHITE));

		// Settings for the input text area
		this.input = new JTextArea(2, 50);
		this.input.setFont(this.font);
		this.input.setBackground(Color.BLACK);
		this.input.setForeground(Color.WHITE);
		this.input.setCaretColor(Color.WHITE);
		this.input.setLineWrap(true);
		this.input.setWrapStyleWord(true);
		this.input.setCaret(new MyCaret());

		// Settings for the inputPanel that holds the username tag and the input textArea
		this.inputPanel = new JPanel(new BorderLayout());
		this.inputPanel.setOpaque(true);
		this.inputPanel.setBackground(Color.black);
		this.inputPanel.add(this.tag, BorderLayout.BEFORE_LINE_BEGINS);
		this.inputPanel.add(new JScrollPane(this.input), BorderLayout.CENTER);

		// Settings for the terminal textArea
		this.terminal = new JTextArea(29, 131);
		this.terminal.setFont(this.font);
		this.terminal.setBackground(Color.BLUE);
		this.terminal.setForeground(Color.GREEN);
		this.terminal.setEditable(false);

		// Sets the scrollbar automatically to the bottom of the terminal textArea
		DefaultCaret c = (DefaultCaret) this.terminal.getCaret();
		c.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

		// Settings for the terminalPanel that holds the terminal textArea
		JPanel tpan = new JPanel(new BorderLayout());
		tpan.add(new JScrollPane(this.terminal), BorderLayout.CENTER);

		// ContentPane holds the terminal Panel and the inputPanel
		this.contentPane.add(tpan, "Center");
		this.contentPane.add(this.inputPanel, "South");

	}

	/* Appends the provided message to the terminal screen */
	public void addMessage(String msg)
	{
		this.terminal.setEditable(true);
		this.terminal.append(msg + "\n");
		this.terminal.setEditable(false);
	}

	/* Clears the terminal screen */
	public void clear()
	{
		this.terminal.setEditable(true);
		this.terminal.setText("");
		this.terminal.setEditable(false);
	}

	/*
	 * Checks if Enter is pressed and runs pre-set commands or broadcasts the
	 * message to other clients
	 */
	public void keyPress() throws IOException
	{
		// Checks for keyPress in the input textArea
		this.input.addKeyListener(new KeyListener() {

			String pass = "";
			boolean flag = false;

			@Override
			public void keyPressed(KeyEvent e)
			{

				// Protects the input so that it is not stolen by wavering eyes
				passwordProtected(e);

				if (e.isShiftDown() && e.getKeyCode() == KeyEvent.VK_ENTER)
					ChatUI.this.input.append("\n");
				else if (e.getKeyCode() == KeyEvent.VK_ENTER)
				{
					// Forwards the message to the server when enter is pressed
					if (ChatUI.this.input.getText().equals("/clear"))
						clear();
					else if (this.flag)
					{
						// Sends the stored input value rather than the face value which are stars
						ChatUI.this.client.sendMessage(this.pass);
						this.flag = false;
						this.pass = "";
					}
					else
						ChatUI.this.client.sendMessage(ChatUI.this.input.getText());

					// Flags the process that the input needs to be password protected
					if (ChatUI.this.input.getText().equals("/clear_cache") || ChatUI.this.input.getText().indexOf("/ban") != -1)
						this.flag = true;
					ChatUI.this.input.setText(null);
				}
			}

			@Override
			public void keyTyped(KeyEvent e)
			{}

			@Override
			public void keyReleased(KeyEvent e)
			{
				if (e.getKeyCode() == KeyEvent.VK_ENTER && !e.isShiftDown())
					ChatUI.this.input.setText(null);

			}

			/* Protects the input textfield by converting the characters to stars and still keeping its typed value */
			private void passwordProtected(KeyEvent e)
			{
				int lastChar = ChatUI.this.input.getText().length() - 1;
				if (this.flag && lastChar != -1)
				{
					if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE)
						this.pass = this.pass.substring(0, lastChar - 1);
					else
						this.pass += ChatUI.this.input.getText().charAt(lastChar);
					ChatUI.this.input.setText(ChatUI.this.input.getText().substring(0, lastChar) + "*");
				}
			}
		});
	}

	/* Creates a custom caret that gives a more terminal feel */
	public class MyCaret extends DefaultCaret {

		/**
		 *
		 */
		private static final long serialVersionUID = -9116419070786218647L;
		private java.awt.Image img = null;

		/* Initializes the new caret by setting its image and its blink rate */
		public MyCaret()
		{
			setBlinkRate(500);
			try
			{
				this.img = new ImageIcon(ChatUI.class.getResource("/cursor.png")).getImage();
			}
			catch (Exception e)
			{
				System.out.println("Cursor image not found!");
			}
		}

		/* Removes the current instance of the caret and repaints it to the terminal */
		@Override
		protected synchronized void damage(Rectangle r)
		{
			if (r == null)
				return;

			this.x = r.x;
			this.y = r.y;
			this.width = ChatUI.this.tag.getFont().getSize() / 2;
			this.height = ChatUI.this.tag.getFont().getSize();
			repaint(); // calls getComponent().repaint(x, y, width, height)
		}

		/* Paints the caret image to the same location every blink interval */
		@Override
		public void paint(Graphics g)
		{
			JTextComponent comp = getComponent();
			if (comp == null)
				return;

			int dot = getDot();
			Rectangle r = null;
			try
			{
				r = comp.modelToView(dot);
			}
			catch (BadLocationException e)
			{
				return;
			}
			if (r == null)
				return;

			// If the location changes remove the current instance and repaint the caret
			// symbol to its new location
			if (this.x != r.x || this.y != r.y)
			{
				repaint(); // erase previous location of caret
				damage(r);
			}

			// Draws the caret on the frame
			if (isVisible())
				g.drawImage(this.img, this.x, this.y + 2, this.width, this.height, Color.white, ChatUI.this.window);
		}
	}
}