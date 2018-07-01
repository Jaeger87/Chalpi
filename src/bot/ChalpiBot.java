package bot;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.IOUtils;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;

import com.botticelli.bot.Bot;
import com.botticelli.bot.request.methods.AnswerCallbackQueryToSend;
import com.botticelli.bot.request.methods.DocumentFileToSend;
import com.botticelli.bot.request.methods.EditMessageTextRequest;
import com.botticelli.bot.request.methods.MessageToSend;
import com.botticelli.bot.request.methods.StickerReferenceToSend;
import com.botticelli.bot.request.methods.types.CallbackQuery;
import com.botticelli.bot.request.methods.types.ChosenInlineResult;
import com.botticelli.bot.request.methods.types.ForceReply;
import com.botticelli.bot.request.methods.types.InlineKeyboardButton;
import com.botticelli.bot.request.methods.types.InlineKeyboardMarkup;
import com.botticelli.bot.request.methods.types.InlineQuery;
import com.botticelli.bot.request.methods.types.Message;
import com.botticelli.bot.request.methods.types.ParseMode;
import com.botticelli.bot.request.methods.types.PreCheckoutQuery;
import com.botticelli.bot.request.methods.types.ShippingQuery;
import com.botticelli.messagereceiver.MessageReceiver;
import com.fatboyindustrial.gsonjodatime.Converters;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import bot.organizerbox.DailyTask;
import bot.organizerbox.Item;
import bot.organizerbox.ItemList;
import bot.organizerbox.OrganizerBox;

public class ChalpiBot extends Bot{

	private HashSet<Long> authorizedUsers;
	private long masterUser;
	private OrganizerBox oBox;
	
	private HashMap<Long, UserStatus> pendingRegister;
	private StickersContainer sc;
	private MenuContainer menuContainer;
	public static final int TIMETOSLEEP = 855;
	private String ipAddress = "";
	private MessageReceiver myOwnmr;
	public static final Gson gson = Converters
			.registerLocalDate(Converters.
					registerDateTime(new GsonBuilder().enableComplexMapKeySerialization())).create();
	
	
	public ChalpiBot(String token) throws FileNotFoundException, UnknownHostException, SocketException {
		super(token);
		Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
        for (NetworkInterface netint : Collections.list(nets))
        	ipAddress += IpUtility.displayInterfaceInformation(netint);

		sc = new StickersContainer();
		pendingRegister = new HashMap<>();
		
		menuContainer = new MenuContainer();
		
		
		//creating the Set for the auth users
		boolean masterFound = false;
		authorizedUsers = new HashSet<>();
		try (Scanner s = new Scanner(new File(Main.filePath + Constants.AUTHORIZEDUSERS)))
		{
			while (s.hasNext())
			{
				long l = s.nextLong();
				authorizedUsers.add(l);
				if(!pendingRegister.containsKey(l))
				    pendingRegister.put(l, new UserStatus(l));
				if(!masterFound)
				{
					masterFound = true;
					masterUser = l;
				}
				
			}
		}
		
		
		
		try (FileInputStream inputStream = new FileInputStream(Main.filePath + Constants.SAVEORGANIZERFILE)) {
			String json = IOUtils.toString(inputStream);
			oBox = gson.fromJson(json, OrganizerBox.class);
		} catch (IOException e) {
			oBox = new OrganizerBox();
		}
	}

	
	public void setMessageReceiver(MessageReceiver myOwnmr)
	{
		this.myOwnmr = myOwnmr;
	}
	
	@Override
	public void audioMessage(Message m) {
		if(isNotAuthorized(m.getFrom().getId()))
			return;
		
	}

