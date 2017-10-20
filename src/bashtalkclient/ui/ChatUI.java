package bashtalkclient.ui;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.regex.*;

import bashtalkclient.core.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.JTextComponent;

public class ChatUI extends JFrame
{
	private static final int WIDTH = 89;
	private static final int HEIGHT = 49;
	
	private Window window;
	public String username = "#username: ";
	private Font font;
	private JPanel contentPane, inputPanel;
	private JTextArea terminal, input;
	private JLabel tag;
	
	private BashTalkClient bash;
	
	public ChatUI(String usr, BashTalkClient clnt)
	{
		username = "#"+usr+": ";
		bash = clnt;
		
		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		int uiscale = (int) Math.round((gd.getDisplayMode().getHeight() / 100.0) + 0.5);
		int scale = (gd.getDisplayMode().getHeight() + gd.getDisplayMode().getWidth()) / 200;
		
		window = this;
		
		setTitle("BashTalk");
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResolution(uiscale, scale);
		setLocationRelativeTo(null);
		
		initComponents(uiscale, scale);
		setContentPane(contentPane);
		
		try {
			run();
		}catch(Exception e) {
			System.out.println("IO Error");
		}
		
	}
	
	public void setResolution(int uiscale, int scale)
	{
		setSize(WIDTH*uiscale, HEIGHT*uiscale);
		font = new Font("Consolas", Font.PLAIN, scale);
	}
	
	private void initComponents(int uiscale, int scale)
	{
		contentPane = new JPanel();
		//contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
		
		tag = new JLabel(username);
		tag.setFont(font);
		tag.setOpaque(true);
		tag.setBackground(Color.BLACK);
		tag.setForeground(Color.GREEN);
		tag.setBorder(BorderFactory.createLineBorder(Color.WHITE));;
		
		input = new JTextArea(2, 50);
		input.setFont(font);
		input.setBackground(Color.BLACK);
		input.setForeground(Color.WHITE);
		input.setCaretColor(Color.WHITE);
		input.setLineWrap(true);
		input.setWrapStyleWord(true);
		//Insert custom caret code here

		inputPanel = new JPanel(new BorderLayout());
		inputPanel.setOpaque(true);
		inputPanel.setBackground(Color.black);
		inputPanel.add(tag, BorderLayout.BEFORE_LINE_BEGINS);
		inputPanel.add(new JScrollPane(input), BorderLayout.CENTER);
		
		JPanel tpan = new JPanel(new BorderLayout());
		
		terminal = new JTextArea(29,131);
		terminal.setFont(font);
		terminal.setBackground(Color.BLUE);
		terminal.setForeground(Color.GREEN);
		terminal.setEditable(false);
		
		tpan.add(new JScrollPane(terminal), BorderLayout.CENTER);
		
		
		contentPane.add(tpan, "Center");
		contentPane.add(inputPanel, "South");
		
	}
	
	public void addMessage(String msg)
	{
		terminal.setEditable(true);
		terminal.append(msg+"\n");
		terminal.setEditable(false);
	}
	
	public void clear()
	{
		terminal.setEditable(true);
	    terminal.setText("");
	    terminal.setEditable(false);
	}
	
	public void run() throws IOException
	{
		input.addKeyListener(new KeyListener() {
			
			@Override
			public void keyPressed(KeyEvent e)
			{
				if(e.isShiftDown() && e.getKeyCode() == KeyEvent.VK_ENTER)
					input.append("\n");
				else if (e.getKeyCode() == KeyEvent.VK_ENTER)
				{
					//terminal.setEditable(true);
					if(input.getText().equals("clear") || input.getText().equals("clr"))
            			clear();
					if(input.getText().equals("exit") || input.getText().equals("quit") || input.getText().equals("bye"))
						window.dispose();
					//addMessage(input.getText());
					//terminal.setEditable(false);
					bash.sendMessage(username+input.getText());
					input.setText(null);
				}
			}
			
			@Override
            public void keyTyped(KeyEvent e) { }

            @Override
            public void keyReleased(KeyEvent e) 
            { 
            	if(e.getKeyCode() == KeyEvent.VK_ENTER)
            		input.setText(null);
            }
		});
	}
	
	public static void main(String args[]) throws Exception 
    {
        //ChatUI chat = new ChatUI("username");
        //chat.setVisible(true);
    }
	
	
	
	/*Creates a custom caret that gives a terminal feel!*/
    public class MyCaret extends DefaultCaret {

        private String mark = "<";
        private java.awt.Image img = null;

        public MyCaret() {
            setBlinkRate(500);
            try {
            	ImageIcon icon = new ImageIcon("C:\\Users\\Neel\\Documents\\NetBeansProjects\\ChatGUI\\src\\cursor.png");
            	img = icon.getImage();
            }catch(Exception e) {}
        }

        @Override
        protected synchronized void damage(Rectangle r) {
            if (r == null) {
                return;
            }

            JTextComponent comp = getComponent();
            FontMetrics fm = comp.getFontMetrics(comp.getFont());
            int textWidth = 35;
            int textHeight = 57;
            x = r.x;
            y = r.y;
            width = textWidth;
            height = textHeight;
            repaint(); // calls getComponent().repaint(x, y, width, height)
        }

        @Override
        public void paint(Graphics g) {
            JTextComponent comp = getComponent();
            if (comp == null) {
                return;
            }

            int dot = getDot();
            Rectangle r = null;
            try {
                r = comp.modelToView(dot);
            } catch (BadLocationException e) {
                return;
            }
            if (r == null) {
                return;
            }

            if ((x != r.x) || (y != r.y)) {
                repaint(); // erase previous location of caret
                damage(r);
            }

            if (isVisible()) {
                FontMetrics fm = comp.getFontMetrics(comp.getFont());
                //g.setColor(comp.getCaretColor());
                //g.drawString(mark, x, y + fm.getAscent());
               // g.drawImage(img, x, y+5,tag.getFont().getSize()/2,tag.getFont().getSize(),Color.white, this);
            }
        }

    }
	
}