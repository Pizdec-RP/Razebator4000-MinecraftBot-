package net.PRP.MCAI.TestServer;

import com.github.steveice10.mc.auth.data.GameProfile;
import com.github.steveice10.mc.auth.exception.request.RequestException;
import com.github.steveice10.mc.auth.service.AuthenticationService;
import com.github.steveice10.mc.auth.service.SessionService;
import com.github.steveice10.mc.protocol.MinecraftConstants;
import com.github.steveice10.mc.protocol.MinecraftProtocol;
import com.github.steveice10.mc.protocol.ServerLoginHandler;
import com.github.steveice10.mc.protocol.data.SubProtocol;
import com.github.steveice10.mc.protocol.data.game.PlayerListEntry;
import com.github.steveice10.mc.protocol.data.game.chunk.Chunk;
import com.github.steveice10.mc.protocol.data.game.chunk.Column;
import com.github.steveice10.mc.protocol.data.game.entity.metadata.ItemStack;
import com.github.steveice10.mc.protocol.data.game.entity.metadata.Position;
import com.github.steveice10.mc.protocol.data.game.entity.player.GameMode;
import com.github.steveice10.mc.protocol.data.game.entity.player.PositionElement;
import com.github.steveice10.mc.protocol.data.game.world.block.BlockChangeRecord;
import com.github.steveice10.mc.protocol.data.status.PlayerInfo;
import com.github.steveice10.mc.protocol.data.status.ServerStatusInfo;
import com.github.steveice10.mc.protocol.data.status.VersionInfo;
import com.github.steveice10.mc.protocol.data.status.handler.ServerInfoBuilder;
import com.github.steveice10.mc.protocol.data.status.handler.ServerInfoHandler;
import com.github.steveice10.mc.protocol.data.status.handler.ServerPingTimeHandler;
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientChatPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientKeepAlivePacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerPositionPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerPositionRotationPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerRotationPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerChatPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerJoinGamePacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.player.ServerPlayerPositionRotationPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.window.ServerSetSlotPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.world.ServerBlockChangePacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.world.ServerChunkDataPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.world.ServerSpawnPositionPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.world.ServerUpdateViewPositionPacket;
import com.github.steveice10.mc.protocol.packet.login.client.LoginStartPacket;
import com.github.steveice10.opennbt.tag.builtin.ByteTag;
import com.github.steveice10.opennbt.tag.builtin.CompoundTag;
import com.github.steveice10.opennbt.tag.builtin.DoubleTag;
import com.github.steveice10.opennbt.tag.builtin.FloatTag;
import com.github.steveice10.opennbt.tag.builtin.IntTag;
import com.github.steveice10.opennbt.tag.builtin.ListTag;
import com.github.steveice10.opennbt.tag.builtin.LongTag;
import com.github.steveice10.opennbt.tag.builtin.StringTag;
import com.github.steveice10.opennbt.tag.builtin.Tag;
import com.github.steveice10.packetlib.ProxyInfo;
import com.github.steveice10.packetlib.Session;
import com.github.steveice10.packetlib.event.server.ServerAdapter;
import com.github.steveice10.packetlib.event.server.ServerClosedEvent;
import com.github.steveice10.packetlib.event.server.SessionAddedEvent;
import com.github.steveice10.packetlib.event.server.SessionRemovedEvent;
import com.github.steveice10.packetlib.event.session.PacketReceivedEvent;
import com.github.steveice10.packetlib.event.session.SessionAdapter;
import com.github.steveice10.packetlib.tcp.TcpClientSession;
import com.github.steveice10.packetlib.tcp.TcpServer;

import net.PRP.MCAI.data.Vector3D;
import net.PRP.MCAI.utils.BotU;
import net.PRP.MCAI.utils.MathU;
import net.PRP.MCAI.utils.ThreadU;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

import java.net.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

public class Server {
	
	private static final boolean SPAWN_SERVER = true;
    private static final boolean VERIFY_USERS = false;
    private static final String HOST = "127.0.0.1";
    private static final int PORT = 25565;
    private static final ProxyInfo PROXY = null;
    private static final Proxy AUTH_PROXY = Proxy.NO_PROXY;
    private static List<PlayerListEntry> players = new CopyOnWriteArrayList<>();
    static CompoundTag dct = new CompoundTag("");
    static CompoundTag dctc = new CompoundTag("");
    
    static long starttime = 0;
    static int starty = 0;
    static double endy = 0;
    static long endtime = 0;

