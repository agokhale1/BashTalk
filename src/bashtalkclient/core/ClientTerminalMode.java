package bashtalkclient.core;

import java.util.*;

public class ClientTerminalMode extends BashTalkClient {
	
	private Scanner terminalIn;
	
	public ClientTerminalMode(String address, String port)
	{
		// Set the server address, port, and username
		this.host = address;
		this.port = Integer.parseInt(port);
		this.username = "";
		this.terminalIn = new Scanner(System.in);
		
		connectToServer();
	}
	
	protected void listenLocalInput()
	{
		
		// Listen for local message send
		while (true)
		{
			String msg = terminalIn.nextLine();
			
			if (msg.equals("/clear"))
			{
				clearOutput();
				continue;
			}
			
			sendMessage(msg);
			
			// Let thread die when exit is sent
			if (msg.equals("/exit"))
				break;
		}
		
		terminalIn.close();
		
	}
	
	protected String getInput(String prompt)
	{
		System.out.print(prompt);
		return terminalIn.nextLine();
	}
	
	public void appendMessage(String msg)
	{
		System.out.println(msg);
		
	}
	
	public void alertMessage(String alert)
	{
		System.out.println(alert);
		
	}
	
	public void clearOutput()
	{
		if (System.getProperty("os.name").toLowerCase().indexOf("win") != -1)
		{
			// Clear command prompt
			try
			{
				new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
			}
			catch (Exception err)
			{
				err.printStackTrace();
				System.out.println("Error clearing screen. My bad.");
			}
		}
		else
		{
			// Clear terminal
			System.out.println("\\033[H\\033[2J");
		}
	}
	
}