	@Override
	public void callback_query(CallbackQuery c) 
	{
		if(isNotAuthorized(c.getFrom().getId()))
			return;
		String[] values = c.getData().split(Constants.CALLBACKSEPARATOR);
		CallBackCodes cbc = CallBackCodes.fromString(values[0]);
		UserStatus ustatus = pendingRegister.get(c.getFrom().getId());
		MessageToSend mts;
		EditMessageTextRequest emt;
		ItemList il;
		int idList;
		int idItem;
		String filename = "";
		LocalDate day;
		int idTask;
		DailyTask dt;
		
		switch(cbc)
		{
			
		case PRINTLIST:
			Printer.printList(oBox.getItemList(Integer.parseInt(values[1])));
			sendAnswerCallBackQuery(c.getId(), Constants.PRINTING);
			break;
		case CALLBACKITEM:
			idList = Integer.parseInt(values[1]);
			idItem = Integer.parseInt(values[2]);
			Item i = oBox.getItemList(idList).getByID(idItem);
			emt = new EditMessageTextRequest(c.getMessage().getChat().getId(), c.getMessage().getMessageID(),
					i.toString());
			emt.setParse_mode(ParseMode.MARKDOWN);	
			emt.setReply_markup(KeyboardUtils.itemKeyboardFactory(idList, idItem));
			editMessageText(emt);
			break;
		case CALLBACKLIST:
			il = oBox.getItemList(Integer.parseInt(values[1]));
			callBackList(il, c.getMessage().getChat().getId(), c.getMessage().getMessageID());
			break;
			
		case CONFERMATIONDELETE:
			oBox.deleteList(Integer.parseInt(values[1]));
			editMessageText(new EditMessageTextRequest(c.getMessage().getChat().getId(), c.getMessage().getMessageID(),Constants.LISTDELETED));
			delay();
			sendListMessage(c.getMessage().getChat().getId());
			break;
			
		case BACKTOMENULIST:
			List<ItemList> lil = oBox.getAllItemLists(); 
			emt = new EditMessageTextRequest(c.getMessage().getChat().getId(), c.getMessage().getMessageID(),
					KeyboardUtils.textListFactory(lil));
			emt.setParse_mode(ParseMode.MARKDOWN);		
			emt.setReply_markup(KeyboardUtils.listKeyboardFactory(lil));
			editMessageText(emt);
			break;
			
		case PRINTPHOTO:
			filename = Constants.IMAGESFOLDER + values[1] + ".png";
			Printer.printPhoto(downloadFileFromTelegramServer(values[1], filename));
			sendAnswerCallBackQuery(c.getId(), Constants.PRINTING);
			break;
			
		case PRINTPANORAMIC:
			filename = Constants.IMAGESFOLDER + values[1] + ".png";
			Printer.printPhoto(downloadFileFromTelegramServer(values[1], filename));
			sendAnswerCallBackQuery(c.getId(), Constants.PRINTING);
			break;
			
		case CREATEITEM:
			ustatus.setUp(UserPendingRequest.ITEMCREATION);
			ustatus.setLastItemListID(Integer.parseInt(values[1]));
			mts = new MessageToSend(c.getMessage().getChat().getId(), Constants.SENDNEWITEM);
			mts.setReplyMarkup(new ForceReply(true));
			sendMessage(mts);
			break;
		case CREATELIST:
			ustatus.setUp(UserPendingRequest.LISTCREATION);
			mts = new MessageToSend(c.getMessage().getChat().getId(), Constants.SENDNEWLIST);
			mts.setReplyMarkup(new ForceReply(true));
			sendMessage(mts);
			break;
			
		case KILLIST:
			ItemList listToKill = oBox.getItemList(Integer.parseInt(values[1]));
			if(listToKill == null)
				return;
			emt = new EditMessageTextRequest(c.getMessage().getChat().getId(), c.getMessage().getMessageID(),
					Constants.CONFERMATIONDELETELIST + "*" + listToKill.getName() + "*" + Constants.CONFERMATION);
			emt.setParse_mode(ParseMode.MARKDOWN);	
			emt.setReply_markup(KeyboardUtils.yesNoDeleteItemKeyboardFactory(listToKill));
			editMessageText(emt);
			break;
		case KILLITEM:
			idList = Integer.parseInt(values[1]);
			idItem = Integer.parseInt(values[2]);
			oBox.removeItem(idList, idItem);
			il = oBox.getItemList(idList);
			callBackList(il, c.getMessage().getChat().getId(), c.getMessage().getMessageID());
			break;
			
		case RENAMELIST:
			ustatus.setUp(UserPendingRequest.RENAMELIST);
			ustatus.setLastItemListID(Integer.parseInt(values[1]));
			mts = new MessageToSend(c.getMessage().getChat().getId(), Constants.SENDNEWLISTNAME);
			mts.setReplyMarkup(new ForceReply(true));
			sendMessage(mts);
			break;
			
		case EDITITEM:
			ustatus.setUp(UserPendingRequest.EDITITEM);
			ustatus.setLastItemListID(Integer.parseInt(values[1]));
			ustatus.setLastItemID(Integer.parseInt(values[2]));
			mts = new MessageToSend(c.getMessage().getChat().getId(), Constants.SENDNEWITEMNAME);
			mts.setReplyMarkup(new ForceReply(true));
			sendMessage(mts);
			break;
			
		case ADDCOLOR:
			ustatus.setUp(UserPendingRequest.ADDCOLOR);
			mts = new MessageToSend(c.getMessage().getChat().getId(), Constants.SENDMENEWCOLOR);
			mts.setReplyMarkup(new ForceReply(true));
			sendMessage(mts);
			break;
			
		case COLORLIST:
			emt = new EditMessageTextRequest(c.getMessage().getChat().getId(), c.getMessage().getMessageID(),
					Constants.MENUCOLOR);
			emt.setParse_mode(ParseMode.MARKDOWN);	
			emt.setReply_markup(KeyboardUtils.colorsKeyboardFactory(oBox));
			editMessageText(emt);
			break;
			
		case ACTIVELIGHTS:
			oBox.setLightsOn();
			sendMessage(new MessageToSend(c.getMessage().getChat().getId(), Constants.LIGHTSON));
			break;
			
		case DISACTIVELIGHTS:
			oBox.setLightsOff();
			sendMessage(new MessageToSend(c.getMessage().getChat().getId(), Constants.LIGHTSOFF));
			break;
		case BACKTOCOLORMENU:
			emt = new EditMessageTextRequest(c.getMessage().getChat().getId(), c.getMessage().getMessageID(),
					Constants.MENUCOLOR);
			emt.setParse_mode(ParseMode.MARKDOWN);
			emt.setReply_markup(menuContainer.getLightsInlineMenu());
			editMessageText(emt);
			break;
		case CHANGECOLOR:
			oBox.setColorLights(values[1]);
			stickerMenuTrick(c.getMessage().getChat().getId());
			break;
		case RAINBOWMODE:
			oBox.rainbowOn();
			stickerRainbow(c.getMessage().getChat().getId());
			break;
		
		case REMOVECOLOR:
			ustatus.setUp(UserPendingRequest.REMOVECOLOR);
			mts = new MessageToSend(c.getMessage().getChat().getId(), Constants.SENDMECOLORTOREMOVE);
			mts.setReplyMarkup(new ForceReply(true));
			sendMessage(mts);
			break;
			
		case NEXTDAY:
			day = Utils.fromStringToDate(values[1]);
			showEditAgenda(day,c.getMessage().getChat().getId(), c.getMessage().getMessageID());
			break;
			
		case PREVIOUSDAY:
			day = Utils.fromStringToDate(values[1]);
			showEditAgenda(day,c.getMessage().getChat().getId(), c.getMessage().getMessageID());
			break;
		
		case ADDDAILYTASK:
			ustatus.setUp(UserPendingRequest.ADDTASK);
		    day = Utils.fromStringToDate(values[1]);
			ustatus.setLastLocalDate(day);
			mts = new MessageToSend(c.getMessage().getChat().getId(), Constants.WHATWHENTASK);
			mts.setReplyMarkup(new ForceReply(true));
			sendMessage(mts);
			break;
		
		case HOURSTOUCH:
			
			break;
			
		case MINUTESTOUCH:
			
			break;
			
		case YESMEMO:
			oBox.addTask(ustatus.getPendingTaskString(), ustatus.getPendingLocalDateTime().toDateTime(), true);
			emt = new EditMessageTextRequest(c.getMessage().getChat().getId(), c.getMessage().getMessageID(),
					Constants.REPEATETASK);
			emt.setParse_mode(ParseMode.MARKDOWN);
			emt.setReply_markup(KeyboardUtils.yesNoRepeatKeyboard());
			editMessageText(emt);
			break;
			
		case NOMEMO:
			oBox.addTask(ustatus.getPendingTaskString(), ustatus.getPendingLocalDateTime().toDateTime(), false);
			emt = new EditMessageTextRequest(c.getMessage().getChat().getId(), c.getMessage().getMessageID(),
					Constants.REPEATETASK);
			emt.setParse_mode(ParseMode.MARKDOWN);
			emt.setReply_markup(KeyboardUtils.yesNoRepeatKeyboard());
			editMessageText(emt);
			break;
			
		case YESREPEAT:
			ustatus.setUp(UserPendingRequest.REPEATASK);
			emt = new EditMessageTextRequest(c.getMessage().getChat().getId(), c.getMessage().getMessageID(),
					Constants.REPEATETASK);
			emt.setParse_mode(ParseMode.MARKDOWN);
			editMessageText(emt);
			
			mts = new MessageToSend(c.getMessage().getChat().getId(), Constants.HOWMANYWEEKS);
			mts.setParseMode(ParseMode.MARKDOWN);
			mts.setReplyMarkup(new ForceReply(true));
			sendMessage(mts);
			
			break;
			
			
		case NOREPEAT:
			stickerMenuTrick(c.getMessage().getChat().getId());
			showSendAgenda(ustatus.getLastLocalDate(), c.getMessage().getChat().getId());
			break;
			
		case DAILYTASK:
			idTask = Integer.parseInt(values[1]);
			day = Utils.fromStringToDate(values[2]);
		    dt = oBox.findTask(day, idTask);
		    
		    emt = new EditMessageTextRequest(c.getMessage().getChat().getId(), c.getMessage().getMessageID(),
					dt.toString());
			emt.setParse_mode(ParseMode.MARKDOWN);
			emt.setReply_markup(KeyboardUtils.taskKeyboard(day, idTask));
			editMessageText(emt);
		    
			break;
			
		case GOTODAY:
			showEditCalendar(LocalDate.now(),c.getMessage().getChat().getId(), c.getMessage().getMessageID());
			break;
			
		case PRINTAGENDA: 
			day = Utils.fromStringToDate(values[1]);
			Printer.printText(agendaStringToPrint(oBox.getDailyAgenda(day), day));
			sendAnswerCallBackQuery(c.getId(), Constants.PRINTING);
			
			break;
		
		case REMOVETASK:
			day = Utils.fromStringToDate(values[1]);
		    idTask = Integer.parseInt(values[2]);
			oBox.removeTask(day, idTask);
			showEditAgenda(day,c.getMessage().getChat().getId(), c.getMessage().getMessageID());
			break;
			
		case ACTIVEMEMO:
			day = Utils.fromStringToDate(values[1]);
		    idTask = Integer.parseInt(values[2]);
		    
		    oBox.enableAgendaTask(day,  idTask);
		    dt = oBox.findTask(day, idTask);
		    
		    sendAnswerCallBackQuery(c.getId(), Constants.EDITSAVED);
		    
		    emt = new EditMessageTextRequest(c.getMessage().getChat().getId(), c.getMessage().getMessageID(),
					dt.toString());
			emt.setParse_mode(ParseMode.MARKDOWN);
			emt.setReply_markup(KeyboardUtils.taskKeyboard(day, idTask));
			editMessageText(emt);
		    
			break;
			
		case DISABLEMEMO:
			day = Utils.fromStringToDate(values[1]);
		    idTask = Integer.parseInt(values[2]);
		    
		    oBox.disableAgendaTask(day,  idTask);
		    dt = oBox.findTask(day, idTask);
		    
		    sendAnswerCallBackQuery(c.getId(), Constants.EDITSAVED);
		    
		    
		    emt = new EditMessageTextRequest(c.getMessage().getChat().getId(), c.getMessage().getMessageID(),
					dt.toString());
			emt.setParse_mode(ParseMode.MARKDOWN);
			emt.setReply_markup(KeyboardUtils.taskKeyboard(day, idTask));
			editMessageText(emt);
			break;
			
		case BACKTOAGENDA:
			day = Utils.fromStringToDate(values[1]);
			showEditAgenda(day,c.getMessage().getChat().getId(), c.getMessage().getMessageID());
			break;
		
		case NEXTMONTH:
			day = Utils.fromStringToDate(values[1]);
			day = day.plusMonths(1);
			showEditCalendar(day,c.getMessage().getChat().getId(), c.getMessage().getMessageID());
			break;
			
		case PREVIOUSMONTH:
			day = Utils.fromStringToDate(values[1]);
			day = day.minusMonths(1);
			showEditCalendar(day,c.getMessage().getChat().getId(), c.getMessage().getMessageID());
			break;
			
		case NODAY:
			sendAnswerCallBackQuery(c.getId(), Constants.NOTVALIDCHOICE);
			break;
			
		case BACKTOTODAY:
			showEditAgenda(LocalDate.now(),c.getMessage().getChat().getId(), c.getMessage().getMessageID());
			break;
			
		case DAYCHOOSEN:
			day = Utils.fromStringToDate(values[1]);
			showEditAgenda(day,c.getMessage().getChat().getId(), c.getMessage().getMessageID());
			break;
			
		default:
			break;

		}
	}

