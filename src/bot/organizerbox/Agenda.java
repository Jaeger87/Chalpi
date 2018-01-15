package bot.organizerbox;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

public class Agenda {

	private HashMap<LocalDate, HashMap<Integer,DailyTask>> agendaDict;
	private int lastId;
	
	public Agenda() 
	{
		agendaDict = new HashMap<>();
		lastId = 0;
	}
	
	public boolean addTask(String task, DateTime schedule)
	{
		if(!schedule.isAfterNow())
			return false;
		LocalDate lc = schedule.toLocalDate();
		if(!agendaDict.containsKey(lc))
			agendaDict.put(lc, new HashMap<>());
		agendaDict.get(lc).put(lastId, new DailyTask(task, schedule,lastId));
		lastId++;
		return true;
	}
	
	public List<DailyTask> checkAgenda()
	{
		LocalDateTime today = new LocalDateTime();
		if(!agendaDict.containsKey(today.toLocalDate()))
		    return null;
		List<DailyTask> nowTasks = agendaDict.get(today.toLocalDate())
				.values()
				.stream()
				.filter(dt -> dt.isEnable())
				.filter(dt -> dt.check(today.toDateTime()))
				.sorted()
				.collect(Collectors.toList());;
		if(nowTasks.isEmpty())
			return null;
		return nowTasks;
	}
	
	
	public void disableTask(LocalDate ld, int id)
	{
		DailyTask dt = findTask(ld, id);
		if(dt == null)
			return;
		dt.disable();
	}
	
	public void enableTask(LocalDate ld, int id)
	{
		DailyTask dt = findTask(ld, id);
		if(dt == null)
			return;
		dt.enable();
	}
	
	private DailyTask findTask(LocalDate ld, int id)
	{
		if(!agendaDict.containsKey(ld))
			return null;
		if(!agendaDict.get(ld).containsKey(id))
			return null;
		return agendaDict.get(ld).get(id);
	}
}
