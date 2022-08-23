package net.PRP.MCAI.TestServer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import com.github.steveice10.mc.auth.data.GameProfile;
import com.github.steveice10.mc.protocol.data.game.ClientRequest;
import com.github.steveice10.mc.protocol.data.game.chunk.Column;
import com.github.steveice10.mc.protocol.data.game.chunk.NibbleArray3d;
import com.github.steveice10.mc.protocol.data.game.entity.metadata.ItemStack;
import com.github.steveice10.mc.protocol.data.game.entity.player.GameMode;
import com.github.steveice10.mc.protocol.data.game.entity.player.Hand;
import com.github.steveice10.mc.protocol.data.game.entity.player.PlayerAction;
import com.github.steveice10.mc.protocol.data.game.entity.player.PositionElement;
import com.github.steveice10.mc.protocol.data.game.recipe.Recipe;
import com.github.steveice10.mc.protocol.data.game.setting.Difficulty;
import com.github.steveice10.mc.protocol.data.game.world.block.BlockChangeRecord;
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientChatPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientPluginMessagePacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientRequestPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerActionPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerChangeHeldItemPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerPlaceBlockPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerPositionPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerPositionRotationPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerRotationPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerSwingArmPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.window.ClientCreativeInventoryActionPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerChatPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerDeclareRecipesPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerDifficultyPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerJoinGamePacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerPluginMessagePacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerUnlockRecipesPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.player.ServerPlayerAbilitiesPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.player.ServerPlayerChangeHeldItemPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.player.ServerPlayerHealthPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.player.ServerPlayerPositionRotationPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.window.ServerWindowItemsPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.world.ServerBlockChangePacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.world.ServerChunkDataPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.world.ServerSpawnPositionPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.world.ServerUpdateViewPositionPacket;
import com.github.steveice10.mc.protocol.packet.login.client.LoginStartPacket;
import com.github.steveice10.packetlib.Session;
import com.github.steveice10.packetlib.event.session.DisconnectedEvent;
import com.github.steveice10.packetlib.event.session.PacketReceivedEvent;
import com.github.steveice10.packetlib.event.session.SessionAdapter;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.PRP.MCAI.Main;
import net.PRP.MCAI.Multiworld;
import net.PRP.MCAI.data.Block;
import net.PRP.MCAI.data.BlockData;
import net.PRP.MCAI.data.ChunkCoordinates;
import net.PRP.MCAI.data.ItemData;
import net.PRP.MCAI.data.Vector3D;
import net.PRP.MCAI.data.oldMinecraftBlocks;
import net.PRP.MCAI.utils.BotU;
import net.PRP.MCAI.utils.VectorUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

public class ClientSession extends SessionAdapter {
	public GameProfile profile;
	public Session session;
	public int entityId;
	public GameMode gm;
	private int tpid = 0;
	public Vector3D pos;
	public float pitch, yaw;
	public int health;
	public int food;
	public Map<Integer, ItemStack> inventory;
	private st state = st.not;
	private int sleepticks = 0;
	private int slot = 0;
	
	private enum st {
		not, lsp, succ
	}
	
	public ClientSession() {
		
	}
	