    public static void main(String[] args) {
    	dct.put(new ByteTag("piglin_safe",(byte) 0));
    	dct.put(new FloatTag("ambient_light",15.0F));
    	dct.put(new StringTag("infiniburn","infiniburn_nether"));
    	dct.put(new IntTag("logical_height", 256));
    	dct.put(new ByteTag("has_raids",(byte)0));
    	dct.put(new ByteTag("respawn_anchor_works",(byte)0));
    	dct.put(new ByteTag("bed_works",(byte) 1));
    	dct.put(new DoubleTag("coordinate_scale",1.0D));
    	dct.put(new ByteTag("natural",(byte) 1));
    	dct.put(new ByteTag("ultrawarm",(byte)0));
    	dct.put(new ByteTag("has_ceiling",(byte)0));
    	dct.put(new ByteTag("has_skylight",(byte)1));
    			
        if(SPAWN_SERVER) {
            SessionService sessionService = new SessionService();
            sessionService.setProxy(AUTH_PROXY);

            TcpServer server = new TcpServer(HOST, PORT, MinecraftProtocol.class);
            server.setGlobalFlag(MinecraftConstants.SESSION_SERVICE_KEY, sessionService);
            server.setGlobalFlag(MinecraftConstants.VERIFY_USERS_KEY, VERIFY_USERS);
            server.setGlobalFlag(MinecraftConstants.SERVER_INFO_BUILDER_KEY, (ServerInfoBuilder) session ->
                    new ServerStatusInfo(
                            new VersionInfo(MinecraftConstants.GAME_VERSION, MinecraftConstants.PROTOCOL_VERSION),
                            new PlayerInfo(100, 0, new GameProfile[0]),
                            Component.text("Hello world!"),
                            null
                    )
            );

            server.setGlobalFlag(MinecraftConstants.SERVER_LOGIN_HANDLER_KEY, (ServerLoginHandler) session ->
                    session.send(new ServerJoinGamePacket(
                            0,
                            false,
                            GameMode.SURVIVAL,
                            GameMode.SURVIVAL,
                            1,
                            new String[] {"minecraft:world"},
                            getDimensionTag(),
                            getOverworldTag(),
                            "minecraft:world",
                            100,
                            0,
                            16,
                            false,
                            false,
                            false,
                            false
                    ))
            );

            server.setGlobalFlag(MinecraftConstants.SERVER_COMPRESSION_THRESHOLD, 100);
            server.addListener(new ServerAdapter() {
                @Override
                public void serverClosed(ServerClosedEvent event) {
                    System.out.println("Server closed.");
                }

                @Override
                public void sessionAdded(SessionAddedEvent event) {
                	int eid = MathU.rnd(0, 100);
                	
                	players.add(new PlayerListEntry(new GameProfile(new UUID(14215,1523),"nigger")));
                    event.getSession().addListener(new SessionAdapter() {
                        @Override
                        public void packetReceived(PacketReceivedEvent event) {
                        	//if (!(event.getPacket() instanceof ClientKeepAlivePacket)) BotU.log(event.getPacket().getClass().getName());
                            if(event.getPacket() instanceof ClientChatPacket) {
                                ClientChatPacket packet = event.getPacket();
                                GameProfile profile = event.getSession().getFlag(MinecraftConstants.PROFILE_KEY);
                                System.out.println(profile.getName() + ": " + packet.getMessage());

                                Component msg = Component.text("Hello, ")
                                        .color(NamedTextColor.GREEN)
                                        .append(Component.text(profile.getName())
                                            .color(NamedTextColor.AQUA)
                                            .decorate(TextDecoration.UNDERLINED))
                                        .append(Component.text("!")
                                                .color(NamedTextColor.GREEN));

                                event.getSession().send(new ServerChatPacket(msg));
                            } else if (event.getPacket() instanceof LoginStartPacket) {
                            	new Thread(()->{
	                            	ThreadU.sleep(2000);
	                            	event.getSession().send(new ServerJoinGamePacket(
	                            			eid,
	                            			false,
	                            			GameMode.SURVIVAL,
	                            			GameMode.SURVIVAL, 
	                            			1,
	                            			new String[] {"minecraft:world"},
	                            			getDimensionTag(),
	                            			getOverworldTag(),
	                            			"minecraft:world",
	                            			4,
	                            			100,
	                            			2,
	                            			true,
	                            			false,
	                            			false,
	                            			false
	                            	));
	                            	Chunk ch = new Chunk();
	                            	for (int x = 0; x < 16; x++) {
	                            		for (int z = 0; z < 16; z++) {
	                            			for (int y = 0; y < 16; y++) {
	    	                            		if (y == 3) {
	    	                            			ch.set(x, y, z, 9);
	    	                            		} else {
	    	                            			ch.set(x, y, z, 0);
	    	                            		}
	    	                            	}
		                            	}
	                            	}
	                            	//Chunk downch = new Chunk();
	                            	Chunk[] chunks = new Chunk[] {ch,ch,ch,ch,ch,ch,ch,ch,ch,ch,ch,ch,ch,ch,ch,ch};
	                            	CompoundTag[] tags = new CompoundTag[] {getPlainsTag()};
	                            	int[] bd = new int[1024];
	                            	
	                            	event.getSession().send(new ServerSpawnPositionPacket(new Position(2,100,2)));
	                            	event.getSession().send(new ServerPlayerPositionRotationPacket(0,100,0,0,0,0,new ArrayList<PositionElement>()));
	                            	event.getSession().send(new ServerUpdateViewPositionPacket(1,1));
	                            	event.getSession().send(new ServerChunkDataPacket(new Column(1,1,chunks,tags, getPlainsTag(), bd)));
	                            	event.getSession().send(new ServerChunkDataPacket(new Column(1,0,chunks,tags, getPlainsTag(), bd)));
	                            	event.getSession().send(new ServerChunkDataPacket(new Column(0,1,chunks,tags, getPlainsTag(), bd)));
	                            	event.getSession().send(new ServerChunkDataPacket(new Column(0,0,chunks,tags, getPlainsTag(), bd)));
	                            	event.getSession().send(new ServerChunkDataPacket(new Column(-1,-1,chunks,tags, getPlainsTag(), bd)));
	                            	event.getSession().send(new ServerChunkDataPacket(new Column(-1,0,chunks,tags, getPlainsTag(), bd)));
	                            	event.getSession().send(new ServerChunkDataPacket(new Column(0,-1,chunks,tags, getPlainsTag(), bd)));
	                            	event.getSession().send(new ServerBlockChangePacket(new BlockChangeRecord(new Position(0,103,0),9)));
	                            	
	                            	event.getSession().send(new ServerSetSlotPacket(0,37,new ItemStack(54)));
	                            	event.getSession().send(new ServerPlayerPositionRotationPacket(0,100,0,0,0,0,new ArrayList<PositionElement>()));
	                            	starttime = System.currentTimeMillis();
	                            	starty = 100;
                            	}).start();
                            } else if (event.getPacket() instanceof ClientPlayerPositionRotationPacket) {
                            	ClientPlayerPositionRotationPacket packet = (ClientPlayerPositionRotationPacket) event.getPacket();
                            	Vector3D a = new Vector3D(packet.getX(),packet.getY(),packet.getZ());
                            	BotU.log("t: "+System.currentTimeMillis()+" pos: "+a.toString());
                            	endy = a.y;
                            } else if (event.getPacket() instanceof ClientPlayerRotationPacket) {
                            	
                            } else if (event.getPacket() instanceof ClientPlayerPositionPacket) {
                            	ClientPlayerPositionPacket packet = (ClientPlayerPositionPacket) event.getPacket();
                            	Vector3D a = new Vector3D(packet.getX(),packet.getY(),packet.getZ());
                            	BotU.log("t: "+System.currentTimeMillis()+" pos: "+a.toString());
                            	endy = a.y;
                            }
                        }
                    });
                }

                @Override
                public void sessionRemoved(SessionRemovedEvent event) {
                    MinecraftProtocol protocol = (MinecraftProtocol) event.getSession().getPacketProtocol();
                    endtime = System.currentTimeMillis();
                    long ms = endtime - starttime;
                    double dist = starty - endy;
                    double total = dist/ms;
                    BotU.log(" time: "+ms+"ms, distance: "+dist+", total: "+total);
                }
            });

            server.bind();
        }

        status();
    }

    private static void status() {
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
    }

    private static CompoundTag getDimensionTag() {
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

    private static CompoundTag getOverworldTag() {
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

    private static CompoundTag getPlainsTag() {
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

    private static CompoundTag convertToValue(String name, int id, Map<String, Tag> values) {
        CompoundTag tag = new CompoundTag(name);
        tag.put(new StringTag("name", name));
        tag.put(new IntTag("id", id));
        CompoundTag element = new CompoundTag("element");
        element.setValue(values);
        tag.put(element);

        return tag;
    }
}
