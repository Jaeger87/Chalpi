package bot;

import java.util.List;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import bot.organizerbox.DailyTask;

public class Utils {

	
	private static DateTimeFormatter formatterDateTime = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss");
	private static DateTimeFormatter formatterDate = DateTimeFormat.forPattern("yyyy-MM-dd");
	private static DateTimeFormatter formatterTime = DateTimeFormat.forPattern("HH:mm");
	
	public static String dailyAgendaString(List<DailyTask> dayAgenda)
	{
		StringBuilder sb = new StringBuilder();
		
		for(DailyTask dt : dayAgenda)
		{
			sb.append(dt.toString());
			sb.append('\n');
		}
		
		return sb.substring(0, sb.length() - 1);
		
	}
	
	public static LocalDateTime fromStringToDateTime(String dateToParse)
	{
		try{
			return formatterDateTime.parseLocalDateTime(dateToParse); 
		}
		
		catch(Exception e)
		{
			
		}
		
		return null;
	}
	

	public static LocalDate fromStringToDate(String dateToParse)
	{
		try{
			return formatterDate.parseLocalDateTime(dateToParse).toLocalDate(); 
		}
		
		catch(Exception e)
		{
			
		}
		
		return null;
	}
	
	
	public static LocalTime fromStringToTime(String timeToParse)
	{
		try{
			return formatterTime.parseLocalTime(timeToParse); 
		}
		
		catch(Exception e)
		{
			
		}
		
		return null;
	}

	
	
}