	@Override
	public void chose_inline_result(ChosenInlineResult m) 
	{
		
	}

	@Override
	public void contactMessage(Message m) {
		if(isNotAuthorized(m.getFrom().getId()))
			return;
	}

	@Override
	public void documentMessage(Message m) {
		if(isNotAuthorized(m.getFrom().getId()))
			return;
	}

	@Override
	public void gameMessage(Message m) 
	{
		if(isNotAuthorized(m.getFrom().getId()))
			return;
		
	}

	@Override
	public void groupChatCreatedMessage(Message m) {
		if(isNotAuthorized(m.getFrom().getId()))
			return;
		
	}

	@Override
	public void groupChatPhotoDeleteMessage(Message m) {
		if(isNotAuthorized(m.getFrom().getId()))
			return;
	}

	@Override
	public void inLineQuery(InlineQuery m) {
		if(isNotAuthorized(m.getFrom().getId()))
			return;
		
	}

	@Override
	public void invoiceMessage(Message m) {
		if(isNotAuthorized(m.getFrom().getId()))
			return;
	}

	@Override
	public void leftChatMemberMessage(Message m) {
		if(isNotAuthorized(m.getFrom().getId()))
			return;
	}

	@Override
	public void locationMessage(Message m) {
		if(isNotAuthorized(m.getFrom().getId()))
			return;		
	}

