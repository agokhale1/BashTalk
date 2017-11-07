package bashtalkclient.core;

import javax.swing.*;

import bashtalkclient.ui.*;

public class ClientUIMode extends BashTalkClient {
	
	private ChatUI chatWindow;
	
	public ClientUIMode (String address, String port, String username)
	{
		// Set the server address, port, and username
		this.host = address;
		this.port = Integer.parseInt(port);
		this.username = username;
		
		chatWindow = new ChatUI(this.username, this);
		chatWindow.setVisible(true);
		connectToServer();
	}
	
	public void setChatWindow(ChatUI chatWindow)
	{
		this.chatWindow = chatWindow;
	}

	// ChatUI handles listening for input
	// Returning here does nothing and lets the generated thread die
	protected void listenLocalInput()
	{
		return;
	}

	protected String getInput(String prompt)
	{
		return JOptionPane.showInputDialog(prompt);
	}

	protected void appendMessage(String msg)
	{
		chatWindow.addMessage(msg);
		
	}

	protected void alertMessage(String alert)
	{
		JOptionPane.showMessageDialog(null, alert);
		
	}

	protected void clearOutput()
	{
		chatWindow.clear();
		
	}
}
