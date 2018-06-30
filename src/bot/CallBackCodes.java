package bot;


public enum CallBackCodes {

	//Lettere disponibili
	/*
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
	 * 1
	 * 2
	 * 3
	 * 4
	 * 5
	 * 6
	 * 7
	 * 8
	 * 9
	 * +
	 * -
	 * ?
	 * £
	 * !
	 * %
	 * &
	 *(
	 *)
	 *=
	 *{
	 *}
	 */
	
	RENAMELIST("R"),EDITITEM("E"),BACKTOMENULIST("A"),PRINTLIST("P") ,CALLBACKLIST("L"),
	PRINTPHOTO("Q"),CALLBACKITEM("I"),CREATELIST("C"), CREATEITEM("N"), CONFERMATIONDELETE("Z"),
	KILLITEM("K"),KILLIST("M"), PRINTPANORAMIC("W"), ADDCOLOR("T"), CHANGECOLOR("Y"), ACTIVELIGHTS("U"),
	DISACTIVELIGHTS("X"), RAINBOWMODE("S"), REMOVECOLOR("B"), COLORLIST("D"), BACKTOCOLORMENU("G"), NEXTDAY("F"),
	PREVIOUSDAY("H"), ADDDAILYTASK("J"), DAILYTASK("O"), GOTODAY("a"), PRINTAGENDA("b"), YESMEMO("c"), NOMEMO("d"),
	YESREPEAT("e"), NOREPEAT("f"), REMOVETASK("g"), ACTIVEMEMO("h"), DISABLEMEMO("j"), BACKTOAGENDA("i"),
	NEXTMONTH("k"), PREVIOUSMONTH("l"), NODAY("m"), DAYCHOOSEN("n"), BACKTOTODAY("o");
	
	
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
		      if (text.equals(c.str)) {
		        return c;
		      }
		    }
		  }
		  return null;
		}
}
