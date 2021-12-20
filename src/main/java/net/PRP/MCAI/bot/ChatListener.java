package net.PRP.MCAI.bot;

import java.util.ArrayList;
import java.util.List;

import com.github.steveice10.mc.protocol.data.message.Message;
import com.github.steveice10.mc.protocol.data.message.TranslationMessage;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerChatPacket;
import com.github.steveice10.packetlib.event.session.PacketReceivedEvent;
import com.github.steveice10.packetlib.event.session.SessionAdapter;

import net.PRP.MCAI.utils.Actions;

public class ChatListener extends SessionAdapter {

	private Bot client;
	
	public ChatListener(Bot client) {
		this.client = client;
	}
	
	@Override
    public void packetReceived(PacketReceivedEvent receiveEvent) {
		if (receiveEvent.getPacket() instanceof ServerChatPacket) {
			List<String> command = messageToCommand(receiveEvent);
			//System.out.println(command.size());
			if (command.size() <= 0) return;
			if (command.get(0).equalsIgnoreCase("minewood")) {
	    		Actions.mineWood(client, Integer.parseInt(command.get(1)));
			}
		}
	}
	
	public static List<String> messageToCommand(PacketReceivedEvent receiveEvent) {
		String message;
		if(((ServerChatPacket) receiveEvent.getPacket()).getMessage() instanceof TranslationMessage){
    		TranslationMessage tm = (TranslationMessage) ((ServerChatPacket) receiveEvent.getPacket()).getMessage();
    		String mess = "";
    		for(Message m : tm.getTranslationParams()){
    			mess = mess + " " + m.getFullText();
    		}
    		message = mess;
    	} else {
    		message = (((ServerChatPacket) receiveEvent.getPacket()).getMessage().getFullText());
    	}
		boolean sw = false;
		List<String> cmd = new ArrayList<String>();
		for (String piece : message.split(" ")) {
			if (piece.startsWith(">>")) {
				sw = true;
				piece = piece.replace(">>", "");
			}
			if (sw) {
				cmd.add(piece);
			}
		}
		return cmd;
	}

}
