package bot;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

public class UserStatus {

	private Long id;
	private int lastItemListID;
	private int lastItemID;
	private LocalDate lastLocalDate;
	private String pendingTaskString;
	private LocalDateTime pendingLocalDateTime;
	private boolean pendingMemo = false;
	
	private UserPendingRequest up = UserPendingRequest.NONE;
	
	public UserStatus(Long id) 
	{
		this.id = id;
	}

	public int getLastItemListID()
	{
		return lastItemListID;
	}

	public void setLastItemListID(int lastItemListID) 
	{
		this.lastItemListID = lastItemListID;
	}

	public int getLastItemID()
	{
		return lastItemID;
	}

	public void setLastItemID(int lastItemID) 
	{
		this.lastItemID = lastItemID;
	}

	public UserPendingRequest getUp()
	{
		return up;
	}

	public void setUp(UserPendingRequest up) 
	{
		this.up = up;
	}

	public Long getId() 
	{
		return id;
	}

	public LocalDate getLastLocalDate()
	{
		return lastLocalDate;
	}

	public void setLastLocalDate(LocalDate lastLocalDate) 
	{
		this.lastLocalDate = lastLocalDate;
	}

	public String getPendingTaskString() 
	{
		return pendingTaskString;
	}

	public void setPendingTaskString(String pendingTaskString) 
	{
		this.pendingTaskString = pendingTaskString;
	}

	public LocalDateTime getPendingLocalDateTime() 
	{
		return pendingLocalDateTime;
	}

	public void setPendingLocalDateTime(LocalDateTime pendingLocalDateTime)
	{
		this.pendingLocalDateTime = pendingLocalDateTime;
	}

	public boolean getPendingMemo()
	{
		return pendingMemo;
	}

	public void setPendingMemo(boolean pendingMemo)
	{
		this.pendingMemo = pendingMemo;
	}
	
	
	
}
