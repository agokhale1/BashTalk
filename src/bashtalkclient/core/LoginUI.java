package bashtalkclient.core;

import java.awt.*;
import java.awt.event.*;
import java.util.regex.Pattern;

import javax.swing.*;
import javax.swing.border.*;

public class LoginUI extends JFrame {
	
	private static final long serialVersionUID = -2816500385275412556L;
	
	private static final int WIDTH = 26;
	private static final int HEIGHT = 36;
	private static final Pattern PATTERN = Pattern.compile("^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");
	
	private Window window;
	private JPanel contentPane;
	
	private Font font;
	private JLabel usernameLbl, addressLbl, portLbl;
	private JTextField username, address, port;
	
	public LoginUI()
	{
		
		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		int uiscale = (int) Math.round((gd.getDisplayMode().getHeight() / 100.0) + 0.5);
		int scale = (gd.getDisplayMode().getHeight() + gd.getDisplayMode().getWidth()) / 200;
		
		window = this;
		
		setTitle("Login");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResolution(uiscale, scale);
		setUIHints(scale);
		setResizable(false);
		setLocationRelativeTo(null);
		
		KeyboardFocusManager kfm = KeyboardFocusManager.getCurrentKeyboardFocusManager();
		kfm.addKeyEventDispatcher(new KeyEventDispatcher() {
			
			@Override
			public boolean dispatchKeyEvent(KeyEvent e)
			{
				
				dispatchEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSING));
				return false;
			}
			
		});
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
		setContentPane(contentPane);
		
		usernameLbl = new JLabel("Enter a Username");
		usernameLbl.setFont(font);
		addressLbl = new JLabel("Enter an IP Address");
		addressLbl.setFont(font);
		portLbl = new JLabel("Enter a Port");
		portLbl.setFont(font);
		
		username = new JTextField(26);
		username.setFont(font);
		address = new JTextField(26);
		address.setFont(font);
		address.addFocusListener(new FocusListener() {
			
			@Override
			public void focusLost(FocusEvent e)
			{
				
				if(!address.getText().equals(""))
				{
					
					if(!validateIP(address.getText()))
					{
						JOptionPane.showMessageDialog(getParent(), "Input a valid IP address!", "Error", JOptionPane.ERROR_MESSAGE);
						address.setForeground(Color.RED);
					}
					else
					{
						address.setForeground(Color.GREEN);
					}
					
				}
				
			}
			
			@Override
			public void focusGained(FocusEvent e)
			{
				address.setForeground(Color.BLACK);				
			}
			
		});
		port = new JTextField(5);
		port.setFont(font);
		
		usernameLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
		addressLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
		portLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
		username.setAlignmentX(Component.CENTER_ALIGNMENT);
		username.setMaximumSize(username.getPreferredSize());
		address.setAlignmentX(Component.CENTER_ALIGNMENT);
		address.setMaximumSize(address.getPreferredSize());
		port.setAlignmentX(Component.CENTER_ALIGNMENT);
		port.setMaximumSize(port.getPreferredSize());
		
		contentPane.add(Box.createVerticalStrut(scale * 3));
		contentPane.add(usernameLbl);
		contentPane.add(username);
		contentPane.add(Box.createVerticalStrut(scale * 3));
		contentPane.add(addressLbl);
		contentPane.add(address);
		contentPane.add(Box.createVerticalStrut(scale * 3));
		contentPane.add(portLbl);
		contentPane.add(port);
		
		JPanel p = new JPanel();
		JButton one = new JButton("Confirm"), two = new JButton("Cancel");
		one.setFont(font);
		two.setFont(font);
		two.setPreferredSize(one.getPreferredSize());
		p.add(one);
		p.add(Box.createHorizontalStrut(scale));
		p.add(two);
		p.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		contentPane.add(Box.createVerticalStrut(scale * 3));
		contentPane.add(p);
		
	}
	
	private void setResolution(int uiscale, int scale)
	{
		
		setSize(WIDTH * uiscale, HEIGHT * uiscale);
		font = new Font("Consolas", Font.PLAIN, scale);
		
	}
	
	private void setUIHints(int scale)
	{
		
		UIManager.put("OptionPane.messageFont", new Font("Consolas", Font.PLAIN, scale * 2));
		UIManager.put("OptionPane.buttonFont", new Font("Consolas", Font.BOLD, scale * 2));
		
	}
	
	private void initComponents(int scale)
	{
		
		
		
	}
	
	private boolean validateIP(String ip)
	{
		return PATTERN.matcher(ip).matches();
	}
	
	public static void main(String[] args)
	{
		
		EventQueue.invokeLater(new Runnable() {
			
			public void run()
			{
				LoginUI ui = new LoginUI();
				ui.setVisible(true);
			}
			
		});
		
	}
		
}



