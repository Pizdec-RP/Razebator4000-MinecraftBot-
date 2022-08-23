package net.PRP.MCAI.TestServer;

import com.github.steveice10.mc.auth.data.GameProfile;
import com.github.steveice10.mc.auth.service.SessionService;
import com.github.steveice10.mc.protocol.MinecraftConstants;
import com.github.steveice10.mc.protocol.MinecraftProtocol;
import com.github.steveice10.mc.protocol.ServerLoginHandler;
import com.github.steveice10.mc.protocol.data.SubProtocol;
import com.github.steveice10.mc.protocol.data.game.chunk.Chunk;
import com.github.steveice10.mc.protocol.data.game.chunk.Column;
import com.github.steveice10.mc.protocol.data.game.command.CommandNode;
import com.github.steveice10.mc.protocol.data.game.entity.metadata.ItemStack;
import com.github.steveice10.mc.protocol.data.game.entity.player.GameMode;
import com.github.steveice10.mc.protocol.data.status.PlayerInfo;
import com.github.steveice10.mc.protocol.data.status.ServerStatusInfo;
import com.github.steveice10.mc.protocol.data.status.VersionInfo;
import com.github.steveice10.mc.protocol.data.status.handler.ServerInfoBuilder;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerDisconnectPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerJoinGamePacket;
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
import com.github.steveice10.packetlib.event.session.PacketReceivedEvent;
import com.github.steveice10.packetlib.packet.Packet;
import com.github.steveice10.packetlib.tcp.TcpServer;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.stream.JsonReader;

