package bot;

import java.util.ArrayList;
import java.util.List;

import com.botticelli.bot.request.methods.types.InlineKeyboardButton;
import com.botticelli.bot.request.methods.types.InlineKeyboardMarkup;
import com.botticelli.bot.request.methods.types.KeyboardButton;
import com.botticelli.bot.request.methods.types.ReplyKeyboardMarkupWithButtons;

public class MenuContainer {

	private ReplyKeyboardMarkupWithButtons mainMenu;
	private InlineKeyboardMarkup lightsInlineMenu; 
	
	
	public MenuContainer()
	{
		//creating the keyboard for the menu
		List<List<KeyboardButton>> keyboard = new ArrayList<List<KeyboardButton>>();
		List<KeyboardButton> line = new ArrayList<>();
		line.add(new KeyboardButton(Constants.AGENDA));
		line.add(new KeyboardButton(Constants.MYLISTS));
		keyboard.add(line);
		line = new ArrayList<>();
		line.add(new KeyboardButton(Constants.MANAGELIGHTS));
		keyboard.add(line);
		mainMenu = new ReplyKeyboardMarkupWithButtons(keyboard);
		mainMenu.setResizeKeyboard(true);
		
		
		List<List<InlineKeyboardButton>> inlKeyboard = new ArrayList<List<InlineKeyboardButton>>();
		List<InlineKeyboardButton> inLineLine = new ArrayList<>();
		InlineKeyboardButton inkB = new InlineKeyboardButton(Constants.ACTIVELIGHTS);
		inkB.setCallback_data(CallBackCodes.ACTIVELIGHTS.toString());
		inLineLine.add(inkB);
		inkB = new InlineKeyboardButton(Constants.DISACTIVELIGHTS);
		inkB.setCallback_data(CallBackCodes.DISACTIVELIGHTS.toString());
		inLineLine.add(inkB);
		inlKeyboard.add(inLineLine);
		inLineLine = new ArrayList<>();
		inkB = new InlineKeyboardButton(Constants.CHANGECOLORS);
		inkB.setCallback_data(CallBackCodes.COLORLIST.toString());
		inLineLine.add(inkB);
		inlKeyboard.add(inLineLine);
		lightsInlineMenu = new InlineKeyboardMarkup(inlKeyboard);
		
	}

	public ReplyKeyboardMarkupWithButtons getMainMenu() 
	{
		return mainMenu;
	}

	public InlineKeyboardMarkup getLightsInlineMenu() 
	{
		return lightsInlineMenu;
	}

	
}
