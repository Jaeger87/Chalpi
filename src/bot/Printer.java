package bot;

import java.io.File;
import bot.organizerbox.ItemList;

public class Printer {

	public final static String printPhotoScript = "script/printphoto.sh";
	public final static String printPhotoPanoramicScript = "script/printphoto_panoramic.sh";
	public final static String printText = "script/print_text.sh";
	public final static String printList = "script/print_list.sh";
	
	public static synchronized boolean printPhoto(File f) 
	{
		return print(new ProcessBuilder(printPhotoScript, Constants.IMAGESFOLDER + f.getName()));
	}

	public static synchronized boolean printPhotoPanoramic(File f) 
	{
		return print(new ProcessBuilder(printPhotoPanoramicScript, Constants.IMAGESFOLDER + f.getName()));
	}
	
	public static synchronized boolean printText(String s) 
	{
		return print(new ProcessBuilder(printText, s));
	}
	
	public static synchronized boolean printList(ItemList list) 
	{
		return print(new ProcessBuilder(printList, list.getName(), list.getListString()));
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
