package bot;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Scanner;

import com.botticelli.messagereceiver.MessageReceiver;

public class Main {

	public static String filePath;
	
	public static void main(String[] args) throws FileNotFoundException, IllegalArgumentException, UnknownHostException, SocketException 
	{
		filePath = new File("").getAbsolutePath() + System.getProperty("file.separator");
		Printer.setFilePath(filePath);
		File token = new File(filePath + Constants.FILETOKEN);
		String tkn = "";
		try (Scanner s = new Scanner(token))
		{
			while (s.hasNext())
			{
				tkn = s.nextLine();
			}
		}
		
		ChalpiBot scb = new ChalpiBot(tkn);
		MessageReceiver mr = new MessageReceiver(scb, 850, 1);
		scb.setMessageReceiver(mr);
		mr.ignoreEditedMessages();
		mr.start();
		
	}
}
