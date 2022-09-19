package net.PRP.MCAI.TestServer;

import com.github.steveice10.mc.auth.data.GameProfile;
import com.github.steveice10.mc.auth.service.SessionService;
import com.github.steveice10.mc.protocol.MinecraftConstants;
import com.github.steveice10.mc.protocol.MinecraftProtocol;
import com.github.steveice10.mc.protocol.data.game.PlayerListEntry;
import com.github.steveice10.mc.protocol.data.game.PlayerListEntryAction;
import com.github.steveice10.mc.protocol.data.game.TitleAction;
import com.github.steveice10.mc.protocol.data.game.chunk.Chunk;
import com.github.steveice10.mc.protocol.data.game.chunk.Column;
import com.github.steveice10.mc.protocol.data.game.command.CommandNode;
import com.github.steveice10.mc.protocol.data.game.command.CommandParser;
import com.github.steveice10.mc.protocol.data.game.command.CommandType;
import com.github.steveice10.mc.protocol.data.game.entity.metadata.EntityMetadata;
import com.github.steveice10.mc.protocol.data.game.entity.metadata.Pose;
import com.github.steveice10.mc.protocol.data.game.entity.metadata.Position;
import com.github.steveice10.mc.protocol.data.game.entity.object.FallingBlockData;
import com.github.steveice10.mc.protocol.data.game.entity.metadata.ItemStack;
import com.github.steveice10.mc.protocol.data.game.entity.player.GameMode;
import com.github.steveice10.mc.protocol.data.game.entity.player.PositionElement;
import com.github.steveice10.mc.protocol.data.game.entity.type.EntityType;
import com.github.steveice10.mc.protocol.data.game.world.block.BlockChangeRecord;
import com.github.steveice10.mc.protocol.data.game.world.block.BlockFace;
import com.github.steveice10.mc.protocol.data.status.PlayerInfo;
import com.github.steveice10.mc.protocol.data.status.ServerStatusInfo;
import com.github.steveice10.mc.protocol.data.status.VersionInfo;
import com.github.steveice10.mc.protocol.data.status.handler.ServerInfoBuilder;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerChatPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerDisconnectPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerPlayerListDataPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerPlayerListEntryPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerTitlePacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityDestroyPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityHeadLookPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityMetadataPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityRotationPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityTeleportPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityVelocityPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.player.ServerPlayerPositionRotationPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.spawn.ServerSpawnEntityPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.spawn.ServerSpawnLivingEntityPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.spawn.ServerSpawnPlayerPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.world.ServerBlockChangePacket;
import com.github.steveice10.mc.protocol.data.game.entity.metadata.MetadataType;
import com.github.steveice10.opennbt.tag.builtin.ByteTag;
import com.github.steveice10.opennbt.tag.builtin.CompoundTag;
import com.github.steveice10.opennbt.tag.builtin.FloatTag;
import com.github.steveice10.opennbt.tag.builtin.IntTag;
import com.github.steveice10.opennbt.tag.builtin.ListTag;
import com.github.steveice10.opennbt.tag.builtin.LongTag;
import com.github.steveice10.opennbt.tag.builtin.StringTag;
import com.github.steveice10.opennbt.tag.builtin.Tag;
import com.github.steveice10.packetlib.event.server.ServerAdapter;
import com.github.steveice10.packetlib.event.server.ServerClosedEvent;
import com.github.steveice10.packetlib.event.server.SessionAddedEvent;
import com.github.steveice10.packetlib.event.server.SessionRemovedEvent;
import com.github.steveice10.packetlib.packet.Packet;
import com.github.steveice10.packetlib.tcp.TcpServer;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.stream.JsonReader;

import net.PRP.MCAI.Main;
import net.PRP.MCAI.Multiworld;
import net.PRP.MCAI.TestServer.entity.*;
import net.PRP.MCAI.data.Block;
import net.PRP.MCAI.data.ChunkCoordinates;
import net.PRP.MCAI.data.Entity;
import net.PRP.MCAI.data.Vector3D;
import net.PRP.MCAI.utils.BotU;
import net.PRP.MCAI.utils.ThreadU;
import net.PRP.MCAI.utils.VectorUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import org.yaml.snakeyaml.Yaml;
/*
 * "__Flashback__": {
            "op": true,
            "pos": "0,10,0",
            "pitch": 0.0,
            "yaw": 0.0,
            "inventory": [
                {
                    "id": 0,
                    "count": 0
                }
            ],//44
            "health": 20,
            "food": 20
        }
 * 
 */

public class Server {
	FileInputStream inputStream;
	Yaml yaml = new Yaml();
    private final boolean VERIFY_USERS = false;
    public static List<ClientSession> players = new CopyOnWriteArrayList<ClientSession>();
    public Map<?, ?> settings;
    public final int Mincolumn = -3, Maxcolumn = 3;
    public State serverState = State.non;
    public int entitiesid = 0;
    public CommandNode[] commands = new CommandNode[] {
    	new CommandNode(CommandType.ROOT,false,new int[] {1,2,3,4,5,6,7,8,9,10},-1,null,null,null,null),
    	new CommandNode(CommandType.LITERAL,true,new int[10], -1, "help", null,null,null),
    	new CommandNode(CommandType.LITERAL,true,new int[10], -1, "ban", null,null,null),
    	new CommandNode(CommandType.ARGUMENT,true,new int[10], -1, "razbanarg", CommandParser.GAME_PROFILE,null,null),
    	new CommandNode(CommandType.LITERAL,true,new int[10], -1, "unban", null,null,null),
    	new CommandNode(CommandType.LITERAL,true,new int[10], -1, "razban", null,null,null),
    	new CommandNode(CommandType.LITERAL,true,new int[10], -1, "title", null,null,null),
    	new CommandNode(CommandType.LITERAL,true,new int[10], -1, "spawnfallingblock", null,null,null),
    	new CommandNode(CommandType.LITERAL,true,new int[10], -1, "spawnitem", null,null,null),
    	new CommandNode(CommandType.LITERAL,true,new int[10], -1, "filll", CommandParser.BLOCK_POS,null,null),
    	new CommandNode(CommandType.LITERAL,true,new int[10], -1, "setBlockByState", CommandParser.BLOCK_POS,null,null),
    	new CommandNode(CommandType.LITERAL,true,new int[10], -1, "printwaterheight", CommandParser.BLOCK_POS,null,null)
    };
    public Vector3D spawnpoint;
    public JsonObject worldData;
    private boolean ul = false;//update locked
    public int tickCounter = 0;
	private int raznica;
	public String HOST = "";
	public int PORT = 0;
    
    public enum State {
    	non, loadWorld, genWorld, ready;
    }
    