	@Override
	public void packetReceived(PacketReceivedEvent event) {
		//BotU.log(event.getPacket().getClass().getName());
		if (event.getPacket() instanceof LoginStartPacket) {
			if (profile == null) {
				LoginStartPacket p = event.getPacket();
				profile = new GameProfile(UUID.randomUUID(), p.getUsername());
				BotU.log(profile.getName());
			}
			double[] ppos = Server.getpos(profile.getName());
			pos = new Vector3D(ppos[0],ppos[1],ppos[2]);
			pitch=(float)ppos[3];
			yaw=(float)ppos[4];
			health = Server.getPlayerObject(profile.getName()).get("health").getAsInt();
			food = Server.getPlayerObject(profile.getName()).get("food").getAsInt();
			Server.players.add(this);
			state = st.lsp;
			sleepticks = 40;
		} else if (event.getPacket() instanceof ClientPluginMessagePacket) {
			ClientPluginMessagePacket p = (ClientPluginMessagePacket)event.getPacket();
			event.getSession().send(new ServerPluginMessagePacket(p.getChannel(), p.getData()));
		} else if (event.getPacket() instanceof ClientPlayerPositionRotationPacket) {
        	ClientPlayerPositionRotationPacket p = (ClientPlayerPositionRotationPacket) event.getPacket();
        	if ((int)Math.floor(p.getX()) >> 4 > Server.Maxcolumn
        			||
        			(int)Math.floor(p.getZ()) >> 4 > Server.Maxcolumn
        			||
        			(int)Math.floor(p.getX()) >> 4 < Server.Mincolumn
        			||
        			(int)Math.floor(p.getZ()) >> 4 < Server.Mincolumn) {
        		session.send(new ServerPlayerPositionRotationPacket(pos.x,pos.y,pos.z,yaw,pitch,tpid++,new ArrayList<PositionElement>()));
        		session.send(new ServerChatPacket(Component.text("нельзя выходить за зону!").color(NamedTextColor.RED)));
        	} else {
	        	this.pos = new Vector3D(p.getX(),p.getY(),p.getZ());
	        	this.yaw = p.getYaw();
	        	this.pitch = p.getPitch();
        	}
        	
        } else if (event.getPacket() instanceof ClientPlayerRotationPacket) {
        	ClientPlayerRotationPacket p = (ClientPlayerRotationPacket) event.getPacket();
        	this.yaw = p.getYaw();
        	this.pitch = p.getPitch();
        	
        } else if (event.getPacket() instanceof ClientPlayerPositionPacket) {
        	ClientPlayerPositionPacket p = (ClientPlayerPositionPacket) event.getPacket();
        	if ((int)Math.floor(p.getX()) >> 4 > Server.Maxcolumn
        			||
        			(int)Math.floor(p.getZ()) >> 4 > Server.Maxcolumn
        			||
        			(int)Math.floor(p.getX()) >> 4 < Server.Mincolumn
        			||
        			(int)Math.floor(p.getZ()) >> 4 < Server.Mincolumn) {
        		session.send(new ServerPlayerPositionRotationPacket(pos.x,pos.y,pos.z,yaw,pitch,tpid++,new ArrayList<PositionElement>()));
        		session.send(new ServerChatPacket(Component.text("нельзя выходить за зону!").color(NamedTextColor.RED)));
        	} else {
	        	this.pos = new Vector3D(p.getX(),p.getY(),p.getZ());
        	}
        	
        } else if (event.getPacket() instanceof ClientChatPacket) {
        	ClientChatPacket packet = event.getPacket();
        	ServerChatPacket p = new ServerChatPacket(Component.text("<"+profile.getName()+"> "+packet.getMessage()));
        	for (ClientSession player : Server.players) {
        		player.session.send(p);
        	}
        } else if (event.getPacket() instanceof ClientRequestPacket) {
        	ClientRequestPacket p = event.getPacket();
        	if (p.getRequest()==ClientRequest.RESPAWN) {
        		this.health = 20;
        		this.food = 20;
        		this.pos = Server.spawnpoint.clone();
        		session.send(new ServerPlayerHealthPacket(health,food,0));
        		session.send(new ServerPlayerPositionRotationPacket(pos.x,pos.y,pos.z,yaw,pitch,tpid++,new ArrayList<PositionElement>()));
        	}
        } else if (event.getPacket() instanceof ClientPlayerActionPacket) {
        	ClientPlayerActionPacket p = event.getPacket();
        	if (p.getAction() == PlayerAction.START_DIGGING) {
        		if (gm == GameMode.CREATIVE) {
        			Multiworld.setBlock(p.getPosition(), 0);
            		Server.sendForEver(new ServerBlockChangePacket(new BlockChangeRecord(p.getPosition(),0)));
        		}
        	} else if (p.getAction() == PlayerAction.FINISH_DIGGING) {
        		Multiworld.setBlock(p.getPosition(), 0);
        		Server.sendForEver(new ServerBlockChangePacket(new BlockChangeRecord(p.getPosition(),0)));
        	}
        } else if (event.getPacket() instanceof ClientPlayerSwingArmPacket) {
        	
        } else if (event.getPacket() instanceof ClientPlayerPlaceBlockPacket) {
        	ClientPlayerPlaceBlockPacket p = event.getPacket();
        	Block beforeBlock = Multiworld.getBlock(VectorUtils.convert(p.getPosition()));
        	if (p.getHand() == Hand.MAIN_HAND) {
        		Vector3D posss = VectorUtils.convert(p.getPosition()).add(VectorUtils.BFtoVec(p.getFace()));
        		ItemStack item = getiteminhand();
        		if (item == null || item.getId() == 0) {
        			//empty click
        			session.send(new ServerBlockChangePacket(new BlockChangeRecord(posss.translate(),0)));
        		} else {
        			for (Entry<Integer, oldMinecraftBlocks> entry : Main.getMCData().blockStates.entrySet()) {
        				ItemData itemdata = Main.getMCData().items.get(item.getId());
        				if (entry.getValue().name.contains(itemdata.name)) {
        					Multiworld.setBlock(posss.translate(), entry.getKey());
        	        		Server.sendForEver(new ServerBlockChangePacket(new BlockChangeRecord(posss.translate(),entry.getKey())));
        	        		return;
        				}
        			}
        			Multiworld.setBlock(p.getPosition(), 0);
            		Server.sendForEver(new ServerBlockChangePacket(new BlockChangeRecord(p.getPosition(),0)));
        		}
        	} else {
        		Server.sendForEver(new ServerBlockChangePacket(new BlockChangeRecord(p.getPosition(),beforeBlock.state)));
        	}
        } else if (event.getPacket() instanceof ClientPlayerChangeHeldItemPacket) {
        	ClientPlayerChangeHeldItemPacket p = event.getPacket();
        	this.slot = p.getSlot();
        	BotU.log(slot);
        } else if (event.getPacket() instanceof ClientCreativeInventoryActionPacket) {
        	ClientCreativeInventoryActionPacket p = event.getPacket();
        	if (p.getSlot() < 0) {
        		tossItem(p.getClickedItem());
        	} else {
        		inventory.replace(p.getSlot(), p.getClickedItem());
        	}
        }
	}
	
