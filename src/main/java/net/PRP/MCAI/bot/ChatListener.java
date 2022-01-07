package net.PRP.MCAI.bot;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.github.steveice10.mc.protocol.data.game.entity.metadata.Position;
import com.github.steveice10.mc.protocol.data.game.entity.player.Hand;
import com.github.steveice10.mc.protocol.data.game.entity.player.PlayerAction;
import com.github.steveice10.mc.protocol.data.game.world.block.BlockFace;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerActionPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerUseItemPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerChatPacket;
import com.github.steveice10.packetlib.event.session.PacketReceivedEvent;
import com.github.steveice10.packetlib.event.session.SessionAdapter;
import com.github.steveice10.packetlib.packet.Packet;

import net.PRP.MCAI.utils.Actions;
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
			List<String> command = messageToCommand(receiveEvent.getPacket());
			if (command == null || command.size() <= 0) {
				//System.out.println("eto ne komanda");
				return;
			} else {
				if (command.get(0).equalsIgnoreCase("minewood")) {
		    		Actions.mineWood(client, Integer.parseInt(command.get(1)));
				} else if (command.get(0).equalsIgnoreCase("mine")) {
		    		Actions.mine3D(client, Integer.parseInt(command.get(1)), Integer.parseInt(command.get(2)));
				} else if (command.get(0).equalsIgnoreCase("goto")) {
		    		Actions.walkTo(client, new Vector3D(Integer.parseInt(command.get(1)), Integer.parseInt(command.get(2)), Integer.parseInt(command.get(3))));
				} else if (command.get(0).equalsIgnoreCase("gotoRad")) {
					try {
						int rad = Integer.parseInt(command.get(4));
						Actions.walkTo2d(client, new Vector3D(Integer.parseInt(command.get(1))-MathU.rnd(-rad, rad*2), Integer.parseInt(command.get(2)), Integer.parseInt(command.get(3))-MathU.rnd(-rad, rad*2)), false);
					} catch (Exception ДАСУКАМНЕПОЕБАТЬ) {
						int rad = 5;
						Actions.walkTo2d(client, new Vector3D(Integer.parseInt(command.get(1))-MathU.rnd(-rad, rad*2), Integer.parseInt(command.get(2)), Integer.parseInt(command.get(3))-MathU.rnd(-rad, rad*2)), false);
					}
				} else if (command.get(0).equalsIgnoreCase("tellmeid")) {
					Vector3D vec = new Vector3D(Integer.parseInt(command.get(1)), Integer.parseInt(command.get(2)), Integer.parseInt(command.get(3)));
					BotU.chat(client, "id: "+vec.getBlock(client).id);
				} else if (command.get(0).equalsIgnoreCase("come")) {
					UUID uuid = ((ServerChatPacket) receiveEvent.getPacket()).getSenderUuid();
					Entity en = null;
					for (Entity entity : client.getWorld().Entites.values()) {
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
					for (Entity entity : client.getWorld().Entites.values()) {
						if (entity.uuid.toString().equalsIgnoreCase(uuid.toString())) {
							en=entity;
							break;
						}
					}
					if (en == null) return;
					int rad = 5;
					Vector3D aye = VectorUtils.findSafePointInRadius(client, rad);
					//System.out.println(aye);
					Actions.walkTo2d(client, aye, false);
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
				} else if (command.get(0).equalsIgnoreCase("say")) {
					String mesg = "";
					for (String s : command) {
						if (!s.equalsIgnoreCase("say")) {
							mesg += s;
							mesg += " ";
						}
					}
					BotU.chat(client, mesg);
				} else if (command.get(0).equalsIgnoreCase("breaktest")) {
					client.getSession().send(new ClientPlayerUseItemPacket(Hand.MAIN_HAND));
					client.getSession().send(new ClientPlayerActionPacket(PlayerAction.START_DIGGING, new Position(Integer.parseInt(command.get(1)),Integer.parseInt(command.get(2)),Integer.parseInt(command.get(3))), BlockFace.UP));
				} else if (command.get(0).equalsIgnoreCase("setslot")) {
					BotU.SetSlot(client, Integer.parseInt(command.get(1)));
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
	
	public static List<String> messageToCommand(Packet event) {
		try {
			String message = chatMessageToString((ServerChatPacket) event);
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
			//System.out.println(e);
			return null;
		}
	}

}