    public Server(String h, int p) {
    	this.HOST = h;
    	this.PORT = p;
    	new ServerGUI(this);
    }
    
    public void tick() {
    	try {
	    	for (ClientSession player : players) {
	    		
	    		if (!player.banned) player.tick();
	    	}
	    	for (DefaultEntity entity : Multiworld.Entities.values()) {
	    		entity.onUpdate();
	    	}
	    	
	    	if (tickCounter%2==0) {
	    		for (ClientSession player : players) {
	    			int eid = player.entityId;
		    		for (ClientSession sender : players) {
		    			if (sender.entityId != eid && !sender.banned) {
		    				sender.session.send(new ServerEntityTeleportPacket(eid,player.pos.x,player.pos.y,player.pos.z,player.yaw,player.pitch,player.onGround));
		    				sender.session.send(new ServerEntityHeadLookPacket(eid,player.yaw));
		    			}
		    		}
	    		}
	    	}
	    	if (tickCounter%20==0) {
	    		sendForEver(
	    				new ServerPlayerListDataPacket(
	    						Component.text("сервер запущен на Pizdec RP core v2.28 build-1488-super-hyper-nigger-fisting-ass").color(NamedTextColor.LIGHT_PURPLE),
	    						Component.text("онлайн: "+players.size()+" нахуй. Длительность тика:"+(raznica>25?raznica+" (overload)":raznica)+"\n")
	    						.append(Component.text("##########\n").color(NamedTextColor.BLUE)
	    						.append(Component.text("##########").color(NamedTextColor.YELLOW)))));
	    		
	    	}
	    	/*for (Tickable e : tickable) {
	    		e.tick();
	    		if (e.getEntity().pos.y<0) {
	    			Multiworld.Entities.remove(e.getEntity().eid);
	    			tickable.remove(e);
	    			Server.sendForEver(new ServerEntityDestroyPacket(new int[] {e.getEntity().eid}));
	    		}
	    	}
	    	if (tickCounter%2==0) {
	    		//BotU.log("entities: "+Multiworld.Entities.size());
	    		for (Tickable e : tickable) {
	    			e.packettick();
	    		}
	    		for (Entry<Integer, DefaultEntity> entity : Multiworld.Entities.entrySet()) {
	    			if (entity.getValue().type == EntityType.PLAYER) {
			    		Integer eid = entity.getKey();
			    		DefaultEntity en = entity.getValue();
			    		//BotU.log("eid:"+eid);
			    		for (ClientSession player : players) {
			    			if (player.entityId != eid && !player.banned) {
			    				//2ticksproblem
			    				//player.session.send(new ServerEntityPositionRotationPacket(eid,en.velocity.x,en.velocity.y,en.velocity.z,en.Yaw,en.Pitch,en.onGround));
			    				
			    				player.session.send(new ServerEntityTeleportPacket(eid,en.pos.x,en.pos.y,en.pos.z,en.yaw,en.pitch,en.onGround));
			    				player.session.send(new ServerEntityHeadLookPacket(eid,en.yaw));
			    				
			    			}
			    		}
	    			}
		    	}
	    	}*/
	    	tickCounter++;
	    	if (tickCounter > 100) {
	    		tickCounter = 0;
	    	}
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }
    
    public void spawnTickableFallingBlock(DefaultEntity t) {
    	sendForEver(new ServerSpawnEntityPacket(
			t.id,
			t.uuid,
			EntityType.FALLING_BLOCK,
			new FallingBlockData(((BlockEntity)t).blockState,0),
			t.getX(),
			t.getY(),
			t.getZ(),
			0,
			0,
			t.getMotionX(),
			t.getMotionY(),
			t.getMotionZ()
        ));
    	sendForEver(new ServerEntityMetadataPacket(t.id,new EntityMetadata[] {
    			 new EntityMetadata(0, MetadataType.BYTE, (byte)0), 
				 new EntityMetadata(1, MetadataType.INT, 0), 
				 new EntityMetadata(2, MetadataType.OPTIONAL_CHAT, null),
				 new EntityMetadata(3, MetadataType.BOOLEAN, false), 
				 new EntityMetadata(4, MetadataType.BOOLEAN, false), 
				 new EntityMetadata(5, MetadataType.BOOLEAN, false), 
				 new EntityMetadata(6, MetadataType.POSE, Pose.STANDING), 
				 new EntityMetadata(7, MetadataType.POSITION, new Position(0,0,0))
    	}));
	    Multiworld.Entities.put(t.id, t);
    }
    
    public void spawnTickableItem(EntityItem t) {
    	sendForEver(new ServerSpawnEntityPacket(
			t.id, 
			t.uuid,
			t.type,
			t.getX(),
			t.getY(),
			t.getZ(),
			0,
			0,
			0,
			0,
			0
        ));
    	sendForEver(new ServerEntityMetadataPacket(t.id,new EntityMetadata[] {
    			 //new EntityMetadata(0, MetadataType.BYTE, (byte)0), 
				 //new EntityMetadata(1, MetadataType.INT, 300), 
				 //new EntityMetadata(2, MetadataType.OPTIONAL_CHAT, null),
				 //new EntityMetadata(3, MetadataType.BOOLEAN, false), 
				 //new EntityMetadata(4, MetadataType.BOOLEAN, false), 
				 //new EntityMetadata(5, MetadataType.BOOLEAN, false), 
				 //new EntityMetadata(6, MetadataType.POSE, Pose.STANDING), 
				 new EntityMetadata(7, MetadataType.ITEM, t.getItem())
    	}));
    	this.sendForEver(new ServerEntityVelocityPacket(t.id,0,0,0));
    	this.sendForEver(new ServerEntityTeleportPacket(t.id,t.getX(),t.getY(),t.getZ(),t.yaw,t.pitch,t.isOnGround()));
    	Multiworld.Entities.put(t.id,t);
    }
    
    public ClientSession csByEID(int eid) {
    	for (ClientSession player:players) {
    		if (player.entityId == eid) return player;
    	}
    	return null;
    }
    
    public void placeBlockEvent (Vector3D pos, BlockFace face, int itemid, ClientSession cs) {
    	try {
	    	if (itemid == 0) return;
			Vector3D pos2 = pos.add(VectorUtils.BFtoVec(face));
			Block beforeBlock = Multiworld.getBlock(pos2);
			int state = -1;
			
			String blockName = Main.getMCData().items.get(itemid).name;
			//BotU.log(blockName);
			//BotU.log("minecraft:"+blockName);
			if (Main.getMCData().blocksJson.has("minecraft:"+blockName)) {
				JsonObject blockObject = Main.getMCData().blocksJson.get("minecraft:"+blockName).getAsJsonObject();
				//BotU.log(blockObject.toString());
				for (JsonElement bstate : blockObject.get("states").getAsJsonArray()) {
					//BotU.log(bstate);
					if (bstate.getAsJsonObject().has("default")) {
						if (bstate.getAsJsonObject().get("default").getAsBoolean()) {
							state = bstate.getAsJsonObject().get("id").getAsInt();
						}
					}
				}
				
				if (state != -1) setBlock(pos2, state);
				else setBlock(pos2, beforeBlock.state);
			} else {
				//item is not block
				
				ItemStack item = cs.getiteminhand();
				cs.Click(item.getId());
	        	if (item.getId() == 572) {//fire the tnt
	        		if (Multiworld.getBlock(pos).id == 137) {
	        			this.setBlock(pos, 0);
	        			//this.spawnTickable(new tnt(pos));
	        			BotU.log("305: spawn tnt");
	        			return;
	        		}
	        	}
			}
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }
    
    public void broadcastMSG(Component text) {
    	sendForEver(new ServerChatPacket(text));
    }
    
    public void newEntityPlayer(ClientSession cs) {
    	//Multiworld.Entities.put(cs.entityId, new Entity(cs.entityId,cs.profile.getId(), EntityType.PLAYER, cs.pos, cs.yaw, cs.pitch));
    	sendForEver(new ServerSpawnPlayerPacket(cs.entityId,
    			cs.profile.getId(),
    			cs.pos.x,
    			cs.pos.y,
    			cs.pos.z,
    			cs.yaw,
    			cs.pitch
    	));
    }
    
    public void handleEntityMove(Vector3D newpos, ClientSession cs) {
    	if ((int)Math.floor(newpos.getX()) >> 4 > this.Maxcolumn
    			||
    			(int)Math.floor(newpos.getZ()) >> 4 > this.Maxcolumn
    			||
    			(int)Math.floor(newpos.getX()) >> 4 < this.Mincolumn
    			||
    			(int)Math.floor(newpos.getZ()) >> 4 < this.Mincolumn) {
    		cs.session.send(new ServerPlayerPositionRotationPacket(cs.beforePos.x,cs.beforePos.y,cs.beforePos.z,cs.yaw,cs.pitch,cs.tpid++,new ArrayList<PositionElement>()));
    		cs.session.send(new ServerChatPacket(Component.text("нельзя выходить за зону!").color(NamedTextColor.RED)));
    		cs.vel.origin();
    	} else if (newpos.y<0) {
    		cs.session.send(new ServerPlayerPositionRotationPacket(cs.beforePos.x,10,cs.beforePos.z,cs.yaw,cs.pitch,cs.tpid++,new ArrayList<PositionElement>()));
    	} else {
    		cs.beforePos = cs.pos;
    		cs.pos = newpos;
    		
    		if (cs.capturedBlock != null && ((BlockEntity)cs.capturedBlock).captured) {
    			List<Vector3D> points = cs.createRay(cs.pos, cs.yaw, cs.pitch, 9, 0.5);
    			cs.capturedBlock.setPos(points.get(points.size()-1));
    			//BotU.log("uncapt pos: "+cs.capturedBlock.getPos().toString());
    		}
    		
    	}
    }
    
    public void sendForEver(Packet p) {
    	for (ClientSession player : players) {
    		if (!player.banned) player.session.send(p);
    	}
    }
    
    public byte[] getServerIcon() {
    	//byte[] icon = new byte[4096];
    	
    	return null;
    }
    
    public void startServer() throws IOException {
    	System.out.println("initializing minecraft data");
    	Main.initializeBlockType();
    	Main.nicks = Main.getnicksinit();
    	System.out.println("loading server settings");
    	updateSettings();
    	
    	System.out.println("processing world");
    	try {
    		serverState = State.loadWorld;
    		loadWorld(false);
    	} catch (Exception e) {
    		e.printStackTrace();
    		System.out.println("failed to load world, generating it again");
    		try {
    			System.out.println("Loading from backup");
    			serverState = State.loadWorld;
        		loadWorld(true);
        	} catch (Exception ew) {
        		ew.printStackTrace();
        		System.out.println("backup is empty or what");
        		serverState = State.genWorld;
    			generateWorld();
    			serverState = State.loadWorld;
        		loadWorld(false);
        	}
    	}
    	try {
    		BotU.log("sleep 10 sec");
    		ThreadU.sleep(10000);
    		BotU.log("end");
        	serverState = State.ready;
        	System.out.println("all ready");
        	String pos = (String)gets("spawnpoint");
        	spawnpoint = new Vector3D(Double.parseDouble((pos.split(" ")[0])),Double.parseDouble((pos.split(" ")[1])),Double.parseDouble((pos.split(" ")[2])));
        	
        	
            SessionService sessionService = new SessionService();
            //sessionService.setProxy(AUTH_PROXY);

            TcpServer server = new TcpServer(HOST,PORT, MinecraftProtocol.class);
            server.setGlobalFlag(MinecraftConstants.SESSION_SERVICE_KEY, sessionService);
            server.setGlobalFlag(MinecraftConstants.VERIFY_USERS_KEY, VERIFY_USERS);
            server.setGlobalFlag(MinecraftConstants.SERVER_INFO_BUILDER_KEY, (ServerInfoBuilder) session ->
                    new ServerStatusInfo(
                            new VersionInfo("PZDC 1.4.88 1.16.5", MinecraftConstants.PROTOCOL_VERSION),
                            new PlayerInfo(
                            		(int) gets("maxPlayers"),
                            		players.size(),
                            		getPlayers()
                            ),
                            Component.text((String)gets("serverName")),
                            getServerIcon()
                    )
            );
            /*server.setGlobalFlag(MinecraftConstants.SERVER_LOGIN_HANDLER_KEY, (ServerLoginHandler) session ->
	            session.send(new ServerJoinGamePacket(
						0,
	        			false,
	        			GameMode.CREATIVE,
	        			GameMode.CREATIVE, 
	        			1,
	        			new String[] {"minecraft:world"},
	        			Server.getDimensionTag(),
	        			Server.getOverworldTag(),
	        			"minecraft:world",
	        			1488,
	        			(int) Server.gets("maxPlayers"),
	        			5,
	        			false,
	        			true,
	        			false,
	        			false
	        	))
            );*/
    	
            server.setGlobalFlag(MinecraftConstants.SERVER_COMPRESSION_THRESHOLD, 100);
            server.addListener(new SA(this));

            server.bind();
    	} catch (Exception ee) {
    		ee.printStackTrace();
    	}
    	new Thread(()->{
    		while (true) {
    			BotU.log("save started");
    			if (!ul) {
	    			try {
	    				updateSettings();
	    				JsonObject tempWorldData = worldData.deepCopy();
	    				//write players
	    				for (ClientSession player : players) {
	    					if (player.profile == null) players.remove(player);
	    					else {
		    					tempWorldData.get("players").getAsJsonObject().get(player.profile.getName()).getAsJsonObject().remove("pos");
		    					tempWorldData.get("players").getAsJsonObject().get(player.profile.getName()).getAsJsonObject().remove("pitch");
		    					tempWorldData.get("players").getAsJsonObject().get(player.profile.getName()).getAsJsonObject().remove("yaw");
		    					tempWorldData.get("players").getAsJsonObject().get(player.profile.getName()).getAsJsonObject().remove("inventory");
		    					tempWorldData.get("players").getAsJsonObject().get(player.profile.getName()).getAsJsonObject().remove("health");
		    					tempWorldData.get("players").getAsJsonObject().get(player.profile.getName()).getAsJsonObject().remove("food");
		    					tempWorldData.get("players").getAsJsonObject().get(player.profile.getName()).getAsJsonObject().remove("op");
		    					tempWorldData.get("players").getAsJsonObject().get(player.profile.getName()).getAsJsonObject().remove("banned");
		    					
		    					tempWorldData.get("players").getAsJsonObject().get(player.profile.getName()).getAsJsonObject().add("op", new JsonPrimitive(player.op));
		    					tempWorldData.get("players").getAsJsonObject().get(player.profile.getName()).getAsJsonObject().add("banned", new JsonPrimitive(player.banned));
		    					tempWorldData.get("players").getAsJsonObject().get(player.profile.getName()).getAsJsonObject().add(
		    		        			"pos",
		    		        			new JsonPrimitive(player.pos.x+" "+player.pos.y+" "+player.pos.z));
		    		        	tempWorldData.get("players").getAsJsonObject().get(player.profile.getName()).getAsJsonObject().add("pitch", new JsonPrimitive(player.pitch));
		    		        	tempWorldData.get("players").getAsJsonObject().get(player.profile.getName()).getAsJsonObject().add("yaw", new JsonPrimitive(player.yaw));
		    		        	JsonArray inv = new JsonArray();
		    					for (int i = 0; i < 45;i++) {
		    						JsonObject itemobj = new JsonObject();
		    						ItemStack item = player.invIsNull()?new ItemStack(0,0):player.getItem(i);
		    						itemobj.add("id", new JsonPrimitive(item==null?0:item.getId()));
		    						itemobj.add("count", new JsonPrimitive(item==null?0:item.getAmount()));
		    						inv.add(itemobj);
		    					}
		    					tempWorldData.get("players").getAsJsonObject().get(player.profile.getName()).getAsJsonObject().add("inventory", inv);
		    					tempWorldData.get("players").getAsJsonObject().get(player.profile.getName()).getAsJsonObject().add("health", new JsonPrimitive(player.health));
		    					tempWorldData.get("players").getAsJsonObject().get(player.profile.getName()).getAsJsonObject().add("food", new JsonPrimitive(player.food));
		    					
		    					
	    					}
	    					
	    				}
	    				
	    				//write world
	    				tempWorldData.add("columns", new JsonObject());
    					for (int cx = Mincolumn; cx <= Maxcolumn; cx++) {
    						for (int cz = Mincolumn; cz <= Maxcolumn; cz++) {
    							tempWorldData.get("columns").getAsJsonObject().add(cx+","+cz, new JsonObject());
    							JsonObject column = tempWorldData.get("columns").getAsJsonObject().get(cx+","+cz).getAsJsonObject();
    							for (int chy = 0; chy < 16; chy++) {
    								column.add("ch"+chy, new JsonObject());
    								JsonObject chunk = column.get("ch"+chy).getAsJsonObject();
    								for (int x = 0; x<16;x++) {
    									for (int y = 0; y<16;y++) {
    										for (int z = 0; z<16;z++) {
    											int state = Multiworld.columns.get(new ChunkCoordinates(cx,cz)).getChunks()[chy].get(x, y, z);
    											chunk.add(x+","+y+","+z, new JsonPrimitive(state));
    										}
    									}
    								}
    							}
    						}
    					}
    					//saving
	    				worldData = tempWorldData;
	    				//worldsave
	    				FileWriter writer = new FileWriter((String)gets("worldfile"));
	    				writer.write(worldData.toString());
	    				writer.close();
	    				BotU.log("save ended, sleeping");
	    				ThreadU.sleep(10000);
	    				BotU.log("creating backup");
	    				FileWriter writer1 = new FileWriter("bak"+((String)gets("worldfile")));
	    				writer1.write(worldData.toString());
	    				writer1.close();
	    				BotU.log("backup ended, sleeping");
	    			} catch (Exception e) {
	    				e.printStackTrace();
	    			}
    			}
    			
    			ThreadU.sleep(10000);
    		}
    	}).start();
    	new Thread(()->{
    		final int tickrate = 50;
    		int needtocompensate = 0;
    		int curcomp = 0;
    		while (true) {
    			long timeone = System.currentTimeMillis();
    			tick();
    			long timetwo = System.currentTimeMillis();
    			this.raznica = (int) (timetwo - timeone);
    			if (needtocompensate > 5000) {
    				needtocompensate = 0;
    				BotU.log("client overloaded, skiped "+needtocompensate/tickrate+" ticks");
    			}
    			if (raznica > 0 && raznica < tickrate) {
    				curcomp = tickrate-raznica;
    				if (Main.debug) System.out.println("comp "+raznica+"ms");
    				if (needtocompensate <= 0) {
    					ThreadU.sleep(curcomp);
    				} else {
    					needtocompensate-=curcomp;
    				}
    			} else if (raznica == 0){
    				if (needtocompensate <= 0) {
    					ThreadU.sleep(tickrate);
    				} else {
    					needtocompensate-=tickrate;
    				}
    			} else {
    				if (Main.debug) System.out.println("pass "+raznica+"ms");
    				needtocompensate += raznica-tickrate;
    			}
    		}
    	}).start();
    }
    
    
	public double[] getpos(String playerName) {
    	ul = true;
    	double[] pos = new double[5];
    	try {
	    	
			
			if (worldData.get("players").getAsJsonObject().get(playerName) == null) {
				initPlayerData(playerName);
			}
			
			pos[0] = Double.parseDouble(worldData
					.get("players").getAsJsonObject()
					.get(playerName).getAsJsonObject()
					.get("pos").getAsString().split(" ")[0]);
			pos[1] = Double.parseDouble(worldData.get("players").getAsJsonObject().get(playerName).getAsJsonObject().get("pos").getAsString().split(" ")[1]);
			pos[2] = Double.parseDouble(worldData.get("players").getAsJsonObject().get(playerName).getAsJsonObject().get("pos").getAsString().split(" ")[2]);
			pos[3] = Double.parseDouble(worldData.get("players").getAsJsonObject().get(playerName).getAsJsonObject().get("pitch").getAsString());
			pos[4] = Double.parseDouble(worldData.get("players").getAsJsonObject().get(playerName).getAsJsonObject().get("yaw").getAsString());
			ul = false;
			return pos;
    	} catch (Exception e) {
    		e.printStackTrace();
    		ul = false;
    		return pos;
    	}
    }
    
    public void initPlayerData(String name) {
    	JsonObject player = new JsonObject();
		player.add("op", new JsonPrimitive(false));
		player.add("banned", new JsonPrimitive(false));
		player.add("pos", new JsonPrimitive(spawnpoint.x+" "+spawnpoint.y+" "+spawnpoint.z));
		player.add("pitch", new JsonPrimitive(0.0f));
		player.add("yaw", new JsonPrimitive(0.0f));
		JsonArray inv = new JsonArray();
		for (int i = 0; i < 45;i++) {
			JsonObject itemobj = new JsonObject();
			itemobj.add("id", new JsonPrimitive(0));
			itemobj.add("count", new JsonPrimitive(0));
			inv.add(itemobj);
		}
		player.add("inventory", inv);
		player.add("health", new JsonPrimitive(20));
		player.add("food", new JsonPrimitive(20));
		
		worldData.get("players").getAsJsonObject().add(name, player);
    }
    
    public void setBlock(Vector3D Vec3, int state) {
    	//BotU.log("placed "+state+" at "+Vec3.toString());
    	if (Vec3.y == 0) {
    		Multiworld.setBlock(Vec3.translate(), 33);
    		sendForEver(new ServerBlockChangePacket(new BlockChangeRecord(Vec3.translate(),33)));
    		return;
    	}
    	Multiworld.setBlock(Vec3.translate(), state);
		sendForEver(new ServerBlockChangePacket(new BlockChangeRecord(Vec3.translate(),state)));
    }
    
	public JsonObject getPlayerObject(String name) {
		ul = true;
		JsonObject obj = worldData;
		if (obj.get("players").getAsJsonObject().get(name) != null) {
			ul = false;
			return obj.get("players").getAsJsonObject().get(name).getAsJsonObject();
		} else {
			initPlayerData(name);
			ul = false;
			return worldData.get("players").getAsJsonObject().get(name).getAsJsonObject();
		}
    }
    
    public void updateSettings() {
    	try {
    		settings = (Map<?, ?>)yaml.load(new FileInputStream(new File("ServerSettings.yml")));
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    public Object gets(String n) {
    	return settings.get(n);
    }
    
    public GameProfile[] getPlayers() {
    	try {
	    	List<ClientSession> s = players;
	    	GameProfile[] a = new GameProfile[s.size()];
	    	for (ClientSession player : s) {
	    		if (player.profile == null) {
	    			players.remove(player);
	    		}
	    	}
	    	for (int i = 0; i < s.size(); i++) {
	    		a[i] = s.get(i).profile;
	    		if (i>10) break;
	    	}
	    	//BotU.log(a.length+" adsad");
	    	if (a.length==0) {
	    		return new GameProfile[] {new GameProfile(UUID.randomUUID(), "server is empty ¯\\_(ツ)_/¯")};
	    	}
	    	return a;
    	} catch (Exception e) {
    		e.printStackTrace();
    		return new GameProfile[0];
    	}
    }
    
    public void generateWorld() {
    	JsonObject obj = new JsonObject();
    	obj.add("players", new JsonObject());
    	obj.add("columns", new JsonObject());
		for (int cx = Mincolumn; cx <= Maxcolumn; cx++) {
			for (int cz = Mincolumn; cz <= Maxcolumn; cz++) {
				obj.get("columns").getAsJsonObject().add(cx+","+cz, new JsonObject());
				JsonObject column = obj.get("columns").getAsJsonObject().get(cx+","+cz).getAsJsonObject();
				for (int chy = 0; chy < 16; chy++) {
					column.add("ch"+chy, new JsonObject());
					JsonObject chunk = column.get("ch"+chy).getAsJsonObject();
					for (int x = 0; x<16;x++) {
						for (int y = 0; y<16;y++) {
							for (int z = 0; z<16;z++) {
								int state = 0;
								if (chy == 0) {
									if (y==0) {
										state = 33;
									} else if (y > 0 && y < 5) {
										state = 10;
									} else if (y == 5) {
										state = 9;
									}
								}
								chunk.add(x+","+y+","+z, new JsonPrimitive(state));
							}
						}
					}
				}
			}
		}
		try {
			FileWriter writer = new FileWriter((String)gets("worldfile"));
			writer.write(obj.toString());
			writer.close();
			worldData = obj;
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}
    }
    
    @SuppressWarnings("deprecation")
	public void loadWorld(boolean backup) throws IOException {
    	JsonReader reader;
    	String worldfile;
    	if (backup) {
    		worldfile = "bak"+((String)gets("worldfile"));
    	} else {
    		worldfile = (String)gets("worldfile");
    	}
		reader = new JsonReader(new FileReader(worldfile));
		JsonObject obj = (JsonObject) new JsonParser().parse(reader);
		for (int cx = Mincolumn; cx <= Maxcolumn; cx++) {
			for (int cz = Mincolumn; cz <= Maxcolumn; cz++) {
				Chunk[] chunks = new Chunk[16];
				for (int chy = 0; chy < 16; chy++) {
					chunks[chy] = new Chunk();
				}
				int[] bd = new int[1024];
            	for (int i = 0; i < bd.length; i++) {
            		bd[i] = 1;
            	}
				CompoundTag[] tags = new CompoundTag[0];
				Multiworld.columns.put(new ChunkCoordinates(cx,cz), new Column(cx,cz,chunks,tags, getPlainsTag(), bd));
			}
		}
		for (int cx = Mincolumn; cx <= Maxcolumn; cx++) {
			for (int cz = Mincolumn; cz <= Maxcolumn; cz++) {
				
				JsonObject column = obj.get("columns").getAsJsonObject().get(cx+","+cz).getAsJsonObject();
				for (int chy = 0; chy < 16; chy++) {
					JsonObject chunk = column.get("ch"+chy).getAsJsonObject();
					for (int x = 0; x<16;x++) {
						for (int y = 0; y<16;y++) {
							for (int z = 0; z<16;z++) {
								int state = chunk.get(x+","+y+","+z).getAsInt();
								Multiworld.columns.get(new ChunkCoordinates(cx,cz)).getChunks()[chy].set(x, y, z, state);
							}
						}
					}
				}
			}
		}
		reader.close();
		worldData = obj;
    }

    /*private void status() {
        SessionService sessionService = new SessionService();
        sessionService.setProxy(AUTH_PROXY);

        MinecraftProtocol protocol = new MinecraftProtocol();
        Session client = new TcpClientSession(HOST, PORT, protocol, PROXY);
        client.setFlag(MinecraftConstants.SESSION_SERVICE_KEY, sessionService);
        client.setFlag(MinecraftConstants.SERVER_INFO_HANDLER_KEY, (ServerInfoHandler) (session, info) -> {
            System.out.println("Version: " + info.getVersionInfo().getVersionName()
                    + ", " + info.getVersionInfo().getProtocolVersion());
            System.out.println("Player Count: " + info.getPlayerInfo().getOnlinePlayers()
                    + " / " + info.getPlayerInfo().getMaxPlayers());
            System.out.println("Players: " + Arrays.toString(info.getPlayerInfo().getPlayers()));
            System.out.println("Description: " + info.getDescription());
            System.out.println("Icon: " + info.getIconPng());
        });

        client.setFlag(MinecraftConstants.SERVER_PING_TIME_HANDLER_KEY, (ServerPingTimeHandler) (session, pingTime) ->
                System.out.println("Server ping took " + pingTime + "ms"));

        client.connect();
        while(client.isConnected()) {
            try {
                Thread.sleep(5);
            } catch(InterruptedException e) {
                e.printStackTrace();
            }
        }
    }*/

    public CompoundTag getDimensionTag() {
        CompoundTag tag = new CompoundTag("");

        CompoundTag dimensionTypes = new CompoundTag("minecraft:dimension_type");
        dimensionTypes.put(new StringTag("type", "minecraft:dimension_type"));
        ListTag dimensionTag = new ListTag("value");
        CompoundTag overworldTag = convertToValue("minecraft:overworld", 0, getOverworldTag().getValue());
        dimensionTag.add(overworldTag);
        dimensionTypes.put(dimensionTag);
        tag.put(dimensionTypes);

        CompoundTag biomeTypes = new CompoundTag("minecraft:worldgen/biome");
        biomeTypes.put(new StringTag("type", "minecraft:worldgen/biome"));
        ListTag biomeTag = new ListTag("value");
        CompoundTag plainsTag = convertToValue("minecraft:plains", 0, getPlainsTag().getValue());
        biomeTag.add(plainsTag);
        biomeTypes.put(biomeTag);
        tag.put(biomeTypes);

        return tag;
    }

    public CompoundTag getOverworldTag() {
        CompoundTag overworldTag = new CompoundTag("");
        overworldTag.put(new StringTag("name", "minecraft:overworld"));
        overworldTag.put(new ByteTag("piglin_safe", (byte) 0));
        overworldTag.put(new ByteTag("natural", (byte) 1));
        overworldTag.put(new FloatTag("ambient_light", 0f));
        overworldTag.put(new StringTag("infiniburn", "minecraft:infiniburn_overworld"));
        overworldTag.put(new ByteTag("respawn_anchor_works", (byte) 0));
        overworldTag.put(new ByteTag("has_skylight", (byte) 1));
        overworldTag.put(new ByteTag("bed_works", (byte) 1));
        overworldTag.put(new StringTag("effects", "minecraft:overworld"));
        overworldTag.put(new ByteTag("has_raids", (byte) 1));
        overworldTag.put(new IntTag("logical_height", 256));
        overworldTag.put(new FloatTag("coordinate_scale", 1f));
        overworldTag.put(new ByteTag("ultrawarm", (byte) 0));
        overworldTag.put(new ByteTag("has_ceiling", (byte) 0));
        return overworldTag;
    }

    public CompoundTag getPlainsTag() {
        CompoundTag plainsTag = new CompoundTag("");
        plainsTag.put(new StringTag("name", "minecraft:plains"));
        plainsTag.put(new StringTag("precipitation", "rain"));
        plainsTag.put(new FloatTag("depth", 0.125f));
        plainsTag.put(new FloatTag("temperature", 0.8f));
        plainsTag.put(new FloatTag("scale", 0.05f));
        plainsTag.put(new FloatTag("downfall", 0.4f));
        plainsTag.put(new StringTag("category", "plains"));

        CompoundTag effects = new CompoundTag("effects");
        effects.put(new LongTag("sky_color", 7907327));
        effects.put(new LongTag("water_fog_color", 329011));
        effects.put(new LongTag("fog_color", 12638463));
        effects.put(new LongTag("water_color", 4159204));

        CompoundTag moodSound = new CompoundTag("mood_sound");
        moodSound.put(new IntTag("tick_delay", 6000));
        moodSound.put(new FloatTag("offset", 2.0f));
        moodSound.put(new StringTag("sound", "minecraft:ambient.cave"));
        moodSound.put(new IntTag("block_search_extent", 8));

        effects.put(moodSound);

        plainsTag.put(effects);

        return plainsTag;
    }

    public CompoundTag convertToValue(String name, int id, Map<String, Tag> values) {
        CompoundTag tag = new CompoundTag(name);
        tag.put(new StringTag("name", name));
        tag.put(new IntTag("id", id));
        CompoundTag element = new CompoundTag("element");
        element.setValue(values);
        tag.put(element);

        return tag;
    }

	public void commandUsed(ClientSession cs, String message) {
		message = message.replace("/", "");
		if (message.equals("iziopka65")) {
			this.sendForEver(new ServerChatPacket(Component.text(cs.profile.getName()+"узнал команду для взлома опки. гордитесь им")));
			cs.op = true;
			return;
		} else if (message.startsWith("help")) {
			cs.session.send(new ServerChatPacket(Component.text("у тебя в команде буква г перевернулась")));
			return;
		} else if (message.startsWith("skick")) {
			
		} else if (message.startsWith("heгp") || message.startsWith("негр")) {
			cs.session.send(new ServerChatPacket(Component.text("асуждаю блять")));
			return;
		} else if (message.startsWith("spawnfallingblock")) {
			
			int id = this.nextEID();
			String[] stat = message.split(" ");
			if (stat.length <= 1) {
				cs.chat("ты не указал state-id");
				return;
			}
			int state = Integer.parseInt(stat[1]);
			cs.chat("spawned item. id: " + id);
			DefaultEntity t = new BlockEntity(id,cs.pos.clone().add(0,1.55,0),state,this);
			
			sendForEver(new ServerSpawnEntityPacket(
				id,
				t.uuid,
				EntityType.FALLING_BLOCK,
				new FallingBlockData(state,0),
				cs.pos.x,
				cs.pos.y,
				cs.pos.z,
				0,
				0,
				0,
				0,
				0
	        ));
	    	sendForEver(new ServerEntityMetadataPacket(id,new EntityMetadata[] {
	    			 new EntityMetadata(0, MetadataType.BYTE, (byte)0), 
					 new EntityMetadata(1, MetadataType.INT, 0), 
					 new EntityMetadata(2, MetadataType.OPTIONAL_CHAT, null),
					 new EntityMetadata(3, MetadataType.BOOLEAN, false), 
					 new EntityMetadata(4, MetadataType.BOOLEAN, false), 
					 new EntityMetadata(5, MetadataType.BOOLEAN, false), 
					 new EntityMetadata(6, MetadataType.POSE, Pose.STANDING), 
					 new EntityMetadata(7, MetadataType.POSITION, new Position(0,0,0))
	    	}));
		    Multiworld.Entities.put(t.id, t);
		    Vector3D dir = VectorUtils.getDirection(cs.yaw, cs.pitch);
		    
		    t.setVel(dir);
		    return;
		} else if (message.startsWith("setBlockByState")) {
			String state = message.split(" ")[1];
			if (state == null || state == "") {
				cs.chat("ты не указал state-id блока");
			} else {
				this.setBlock(cs.pos.clone(), Integer.parseInt(state));
			}
			return;
		} else if (message.startsWith("printwaterheight")) {
			if (Multiworld.getBlock(cs.pos.clone().floor()).isWater()) {
				cs.chat("fh: "+Multiworld.getBlock(cs.pos.clone().floor()).getFluidHeight());
			} else {
				cs.chat("ты должен стоять в воде чтобы команда выполнилась");
			}
			return;
		}
		if (!cs.op) {
			this.sendForEver(new ServerChatPacket(Component.text(cs.profile.getName()+" хотел заюзать команду:"+message+" но у него нету опки поэтому он соснул хуйца и теперь все знают че он хотел написать")));
			cs.session.send(new ServerChatPacket(Component.text("у тебя опkи нет, лошня").color(NamedTextColor.RED)));
			return;
		}
		
		if (message.startsWith("filll")) {
			String[] s = message.split(" ");
			if (s.length < 7) {
				cs.chat("ты указал не все аргументы");
				return;
			}
			Vector3D pos1 = new Vector3D(Integer.parseInt(s[1]),Integer.parseInt(s[2]),Integer.parseInt(s[3]));
			Vector3D pos2 = new Vector3D(Integer.parseInt(s[4]),Integer.parseInt(s[5]),Integer.parseInt(s[6]));
			
			ItemStack item = cs.getiteminhand();
			int state = -1;
			int oid = Main.getMCData().itemToOldId(item.getId());
			if (oid != -1) {
				state = Main.getMCData().oldIdToNew(oid);
			}
			if (state == -1 ) {
				cs.session.send(new ServerChatPacket(Component.text("ты не держишь подходящего предмета в руке")));
				return;
			}
			for (int x = (int) Math.min(pos1.x,pos2.x); x <= Math.max(pos1.x,pos2.x);x++) {
				
				for (int z = (int) Math.min(pos1.z,pos2.z); z <= Math.max(pos1.z,pos2.z);z++) {
					
					for (int y = (int) Math.min(pos1.y,pos2.y); y <= Math.max(pos1.y,pos2.y);y++) {
						
						this.setBlock(new Vector3D(x,y,z), state);
					}
				}
			}
		} else if (message.startsWith("title")) {
			sendForEver(new ServerTitlePacket(TitleAction.TITLE, Component.text(message.replace("title ", ""))));
		} else if (message.startsWith("ban")) {
			String target = message.split(" ")[1];	
			String reason = message.replace(target, "").replace("ban ", "");
			for (ClientSession player : players) {
				
				if (player.profile.getName().contains(target)) {
					player.banned = true;
					sendForEver(new ServerChatPacket(Component.text(player.profile.getName()+" был забанен нахуй"+( message.split(" ").length >= 3  ? "причина: "+reason : "")+" хуесосим его.").color(NamedTextColor.RED)));
					player.session.send(new ServerChatPacket(Component.text("ты был опущен(забанен) на этом серве. причина - "+( message.split(" ").length >= 3  ? reason : "лох(причина не указана)")+". мне лень тебя кикать поэтому выйди сам (всё что ты сделаешь в этом состоянии игнорируется. можешь хоть нюкер врубить мне похуй)").color(NamedTextColor.RED)));
					player.session.send(new ServerChatPacket(Component.text("server closed the connection").color(NamedTextColor.AQUA)));
					sendForEver(new ServerPlayerListEntryPacket(
							PlayerListEntryAction.UPDATE_DISPLAY_NAME,
							new PlayerListEntry[] {
								new PlayerListEntry(player.profile,Component.text("тень забаненого игрока "+player.profile.getName()))
							}
						));
				}
			}
		} else if (message.startsWith("unban") || message.startsWith("razban")) {
			String target = message.split(" ")[1];
			ul = true;
			this.worldData.get("players").getAsJsonObject().get(target).getAsJsonObject().remove("banned");
			this.worldData.get("players").getAsJsonObject().get(target).getAsJsonObject().add("banned",new JsonPrimitive(false));
			ul = false;
			this.sendForEver(new ServerChatPacket(Component.text("игрок "+target+" разбанен и больше не сосет хуй")));
			for (ClientSession player : players) {
    			if (player.profile.getName().equals(target)) {
    				player.banned = false;
    				player.session.send(new ServerChatPacket(Component.text("ты был разбанен и возвращен в мир. не делай хуйни больше").color(NamedTextColor.GREEN)));
    				player.sendWorld();
    				player.sendPlayers();
    				player.sendEntities();
    				sendForEver(new ServerPlayerListEntryPacket(
						PlayerListEntryAction.UPDATE_DISPLAY_NAME,
						new PlayerListEntry[] {
							new PlayerListEntry(player.profile,Component.text(player.profile.getName()))
						}
					));
    			}
    		}
		} else if (message.startsWith("spawnitem")) {
			int id = this.nextEID();
			UUID uuid = UUID.randomUUID();
			cs.chat("spawned item. id: " + id+" uuid: "+uuid.toString());
			DefaultEntity t = new EntityItem(new ItemStack(2,1), uuid,id,new Vector3D(0,10,0),this);
			
			sendForEver(new ServerSpawnEntityPacket(
				id, 
				uuid,
				EntityType.ITEM,
				cs.pos.x,
				cs.pos.y,
				cs.pos.z,
				0,
				0,
				0,
				0,
				0
	        ));
	    	sendForEver(new ServerEntityMetadataPacket(id,new EntityMetadata[] {
	    			 //new EntityMetadata(0, MetadataType.BYTE, (byte)0), 
					 //new EntityMetadata(1, MetadataType.INT, 300), 
					 //new EntityMetadata(2, MetadataType.OPTIONAL_CHAT, null),
					 //new EntityMetadata(3, MetadataType.BOOLEAN, false), 
					 //new EntityMetadata(4, MetadataType.BOOLEAN, false), 
					 //new EntityMetadata(5, MetadataType.BOOLEAN, false), 
					 //new EntityMetadata(6, MetadataType.POSE, Pose.STANDING), 
					 new EntityMetadata(7, MetadataType.ITEM, new ItemStack(2,1))
	    	}));
	    	this.sendForEver(new ServerEntityVelocityPacket(id,0,0,0));
		    this.sendForEver(new ServerEntityTeleportPacket(id,0,10,0,0,0,true));
		    Multiworld.Entities.put(t.id, t);
		    //this.spawnTickableItem(new Item(new ItemStack(2,1),cs.pos.clone(),new Vector3D(0,0,0)));
			//cs.session.send(new ServerChatPacket(Component.text("entity created")));
		
	    } else if (message.startsWith("fp")) {
			cs.session.send(new ServerChatPacket(Component.text("юзаем команду...")));
			int eid = nextEID();
			UUID uuid = UUID.randomUUID();
			String name = Main.nextNick();
			sendForEver(new ServerPlayerListEntryPacket(
				PlayerListEntryAction.ADD_PLAYER,
				new PlayerListEntry[] {
					new PlayerListEntry(new GameProfile(uuid,name),GameMode.SURVIVAL)
				}
			));
			sendForEver(new ServerPlayerListEntryPacket(
				PlayerListEntryAction.UPDATE_DISPLAY_NAME,
				new PlayerListEntry[] {
					new PlayerListEntry(new GameProfile(uuid,name),Component.text(name))
				}
			));
			
			sendForEver(new ServerSpawnPlayerPacket(eid,
	    			uuid,
	    			cs.pos.x,
	    			cs.pos.y,
	    			cs.pos.z,
	    			cs.pitch,
	    			cs.yaw
	    			));
			sendForEver(new ServerEntityTeleportPacket(eid,
	    			cs.pos.x,
	    			cs.pos.y,
	    			cs.pos.z,
	    			cs.yaw,
	    			cs.pitch,
	    			true
	    			));
			sendForEver(new ServerEntityRotationPacket(eid,
	    			cs.yaw,
	    			cs.pitch,
	    			true
	    			));
			ServerEntityMetadataPacket p = new ServerEntityMetadataPacket(eid, new EntityMetadata[] {
					 //new EntityMetadata(0, MetadataType.BYTE, (byte)0), 
					 //new EntityMetadata(1, MetadataType.INT, 300), 
					 //new EntityMetadata(2, MetadataType.OPTIONAL_CHAT, null), 
					 //new EntityMetadata(3, MetadataType.BOOLEAN, false), 
					 //new EntityMetadata(4, MetadataType.BOOLEAN, false), 
					// new EntityMetadata(5, MetadataType.BOOLEAN, false), 
					 new EntityMetadata(6, MetadataType.POSE, Pose.STANDING), 
					 //new EntityMetadata(7, MetadataType.BYTE, (byte)0),
					 //new EntityMetadata(8, MetadataType.FLOAT, 20.0F), 
					 //new EntityMetadata(9, MetadataType.INT, 0), 
					 //new EntityMetadata(10, MetadataType.BOOLEAN, false),
					 //new EntityMetadata(11, MetadataType.INT, 0),
					 //new EntityMetadata(12, MetadataType.INT, 0),
					 //new EntityMetadata(13, MetadataType.OPTIONAL_POSITION, null),
					 //new EntityMetadata(14, MetadataType.FLOAT, 0.0F),
					 //new EntityMetadata(15, MetadataType.INT, 292),
					 //new EntityMetadata(16, MetadataType.BYTE, (byte)127),
					 //new EntityMetadata(17, MetadataType.BYTE, (byte)1),
					 //new EntityMetadata(18, MetadataType.NBT_TAG, new CompoundTag("")),
					 //new EntityMetadata(19, MetadataType.NBT_TAG, new CompoundTag(""))
					 }
			 );
			 sendForEver(p);
		} else if (message.startsWith("piih")) {
			cs.chat(cs.getiteminhand().toString());
		}
	}

	public int nextEID() {
		entitiesid += 1;
		return entitiesid;
	}
}

class SA extends ServerAdapter {
	Server s;
	SA(Server s) {
		this.s = s;
	}
	
    @Override
    public void serverClosed(ServerClosedEvent event) {
        System.out.println("Connection closed");
    }

    @Override
    public void sessionAdded(SessionAddedEvent event) {
    	if (s.serverState == net.PRP.MCAI.TestServer.Server.State.non) {
    		event.getSession().send(new ServerDisconnectPacket(Component.text("Сервер запускается")));
    	} else if (s.serverState == net.PRP.MCAI.TestServer.Server.State.genWorld) {
    		event.getSession().send(new ServerDisconnectPacket(Component.text("Мир еще не создался")));
    	} else if (s.serverState == net.PRP.MCAI.TestServer.Server.State.loadWorld) {
    		event.getSession().send(new ServerDisconnectPacket(Component.text("Мир еще не загрузился")));
    	} else {
    		MinecraftProtocol protocol = (MinecraftProtocol) event.getSession().getPacketProtocol();
    		BotU.log(protocol.getSubProtocol().toString());
    		
        	GameProfile profile = event.getSession().getFlag(MinecraftConstants.PROFILE_KEY);
        	
        	//System.out.println(profile.getName()+" connected");
    		ClientSession cs = new ClientSession(s);
    		
    		cs.profile = profile;
    		cs.entityId = s.nextEID();
    		cs.gm = GameMode.CREATIVE;
    		cs.session = event.getSession();
    		
    		event.getSession().addListener(cs);
    	}
    }

    @Override
    public void sessionRemoved(SessionRemovedEvent event) {
    	throw new IllegalArgumentException();
        //MinecraftProtocol protocol = (MinecraftProtocol) event.getSession().getPacketProtocol();
        /*GameProfile profile = event.getSession().getFlag(MinecraftConstants.PROFILE_KEY);
        if (profile != null && profile.getName() != null) {
    		System.out.println(profile.getName()+" disconnected");
    		for (ClientSession player : s.players) {
    			if (player.profile.getName() == profile.getName()) {
    				player.session.removeListener(player);
    				s.players.remove(player);
    			}
    		}
    	}*/
        
    }
}
