package bashtalkclient.core;

import java.awt.*;
import java.awt.event.*;
import java.util.regex.*;

import javax.swing.*;
import javax.swing.border.*;

public class ChatUI extends JFrame
{
	private static final int WIDTH = 89;
	private static final int HEIGHT = 49;
	
	
	private String username = "#hello: ";
	private Font font;
	private JPanel contentPane, inputPanel;
	private JEditorPane terminal, input;
	private JLabel tag;
	
	public ChatUI()
	{
		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		int uiscale = (int) Math.round((gd.getDisplayMode().getHeight() / 100.0) + 0.5);
		int scale = (gd.getDisplayMode().getHeight() + gd.getDisplayMode().getWidth()) / 200;
		
		setTitle("BashTalk");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResolution(uiscale, scale);
		setLocationRelativeTo(null);
		
		initComponents(uiscale, scale);
		setContentPane(contentPane);
		
		
		
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
		tag.setBackground(Color.black);
		tag.setForeground(Color.green);
		
		input = new JEditorPane();
		input.setFont(font);
		input.setBackground(Color.BLACK);
		input.setForeground(Color.WHITE);
		input.setCaretColor(Color.WHITE);
		//Insert custom caret code here

		inputPanel = new JPanel(new BorderLayout());
		inputPanel.setOpaque(true);
		inputPanel.setBackground(Color.black);
		inputPanel.add(tag, BorderLayout.BEFORE_LINE_BEGINS);
		inputPanel.add(new JScrollPane(input), BorderLayout.CENTER);
		
		JPanel tpan = new JPanel(new BorderLayout());
		
		terminal = new JEditorPane();
		terminal.setFont(font);
		terminal.setBackground(Color.BLUE);
		terminal.setForeground(Color.GREEN);
		
		tpan.add(new JScrollPane(terminal), BorderLayout.CENTER);
		
		
		contentPane.add(tpan, "Center");
		contentPane.add(inputPanel, "South");
		
	}
	
	public static void main(String args[]) throws Exception 
    {
        ChatUI client = new ChatUI();
        client.setVisible(true);
    }
	
}
