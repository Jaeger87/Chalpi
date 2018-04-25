package bot.organizerbox;

public class Item implements ListableOboxItems{

	private String text;
	private int id;
	
	
	public Item(String text, int id) 
	{
		this.text = text;
		this.id = id;
	}


	public String getText() 
	{
		return text;
	}


	public void setText(String text) 
	{
		this.text = text;
	}


	public int getId() 
	{
		return id;
	}
	
	@Override
	public String toString()
	{
		return text;
	}
	
	
}
