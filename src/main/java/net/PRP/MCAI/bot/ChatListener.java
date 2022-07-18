package net.PRP.MCAI.bot;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import com.github.steveice10.mc.protocol.data.game.MessageType;
import com.github.steveice10.mc.protocol.data.game.PlayerListEntry;
import com.github.steveice10.mc.protocol.data.game.entity.metadata.Position;
import com.github.steveice10.mc.protocol.data.game.entity.player.Hand;
import com.github.steveice10.mc.protocol.data.game.entity.player.PlayerAction;
import com.github.steveice10.mc.protocol.data.game.world.block.BlockFace;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerActionPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerPlaceBlockPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerSwingArmPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerUseItemPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerChatPacket;
import com.github.steveice10.packetlib.event.session.PacketReceivedEvent;
import com.github.steveice10.packetlib.event.session.SessionAdapter;
import com.github.steveice10.packetlib.packet.Packet;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import net.PRP.MCAI.Main;
import net.PRP.MCAI.bot.specific.Living.raidState;
import net.PRP.MCAI.bot.specific.Miner.bbmct;
import net.PRP.MCAI.data.Block;
import net.PRP.MCAI.data.Entity;
import net.PRP.MCAI.data.MinecraftData.Type;
import net.PRP.MCAI.data.Vector3D;
import net.PRP.MCAI.utils.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.minecraft.server.v1_12_R1.Slot;

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
			
			
			
			for (Component cld:((ServerChatPacket)receiveEvent.getPacket()).getMessage().children()) {
				
			}
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
					BotU.chat(client, "state-id: "+vec.getBlock(client).state);
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
					client.rl.tasklist.add("come "+en.Position.forCommand());
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
					if (uuid.equals(client.getUUID())) return;
					Entity en = null;
					for (Entity entity : client.getWorld().Entites.values()) {
						if (entity.uuid.toString().equalsIgnoreCase(uuid.toString())) {
							en=entity;
							break;
						}
					}
					if (en == null) return;
					client.pvp.pvp(en.EntityID);
					client.rl.state = raidState.PVP;
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
				} else if (command.get(0).equalsIgnoreCase("forward")) {
					double d = 1;
					client.setposto(client.getPosition().add(VectorUtils.vector(client.getYaw(), client.getPitch(), d,client)));
					ThreadU.sleep(200);
					client.setposto(client.getPosition().add(VectorUtils.vector(client.getYaw(), client.getPitch(), d,client)));
					ThreadU.sleep(200);
					client.setposto(client.getPosition().add(VectorUtils.vector(client.getYaw(), client.getPitch(), d,client)));
					ThreadU.sleep(200);
					client.setposto(client.getPosition().add(VectorUtils.vector(client.getYaw(), client.getPitch(), d,client)));
					ThreadU.sleep(200);
					client.setposto(client.getPosition().add(VectorUtils.vector(client.getYaw(), client.getPitch(), d,client)));
					ThreadU.sleep(200);
					
				} else if (command.get(0).equalsIgnoreCase("jump")) {
					client.pm.jump();
				} else if (command.get(0).equalsIgnoreCase("isliquid")) {
					Vector3D aa = new Vector3D(Integer.parseInt(command.get(1)),Integer.parseInt(command.get(2)),Integer.parseInt(command.get(3)));
					BotU.chat(client, ">"+aa.getBlock(client).isLiquid());
				} else if (command.get(0).equalsIgnoreCase("hbc")) {
					for (Vector3D n : client.getHitbox().getCorners()) {
						BotU.chat(client, n.toString());
					}
				} else if (command.get(0).equalsIgnoreCase("jn")) {
					if (client.name.contains(command.get(1))) client.pm.jump();
				} else if (command.get(0).equalsIgnoreCase("dn")) {
					if (client.name.contains(command.get(1))) client.getSession().disconnect("disconected");
				} else if (command.get(0).equalsIgnoreCase("mine")) {
					Vector3D block;
					block = VectorUtils.findBlockByName(client, command.get(1), client.rl.blacklist);
					if (block == null) {
					} else {
						if (block.getBlock(client).touchLiquid(client)) {
							return;
						}
						if (VectorUtils.sqrt(client.getEyeLocation(), block) <= (int)Main.gamerule("maxpostoblock")) {//блок довольно близко
							
							if (VectorUtils.sqrt(client.getEyeLocation(), block) <= 2.2) {
								client.bbm.setup(block);
								return;
							}
							Vector3D pos = VectorUtils.func_31(client, block, (int)Main.gamerule("maxpostoblock"));
							if (pos != null) {//к нему можно приблизиться
								client.pathfinder.setup(pos);
							} else {
								client.bbm.setup(block);
							}
							return;
					    } else {
					    	Vector3D pos = VectorUtils.func_31(client, block, (int)Main.gamerule("maxpostoblock"));
					    	if (pos == null) {
					    		return;
					    	}
					    	if (!client.pathfinder.testForPath(pos)) {
					    		return;
					    	}
					    	client.pathfinder.setup(pos);
					    	return;
					    }
					}
				} else if (command.get(0).equalsIgnoreCase("tellmetype")) {
					Vector3D vec = new Vector3D(Integer.parseInt(command.get(1)), Integer.parseInt(command.get(2)), Integer.parseInt(command.get(3)));
					BotU.chat(client, vec.getBlock(client).type.toString());
				} else if (command.get(0).equalsIgnoreCase("demolit")) {
					Vector3D min = new Vector3D(Integer.parseInt(command.get(1)), Integer.parseInt(command.get(2)), Integer.parseInt(command.get(3)));
					Vector3D max = new Vector3D(Integer.parseInt(command.get(4)), Integer.parseInt(command.get(5)), Integer.parseInt(command.get(6)));
					List<Vector3D> temp = new ArrayList<>();
					int i = 0;
					for (int y = (int) Math.max(min.y, max.y); y >= Math.min(min.y, max.y); y--) {
						for (int x = (int) Math.min(min.x, max.x); x <= Math.max(min.x, max.x); x++) {
							for (int z = (int) Math.min(min.z, max.z); z <= Math.max(min.z, max.z); z++) {
								if (client.getWorld().getBlock(x, y, z).type != Type.AIR)temp.add(new Vector3D(x,y,z));
							}
						}
						while (!temp.isEmpty()) {
							CopyOnWriteArrayList<Vector3D> a = new CopyOnWriteArrayList<>();
							a.addAll(temp);
							if (!Main.tomine.contains(a)) Main.tomine.add(a);
							temp.clear();
						}
					}
					
				} else if (command.get(0).equalsIgnoreCase("gimme")) {
					UUID uuid = ((ServerChatPacket) receiveEvent.getPacket()).getSenderUuid();
					Entity en = null;
					for (Entity entity : client.getWorld().Entites.values()) {
						if (entity.uuid.toString().equalsIgnoreCase(uuid.toString())) {
							en=entity;
							break;
						}
					}
					if (en == null) return;
					Vector3D pos = VectorUtils.randomPointInRaduis(client, 2,2,(int)en.Position.x,(int)en.Position.z);
					if (client.pathfinder.testForPath(pos)) {
						client.rl.tasklist.add("come "+(int)en.Position.x+" "+(int)en.Position.y+" "+(int)en.Position.z);
						client.rl.tasklist.add("faceto "+(int)en.Position.x+" "+((int)en.Position.y++) +" "+(int)en.Position.z);
						client.rl.tasklist.add("dropitemstack "+command.get(1));
					}
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
					Vector3D pos = VectorUtils.randomPointInRaduis(client, Integer.parseInt(command.get(1)),Integer.parseInt(command.get(2)),(int)en.Position.x,(int)en.Position.z);
					client.rl.tasklist.add("come "+(int)pos.x+" "+(int)pos.y+" "+(int)pos.z);
				} else if (command.get(0).equalsIgnoreCase("printentities")) {
					System.out.println(client.name+" "+client.getWorld().Entites.toString());
				} else if (command.get(0).equalsIgnoreCase("printtab")) {
					for (PlayerListEntry player : client.getWorld().ServerTabPanel) {
						System.out.println(((TextComponent)player.getDisplayName()).content());
					}
				} else if (command.get(0).equalsIgnoreCase("useitem")) {
					client.getSession().send(new ClientPlayerUseItemPacket(Hand.OFF_HAND));
				} else if (command.get(0).equalsIgnoreCase("shield")) {
					int shieldslot = client.playerInventory.getSlotWithItem("shield");
					if (shieldslot != 45) {
						client.crafter.fromSlotToSlotStack(shieldslot, 45);
					}
				} else if (command.get(0).equalsIgnoreCase("t1")) {
					client.getSession().send(new ClientPlayerActionPacket(PlayerAction.RELEASE_USE_ITEM, new Position(0,0,0),BlockFace.UP));
				} else if (command.get(0).equalsIgnoreCase("youid")) {
					BotU.chat(client, "myid: "+client.getId());
				}
			}
		} 
	}
	
	public static String chatMessageToString(ServerChatPacket packet) {
		//System.out.println(packet.getMessage().toString());
		StringBuilder message = new StringBuilder();
		if (packet.getMessage() instanceof TranslatableComponent) {
			TranslatableComponent a = (TranslatableComponent)packet.getMessage();
			for (Component arg : a.args()) {
				if (arg instanceof TextComponent) {
					message.append(" ").append(((TextComponent)arg).content());
				}
			}
		} else {
			System.out.println(packet.getMessage());
		}
		System.out.println(message.toString());
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
