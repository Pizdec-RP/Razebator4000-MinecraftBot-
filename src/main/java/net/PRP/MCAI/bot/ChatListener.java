package net.PRP.MCAI.bot;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.github.steveice10.mc.protocol.packet.ingame.server.ServerChatPacket;
import com.github.steveice10.packetlib.event.session.PacketReceivedEvent;
import com.github.steveice10.packetlib.event.session.SessionAdapter;
import com.github.steveice10.packetlib.packet.Packet;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import net.PRP.MCAI.bot.BlockBreakManager.bbmct;
import net.PRP.MCAI.data.Entity;
import net.PRP.MCAI.utils.*;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;

public class ChatListener extends SessionAdapter {

	private Bot client;
	
	public ChatListener(Bot client) {
		this.client = client;
	}
	
	@Override
    public void packetReceived(PacketReceivedEvent receiveEvent) {
		if (receiveEvent.getPacket() instanceof ServerChatPacket) {
			//System.out.println(((ServerChatPacket)receiveEvent.getPacket()).getMessage());
			new Thread(()->{
				List<String> command = messageToCommand(receiveEvent.getPacket());
				if (command == null || command.size() <= 0) {
					//System.out.println("eto ne komanda");
					return;
				} else {
					/*if (command.get(0).equalsIgnoreCase("minewood")) {
			    		Actions.mineWood(client, Integer.parseInt(command.get(1)));
					} else if (command.get(0).equalsIgnoreCase("mine")) {
			    		Actions.mine3D(client, Integer.parseInt(command.get(1)), Integer.parseInt(command.get(2)));
					} else if (command.get(0).equalsIgnoreCase("goto")) {
			    		Actions.walkTo(client, new Vector3D(Integer.parseInt(command.get(1)), Integer.parseInt(command.get(2)), Integer.parseInt(command.get(3))));
					} else if (command.get(0).equalsIgnoreCase("gotoRad")) {
						try {
							int rad = Integer.parseInt(command.get(4));
							Actions.walkTo2d(client, new Vector3D(Integer.parseInt(command.get(1))-MathU.rnd(-rad, rad*2), Integer.parseInt(command.get(2)), Integer.parseInt(command.get(3))-MathU.rnd(-rad, rad*2)), false);
						} catch (Exception ignored) {
							int rad = 5;
							Actions.walkTo2d(client, new Vector3D(Integer.parseInt(command.get(1))-MathU.rnd(-rad, rad*2), Integer.parseInt(command.get(2)), Integer.parseInt(command.get(3))-MathU.rnd(-rad, rad*2)), false);
						}
					} else */if (command.get(0).equalsIgnoreCase("tellmeid")) {
						Vector3D vec = new Vector3D(Integer.parseInt(command.get(1)), Integer.parseInt(command.get(2)), Integer.parseInt(command.get(3)));
						BotU.chat(client, "id: "+vec.getBlock(client).id);
					} else if (command.get(0).equalsIgnoreCase("tellmestate")) {
						Vector3D vec = new Vector3D(Integer.parseInt(command.get(1)), Integer.parseInt(command.get(2)), Integer.parseInt(command.get(3)));
						BotU.chat(client, "id: "+vec.getBlock(client).state);
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
						client.pathfinder.setup(client.getPosition(), en.Position);
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
						int rad;
						try {rad = Integer.parseInt(command.get(1));} catch (Exception ignd) {rad = 5;}
						Vector3D aye = VectorUtils.findSafePointInRadius(en.Position, rad);
						//System.out.println(aye);
						client.pathfinder.setup(aye);
					} else if (command.get(0).equalsIgnoreCase("record")) {
						if (!client.entityListener.isRecordpos()) {
							System.out.println("-----------------------");
							UUID uuid = ((ServerChatPacket) receiveEvent.getPacket()).getSenderUuid();
							client.entityListener.setRecorduuid(uuid);
							client.entityListener.setRecordpos(true);
							BotU.chat(client, "отслеживаю "+uuid);
						} else {
							client.entityListener.setRecordpos(false);
							BotU.chat(client, "больше не отслеживаю");
							System.out.println("-----------------------");
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
						Vector3D pos12 = new Vector3D(Integer.parseInt(command.get(1)),Integer.parseInt(command.get(2)),Integer.parseInt(command.get(3)));
						client.bbm.setBlockPos(pos12);
						client.bbm.state = bbmct.STARTED;
						client.bbm.setReadyToBreak(true);
					} else if (command.get(0).equalsIgnoreCase("setslot")) {
						BotU.SetSlot(client, Integer.parseInt(command.get(1)));
					} else if (command.get(0).equalsIgnoreCase("youpos")) {
						BotU.chat(client, client.getPosition().toString());
					} else if (command.get(0).equalsIgnoreCase("settoitem")) {
						BotU.chat(client, client.setToSlotInHotbarWithItemId(Integer.parseInt(command.get(1))).toString());
					} else if (command.get(0).equalsIgnoreCase("calcticks")) {
						Vector3D pos12 = new Vector3D(Integer.parseInt(command.get(1)),Integer.parseInt(command.get(2)),Integer.parseInt(command.get(3)));
						client.bbm.setBlockPos(pos12);
						BotU.chat(client, "bt: "+client.bbm.calculateBreakTime());
					} else if (command.get(0).equalsIgnoreCase("mypos")) {
						UUID uuid = ((ServerChatPacket) receiveEvent.getPacket()).getSenderUuid();
						Entity en = null;
						for (Entity entity : client.getWorld().Entites.values()) {
							if (entity.uuid.toString().equalsIgnoreCase(uuid.toString())) {
								en=entity;
								break;
							}
						}
						if (en == null) return;
						BotU.chat(client, en.Position.toStringInt());
					}
				}
			}).start();
		} 
	}
	
	public static String chatMessageToString(ServerChatPacket packet) {
		//System.out.println(packet.getMessage().toString());
		if (packet.getMessage() instanceof TextComponent)  {
			JsonElement asds = GsonComponentSerializer.gson().serializeToTree((TextComponent)packet.getMessage());
			String as = "";
			if (asds.getAsJsonObject().get("content").getAsString() == "" && asds.getAsJsonObject().get("content").getAsString() == null) {
				//asds.getAsJsonObject().get("children").getAsJsonArray()
			} else {
				for (JsonElement asdsa : asds.getAsJsonObject().get("extra").getAsJsonArray()) {
					as += asdsa.getAsJsonObject().get("text");
					as += " ";
				}
			}
			as = as.replace("\"", "");
			return as;
		} else {
			JsonElement json = GsonComponentSerializer.gson().serializeToTree(packet.getMessage());
		    if (json.getAsJsonObject().get("with") == null) return null;
			JsonArray asd = json.getAsJsonObject().get("with").getAsJsonArray();
			if (asd.size() == 2) {
				JsonElement asdasd = asd.get(1);
				if (asdasd.getAsJsonObject().get("text") != null) return asdasd.getAsJsonObject().get("text").getAsString();
			}
			return "";
		}
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
