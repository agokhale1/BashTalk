package bashtalkclient.core;

import java.awt.*;

import javax.swing.*;

import bashtalkclient.ui.*;

/**
 * Graphical implementation of the BashTalk client.
 * 
 * @version 1.0.0
 */
public class GUIClient extends BashTalkClient {
	
	private LoginUI loginUI;
	private ChatUI chatUI;
	
	public GUIClient()
	{
		
		EventQueue.invokeLater(new Runnable() {
			
			@Override
			public void run()
			{
				
				loginUI = new LoginUI(this);
				loginUI.setVisible(true);
				
			}
			
		});
		
	}
	
	/**
	 * Sets the BashTalk client's IP address, port, and requested username
	 * from the input provided in the login GUI and then connects to the
	 * server.
	 * 
	 * @param host - IP address of server
	 * @param port - Port of server
	 * @param username - Requested username
	 */
	public void setCredentials(String host, int port, String username)
	{
		
		this.host = host;
		this.port = port;
		this.username = username;
		
		connect();
		
	}
	
	@Override
	protected void listenLocalInput()
	{
		return;
	}
	
	@Override
	protected String getLocalInput(String prompt)
	{
		return JOptionPane.showInputDialog(prompt);
	}
	
	@Override
	protected void displayMessage(String msg)
	{
		chatUI.addMessage(msg);
	}
	
	@Override
	protected void alertMessage(String alert)
	{
		JOptionPane.showMessageDialog(chatUI, alert);
	}
	
	@Override
	protected void clearOutput()
	{
		chatUI.clear();
	}
	
}