	@Override
	public void newChatMemberMessage(Message m) {
		if(isNotAuthorized(m.getFrom().getId()))
			return;
	}

	@Override
	public void newChatMembersMessage(Message m) {
		if(isNotAuthorized(m.getFrom().getId()))
			return;
	}

	@Override
	public void newChatPhotoMessage(Message m) {
		if(isNotAuthorized(m.getFrom().getId()))
			return;
	}

	@Override
	public void newChatTitleMessage(Message m) {
		if(isNotAuthorized(m.getFrom().getId()))
			return;
	}

	@Override
	public void photoMessage(Message m) {
		if(isNotAuthorized(m.getFrom().getId()))
			return;
		MessageToSend mts = new MessageToSend(m.getChat().getId(), Constants.PRESSTOPRINT);
		mts.setReplyToMessageID(m.getMessageID());
		
		List<List<InlineKeyboardButton>> inlKeyboard = new ArrayList<List<InlineKeyboardButton>>();
		List<InlineKeyboardButton> ikbl = new ArrayList<>();
		ikbl = new ArrayList<>();
		
		InlineKeyboardButton inkB = new InlineKeyboardButton(Constants.PRINTPHOTO);
		
		String photoID = m.getPhoto()
		.stream()
		.reduce(m.getPhoto().get(0), (p1,p2) -> {if(p1.getfileSize() > p2.getfileSize()) return p1; else return p2;})
		.getFileID();
		
		inkB.setCallback_data(CallBackCodes.PRINTPHOTO + Constants.CALLBACKSEPARATOR + photoID);
		
		ikbl.add(inkB);
		inlKeyboard.add(ikbl);
		mts.setReplyMarkup(new InlineKeyboardMarkup(inlKeyboard));
		
		sendMessage(mts);
	}

