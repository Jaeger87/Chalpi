package bot.organizerbox;

import java.util.ArrayList;

public class ItemList{

	private ArrayList<Item> itemList;
	private String name;
	private int id;
	private int lastItemId;
	
	public ItemList(String name, int id)
	{
		this.name = name;
		this.id = id;
		itemList = new ArrayList<>();
	}

	public boolean add(String s) 
	{
		lastItemId++;
		return itemList.add(new Item(s,lastItemId));
	}

	public void reset() 
	{
		itemList.clear();
	}

	public Item get(int index) 
	{
		if(index < itemList.size())
			return itemList.get(index);
		return null;
	}

	public Item getByID(int idItem) 
	{
		for(int i = 0; i<itemList.size(); i++)
			if(itemList.get(i).getId() == idItem)
				return itemList.get(i);
		return null;
	}
	
	public boolean isEmpty() 
	{
		return itemList.isEmpty();
	}
	
	public boolean remove(String s) 
	{
		for(int i = 0; i<itemList.size(); i++)
			if(itemList.get(i).equals(s))
			{
				itemList.remove(i);
				return true;
			}
		return false;
	}

	public boolean remove(int idItem) 
	{
		for(int i = 0; i<itemList.size(); i++)
			if(itemList.get(i).getId() == idItem)
			{
				itemList.remove(i);
				return true;
			}
		return false;
	}
	
	public int size() 
	{
		return itemList.size();
	}

	public String getName() 
	{
		return name;
	}

	
	public void setName(String name) 
	{
		this.name = name;
	}
	
	
	@Override
	public String toString()
	{
		return "*"+ name +  "* :\n\n" + getListString();
	}


	public String getListString()
	{
		String result = "";
		for(int i = 0; i < itemList.size(); i++)
			if(itemList.get(i) != null)
			    result += i+1 + ") " + itemList.get(i).toString() + "\n";
		return result;
	}
	
	
	public int getId() 
	{
		return id;
	}
	
	public void editItemNameByID(String name, int IDItem)
	{
		for(Item i: itemList)
			if(i.getId() == IDItem)
			{
				i.setText(name);
				return;
			}
	}
}
