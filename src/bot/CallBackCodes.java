package bot;


public enum CallBackCodes {

	//Lettere disponibili
	/*
	 * b
	 * c
	 * d
	 * e
	 * f
	 * g
	 * h
	 * j
	 * k
	 * l
	 * m
	 * n
	 * o
	 * p
	 * q
	 * r
	 * s
	 * t
	 * u
	 * v
	 * w
	 * y
	 * z
	 */
	
	RENAMELIST("R"),EDITITEM("E"),BACKTOMENULIST("A"),PRINTLIST("P") ,CALLBACKLIST("L"),
	PRINTPHOTO("Q"),CALLBACKITEM("I"),CREATELIST("C"), CREATEITEM("N"), CONFERMATIONDELETE("Z"),
	KILLITEM("K"),KILLIST("M"), PRINTPANORAMIC("W"), ADDCOLOR("T"), CHANGECOLOR("Y"), ACTIVELIGHTS("U"),
	DISACTIVELIGHTS("X"), RAINBOWMODE("S"), REMOVECOLOR("B"), COLORLIST("D"), BACKTOCOLORMENU("G"), NEXTDAY("F"),
	PREVIOUSDAY("H"), ADDDAILYTASK("J"), DAILYTASK("O"), GOTODAY("a");
	
	
	private String str;
	/**
	 * Costruttore privato che costruisce l'enum da stringa
	 * @param str
	 */
	private CallBackCodes(String str)
	{
		this.str=str;
	}
	
	@Override
	public String toString()
	{
		return str;
	}
	/**
	 * Metodo per poter costruire l'enum da Stringa
	 * @param text
	 * @return
	 */
	public static CallBackCodes fromString(String text) {
		  if (text != null) {
		    for (CallBackCodes c : CallBackCodes.values()) {
		      if (text.equalsIgnoreCase(c.str)) {
		        return c;
		      }
		    }
		  }
		  return null;
		}
}
