package bot.organizerbox;

public class LedColor {

	private int red;
	private int green;
	private int blue;
	
	public LedColor(int red, int green, int blue) 
	{
		this.red = (short) red;
		this.green = green;
		this.blue = blue;
	}
	public int getRed() 
	{
		return red;
	}

	public int getGreen() 
	{
		return green;
	}
	public int getBlue() 
	{
		return blue;
	}
	
	
}
