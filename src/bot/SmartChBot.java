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
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.botticelli.bot.Bot;
import com.botticelli.bot.request.methods.DocumentFileToSend;
import com.botticelli.bot.request.methods.EditMessageTextRequest;
import com.botticelli.bot.request.methods.MessageToSend;
import com.botticelli.bot.request.methods.StickerReferenceToSend;
import com.botticelli.bot.request.methods.types.CallbackQuery;
import com.botticelli.bot.request.methods.types.ChosenInlineResult;
import com.botticelli.bot.request.methods.types.ForceReply;
import com.botticelli.bot.request.methods.types.GsonOwner;
import com.botticelli.bot.request.methods.types.InlineKeyboardButton;
import com.botticelli.bot.request.methods.types.InlineKeyboardMarkup;
import com.botticelli.bot.request.methods.types.InlineQuery;
import com.botticelli.bot.request.methods.types.Message;
import com.botticelli.bot.request.methods.types.ParseMode;
import com.botticelli.bot.request.methods.types.PreCheckoutQuery;
import com.botticelli.bot.request.methods.types.ShippingQuery;
import com.botticelli.messagereceiver.MessageReceiver;

import bot.organizerbox.DailyTask;
import bot.organizerbox.Item;
import bot.organizerbox.ItemList;
import bot.organizerbox.ListableOboxItems;
import bot.organizerbox.OrganizerBox;

public class SmartChBot extends Bot{

	private HashSet<Long> authorizedUsers;
	private OrganizerBox oBox;
	
	private HashMap<Long, UserStatus> pendingRegister;
	private StickersContainer sc;
	private MenuContainer menuContainer;
	public static final int TIMETOSLEEP = 855;
	private String ipAddress = "";
	private MessageReceiver myOwnmr;
	
