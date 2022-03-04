package net.PRP.MCAI.bot;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.github.steveice10.mc.protocol.data.game.entity.player.Hand;
import com.github.steveice10.mc.protocol.data.game.world.block.BlockFace;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerPlaceBlockPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerChatPacket;
import com.github.steveice10.packetlib.event.session.PacketReceivedEvent;
import com.github.steveice10.packetlib.event.session.SessionAdapter;
import com.github.steveice10.packetlib.packet.Packet;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import net.PRP.MCAI.bot.specific.BlockBreakManager.bbmct;
import net.PRP.MCAI.data.Block;
import net.PRP.MCAI.data.Entity;
import net.PRP.MCAI.data.Vector3D;
import net.PRP.MCAI.utils.*;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;

public class ChatListener extends SessionAdapter {

	private Bot client;
	
	public ChatListener(Bot client) {
		this.client = client;
	}
	
	@SuppressWarnings("deprecation")
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
						client.pathfinder.setup(en.Position);
					} else if (command.get(0).equalsIgnoreCase("goto")) {
						client.pathfinder.setup(new Vector3D(Integer.parseInt(command.get(1)), Integer.parseInt(command.get(2)), Integer.parseInt(command.get(3))));
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
						BotU.chat(client, client.getPosition().toString()+" onGround:"+client.onGround);
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
					} else if (command.get(0).equalsIgnoreCase("gfb")) {
						for (Block b : client.vis.getVisibleBlocks()) {
							System.out.print(b.getId()+" ");
						}
						System.out.println(" vse");
					} else if (command.get(0).equalsIgnoreCase("youface")) {
						BotU.chat(client, "y:"+client.getYaw()+" p:"+client.getPitch());
					} else if (command.get(0).equalsIgnoreCase("aroundme")) {
						if (client.ztp) {
							client.ztp = false;
						} else {
							UUID uuid = ((ServerChatPacket) receiveEvent.getPacket()).getSenderUuid();
							Entity en = null;
							for (Entity entity : client.getWorld().Entites.values()) {
								if (entity.uuid.toString().equalsIgnoreCase(uuid.toString())) {
									en=entity;
									break;
								}
							}
							if (en == null) return;
							client.targetpos = en.Position;
							client.ztp = true;
						}
					} else if (command.get(0).equalsIgnoreCase("living")) {
						if (client.rl.trusted) {
							client.rl.trusted = false;
						} else {
							client.rl.trusted = true;
						}
					} else if (command.get(0).equalsIgnoreCase("tt")) {
						Vector3D pos = new Vector3D(Double.parseDouble(command.get(1)),Double.parseDouble(command.get(2)),Double.parseDouble(command.get(3)));
						if (pos.x < 0) {
							pos.x = pos.x+1;
						}
						if (pos.z < 0) {
							pos.z = pos.z+1;
						}
						int bx = (int)pos.getX() & 15;
			            int by = (int)pos.getY() & 15;
			            int bz = (int)pos.getZ() & 15;
			            int cx = (int)pos.getX() >> 4;
			            int cy = (int)pos.getY() >> 4;
			            int cz = (int)pos.getZ() >> 4;
			            BotU.chat(client, "block: x:"+bx+" y:"+by+" z:"+bz+" / chunk: x:"+cx+" y:"+cy+" z:"+cz);
					} else if (command.get(0).equalsIgnoreCase("killme")) {
						UUID uuid = ((ServerChatPacket) receiveEvent.getPacket()).getSenderUuid();
						Entity en = null;
						for (Entity entity : client.getWorld().Entites.values()) {
							if (entity.uuid.toString().equalsIgnoreCase(uuid.toString())) {
								en=entity;
								break;
							}
						}
						if (en == null) return;
						client.rl.enemy = en.EntityID;
					} else if (command.get(0).equalsIgnoreCase("isavoid")) {
						BotU.chat(client, VectorUtils.BTavoid(new Vector3D(Integer.parseInt(command.get(1)),Integer.parseInt(command.get(2)),Integer.parseInt(command.get(3))).getBlock(client).type)+"");
					} else if (command.get(0).equalsIgnoreCase("moveinvtest")) {
						client.crafter.fromSlotToSlot(36,37);
					} else if (command.get(0).equalsIgnoreCase("craft")) {
						client.crafter.setup(command.get(1), null);
					} else if (command.get(0).equalsIgnoreCase("rndwalk")) {
						client.pathfinder.setup(VectorUtils.randomPointInRaduis(client, 25));
					} else if (command.get(0).equalsIgnoreCase("facing")) {
						BotU.LookHead(client, new Vector3D(Integer.parseInt(command.get(1)),Integer.parseInt(command.get(2)),Integer.parseInt(command.get(3))));
					} else if (command.get(0).equalsIgnoreCase("placetest")) {
						Vector3D aa = new Vector3D(Integer.parseInt(command.get(1)),Integer.parseInt(command.get(2)),Integer.parseInt(command.get(3)));
						client.getSession().send(new ClientPlayerPlaceBlockPacket(aa.translate(), BlockFace.DOWN, Hand.MAIN_HAND, 0,0,0, false));
					} else if (command.get(0).equalsIgnoreCase("place")) {
						VectorUtils.placeBlockNear(client, command.get(1));
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