	@Override
	public void pinnedMessage(Message m) {
		if(isNotAuthorized(m.getFrom().getId()))
			return;
	}

	@Override
	public void preCheckOutQueryMessage(PreCheckoutQuery m) {
		if(isNotAuthorized(m.getFrom().getId()))
			return;
		
	}

	@Override
	public void shippingQueryMessage(ShippingQuery m) {
		if(isNotAuthorized(m.getFrom().getId()))
			return;
		
	}

	@Override
	public void stickerMessage(Message m) {
		if(isNotAuthorized(m.getFrom().getId()))
			return;
		System.out.println(m.getSticker().getFileID());	
	}

	@Override
	public void successfulPaymentMessage(Message m) {
		
		
	}

	@Override
	public void textMessage(Message m) {
		if(isNotAuthorized(m.getFrom().getId()))
			return;
		
		if(m.getText().equals(Constants.DEBUG))
		{
			MessageToSend mts = new MessageToSend(m.getChat().getId(), "debug");
			mts.setReplyMarkup(KeyboardUtils.getMinuteKeyboard());
			mts.setParseMode(ParseMode.MARKDOWN);
			sendMessage(mts);
		}
		
		
		if(m.getText().equals(Constants.REBOOT))
		{
			
			oBox.setLightsOff();
			//
			delay();
			myOwnmr.stopExecution();
			
			ProcessBuilder pb = new ProcessBuilder("sudo", "reboot");
			try 
			{
				Process p = pb.start();
				p.waitFor();
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			} 
			catch (InterruptedException e) 
			{
				e.printStackTrace();
			}
			return;
		}
		
		if(m.getText().equals(Constants.SHUTDOWN))
		{
			oBox.setLightsOff();
			//
			delay();
			myOwnmr.stopExecution();
			ProcessBuilder pb = new ProcessBuilder("sudo", "shutdown", "-h", "now");
			try 
			{
				Process p = pb.start();
				p.waitFor();
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			} 
			catch (InterruptedException e) 
			{
				e.printStackTrace();
			}
			return;
		}
		
		if(m.getText().equals(Constants.BACKUP))
		{
			sendDocumentFile(new DocumentFileToSend(m.getFrom().getId(), new File(Constants.SAVEORGANIZERFILE)));
			return;
		}
		
		
		if(m.getText().equals(Constants.SLASHSTART))
		{
			MessageToSend mts = new MessageToSend(m.getChat().getId(), Constants.WELCOMEMESSAGE);
			mts.setReplyMarkup(menuContainer.getMainMenu());
			sendMessage(mts);
			return;
		}
		
		if(m.getText().startsWith(Constants.PRINTCOMMAND))
		{
			Printer.printText(m.getText().substring(Constants.PRINTCOMMAND.length()));
			return;
		}
		
		if(m.getText().equals(Constants.AGENDA))
		{
			showSendAgenda(new LocalDate(), m.getChat().getId());
			return;
		}
		
		if(m.getText().equals(Constants.MYLISTS))
		{
			sendListMessage(m.getChat().getId());
			return;
		}
		
		if(m.getReplyToMessage() != null)
		{
			if(m.getReplyToMessage().getText().equals(Constants.SENDNEWITEM))
			{
				if(!pendingRegister.get(m.getFrom().getId()).getUp().equals(UserPendingRequest.ITEMCREATION))
					return;
				int idList = pendingRegister.get(m.getFrom().getId()).getLastItemListID();
				pendingRegister.get(m.getFrom().getId()).setUp(UserPendingRequest.NONE);
				if(oBox.addItemToList(m.getText(), idList))
				{
					stickerMenuTrick(m.getChat().getId());
					
					MessageToSend mts = new MessageToSend(m.getChat().getId(), oBox.getItemList(idList).toString() + Constants.ITEMMESSAGE);
					mts.setReplyMarkup(KeyboardUtils.ItemListKeyboardFactory(oBox.getItemList(idList)));
					mts.setParseMode(ParseMode.MARKDOWN);
					sendMessage(mts);
				}
				return;
			}
			
			if(m.getReplyToMessage().getText().equals(Constants.SENDMENEWCOLOR))
			{
				if(!pendingRegister.get(m.getFrom().getId()).getUp().equals(UserPendingRequest.ADDCOLOR))
					return;
				pendingRegister.get(m.getFrom().getId()).setUp(UserPendingRequest.NONE);
				String[] values = m.getText().split(" ");
				MessageToSend mts;
				
				try
				{
				short red = Short.parseShort(values[1]);
				short green = Short.parseShort(values[2]);
				short blue = Short.parseShort(values[3]);
			
				if (oBox.addColor(values[0], red, green, blue))
					stickerMenuTrick(m.getChat().getId());
				else
					opsMenuTrick(m.getChat().getId());
				
				}
				catch(Exception e)
				{
					opsMenuTrick(m.getChat().getId());
				}
				
				mts = new MessageToSend(m.getChat().getId(),Constants.MENUCOLOR);
				mts.setParseMode(ParseMode.MARKDOWN);	
				mts.setReplyMarkup(KeyboardUtils.colorsKeyboardFactory(oBox));
				sendMessage(mts);
				return;
			}
			
			if(m.getReplyToMessage().getText().equals(Constants.SENDMECOLORTOREMOVE))
			{
				if(!pendingRegister.get(m.getFrom().getId()).getUp().equals(UserPendingRequest.REMOVECOLOR))
					return;
				
				if(!oBox.removeColor(m.getText()))
					opsMenuTrick(m.getChat().getId());
				else
					stickerMenuTrick(m.getChat().getId());
				return;
			}
			
			
			if(m.getReplyToMessage().getText().equals(Constants.SENDNEWLIST))
			{
				if(!pendingRegister.get(m.getFrom().getId()).getUp().equals(UserPendingRequest.LISTCREATION))
					return;
				pendingRegister.get(m.getFrom().getId()).setUp(UserPendingRequest.NONE);
				if(oBox.addItemList(m.getText()))
				{
					stickerMenuTrick(m.getChat().getId());
					
					sendListMessage(m.getChat().getId());
					return;
				}
				return;
			}
			
			if(m.getReplyToMessage().getText().equals(Constants.SENDNEWLISTNAME))
			{
				if(!pendingRegister.get(m.getFrom().getId()).getUp().equals(UserPendingRequest.RENAMELIST))
					return;
				pendingRegister.get(m.getFrom().getId()).setUp(UserPendingRequest.NONE);
				if(oBox.editListName(m.getText(), pendingRegister.get(m.getFrom().getId()).getLastItemListID()))
				{
					stickerMenuTrick(m.getChat().getId());
					
					sendListMessage(m.getChat().getId());
					return;
				}
				return;
			}
			
			if(m.getReplyToMessage().getText().equals(Constants.SENDNEWITEMNAME))
			{
				if(!pendingRegister.get(m.getFrom().getId()).getUp().equals(UserPendingRequest.EDITITEM))
					return;
				pendingRegister.get(m.getFrom().getId()).setUp(UserPendingRequest.NONE);
				int idList = pendingRegister.get(m.getFrom().getId()).getLastItemListID();
				oBox.editItemNameByID(m.getText(), idList,
						pendingRegister.get(m.getFrom().getId()).getLastItemID());
				
				stickerMenuTrick(m.getChat().getId());

				MessageToSend mts = new MessageToSend(m.getChat().getId(), oBox.getItemList(idList).toString() + Constants.ITEMMESSAGE);
				mts.setReplyMarkup(KeyboardUtils.ItemListKeyboardFactory(oBox.getItemList(idList)));
				mts.setParseMode(ParseMode.MARKDOWN);			
				sendMessage(mts);

				return;
			}
			
			if(m.getReplyToMessage().getText().equals(Constants.WHATWHENTASK))
			{
				
				UserStatus userStatus = pendingRegister.get(m.getFrom().getId());
				
				if(!userStatus.getUp().equals(UserPendingRequest.ADDTASK))
					return;
				userStatus.setUp(UserPendingRequest.NONE);
				
				
				
				String[] values = m.getText().split("/");
				
				if(values == null || values.length != 2)
				{
					sendOPSMessage(userStatus, m.getChat().getId());
					return;
				}
				
				LocalTime lt = Utils.fromStringToTime(values[1]);
				
				if(lt == null)
				{
					sendOPSMessage(userStatus, m.getChat().getId());
					return;
				}
				
				LocalDate day = pendingRegister.get(m.getFrom().getId()).getLastLocalDate();
				
				userStatus.setPendingLocalDateTime(day.toLocalDateTime(lt));
				userStatus.setPendingTaskString(values[0]);
				
				MessageToSend mts = new MessageToSend(m.getChat().getId(), Constants.WANNANOTICETASK);
				mts.setReplyMarkup(KeyboardUtils.yesNoMemoKeyboard());
				mts.setParseMode(ParseMode.MARKDOWN);			
				sendMessage(mts);
				
				return;
			}
			
			
			if(m.getReplyToMessage().getText().equals(Constants.HOWMANYWEEKS))
			{
				
				UserStatus userStatus = pendingRegister.get(m.getFrom().getId());
				
				if(!userStatus.getUp().equals(UserPendingRequest.REPEATASK))
					return;
				userStatus.setUp(UserPendingRequest.NONE);
				
				int weeks = 0;
				
				try
				{
			        weeks = Integer.parseInt(m.getText());
				}
				
				catch(Exception e)
				{
					userStatus.setUp(UserPendingRequest.NONE);
					MessageToSend mts = new MessageToSend(m.getChat().getId(), Constants.OPSWEEK);
					mts.setReplyMarkup(menuContainer.getMainMenu());
					mts.setParseMode(ParseMode.MARKDOWN);
					sendMessage(mts);
					return;
				}
				
				LocalDateTime day = pendingRegister.get(m.getFrom().getId()).getPendingLocalDateTime();
				
				for(int i = 0; i < weeks; i++)
				{
					day = day.plusDays(7);
					oBox.addTask(userStatus.getPendingTaskString(), day.toDateTime(), userStatus.getPendingMemo());
				}

				
				stickerMenuTrick(m.getChat().getId());
				showSendAgenda(userStatus.getLastLocalDate(), m.getChat().getId());
				
				return;
			}
			
			return;
			
		}
		
		if(m.getText().equals(Constants.IPCOMMAND))
		{
			sendMessage(new MessageToSend(m.getChat().getId(), ipAddress));
			return;
		}
		
		if(m.getText().equals(Constants.MANAGELIGHTS))
		{
			MessageToSend mts = new MessageToSend(m.getChat().getId(), Constants.WHATYOUWANT);
			mts.setReplyMarkup(menuContainer.getLightsInlineMenu());
			sendMessage(mts);
			return;
		}
		
		MessageToSend mts = new MessageToSend(m.getChat().getId(), Constants.WELCOMEMESSAGE);
		mts.setParseMode(ParseMode.MARKDOWN);
		mts.setReplyMarkup(menuContainer.getMainMenu());
		sendMessage(mts);
		
	}

