package bot.organizerbox;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import com.botticelli.bot.request.methods.types.GsonOwner;
import com.google.gson.Gson;

import bot.Constants;

public class Settings {

	private boolean lightsOff = false;
	private boolean rainbow = false;
	private LedColor currentColor;
	
	public Settings()
	{
		currentColor = new LedColor(255,0,0);
	}
	
	public void setLights(boolean lightsOff)
	{
		this.lightsOff = lightsOff;
		saveMe();
	}
	
	public boolean setColorLights(LedColor currentColor)
	{
		if(currentColor == null)
			return false;
		
		this.currentColor = currentColor;
		rainbow = false;
		saveMe();
		return true;
	}

	public boolean isLightsOff() 
	{
		return lightsOff;
	}

	public boolean isRainbow() 
	{
		return rainbow;
	}

	public void setRainbow(boolean rainbow) 
	{
		this.rainbow = rainbow;
		saveMe();
	}
	
	
	private void saveMe()
	{
		Gson gson = GsonOwner.getInstance().getGson();
		try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(Constants.SETTINGSFILE), "utf-8"))) {
			writer.write(gson.toJson(this));
		} catch (UnsupportedEncodingException e) {

		} catch (FileNotFoundException e) {
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
