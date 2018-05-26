package bot;

import java.io.File;
import bot.organizerbox.ItemList;

public class Printer {

	public static String printPhotoScript;
	public static String printPhotoPanoramicScript;
	public static String printText;
	public static String printList;
	public static String filePath;
	
	public static void setFilePath(String filePathn)
	{
		filePath = filePathn; 
		printPhotoScript = Main.filePath + "script/printphoto.sh";
		printPhotoPanoramicScript = Main.filePath + "script/printphoto_panoramic.sh";
		printText = Main.filePath + "script/print_text.sh";
		printList = Main.filePath + "script/print_list.sh";
	}
	
	
	public static synchronized boolean printPhoto(File f) 
	{
		return print(new ProcessBuilder("bash" , printPhotoScript, f.getAbsolutePath()));
	}

	public static synchronized boolean printPhotoPanoramic(File f) 
	{
		return print(new ProcessBuilder("bash" , printPhotoPanoramicScript, filePath + Constants.IMAGESFOLDER + f.getName()));
	}
	
	public static synchronized boolean printText(String s) 
	{
		return print(new ProcessBuilder("bash", printText, s + "\n\n\n"));
	}
	
	public static synchronized boolean printList(ItemList list) 
	{
		if (!printText(list.getName()))
			return false;
		return printText(list.getListString());
	}
	
	
	private static synchronized boolean print(ProcessBuilder pb)
	{
		
		try 
		{
			Process p = pb.start();
			p.waitFor();
		}

		catch (Exception e) 
		{
			return false;
		}
		return true;
	}
}
