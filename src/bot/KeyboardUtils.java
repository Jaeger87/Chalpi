package bot;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.joda.time.LocalDate;

import com.botticelli.bot.request.methods.types.InlineKeyboardButton;
import com.botticelli.bot.request.methods.types.InlineKeyboardMarkup;

import bot.organizerbox.DailyTask;
import bot.organizerbox.ItemList;
import bot.organizerbox.ListableOboxItems;
import bot.organizerbox.OrganizerBox;

public class KeyboardUtils {

	
	private static InlineKeyboardMarkup yesNoMemoKeyboardStatic;
	private static InlineKeyboardMarkup yesNoRepeatKeyboardStatic;
	private static InlineKeyboardMarkup hoursKeyboard;
	private static InlineKeyboardMarkup minutesKeyboard;
	
	public static InlineKeyboardMarkup ItemListKeyboardFactory(ItemList itli)
	{
		
		List<List<InlineKeyboardButton>> inlKeyboard = new ArrayList<List<InlineKeyboardButton>>();
		for(int i = 0; i<itli.size(); i++)
		{
			List<InlineKeyboardButton> lastLine = new ArrayList<>();
			InlineKeyboardButton button = new InlineKeyboardButton(" " + (i+1));
			button.setCallback_data(CallBackCodes.CALLBACKITEM + Constants.CALLBACKSEPARATOR + itli.getId() + Constants.CALLBACKSEPARATOR + itli.get(i).getId());
			lastLine.add(button);
			inlKeyboard.add(lastLine);
		}
		List<InlineKeyboardButton> lastLine = new ArrayList<>();
		InlineKeyboardButton button = new InlineKeyboardButton(Constants.CREATEITEM);
		button.setCallback_data(CallBackCodes.CREATEITEM + Constants.CALLBACKSEPARATOR + itli.getId());
		lastLine.add(button);
		inlKeyboard.add(lastLine);
		
		lastLine = new ArrayList<>();
		button = new InlineKeyboardButton(Constants.PRINTLIST);
		button.setCallback_data(CallBackCodes.PRINTLIST + Constants.CALLBACKSEPARATOR + itli.getId());
		
		lastLine.add(button);
		
		button = new InlineKeyboardButton(Constants.BACKTOLISTMENU);
		button.setCallback_data(CallBackCodes.BACKTOMENULIST.toString());
		
		lastLine.add(button);
		
		inlKeyboard.add(lastLine);
		lastLine = new ArrayList<>();
		
		button = new InlineKeyboardButton(Constants.RENAMELIST);
		button.setCallback_data(CallBackCodes.RENAMELIST + Constants.CALLBACKSEPARATOR + itli.getId());
		
		lastLine.add(button);
		
		button = new InlineKeyboardButton(Constants.KILLLIST);
		button.setCallback_data(CallBackCodes.KILLIST + Constants.CALLBACKSEPARATOR + itli.getId());
		
		lastLine.add(button);
		inlKeyboard.add(lastLine);
		
		
		return new InlineKeyboardMarkup(inlKeyboard);
		
	}
	
	
	
	public static InlineKeyboardMarkup itemKeyboardFactory(int listID, int itemID)
	{
		List<List<InlineKeyboardButton>> inlKeyboard = new ArrayList<List<InlineKeyboardButton>>();
		List<InlineKeyboardButton> lastLine = new ArrayList<>();
		lastLine = new ArrayList<>();
		
		InlineKeyboardButton button = new InlineKeyboardButton(Constants.EDITITEM);
		button.setCallback_data(CallBackCodes.EDITITEM + Constants.CALLBACKSEPARATOR + listID + Constants.CALLBACKSEPARATOR + itemID);
		
		lastLine.add(button);
		
		button = new InlineKeyboardButton(Constants.BACKTOLIST);
		button.setCallback_data(CallBackCodes.CALLBACKLIST + Constants.CALLBACKSEPARATOR + listID);
	
		lastLine.add(button);
		
		inlKeyboard.add(lastLine);
		
		lastLine = new ArrayList<>();
		
		button = new InlineKeyboardButton(Constants.KILLITEM);
		button.setCallback_data(CallBackCodes.KILLITEM + Constants.CALLBACKSEPARATOR + listID + Constants.CALLBACKSEPARATOR + itemID);
		
		lastLine.add(button);
		inlKeyboard.add(lastLine);
		
		
		return new InlineKeyboardMarkup(inlKeyboard);
	}
	
	
	