	public SmartChBot(String token) throws FileNotFoundException, UnknownHostException, SocketException {
		super(token);
		Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
        for (NetworkInterface netint : Collections.list(nets))
        	ipAddress += IpUtility.displayInterfaceInformation(netint);

		sc = new StickersContainer();
		pendingRegister = new HashMap<>();
		
		menuContainer = new MenuContainer();
		
		
		//creating the Set for the auth users
		authorizedUsers = new HashSet<>();
		try (Scanner s = new Scanner(new File(Main.filePath + Constants.AUTHORIZEDUSERS)))
		{
			while (s.hasNext())
			{
				long l = s.nextLong();
				authorizedUsers.add(l);
				if(!pendingRegister.containsKey(l))
				    pendingRegister.put(l, new UserStatus(l));
			}
		}
		
		try (FileInputStream inputStream = new FileInputStream(Main.filePath + Constants.SAVEORGANIZERFILE)) {
			String json = IOUtils.toString(inputStream);
			oBox = GsonOwner.getInstance().getGson().fromJson(json, OrganizerBox.class);
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
		
		
		switch(cbc)
		{
			
		case PRINTLIST:
			Printer.printList(oBox.getItemList(Integer.parseInt(values[1])));
			break;
		case CALLBACKITEM:
			idList = Integer.parseInt(values[1]);
			idItem = Integer.parseInt(values[2]);
			Item i = oBox.getItemList(idList).getByID(idItem);
			emt = new EditMessageTextRequest(c.getMessage().getChat().getId(), c.getMessage().getMessageID(),
					i.toString());
			emt.setParse_mode(ParseMode.MARKDOWN);	
			emt.setReply_markup(itemKeyboardFactory(idList, idItem));
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
					textListFactory(lil));
			emt.setParse_mode(ParseMode.MARKDOWN);		
			emt.setReply_markup(listKeyboardFactory(lil));
			editMessageText(emt);
			break;
			
		case PRINTPHOTO:
			filename = Constants.IMAGESFOLDER + values[1] + ".png";
			Printer.printPhoto(downloadFileFromTelegramServer(values[1], filename));
			break;
			
		case PRINTPANORAMIC:
			filename = Constants.IMAGESFOLDER + values[1] + ".png";
			Printer.printPhoto(downloadFileFromTelegramServer(values[1], filename));
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
			emt.setReply_markup(yesNoKeyboardFactory(listToKill));
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
			emt.setReply_markup(colorsKeyboardFactory());
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
			break;
			
		case PREVIOUSDAY:
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

		System.out.println(m.getText());
		
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
					mts.setReplyMarkup(ItemListKeyboardFactory(oBox.getItemList(idList)));
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
				mts.setReplyMarkup(colorsKeyboardFactory());
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
				mts.setReplyMarkup(ItemListKeyboardFactory(oBox.getItemList(idList)));
				sendMessage(mts);

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
	
	
	private InlineKeyboardMarkup listKeyboardFactory(List<ItemList> all)
	{
		List<ListableOboxItems> listToInput = new ArrayList<>();
		
		for(ItemList il: all)
			listToInput.add(il);
		return inlineListOfListable(listToInput, CallBackCodes.CALLBACKLIST, CallBackCodes.CREATELIST, Constants.CREATELIST);
	
	}
	
	
	
	private InlineKeyboardMarkup dailyAgendaKeyboardFactory(List<DailyTask> dtDayly, LocalDateTime day)
	{
		List<ListableOboxItems> listToInput = new ArrayList<>();
		
		for(DailyTask dt: dtDayly)
			listToInput.add(dt);
		InlineKeyboardMarkup result = inlineListOfListable(listToInput, CallBackCodes.DAILYTASK, 
				CallBackCodes.ADDDAILYTASK, Constants.ADDDAILYTASK);
		
		ArrayList<InlineKeyboardButton> lastLine = new ArrayList<>();
		InlineKeyboardButton button = new InlineKeyboardButton(Constants.PREVIOUSDAY);
		button.setCallback_data(CallBackCodes.PREVIOUSDAY + Constants.CALLBACKSEPARATOR + day.minusDays(1).toString());
		lastLine.add(button);
		
		button = new InlineKeyboardButton(Constants.GOTODAY);
		button.setCallback_data(CallBackCodes.GOTODAY + Constants.CALLBACKSEPARATOR);
		lastLine.add(button);
		
		button = new InlineKeyboardButton(Constants.NEXTDAY);
		button.setCallback_data(CallBackCodes.NEXTDAY + Constants.CALLBACKSEPARATOR + day.plusDays(1).toString());
		lastLine.add(button);
		
		result.AddLine(lastLine);
		
		return result;
	}
	
	
	
	private InlineKeyboardMarkup inlineListOfListable(List<ListableOboxItems> loi, CallBackCodes itemCBC,
			CallBackCodes addCBC, String addString)
	{
		List<List<InlineKeyboardButton>> inlKeyboard = new ArrayList<List<InlineKeyboardButton>>();
		for(int i = 0; i < loi.size(); i++)
		{
			List<InlineKeyboardButton> ikbl = new ArrayList<>();
			InlineKeyboardButton inkB = new InlineKeyboardButton(" " + (i+1));
			inkB.setCallback_data(itemCBC + Constants.CALLBACKSEPARATOR + loi.get(i).getId());
			ikbl.add(inkB);
			inlKeyboard.add(ikbl);
		}
		List<InlineKeyboardButton> ikbl = new ArrayList<>();
		InlineKeyboardButton inkB = new InlineKeyboardButton(addString);
		inkB.setCallback_data(addCBC + Constants.CALLBACKSEPARATOR);
		ikbl.add(inkB);
		inlKeyboard.add(ikbl);
		return new InlineKeyboardMarkup(inlKeyboard);
	}
	
	private String textListFactory(List<ItemList> all)
	{
		String text = "";
		for(int i = 0; i < all.size(); i++)
			text += (i+1) + ") " + all.get(i).getName() + "\n";

		return text += Constants.ALLISTMESSAGE;

	}
	
	
	private InlineKeyboardMarkup ItemListKeyboardFactory(ItemList itli)
	{
		
		List<List<InlineKeyboardButton>> inlKeyboard = new ArrayList<List<InlineKeyboardButton>>();
		for(int i = 0; i<itli.size(); i++)
		{
			List<InlineKeyboardButton> ikbl = new ArrayList<>();
			InlineKeyboardButton inkB = new InlineKeyboardButton(" " + (i+1));
			inkB.setCallback_data(CallBackCodes.CALLBACKITEM + Constants.CALLBACKSEPARATOR + itli.getId() + Constants.CALLBACKSEPARATOR + itli.get(i).getId());
			ikbl.add(inkB);
			inlKeyboard.add(ikbl);
		}
		List<InlineKeyboardButton> ikbl = new ArrayList<>();
		InlineKeyboardButton inkB = new InlineKeyboardButton(Constants.CREATEITEM);
		inkB.setCallback_data(CallBackCodes.CREATEITEM + Constants.CALLBACKSEPARATOR + itli.getId());
		ikbl.add(inkB);
		inlKeyboard.add(ikbl);
		
		ikbl = new ArrayList<>();
		inkB = new InlineKeyboardButton(Constants.PRINTLIST);
		inkB.setCallback_data(CallBackCodes.PRINTLIST + Constants.CALLBACKSEPARATOR + itli.getId());
		
		ikbl.add(inkB);
		
		inkB = new InlineKeyboardButton(Constants.BACKTOLISTMENU);
		inkB.setCallback_data(CallBackCodes.BACKTOMENULIST.toString());
		
		ikbl.add(inkB);
		
		inlKeyboard.add(ikbl);
		ikbl = new ArrayList<>();
		
		inkB = new InlineKeyboardButton(Constants.RENAMELIST);
		inkB.setCallback_data(CallBackCodes.RENAMELIST + Constants.CALLBACKSEPARATOR + itli.getId());
		
		ikbl.add(inkB);
		
		inkB = new InlineKeyboardButton(Constants.KILLLIST);
		inkB.setCallback_data(CallBackCodes.KILLIST + Constants.CALLBACKSEPARATOR + itli.getId());
		
		ikbl.add(inkB);
		inlKeyboard.add(ikbl);
		
		
		return new InlineKeyboardMarkup(inlKeyboard);
		
		
	}
	
	private InlineKeyboardMarkup yesNoKeyboardFactory(ItemList itli)
	{
		List<List<InlineKeyboardButton>> inlKeyboard = new ArrayList<List<InlineKeyboardButton>>();
		List<InlineKeyboardButton> ikbl = new ArrayList<>();
		InlineKeyboardButton inkB = new InlineKeyboardButton(Constants.YESKILL);
		inkB.setCallback_data(CallBackCodes.CONFERMATIONDELETE + Constants.CALLBACKSEPARATOR + itli.getId());
		ikbl.add(inkB);
		inkB = new InlineKeyboardButton(Constants.NOKILL);
		inkB.setCallback_data(CallBackCodes.CALLBACKLIST + Constants.CALLBACKSEPARATOR + itli.getId());
		ikbl.add(inkB);
		inlKeyboard.add(ikbl);
		return new InlineKeyboardMarkup(inlKeyboard);
	}
	

	private void sendListMessage(long recipient)
	{
		List<ItemList> lil = oBox.getAllItemLists(); 
		MessageToSend mts = new MessageToSend(recipient, textListFactory(lil));
		mts.setParseMode(ParseMode.MARKDOWN);
		mts.setReplyMarkup(listKeyboardFactory(lil));
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
	
	private InlineKeyboardMarkup itemKeyboardFactory(int listID, int itemID)
	{
		List<List<InlineKeyboardButton>> inlKeyboard = new ArrayList<List<InlineKeyboardButton>>();
		List<InlineKeyboardButton> ikbl = new ArrayList<>();
		ikbl = new ArrayList<>();
		
		InlineKeyboardButton inkB = new InlineKeyboardButton(Constants.EDITITEM);
		inkB.setCallback_data(CallBackCodes.EDITITEM + Constants.CALLBACKSEPARATOR + listID + Constants.CALLBACKSEPARATOR + itemID);
		
		ikbl.add(inkB);
		
		inkB = new InlineKeyboardButton(Constants.BACKTOLIST);
		inkB.setCallback_data(CallBackCodes.CALLBACKLIST + Constants.CALLBACKSEPARATOR + listID);
	
		ikbl.add(inkB);
		
		inlKeyboard.add(ikbl);
		
		ikbl = new ArrayList<>();
		
		inkB = new InlineKeyboardButton(Constants.KILLITEM);
		inkB.setCallback_data(CallBackCodes.KILLITEM + Constants.CALLBACKSEPARATOR + listID + Constants.CALLBACKSEPARATOR + itemID);
		
		ikbl.add(inkB);
		inlKeyboard.add(ikbl);
		
		
		return new InlineKeyboardMarkup(inlKeyboard);
	}
	
	private void callBackList(ItemList il, long recipient, int messageID)
	{
		EditMessageTextRequest emt = new EditMessageTextRequest(recipient,messageID,
				il.toString() + Constants.ITEMMESSAGE);
		emt.setParse_mode(ParseMode.MARKDOWN);		
		emt.setReply_markup(ItemListKeyboardFactory(il));
		editMessageText(emt);
	}
	
	private InlineKeyboardMarkup colorsKeyboardFactory()
	{		
		List<InlineKeyboardButton> colors = oBox.getColors()
				.stream()
				.map(c -> new InlineKeyboardButton(c))
				.peek(c -> c.setCallback_data(createCallBackData(CallBackCodes.CHANGECOLOR,c.getText())))
				.collect(Collectors.toList());

		List<List<InlineKeyboardButton>> keyboard = new ArrayList<List<InlineKeyboardButton>>();
		List<InlineKeyboardButton> line = new ArrayList<>();
		if (colors.size() > 0) 
		{
			line.add(colors.get(0));
			for (int i = 1; i < colors.size(); i++) 
			{
				if (i % 3 == 0) 
				{
					keyboard.add(line);
					line = new ArrayList<>();
				}
				line.add(colors.get(i));
			}
			keyboard.add(line);
		}
		line = new ArrayList<>();
		InlineKeyboardButton rainbowButton = new InlineKeyboardButton(Constants.RAINBOWMODE);
		InlineKeyboardButton removeButton = new InlineKeyboardButton(Constants.REMOVECOLOR);
		InlineKeyboardButton createButton = new InlineKeyboardButton(Constants.ADDCOLOR);
		
		rainbowButton.setCallback_data(CallBackCodes.RAINBOWMODE.toString());
		removeButton.setCallback_data(CallBackCodes.REMOVECOLOR.toString());
		createButton.setCallback_data(CallBackCodes.ADDCOLOR.toString());
		
		line.add(rainbowButton);
		keyboard.add(line);
		line = new ArrayList<>();
		line.add(createButton);
		line.add(removeButton);
		
		keyboard.add(line);
		
		line = new ArrayList<>();
		InlineKeyboardButton backButton = new InlineKeyboardButton(Constants.BACK);
		backButton.setCallback_data(CallBackCodes.BACKTOCOLORMENU.toString());
		line.add(backButton);
		keyboard.add(line);
		
		return new InlineKeyboardMarkup(keyboard);
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
	
	
	private String createCallBackData(CallBackCodes cbc, String... strings)
	{
		StringBuffer sb = new StringBuffer();
		sb.append(cbc.toString());
		for(String s : strings)
		{
			sb.append(Constants.CALLBACKSEPARATOR);
			sb.append(s);
		}
		return sb.toString();
	}
	
	private void stickerRainbow(long id)
	{
		StickerReferenceToSend srs = new StickerReferenceToSend(id, sc.getRandomRainbowStickers());
		sendStickerbyReference(srs);
		delay();
	}


	@Override
	public void routine() {
	
		
		DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss");
		DateTime dt = formatter.parseDateTime("11/01/1987 11:00:12");
		System.out.println(dt.getYear());
		System.out.println(dt.getDayOfMonth());
		System.out.println(dt.toLocalDate());
		LocalDate today = new LocalDate();
		System.out.println(today);
	}
}