//OLD CODE

//package bashtalk.core;
//
//import java.awt.*;
//import java.awt.event.*;
//import java.util.regex.Pattern;
//
//import javax.swing.*;
//import javax.swing.border.*;
//
//public class LoginUI extends JFrame {
//	
//	private static final long serialVersionUID = -2816500385275412556L;
//	
//	private static final int WIDTH = 26;
//	private static final int HEIGHT = 36;
//	private static final Pattern PATTERN = Pattern.compile("^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");
//	
//	private JPanel contentPane;
//	
//	private Font font;
//	private JLabel usernameLbl, addressLbl, portLbl;
//	private JTextField username, address, port;
//	
//	public LoginUI()
//	{
//		
//		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
//		int scale = (gd.getDisplayMode().getWidth() + gd.getDisplayMode().getHeight()) / 200;
//		int uiscale = gd.getDisplayMode().getHeight() / 100;
//		System.out.println(uiscale + " | " + scale);
//		setTitle("Login");
//		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		setResolution(uiscale, scale);
//		setResizable(false);
//		setLocationRelativeTo(null);
//		
//		contentPane = new JPanel();
//		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
//		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
//		setContentPane(contentPane);
//		
//		usernameLbl = new JLabel("Enter a Username");
//		usernameLbl.setFont(font);
//		addressLbl = new JLabel("Enter an IP Address");
//		addressLbl.setFont(font);
//		portLbl = new JLabel("Enter a Port");
//		portLbl.setFont(font);
//		
//		username = new JTextField(26);
//		username.setFont(font);
//		address = new JTextField(26);
//		address.setFont(font);
//		address.addFocusListener(new FocusListener() {
//			
//			@Override
//			public void focusLost(FocusEvent e)
//			{
//				
//				if(!address.getText().equals(""))
//				{
//					
//					if(!validateIP(address.getText()))
//					{
//						JOptionPane.showMessageDialog(getParent(), "Input a valid IP address!", "Error", JOptionPane.ERROR_MESSAGE);
//						address.setForeground(Color.RED);
//					}
//					else
//					{
//						address.setForeground(Color.GREEN);
//					}
//					
//				}
//				
//			}
//			
//			@Override
//			public void focusGained(FocusEvent e)
//			{
//				address.setForeground(Color.BLACK);				
//			}
//			
//		});
//		port = new JTextField(5);
//		port.setFont(font);
//		
//		usernameLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
//		addressLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
//		portLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
//		username.setAlignmentX(Component.CENTER_ALIGNMENT);
//		username.setMaximumSize(username.getPreferredSize());
//		address.setAlignmentX(Component.CENTER_ALIGNMENT);
//		address.setMaximumSize(address.getPreferredSize());
//		port.setAlignmentX(Component.CENTER_ALIGNMENT);
//		port.setMaximumSize(port.getPreferredSize());
//		
//		contentPane.add(Box.createVerticalStrut(scale * 3));
//		contentPane.add(usernameLbl);
//		contentPane.add(username);
//		contentPane.add(Box.createVerticalStrut(scale * 3));
//		contentPane.add(addressLbl);
//		contentPane.add(address);
//		contentPane.add(Box.createVerticalStrut(scale * 3));
//		contentPane.add(portLbl);
//		contentPane.add(port);
//		
//		JPanel p = new JPanel();
//		JButton one = new JButton("Confirm"), two = new JButton("Cancel");
//		one.setFont(font);
//		two.setFont(font);
//		two.setPreferredSize(one.getPreferredSize());
//		p.add(one);
//		p.add(Box.createHorizontalStrut(scale));
//		p.add(two);
//		p.setAlignmentX(Component.CENTER_ALIGNMENT);
//		
//		contentPane.add(Box.createVerticalStrut(scale * 2));
//		contentPane.add(p);
//		
//	}
//	
//	private void setResolution(int uiscale, int scale)
//	{
//		
//		setSize(WIDTH * uiscale, HEIGHT * uiscale);
//		font = new Font("Consolas", Font.PLAIN, scale);
//		
//		UIManager.put("OptionPane.messageFont", new Font("Consolas", Font.PLAIN, scale * 2));
//		UIManager.put("OptionPane.buttonFont", new Font("Consolas", Font.BOLD, scale * 2));
//		
//	}
//	
//	private void initComponents(int scale)
//	{
//		
//		
//		
//	}
//	
//	private boolean validateIP(String ip)
//	{
//		return PATTERN.matcher(ip).matches();
//	}
//	
//	public static void main(String[] args)
//	{
//		
//		EventQueue.invokeLater(new Runnable() {
//			
//			public void run()
//			{
//				LoginUI ui = new LoginUI();
//				ui.setVisible(true);
//			}
//			
//		});
//		
//	}
//	
//}