	public static InlineKeyboardMarkup dailyAgendaKeyboardFactory(List<DailyTask> dtDayly, LocalDate day)
	{
		
		
		List<ListableOboxItems> listToInput = new ArrayList<>();
		
		if(dtDayly != null)		
			for(DailyTask dt: dtDayly)
				listToInput.add(dt);
		
		InlineKeyboardMarkup result;
		
		if(day.isBefore(LocalDate.now()))
			result = inlineListOfListable(listToInput, CallBackCodes.DAILYTASK, 
					null, null, day.toString(), day.toString());
		else
			result = inlineListOfListable(listToInput, CallBackCodes.DAILYTASK, 
					CallBackCodes.ADDDAILYTASK, Constants.ADDDAILYTASK, day.toString(), day.toString());
		
		ArrayList<InlineKeyboardButton> lastLine = new ArrayList<>();
		InlineKeyboardButton button = new InlineKeyboardButton(Constants.PREVIOUSDAY);
		button.setCallback_data(CallBackCodes.PREVIOUSDAY + Constants.CALLBACKSEPARATOR + day.minusDays(1).toString());
		lastLine.add(button);
		
		button = new InlineKeyboardButton(Constants.GOTODAY);
		button.setCallback_data(CallBackCodes.GOTODAY + Constants.CALLBACKSEPARATOR);
		lastLine.add(button);
		
		button = new InlineKeyboardButton(Constants.NEXTDAY);
		button.setCallback_data(CallBackCodes.NEXTDAY + Constants.CALLBACKSEPARATOR + day.plusDays(1).toString());
		lastLine.add(button);
		
		result.AddLine(lastLine);
		
		if(dtDayly != null)
		{
			lastLine = new ArrayList<>();
			button = new InlineKeyboardButton(Constants.PRINTAGENDA);
			button.setCallback_data(CallBackCodes.PRINTAGENDA + Constants.CALLBACKSEPARATOR + day.toString());
			lastLine.add(button);		
			result.AddLine(lastLine);
		}
		
		
		return result;
	}
	
	
	public static InlineKeyboardMarkup listKeyboardFactory(List<ItemList> all)
	{
		List<ListableOboxItems> listToInput = new ArrayList<>();
		
		for(ItemList il: all)
			listToInput.add(il);
		return KeyboardUtils.inlineListOfListable(listToInput, CallBackCodes.CALLBACKLIST, CallBackCodes.CREATELIST, Constants.CREATELIST, null, null);
	
	}
	

	private static InlineKeyboardMarkup inlineListOfListable(List<ListableOboxItems> loi, CallBackCodes itemCBC,
			CallBackCodes addCBC, String addString, String optionalAddValue, String optionalListableValue)
	{
		String optListable = "";
		
		if(optionalAddValue != null)
			optListable += Constants.CALLBACKSEPARATOR + optionalAddValue;
		
		List<List<InlineKeyboardButton>> inlKeyboard = new ArrayList<List<InlineKeyboardButton>>();
		for(int i = 0; i < loi.size(); i++)
		{
			List<InlineKeyboardButton> lastLine = new ArrayList<>();
			InlineKeyboardButton button = new InlineKeyboardButton(" " + (i+1));
			button.setCallback_data(itemCBC + Constants.CALLBACKSEPARATOR + loi.get(i).getId() + optListable);
			lastLine.add(button);
			inlKeyboard.add(lastLine);
		}
		
		if(addCBC != null && addString != null)
		{
			List<InlineKeyboardButton> lastLine = new ArrayList<>();
			InlineKeyboardButton button = new InlineKeyboardButton(addString);
		
			String callbackData = (optionalAddValue != null) ? addCBC + Constants.CALLBACKSEPARATOR + optionalAddValue : "" + addCBC;
		
			button.setCallback_data(callbackData);
			lastLine.add(button);
			inlKeyboard.add(lastLine);
		}
		return new InlineKeyboardMarkup(inlKeyboard);
	}

	
	public static InlineKeyboardMarkup yesNoDeleteItemKeyboardFactory(ItemList itli)
	{
		List<List<InlineKeyboardButton>> inlKeyboard = new ArrayList<List<InlineKeyboardButton>>();
		List<InlineKeyboardButton> lastLine = new ArrayList<>();
		InlineKeyboardButton button = new InlineKeyboardButton(Constants.YESKILL);
		button.setCallback_data(CallBackCodes.CONFERMATIONDELETE + Constants.CALLBACKSEPARATOR + itli.getId());
		lastLine.add(button);
		button = new InlineKeyboardButton(Constants.NOKILL);
		button.setCallback_data(CallBackCodes.CALLBACKLIST + Constants.CALLBACKSEPARATOR + itli.getId());
		lastLine.add(button);
		inlKeyboard.add(lastLine);
		return new InlineKeyboardMarkup(inlKeyboard);
	}
	
