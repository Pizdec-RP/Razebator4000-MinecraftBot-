package net.PRP.MCAI.bot;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.github.steveice10.mc.protocol.packet.ingame.server.ServerChatPacket;
import com.github.steveice10.packetlib.event.session.PacketReceivedEvent;
import com.github.steveice10.packetlib.event.session.SessionAdapter;

import net.PRP.MCAI.utils.Actions;
import net.PRP.MCAI.Main;
import net.PRP.MCAI.utils.*;
import net.PRP.MCAI.utils.Vector3D;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TranslatableComponent;
import world.Entity;

public class ChatListener extends SessionAdapter {

	private Bot client;
	
	public ChatListener(Bot client) {
		this.client = client;
	}
	
	@Override
    public void packetReceived(PacketReceivedEvent receiveEvent) {
		if (receiveEvent.getPacket() instanceof ServerChatPacket) {
			List<String> command = messageToCommand(receiveEvent);
			if (command == null || command.size() <= 0) {
				return;
			} else {
				if (command.get(0).equalsIgnoreCase("minewood")) {
		    		Actions.mineWood(client, Integer.parseInt(command.get(1)));
				} else if (command.get(0).equalsIgnoreCase("goto")) {
		    		Actions.walkTo(client, new Vector3D(Integer.parseInt(command.get(1)), Integer.parseInt(command.get(2)), Integer.parseInt(command.get(3))));
				} else if (command.get(0).equalsIgnoreCase("gotoRad")) {
					try {
						int rad = Integer.parseInt(command.get(4));
						Actions.walkTo2d(client, new Vector3D(Integer.parseInt(command.get(1))-MathU.rnd(-rad, rad*2), Integer.parseInt(command.get(2)), Integer.parseInt(command.get(3))-MathU.rnd(-rad, rad*2)));
					} catch (Exception ДАСУКАМНЕПОЕБАТЬ) {
						int rad = 5;
						Actions.walkTo2d(client, new Vector3D(Integer.parseInt(command.get(1))-MathU.rnd(-rad, rad*2), Integer.parseInt(command.get(2)), Integer.parseInt(command.get(3))-MathU.rnd(-rad, rad*2)));
					}
				} else if (command.get(0).equalsIgnoreCase("tellmeid")) {
					Vector3D vec = new Vector3D(Integer.parseInt(command.get(1)), Integer.parseInt(command.get(2)), Integer.parseInt(command.get(3)));
					BotU.chat(client, "id: "+vec.getBlock().id);
				} else if (command.get(0).equalsIgnoreCase("come")) {
					UUID uuid = ((ServerChatPacket) receiveEvent.getPacket()).getSenderUuid();
					Entity en = null;
					for (Entity entity : Main.getWorld().Entites.values()) {
						if (entity.uuid.toString().equalsIgnoreCase(uuid.toString())) {
							en=entity;
							break;
						}
					}
					if (en == null) return;
					Actions.walkTo(client, en.Position.VecToInt());
				} else if (command.get(0).equalsIgnoreCase("cr")) {
					UUID uuid = ((ServerChatPacket) receiveEvent.getPacket()).getSenderUuid();
					Entity en = null;
					for (Entity entity : Main.getWorld().Entites.values()) {
						if (entity.uuid.toString().equalsIgnoreCase(uuid.toString())) {
							en=entity;
							break;
						}
					}
					if (en == null) return;
					int rad = 5;
					Vector3D aye = en.Position.VecToInt();
					aye = aye.add(MathU.rnd(-rad, rad*2), 0, MathU.rnd(-rad, rad*2));
					Actions.walkTo2d(client, aye);
				} else if (command.get(0).equalsIgnoreCase("record")) {
					if (!client.entityListener.isRecordpos()) {
						UUID uuid = ((ServerChatPacket) receiveEvent.getPacket()).getSenderUuid();
						client.entityListener.setRecorduuid(uuid);
						client.entityListener.setRecordpos(true);
						BotU.chat(client, "отслеживаю "+uuid);
					} else {
						client.entityListener.setRecordpos(false);
						BotU.chat(client, "больше не отслеживаю");
					}
				}
			}
		}
	}
	
	public static String chatMessageToString(ServerChatPacket packet) {
	    StringBuilder message = new StringBuilder();
	    ((TranslatableComponent) packet.getMessage()).args().forEach((component) -> {
	        if (component instanceof TextComponent)
	            message.append(String.format(message.length() == 0 ? "<%s>" : " %s", ((TextComponent) component).content()));
	    });
	    return message.toString();
	}
	
	public static List<String> messageToCommand(PacketReceivedEvent event) {
		try {
			String message = chatMessageToString((ServerChatPacket) event.getPacket());
			//System.out.println(message);
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
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
