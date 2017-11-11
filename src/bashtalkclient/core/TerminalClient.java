package bashtalkclient.core;

import java.io.*;
import java.util.*;

/**
 * Terminal implementation of the BashTalk client.
 * 
 * @version 1.0.0
 */
public class TerminalClient extends BashTalkClient {
	
	private Scanner input;
	
	/**
	 * Constructor to initialize a client using a terminal to connect to the server.
	 * 
	 * @param host - IP address of server
	 * @param port - Port of sever
	 */
	public TerminalClient(String host, int port)
	{
		
		super(host, port, "");
		input = new Scanner(System.in);
		
		connect();
	}
	
	@Override
	protected void listenLocalInput()
	{
		
		while (running)
		{
			
			String msg = input.nextLine();
			
			if (msg.equals("/clear"))
			{
				
				clearOutput();
				continue;
				
			}
			
			sendMessage(msg);
			
			if (msg.equals("/exit"))
				disconnect();
			
		}
		input.close();
	}
	
	@Override
	protected String getLocalInput(String prompt)
	{
		System.out.print(prompt);
		
		return input.nextLine();
	}
	
	@Override
	public void displayMessage(String msg)
	{
		System.out.println(msg);
	}
	
	@Override
	public void alertMessage(String alert)
	{
		System.out.println(alert);
	}
	
	@Override
	public void clearOutput()
	{
		
		if (System.getProperty("os.name").toLowerCase().indexOf("win") != -1)
		{
			
			try
			{
				new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
			}
			catch (IOException | InterruptedException e)
			{
				e.printStackTrace();
			}
			
		}
		else
			System.out.println("\\033[H\\033[2J");
		
	}
	
}