	public static InlineKeyboardMarkup yesNoMemoKeyboard()
	{
		if(yesNoMemoKeyboardStatic != null)
			return yesNoMemoKeyboardStatic;
		yesNoMemoKeyboardStatic = yesNOKeyboardFactory(CallBackCodes.YESMEMO, Constants.YESMEMO, null,
				CallBackCodes.NOMEMO, Constants.NOMEMO, null);
		return yesNoMemoKeyboardStatic;
	}
	
	public static InlineKeyboardMarkup yesNoRepeatKeyboard()
	{
		if(yesNoRepeatKeyboardStatic != null)
			return yesNoRepeatKeyboardStatic;
		yesNoRepeatKeyboardStatic = yesNOKeyboardFactory(CallBackCodes.YESREPEAT, Constants.YESREPEAT, null,
				CallBackCodes.NOREPEAT, Constants.NOREPEAT, null);
		return yesNoRepeatKeyboardStatic;
	}
	
	
	private static InlineKeyboardMarkup yesNOKeyboardFactory(CallBackCodes callbackYES, String textYES,
			List<String> argsYES, CallBackCodes callbackNO, String textNO, List<String> argsNO)
	{
		List<List<InlineKeyboardButton>> inlKeyboard = new ArrayList<List<InlineKeyboardButton>>();
		List<InlineKeyboardButton> lastLine = new ArrayList<>();
		lastLine.add(createButton(callbackYES, textYES, argsYES));
		lastLine.add(createButton(callbackNO, textNO, argsNO));
		inlKeyboard.add(lastLine);
		return new InlineKeyboardMarkup(inlKeyboard);
	}
	
	
	public static InlineKeyboardMarkup taskKeyboard(LocalDate day, int id)
	{
		List<List<InlineKeyboardButton>> inlKeyboard = new ArrayList<List<InlineKeyboardButton>>();
		
		List<String> args = new ArrayList<>();
		args.add(day.toString());
		args.add(String.valueOf(id));
		
		List<InlineKeyboardButton> lastLine = new ArrayList<>();
		
		
		lastLine.add(createButton(CallBackCodes.ACTIVEMEMO, Constants.ACTIVEMEMO, args));
		lastLine.add(createButton(CallBackCodes.DISABLEMEMO, Constants.DISABLEMEMO, args));
		inlKeyboard.add(lastLine);
		
		lastLine = new ArrayList<>();
		lastLine.add(createButton(CallBackCodes.REMOVETASK,Constants.DELETETASK, args));
		inlKeyboard.add(lastLine);
		
		
		lastLine = new ArrayList<>();
		lastLine.add(createButton(CallBackCodes.BACKTOAGENDA,Constants.BACKTOAGENDA, args.subList(0, 1)));
		inlKeyboard.add(lastLine);
		return new InlineKeyboardMarkup(inlKeyboard);
	}
	
	
	private static InlineKeyboardButton createButton(CallBackCodes callback, String text, List<String> args)
	{
		InlineKeyboardButton button = new InlineKeyboardButton(text);
		String callBackData = "" + callback;
		
		if(args != null)
			for(String s : args)
				callBackData += Constants.CALLBACKSEPARATOR + s;
		button.setCallback_data(callBackData);
		
		return button;
	}
	
	
	private static InlineKeyboardButton createButtonOneArg(CallBackCodes callback, String text, String arg)
	{
		InlineKeyboardButton button = new InlineKeyboardButton(text);
		String callBackData = "" + callback;
		
		if(arg != null)
			callBackData += Constants.CALLBACKSEPARATOR + arg;
		button.setCallback_data(callBackData);
		
		return button;
	}
	