import net.PRP.MCAI.Main;
import net.PRP.MCAI.Multiworld;
import net.PRP.MCAI.data.ChunkCoordinates;
import net.PRP.MCAI.data.Vector3D;
import net.PRP.MCAI.utils.BotU;
import net.PRP.MCAI.utils.ThreadU;
import net.kyori.adventure.text.Component;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.Proxy;
import java.util.List;
import java.util.Map;
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
	static FileInputStream inputStream;
	static Yaml yaml = new Yaml();
    private static final boolean VERIFY_USERS = false;
    public static List<ClientSession> players = new CopyOnWriteArrayList<ClientSession>();
    public static Map<?, ?> settings;
    public static final int Mincolumn = -2, Maxcolumn = 2;
    public static State serverState = State.non;
    public static int entitiesid = 0;
    public static CommandNode[] commands = new CommandNode[0];
    public static Vector3D spawnpoint;
    public static JsonObject worldData;
    private static boolean ul = false;//update locked
    public static int tickCounter = 0;
    
    public enum State {
    	non, loadWorld, genWorld, ready;
    }
    
    public enum ps {
    	main, end
    }
    
    public static void main(String[] args) {
    	new Thread(()->{
    		startServer();
    	}).start();
    }
    
    public static void tick() {
    	for (ClientSession player : players) {
    		player.tick();
    	}
    	tickCounter++;
    	if (tickCounter > 100) {
    		tickCounter = 0;
    	}
    }
    
    public static void sendForEver(Packet p) {
    	for (ClientSession player : players) {
    		player.session.send(p);
    	}
    }
    
    public static void startServer() {
    	System.out.println("initializing minecraft data");
    	Main.initializeBlockType();
    	System.out.println("loading server settings");
    	updateSettings();
    	System.out.println("processing world");
    	try {
    		serverState = State.loadWorld;
    		loadWorld();
    	} catch (Exception e) {
    		e.printStackTrace();
    		System.out.println("failed to load world, generating it again");
    		try {
    			System.out.println("generating new");
    			serverState = State.genWorld;
    			generateWorld();
    			System.out.println("generated. Loading");
    			serverState = State.loadWorld;
        		loadWorld();
        	} catch (Exception ew) {
        		ew.printStackTrace();
        		System.out.println("i cant do this mf");
        		System.exit(0);
        	}
    	}
    	try {
        	serverState = State.ready;
        	System.out.println("all ready");
        	String pos = (String)gets("spawnpoint");
        	spawnpoint = new Vector3D(Double.parseDouble((pos.split(",")[0])),Double.parseDouble((pos.split(",")[1])),Double.parseDouble((pos.split(",")[2])));
        	
        	
            SessionService sessionService = new SessionService();
            //sessionService.setProxy(AUTH_PROXY);

            TcpServer server = new TcpServer((String)gets("host"), (int)gets("port"), MinecraftProtocol.class);
            server.setGlobalFlag(MinecraftConstants.SESSION_SERVICE_KEY, sessionService);
            server.setGlobalFlag(MinecraftConstants.VERIFY_USERS_KEY, VERIFY_USERS);
            server.setGlobalFlag(MinecraftConstants.SERVER_INFO_BUILDER_KEY, (ServerInfoBuilder) session ->
                    new ServerStatusInfo(
                            new VersionInfo("PZDC_1.16.5.1", MinecraftConstants.PROTOCOL_VERSION),
                            new PlayerInfo(
                            		(int) gets("maxPlayers"),
                            		players.size(),
                            		getPlayers()
                            ),
                            Component.text((String)gets("serverName")),
                            null
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
            server.addListener(new ServerAdapter() {
            	
                @Override
                public void serverClosed(ServerClosedEvent event) {
                    System.out.println("Connection closed");
                }

                @Override
                public void sessionAdded(SessionAddedEvent event) {
                	if (serverState == State.non) {
                		event.getSession().send(new ServerDisconnectPacket(Component.text("Сервер запускается")));
                	} else if (serverState == State.genWorld) {
                		event.getSession().send(new ServerDisconnectPacket(Component.text("Мир еще не создался")));
                	} else if (serverState == State.loadWorld) {
                		event.getSession().send(new ServerDisconnectPacket(Component.text("Мир еще не загрузился")));
                	} else {
                		MinecraftProtocol protocol = (MinecraftProtocol) event.getSession().getPacketProtocol();
                		BotU.log(protocol.getSubProtocol().toString());
                		
	                	GameProfile profile = event.getSession().getFlag(MinecraftConstants.PROFILE_KEY);
	                	
	                	//System.out.println(profile.getName()+" connected");
                		ClientSession cs = new ClientSession();
                		
                		cs.profile = profile;
                		cs.entityId = entitiesid++;
                		cs.gm = GameMode.CREATIVE;
                		cs.session = event.getSession();
                		
                		event.getSession().addListener(cs);
                	}
                }

                @Override
                public void sessionRemoved(SessionRemovedEvent event) {
                    //MinecraftProtocol protocol = (MinecraftProtocol) event.getSession().getPacketProtocol();
                    GameProfile profile = event.getSession().getFlag(MinecraftConstants.PROFILE_KEY);
                    if (profile != null && profile.getName() != null) {
                		System.out.println(profile.getName()+" disconnected");
                		for (ClientSession player : players) {
                			if (player.profile.getName() == profile.getName()) {
                				player.session.removeListener(player);
                				players.remove(player);
                			}
                		}
                	}
                    
                }
            });

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
		    					
		    					tempWorldData.get("players").getAsJsonObject().get(player.profile.getName()).getAsJsonObject().add(
		    		        			"pos",
		    		        			new JsonPrimitive(player.pos.x+","+player.pos.y+","+player.pos.z));
		    		        	tempWorldData.get("players").getAsJsonObject().get(player.profile.getName()).getAsJsonObject().add("pitch", new JsonPrimitive(player.pitch));
		    		        	tempWorldData.get("players").getAsJsonObject().get(player.profile.getName()).getAsJsonObject().add("yaw", new JsonPrimitive(player.yaw));
		    		        	JsonArray inv = new JsonArray();
		    					for (int i = 0; i < 45;i++) {
		    						JsonObject itemobj = new JsonObject();
		    						ItemStack item = player.inventory==null?null:player.inventory.get(i);
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
	    			} catch (Exception e) {
	    				e.printStackTrace();
	    			}
    			}
    			BotU.log("save ended, sleeping");
    			ThreadU.sleep(20000);
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
    			int raznica = (int) (timetwo - timeone);
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
    
    
	public static double[] getpos(String playerName) {
    	ul = true;
    	double[] pos = new double[5];
    	try {
	    	
			
			if (worldData.get("players").getAsJsonObject().get(playerName) == null) {
				initPlayerData(playerName);
			}
			
			pos[0] = Double.parseDouble(worldData
					.get("players").getAsJsonObject()
					.get(playerName).getAsJsonObject()
					.get("pos").getAsString().split(",")[0]);
			pos[1] = Double.parseDouble(worldData.get("players").getAsJsonObject().get(playerName).getAsJsonObject().get("pos").getAsString().split(",")[1]);
			pos[2] = Double.parseDouble(worldData.get("players").getAsJsonObject().get(playerName).getAsJsonObject().get("pos").getAsString().split(",")[2]);
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
    
    public static void initPlayerData(String name) {
    	JsonObject player = new JsonObject();
		player.add("op", new JsonPrimitive(false));
		player.add("pos", new JsonPrimitive(spawnpoint.x+","+spawnpoint.y+""+spawnpoint.z));
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
    
	public static JsonObject getPlayerObject(String name) {
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
    
    public static void updateSettings() {
    	try {
    		settings = (Map<?, ?>)yaml.load(new FileInputStream(new File("ServerSettings.yml")));
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    public static Object gets(String n) {
    	return settings.get(n);
    }
    
    public static GameProfile[] getPlayers() {
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
    
    public static void generateWorld() {
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
	public static void loadWorld() throws IOException {
    	JsonReader reader;
		reader = new JsonReader(new FileReader((String)gets("worldfile")));
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

    /*private static void status() {
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

    public static CompoundTag getDimensionTag() {
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

    public static CompoundTag getOverworldTag() {
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

    public static CompoundTag getPlainsTag() {
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

    public static CompoundTag convertToValue(String name, int id, Map<String, Tag> values) {
        CompoundTag tag = new CompoundTag(name);
        tag.put(new StringTag("name", name));
        tag.put(new IntTag("id", id));
        CompoundTag element = new CompoundTag("element");
        element.setValue(values);
        tag.put(element);

        return tag;
    }
}