	@Override
	public void venueMessage(Message m) {
		if(isNotAuthorized(m.getFrom().getId()))
			return;
	}

	@Override
	public void videoMessage(Message m) {
		if(isNotAuthorized(m.getFrom().getId()))
			return;
		
	}

	@Override
	public void videoNoteMessage(Message m) {
		if(isNotAuthorized(m.getFrom().getId()))
			return;
	}

	@Override
	public void voiceMessage(Message m) {
		if(isNotAuthorized(m.getFrom().getId()))
			return;
		
	}
	
	private boolean isNotAuthorized(Long user)
	{
		if(!authorizedUsers.contains(user))
			return true;
		return false;
	}
	
	
	private void sendListMessage(long recipient)
	{
		List<ItemList> lil = oBox.getAllItemLists(); 
		MessageToSend mts = new MessageToSend(recipient, KeyboardUtils.textListFactory(lil));
		mts.setParseMode(ParseMode.MARKDOWN);
		mts.setReplyMarkup(KeyboardUtils.listKeyboardFactory(lil));
		sendMessage(mts);
	}
	
	private void delay()
	{
		try {
			TimeUnit.MILLISECONDS.sleep(TIMETOSLEEP);
		} catch (InterruptedException e) {
			
			e.printStackTrace();
		}
	}
	