	public static String textListFactory(List<ItemList> all)
	{
		if (all.isEmpty())
			return Constants.EMPTYLIST;
		
		String text = "";
		for(int i = 0; i < all.size(); i++)
			text += (i+1) + ") " + all.get(i).getName() + "\n";

		return text += Constants.ALLISTMESSAGE;

	}
	
	public static InlineKeyboardMarkup colorsKeyboardFactory(OrganizerBox oBox)
	{		
		List<InlineKeyboardButton> colors = oBox.getColors()
				.stream()
				.map(c -> new InlineKeyboardButton(c))
				.peek(c -> c.setCallback_data(createCallBackData(CallBackCodes.CHANGECOLOR,c.getText())))
				.collect(Collectors.toList());

		List<List<InlineKeyboardButton>> keyboard = new ArrayList<List<InlineKeyboardButton>>();
		List<InlineKeyboardButton> line = new ArrayList<>();
		if (colors.size() > 0) 
		{
			line.add(colors.get(0));
			for (int i = 1; i < colors.size(); i++) 
			{
				if (i % 3 == 0) 
				{
					keyboard.add(line);
					line = new ArrayList<>();
				}
				line.add(colors.get(i));
			}
			keyboard.add(line);
		}
		line = new ArrayList<>();
		InlineKeyboardButton rainbowButton = new InlineKeyboardButton(Constants.RAINBOWMODE);
		InlineKeyboardButton removeButton = new InlineKeyboardButton(Constants.REMOVECOLOR);
		InlineKeyboardButton createButton = new InlineKeyboardButton(Constants.ADDCOLOR);
		
		rainbowButton.setCallback_data(CallBackCodes.RAINBOWMODE.toString());
		removeButton.setCallback_data(CallBackCodes.REMOVECOLOR.toString());
		createButton.setCallback_data(CallBackCodes.ADDCOLOR.toString());
		
		line.add(rainbowButton);
		keyboard.add(line);
		line = new ArrayList<>();
		line.add(createButton);
		line.add(removeButton);
		
		keyboard.add(line);
		
		line = new ArrayList<>();
		InlineKeyboardButton backButton = new InlineKeyboardButton(Constants.BACK);
		backButton.setCallback_data(CallBackCodes.BACKTOCOLORMENU.toString());
		line.add(backButton);
		keyboard.add(line);
		
		return new InlineKeyboardMarkup(keyboard);
	}
	
	
	private static String createCallBackData(CallBackCodes cbc, String... strings)
	{
		StringBuffer sb = new StringBuffer();
		sb.append(cbc.toString());
		if(strings != null)
			for(String s : strings)
			{
				sb.append(Constants.CALLBACKSEPARATOR);
				sb.append(s);
			}
		return sb.toString();
	}
	

