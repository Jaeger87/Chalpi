package bot.organizerbox;

import org.joda.time.DateTime;

public class DailyTask implements Comparable<DailyTask>, ListableOboxItems{

	private String task;
	private DateTime schedule;
	private int id;
	private boolean enable;
	private boolean notice;
	private static final char TIMESEPARATOR = ':';
	
	
	public DailyTask(String task, DateTime schedule, int id, boolean notice) 
	{
		this.task = task;
		this.schedule = schedule;
		this.id = id;
		this.enable = true;
		this.notice = notice;
	}
	
	public String getTask() 
	{
		return task;
	}
	
	public void setTask(String task)
	{
		this.task = task;
	}
	
	public DateTime getSchedule() 
	{
		return schedule;
	}
	
	public void setSchedule(DateTime schedule) 
	{
		this.schedule = schedule;
	}
	
	public boolean isEnable()
	{
		return enable;
	}

	public void disable()
	{
		enable = false;
	}

	public void enable()
	{
		enable = true;
	}
	
	public int getId() 
	{
		return id;
	}

	@Override
	public int compareTo(DailyTask odt) {
		if(schedule.isBefore(odt.getSchedule()))
			return -1;
		return 1;
		
	}
	
	public boolean check(DateTime now)
	{
		
		if(now.getMillis() - schedule.getMillis() > 0)
			return true;
		return false;
	}
	
	@Override
	public String toString()
	{
		return task + " / " + schedule.getHourOfDay() 
		+ TIMESEPARATOR + schedule.getMinuteOfHour() 
		+ TIMESEPARATOR +schedule.getSecondOfMinute();
	}

	public boolean isNotice()
	{
		return notice;
	}


	
	
}