	private void callBackList(ItemList il, long recipient, int messageID)
	{
		EditMessageTextRequest emt = new EditMessageTextRequest(recipient,messageID,
				il.toString() + Constants.ITEMMESSAGE);
		emt.setParse_mode(ParseMode.MARKDOWN);		
		emt.setReply_markup(KeyboardUtils.ItemListKeyboardFactory(il));
		editMessageText(emt);
	}
	
	
	
	private void stickerMenuTrick(long id)
	{
		StickerReferenceToSend srs = new StickerReferenceToSend(id, sc.getRandomStickers());
		srs.setReplyMarkup(menuContainer.getMainMenu());
		sendStickerbyReference(srs);
		delay();
	}
	
	private void opsMenuTrick(long id)
	{
		MessageToSend mts = new MessageToSend(id, Constants.OPS);
		mts.setReplyMarkup(menuContainer.getMainMenu());
		sendMessage(mts);
		//delay();
	}
	
	private void stickerRainbow(long id)
	{
		StickerReferenceToSend srs = new StickerReferenceToSend(id, sc.getRandomRainbowStickers());
		sendStickerbyReference(srs);
		delay();
	}

	
	private void showSendAgenda(LocalDate day, long chatID)
	{
		List<DailyTask> agenda = oBox.getDailyAgenda(day);
		String textMessage = agendaString(agenda, day);
		
		MessageToSend mts = new MessageToSend(chatID, textMessage);
		mts.setReplyMarkup(KeyboardUtils.dailyAgendaKeyboardFactory(agenda, day));
		mts.setParseMode(ParseMode.MARKDOWN);
		sendMessage(mts);
		
		return;
	}
	