	public static InlineKeyboardMarkup getCalendar(LocalDate day)
	{
		List<List<InlineKeyboardButton>> keyboard = new ArrayList<List<InlineKeyboardButton>>();
		
		String month = day.toString("MMMM yyyy", Locale.ITALY);
		
		month = month.substring(0, 1).toUpperCase() + month.substring(1);
		
		List<InlineKeyboardButton> firstRow = new ArrayList<>();
		
		firstRow.add(createButton(CallBackCodes.PREVIOUSMONTH,
				Constants.PREVIOUSMONTH, Utils.list1FromString(day.toString())));
		
		firstRow.add(createButton(CallBackCodes.NODAY,month, null));
		
		firstRow.add(createButton(CallBackCodes.NEXTMONTH,
				Constants.NEXTMONTH, Utils.list1FromString(day.toString())));
		
		
		keyboard.add(firstRow);
		
		int dayOfMonth = Utils.daysOfMonth(day);
		
		LocalDate indexDay = day.minusDays(day.getDayOfMonth() - 1);
		
		List<Triple<CallBackCodes, String, List<String>>> row = new LinkedList<>();
		
		int rowIndex = 1;
		
		for(; rowIndex < indexDay.getDayOfWeek(); rowIndex++)
			row.add(new Triple<CallBackCodes, String,  List<String>>(CallBackCodes.NODAY, "-", null)); 
		
		for(; rowIndex <= 7; rowIndex++)
		{
			row.add(new Triple<CallBackCodes, String, List<String>>(CallBackCodes.DAYCHOOSEN, "" +
					indexDay.getDayOfMonth(), Utils.list1FromString(indexDay.toString())));
			indexDay = indexDay.plusDays(1);
		}
		
		keyboard.add(buildBottonsLine(row));
		
		while(indexDay.getDayOfMonth() < dayOfMonth - 7)
		{
			row = new LinkedList<>();
			for(int i = 0; i < 7; i++)
			{
				row.add(new Triple<CallBackCodes, String, List<String>>(CallBackCodes.DAYCHOOSEN, "" +
						indexDay.getDayOfMonth(), Utils.list1FromString(indexDay.toString())));
				indexDay = indexDay.plusDays(1);
			}
			keyboard.add(buildBottonsLine(row));
		}
		
		rowIndex = 0;
		row = new LinkedList<>();
		
		while(indexDay.getDayOfMonth() <= dayOfMonth && indexDay.getDayOfMonth() > 1 && rowIndex < 7)
		{
			row.add(new Triple<CallBackCodes, String, List<String>>(CallBackCodes.DAYCHOOSEN, "" +
					indexDay.getDayOfMonth(), Utils.list1FromString(indexDay.toString())));
			indexDay = indexDay.plusDays(1);
			rowIndex++;
		}
		
		if(rowIndex == 7)
		{
			keyboard.add(buildBottonsLine(row));
			row = new LinkedList<>();
			rowIndex = 0;
			while(indexDay.getDayOfMonth() <= dayOfMonth && indexDay.getDayOfMonth() > 1 && rowIndex < 7)
			{
				row.add(new Triple<CallBackCodes, String, List<String>>(CallBackCodes.DAYCHOOSEN, "" +
						indexDay.getDayOfMonth(), Utils.list1FromString(indexDay.toString())));
				indexDay = indexDay.plusDays(1);
				rowIndex++;
			}
		}
		
		for(; rowIndex < 7; rowIndex++)
			row.add(new Triple<CallBackCodes, String,  List<String>>(CallBackCodes.NODAY, "-", null)); 
		
		keyboard.add(buildBottonsLine(row));
		
		InlineKeyboardButton todayButton = createButton(CallBackCodes.BACKTOTODAY, Constants.BACKTOTODAY, null);
		List<InlineKeyboardButton> lastRow = new ArrayList<>();
		lastRow.add(todayButton);
		keyboard.add(lastRow);
		
		return new InlineKeyboardMarkup(keyboard);
		
	}
	
	
	private static List<InlineKeyboardButton> buildBottonsLine(List<Triple<CallBackCodes, String, List<String>>> triples)
	{
		List<InlineKeyboardButton> line = new ArrayList<>();
		
		for(Triple<CallBackCodes, String, List<String>> t : triples)
			line.add(createButton(t.getLeft(), t.getCenter(), t.getRight()));
		return line;
	}
	
	
	public static InlineKeyboardMarkup getHoursKeyboard()
	{
		if(hoursKeyboard != null)
			return hoursKeyboard;
		
		List<List<InlineKeyboardButton>> keyboard = new ArrayList<List<InlineKeyboardButton>>();

		for(int rowIndex = 0; rowIndex < 4; rowIndex++)
		{
			List<InlineKeyboardButton> line = new ArrayList<>();
			for(int hourIndex = 6 * rowIndex; hourIndex < 6 * (rowIndex+1); hourIndex++)
			{
				InlineKeyboardButton button = createButtonOneArg(CallBackCodes.HOURSTOUCH, "" + hourIndex, "" + hourIndex);
				line.add(button);
			}
			keyboard.add(line);
			
		}
		
		hoursKeyboard = new InlineKeyboardMarkup(keyboard);
		return hoursKeyboard;
	}
	
	public static InlineKeyboardMarkup getMinuteKeyboard()
	{
		if(minutesKeyboard != null)
			return minutesKeyboard;
		
		List<List<InlineKeyboardButton>> keyboard = new ArrayList<List<InlineKeyboardButton>>();

		for(int rowIndex = 0; rowIndex < 4; rowIndex++)
		{
			List<InlineKeyboardButton> line = new ArrayList<>();
			for(int minuteIndex = 15 * rowIndex; minuteIndex < 15 * (rowIndex+1); minuteIndex+=5)
			{
				InlineKeyboardButton button = createButtonOneArg(CallBackCodes.MINUTESTOUCH, "" + minuteIndex, "" + minuteIndex);
				line.add(button);
			}
			keyboard.add(line);
			
		}
		
		minutesKeyboard = new InlineKeyboardMarkup(keyboard);
		return minutesKeyboard;
	}
	
}
