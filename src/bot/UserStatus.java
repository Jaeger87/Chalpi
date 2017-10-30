package bot;

public class UserStatus {

	private Long id;
	private int lastItemListID;
	private int lastItemID;
	private UserPendingRequest up = UserPendingRequest.NONE;
	
	public UserStatus(Long id) {
		this.id = id;
	}

	public int getLastItemListID() {
		return lastItemListID;
	}

	public void setLastItemListID(int lastItemListID) {
		this.lastItemListID = lastItemListID;
	}

	public int getLastItemID() {
		return lastItemID;
	}

	public void setLastItemID(int lastItemID) {
		this.lastItemID = lastItemID;
	}

	public UserPendingRequest getUp() {
		return up;
	}

	public void setUp(UserPendingRequest up) {
		this.up = up;
	}

	public Long getId() {
		return id;
	}
	
	
	
}