	public void tossItem(ItemStack item) {
		
	}
	
	public ItemStack getiteminhand() {
		return inventory.get(36+slot);
	}
	
	public void tick() {
		if (sleepticks > 0) {
			sleepticks--;
			return;
		}
		if (state == st.lsp) {
			sendDefaultPackets();
			state = st.succ;
		} else if (state == st.succ) {
			//normal tick
		}
	}
	
	@Override
    public void disconnected(DisconnectedEvent event) {
		BotU.log(event.getReason());
		event.getCause().printStackTrace();
	}
	
	public void onRespawn() {
		
	}
	
	public void sendWorld() {
    	int i = 0;
    	NibbleArray3d[] skyLight = new NibbleArray3d[18];
    	for (i = 0; i<18;i++) {
    		skyLight[i] = new NibbleArray3d(4096);
    		skyLight[i].fill(4);
    	}
    	NibbleArray3d[] blockLight = new NibbleArray3d[18];
    	for (i = 0; i<18;i++) {
    		blockLight[i] = new NibbleArray3d(4096);
    		blockLight[i].fill(4);
    	}
		for (int cx = Server.Mincolumn; cx <= Server.Maxcolumn; cx++) {
			for (int cz = Server.Mincolumn; cz <= Server.Maxcolumn; cz++) {
				//BotU.log("otoslal x:"+cx+" z:"+cz);
				Column column = Multiworld.columns.get(new ChunkCoordinates(cx,cz));
				session.send(new ServerChunkDataPacket(column));
		    	//session.send(new ServerUpdateLightPacket(cx,cz,true, skyLight, blockLight));
			}
		}
	}
	
	public void sendDefaultPackets() {
		session.send(new ServerJoinGamePacket(
				0,
    			false,
    			gm,
    			gm,
    			1,
    			new String[] {"minecraft:world"},
    			Server.getDimensionTag(),
    			Server.getOverworldTag(),
    			"minecraft:world",
    			1488,
    			(int) Server.gets("maxPlayers"),
    			6,
    			false,
    			true,
    			false,
    			false
    	));
		session.send(new ServerDifficultyPacket(Difficulty.NORMAL, true));
		session.send(new ServerPlayerAbilitiesPacket(gm==GameMode.CREATIVE,gm==GameMode.CREATIVE,gm==GameMode.CREATIVE,gm==GameMode.CREATIVE,0.1F,0.1F));
		session.send(new ServerPlayerChangeHeldItemPacket(0));
		session.send(new ServerDeclareRecipesPacket(new Recipe[0]));
		session.send(new ServerSpawnPositionPacket(Server.spawnpoint.clone().translate()));
		//session.send(new ServerDeclareTagsPacket());
		//session.send(new ServerDeclareCommandsPacket(Server.commands, 0));
		session.send(new ServerUnlockRecipesPacket(new String[0], false, false,false,false,false,false,false,false, new String[0]));
		
		session.send(new ServerPlayerPositionRotationPacket(pos.x,pos.y,pos.z,yaw,pitch,tpid++,new ArrayList<PositionElement>()));
		session.send(new ServerUpdateViewPositionPacket((int)pos.x>>4,(int)pos.z>>4));
		sendWorld();
		session.send(new ServerPlayerHealthPacket(health,food,0));
		inventory = new HashMap<>();
		JsonArray tempinv = Server.getPlayerObject(profile.getName()).get("inventory").getAsJsonArray();
		ItemStack[] ti = new ItemStack[tempinv.size()];
		for (int i = 0; i < 45;i++) {
			JsonObject itemobj = tempinv.get(i).getAsJsonObject();
			ItemStack item = new ItemStack(itemobj.get("id").getAsInt(), itemobj.get("count").getAsInt());
			inventory.put(i, item);
			ti[i] = item;
		}
		session.send(new ServerWindowItemsPacket(0, ti));
		ServerChatPacket p = new ServerChatPacket(Component.text(profile.getName()+" joined the game").color(NamedTextColor.GOLD));
		for (ClientSession player : Server.players) {
			player.session.send(p);
		}
	}
}