	private void showEditCalendar(LocalDate day, long chatID, int messageID)
	{	
		EditMessageTextRequest emt = new EditMessageTextRequest(chatID, messageID,
				Constants.TOUCHDAY);
		emt.setParse_mode(ParseMode.MARKDOWN);
		emt.setReply_markup(KeyboardUtils.getCalendar(day));
		editMessageText(emt);
		
		return;
	}
	
	private void showEditAgenda(LocalDate day, long chatID, int messageID)
	{
		List<DailyTask> agenda = oBox.getDailyAgenda(day);
		String textMessage = agendaString(agenda, day);
		
		
		EditMessageTextRequest emt = new EditMessageTextRequest(chatID, messageID,
				textMessage);
		emt.setParse_mode(ParseMode.MARKDOWN);
		emt.setReply_markup(KeyboardUtils.dailyAgendaKeyboardFactory(agenda, day));
		editMessageText(emt);
		
		return;
	}
	
	
	private String agendaString(List<DailyTask> agenda, LocalDate day)
	{
		String textMessage = "***Agenda del: " + Utils.localDateToString(day) + "***\n\n";
		if(agenda == null)
			textMessage += Constants.NOTASKTODAY;
		else
			textMessage += Utils.dailyAgendaString(agenda);
		return textMessage;
	}
	
	
	private String agendaStringToPrint(List<DailyTask> agenda, LocalDate day)
	{
		String textMessage = "Agenda del: " + Utils.localDateToString(day) + "\n\n";
		if(agenda == null)
			textMessage += Constants.NOTASKTODAY;
		else
			textMessage += Utils.dailyAgendaStringToPrint(agenda);
		return textMessage;
	}
	
	private void sendAnswerCallBackQuery(String callBackID, String text)
	{
		AnswerCallbackQueryToSend acqs = new AnswerCallbackQueryToSend(callBackID);
	    acqs.setText(text);	    
	    answerCallbackQuery(acqs);
	}
	
	
	@Override
	public void routine() {
	

		List<DailyTask> alerts = oBox.checkTodayAgenda();
		
		if(alerts != null)
		{
			for(DailyTask dt : alerts)
			{
				MessageToSend mts = new MessageToSend(masterUser, dt.getTask());
				sendMessage(mts);
				delay();
			}
		
		}
		
	}
	
	
	private void sendOPSMessage(UserStatus userStatus, long chatID)
	{
		userStatus.setUp(UserPendingRequest.NONE);
		MessageToSend mts = new MessageToSend(chatID, Constants.OPSTASK);
		mts.setReplyMarkup(menuContainer.getMainMenu());
		mts.setParseMode(ParseMode.MARKDOWN);
		sendMessage(mts);
		return;
	}
	
	
}
