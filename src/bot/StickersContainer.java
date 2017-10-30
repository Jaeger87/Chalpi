package bot;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class StickersContainer {

	private ArrayList<String> stickers;
	private ArrayList<String> rainbowStickers;
	public StickersContainer()
	{
		stickers = new ArrayList<>();
		stickers.add("CAADBQAD5gADin6hDEGCw-BwZdwiAg");//occhilino faccina
		stickers.add("CAADBAAD3gEAAu7-tAABTjiJNHD9B_kC");//pikachu
		stickers.add("CAADBAAD_wADNbs2AAGBNgQviolniQI");//fox 1
		stickers.add("CAADAgADCwMAAkf7CQwsHHHsMcKGCwI");//fox 2
		stickers.add("CAADBAADhgADgaJbBsBGzrsyRXGAAg");//yes
		stickers.add("CAADAgADbQUAAgi3GQLljwGeBDBnUQI");//jaime
		
		rainbowStickers = new ArrayList<>();
		rainbowStickers.add("CAADBAAD8ggAAsn1kQdPcW9mJuLfUwI");//Disagio
		rainbowStickers.add("CAADBAADUQADq37BCRVlHatZ5UX-Ag");//Sto cazzo
		rainbowStickers.add("CAADBAADuAUAApv7sgAB6ck3g2tRWpgC");//Spongebob
		rainbowStickers.add("CAADBAADuAADNbs2AAFPpEcBlZDoZgI");//VomitoPC
		rainbowStickers.add("CAADBAADeQEAAjW7NgABzkILS4mFHwwC");//Vomito
		rainbowStickers.add("CAADAgADwQAD7sShCqT2nfrzo6HTAg");//Carlino
	}
	
	public String getRandomStickers()
	{
		return stickers.get(ThreadLocalRandom.current().nextInt(0, stickers.size()));
	}
	
	public String getRandomRainbowStickers()
	{
		return rainbowStickers.get(ThreadLocalRandom.current().nextInt(0, stickers.size()));
	}
}
