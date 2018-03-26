package bot.organizerbox;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import org.joda.time.LocalDate;

import com.botticelli.bot.request.methods.types.GsonOwner;
import com.google.gson.Gson;

import bot.Constants;

public class OrganizerBox {

	private LinkedHashMap<Integer,ItemList> itemMap;
	private int itemListLastID;
	private Settings settings;
	private LinkedHashMap<String,LedColor> colors;
	private Agenda agenda;
	
	public OrganizerBox()
	{
		itemMap = new LinkedHashMap<>();
		colors = new LinkedHashMap<>();
		settings = new Settings();
		itemListLastID = 0;
		agenda = new Agenda();
	}
	
	public boolean addItemList(String name)
	{
		if(checkDuplicate(name))
			return false;
		itemMap.put(itemListLastID,new ItemList(name,itemListLastID));
		itemListLastID++;
		saveMe();
		return true;
	}
	
	private void saveMe()
	{
		Gson gson = GsonOwner.getInstance().getGson();
		try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(Constants.SAVEORGANIZERFILE), "utf-8"))) {
			writer.write(gson.toJson(this));
		} catch (UnsupportedEncodingException e) {

		} catch (FileNotFoundException e) {
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public List<ItemList> getAllItemLists()
	{
	     return new LinkedList<>(itemMap.values());
	}
	
	@Override
	public String toString()
	{
		return itemMap.toString();
	}
	
	public ItemList getItemList(int i)
	{
		return itemMap.get(i);
	}
	
	public boolean addItemToList(String item, int idItemList)
	{
		if(!itemMap.containsKey(idItemList))
			return false;
		itemMap.get(idItemList).add(item);
		saveMe();
		return true;
	}
	
	public boolean deleteList(int idItemList)
	{
		if(!itemMap.containsKey(idItemList))
			return false;
		itemMap.remove(idItemList);
		saveMe();
		return true;
	}
	
	public boolean removeItem(int idItemList, int idItem)
	{
		if(!itemMap.containsKey(idItemList))
			return false;
		itemMap.get(idItemList).remove(idItem);
		saveMe();
		return true;
	}
	
	public boolean editListName(String name, int idItemList)
	{
		if(!itemMap.containsKey(idItemList))
			return false;
		if(checkDuplicate(name))
			return false;
		itemMap.get(idItemList).setName(name);
		saveMe();
		return true;
	}
	
	
	private boolean checkDuplicate(String name)
	{
		LinkedList<ItemList> values = new LinkedList<>(itemMap.values());
		for(ItemList il : values)
			if(il.getName().equals(name))
				return true;
		return false;
	}
	
	
	public void editItemNameByID(String name,int idList, int IDItem)
	{
		itemMap.get(idList).editItemNameByID(name, IDItem);
		saveMe();
	}

	public boolean setColorLights(String name)
	{
		if(!colors.containsKey(name))
			return false;
		
		if(!settings.setColorLights(colors.get(name)))
			return false;
		saveMe();
		return true;
	}
	
	public void setLightsOn()
	{
		this.settings.setLights(false);
		saveMe();
	}
	
	public void setLightsOff()
	{
		this.settings.setLights(true);
		saveMe();
	}
	
	public void rainbowOn()
	{
		this.settings.setRainbow(true);
		saveMe();
	}
	
	public void rainbowOff()
	{
		this.settings.setRainbow(false);
		saveMe();
	}
	
	public boolean addColor(String name, short red, short green, short blue)
	{
		if(colors.containsKey(name))
			return false;
		if(red > 255 || red < 0)
			return false;
		if(green > 255 || green < 0)
			return false;
		if(blue > 255 || blue < 0)
			return false;
		
		
		
		colors.put(name, new LedColor(red,green,blue));
		saveMe();
		return true;
	}
	
	public ArrayList<String> getColors()
	{		
		return new ArrayList<String>(colors.keySet());
	}
	
	public boolean removeColor(String name)
	{
		if(!colors.containsKey(name))
			return false;
	    colors.remove(name);
	    saveMe();
	    return true;
		
	}
	
	
	public void disableAgendaTask(LocalDate ld, int id)
	{
		agenda.disableTask(ld, id);
	}
	
	public void enableAgendaTask(LocalDate ld, int id)
	{
		agenda.enableTask(ld, id);
	}
	
	
	private String dailyAgendaString(List<DailyTask> dayAgenda)
	{
		StringBuilder sb = new StringBuilder();
		
		for(DailyTask dt : dayAgenda)
		{
			sb.append(dt.toString());
			sb.append('\n');
		}
		
		return sb.substring(0, sb.length() - 1);
		
	}
	
}
