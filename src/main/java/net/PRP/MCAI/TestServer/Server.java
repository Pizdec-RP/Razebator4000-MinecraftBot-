package net.PRP.MCAI.TestServer;

import com.github.steveice10.mc.auth.data.GameProfile;
import com.github.steveice10.mc.auth.exception.request.RequestException;
import com.github.steveice10.mc.auth.service.AuthenticationService;
import com.github.steveice10.mc.auth.service.SessionService;
import com.github.steveice10.mc.protocol.MinecraftConstants;
import com.github.steveice10.mc.protocol.MinecraftProtocol;
import com.github.steveice10.mc.protocol.ServerLoginHandler;
import com.github.steveice10.mc.protocol.data.SubProtocol;
import com.github.steveice10.mc.protocol.data.game.ClientRequest;
import com.github.steveice10.mc.protocol.data.game.PlayerListEntry;
import com.github.steveice10.mc.protocol.data.game.PlayerListEntryAction;
import com.github.steveice10.mc.protocol.data.game.TitleAction;
import com.github.steveice10.mc.protocol.data.game.chunk.Chunk;
import com.github.steveice10.mc.protocol.data.game.chunk.Column;
import com.github.steveice10.mc.protocol.data.game.entity.metadata.ItemStack;
import com.github.steveice10.mc.protocol.data.game.entity.metadata.Position;
import com.github.steveice10.mc.protocol.data.game.entity.player.GameMode;
import com.github.steveice10.mc.protocol.data.game.entity.player.PositionElement;
import com.github.steveice10.mc.protocol.data.game.entity.type.EntityType;
import com.github.steveice10.mc.protocol.data.game.world.block.BlockChangeRecord;
import com.github.steveice10.mc.protocol.data.status.PlayerInfo;
import com.github.steveice10.mc.protocol.data.status.ServerStatusInfo;
import com.github.steveice10.mc.protocol.data.status.VersionInfo;
import com.github.steveice10.mc.protocol.data.status.handler.ServerInfoBuilder;
import com.github.steveice10.mc.protocol.data.status.handler.ServerInfoHandler;
import com.github.steveice10.mc.protocol.data.status.handler.ServerPingTimeHandler;
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientChatPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientKeepAlivePacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientPluginMessagePacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientRequestPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerPositionPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerPositionRotationPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerRotationPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerChatPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerDisconnectPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerJoinGamePacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerPlayerListDataPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerPlayerListEntryPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerTitlePacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.player.ServerPlayerHealthPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.player.ServerPlayerPositionRotationPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.spawn.ServerSpawnEntityPacket;
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

import net.PRP.MCAI.Main;
import net.PRP.MCAI.data.Vector3D;
import net.PRP.MCAI.shit.R8onse;
import net.PRP.MCAI.utils.BotU;
import net.PRP.MCAI.utils.MathU;
import net.PRP.MCAI.utils.StringU;
import net.PRP.MCAI.utils.ThreadU;
import net.PRP.MCAI.utils.VectorUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
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
    private static final ProxyInfo PROXY = null;
    private static final Proxy AUTH_PROXY = Proxy.NO_PROXY;
    private static List<GameProfile> players = new CopyOnWriteArrayList<GameProfile>() {
		private static final long serialVersionUID = 1L;

	{
    	add(new GameProfile(UUID.randomUUID(), "kopilop"));
    	add(new GameProfile(UUID.randomUUID(), "Fraiker"));
    	add(new GameProfile(UUID.randomUUID(), "Titlehhh"));
    	add(new GameProfile(UUID.randomUUID(), "SblR"));
    	add(new GameProfile(UUID.randomUUID(), "kisel4ik"));
    	add(new GameProfile(UUID.randomUUID(), "kybersosika"));
    	add(new GameProfile(UUID.randomUUID(), "BratishkinOFF"));
    	add(new GameProfile(UUID.randomUUID(), "Van_Sama"));
    	add(new GameProfile(UUID.randomUUID(), "niggaCAT"));
    	add(new GameProfile(UUID.randomUUID(), "Template11"));
    	add(new GameProfile(UUID.randomUUID(), "Slimboba"));
    	add(new GameProfile(UUID.randomUUID(), "flacody"));
    }};
    static CompoundTag dct = new CompoundTag("");
    static CompoundTag dctc = new CompoundTag("");
    
    static long starttime = 0;
    static int starty = 0;
    static double endy = 0;
    static long endtime = 0;
    
    static long beforetime = 0;
    
    static int[] midcalcarray = new int[10];
    static int tickcount = 0;
    static ps prankstate = ps.main;
    
    public enum ps {
    	main, end
    }
    
    public static GameProfile[] getPlayers() {
    	try {
	    	GameProfile[] g = new GameProfile[players.size()];
	    	int i = 0;
	    	for (;i < players.size(); i++) {
	    		GameProfile pl = players.get(i);
	    		if (pl != null) {
		    		g[i] = pl;
	    		}
	    	}
	    	for (int s = 0; s < g.length; s++) {
	    		if (g[s] == null) {
	    			g[s] = new GameProfile(UUID.randomUUID(),(String)MathU.random(Main.nicks));
	    			BotU.log("nullprofile "+s);
	    		}
	    	}
	    	return g;
    	} catch (Exception e) {
    		e.printStackTrace();
    		return new GameProfile[0];
    	}
    }
    
    public static PlayerListEntry[] getPlayersLE() {
    	try {
    		PlayerListEntry[] g = new PlayerListEntry[players.size()];
	    	int i = 0;
	    	for (;i < players.size(); i++) {
	    		GameProfile pl = players.get(i);
	    		if (pl != null) {
		    		g[i] = new PlayerListEntry(pl);
	    		}
	    	}
	    	for (int s = 0; s < g.length; s++) {
	    		if (g[s] == null) {
	    			g[s] = new PlayerListEntry(new GameProfile(UUID.randomUUID(),(String)MathU.random(Main.nicks)));
	    			BotU.log("nullprofile "+s);
	    		}
	    	}
	    	return g;
    	} catch (Exception e) {
    		e.printStackTrace();
    		return new PlayerListEntry[0];
    	}
    }
    
    public static void removePlayer(GameProfile profile) {
    	if (profile == null) return;
    	try {
	    	for (GameProfile pl : players) {
	    		if (pl != null && pl.getId().equals(profile.getId())) {
	    			players.remove(pl);
	    			return;
	    		}
	    	}
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
	}
    
    public static void main(String[] args) {
    	/*createServer(
    		"192.168.0.104",
    		25566,
			"2.13.9",
			MinecraftConstants.PROTOCOL_VERSION, 
			10000000,
    		6540000+MathU.rnd(100, 999)+MathU.rnd(5000, 6000),
			Component.text("§l§aDi§bVerzium §f- §r§6CAM�� �O����PH�� CEPBEP B CH�\n").append(Component.text("§b§oC HAM� M����OH� ��POKOB, �P�COE��H��C�"))
    	);*/
    	createServer(
        		"192.168.0.104",
        		228,
    			"1.16.5",
    			MinecraftConstants.PROTOCOL_VERSION, 
    			0,
        		666,
    			Component.text("общение с искусственным интеллектом")
        	);
    }
    

    public static void createServer(String HOST, int PORT, String version, int protocol, int online, int maxonline, TextComponent name) {
    	Main.nicks = Main.getnicksinit();
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
                            new VersionInfo(version, protocol),
                            new PlayerInfo(
                            		maxonline,
                            		online == 0 ? 500+MathU.rnd(17, 55): online,
                            		getPlayers()
                            ),
                            name,
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
                    System.out.println("Connection closed");
                }

                @Override
                public void sessionAdded(SessionAddedEvent event) {
                	int eid = MathU.rnd(0, 100);
                	event.getSession().addListener(new SessionAdapter() {
                        @Override
                        public void packetReceived(PacketReceivedEvent event) {
                        	//if (!(event.getPacket() instanceof ClientKeepAlivePacket)) System.out.println(event.getPacket().getClass().getName());
                            if(event.getPacket() instanceof ClientChatPacket) {
                                ClientChatPacket packet = event.getPacket();
                                GameProfile profile = event.getSession().getFlag(MinecraftConstants.PROFILE_KEY);
                                if (packet.getMessage().equals("/q")) {
                                	event.getSession().send(new ServerDisconnectPacket(Component.text("Client closed the connection. Code: -1")));
                                	Main.write("[tsm] "+profile.getName()+" ", packet.getMessage());
                                } else {
                                	event.getSession().send(new ServerChatPacket(Component.text("<"+profile.getName()+"> "+packet.getMessage())));
                                	String resp = new R8onse().get(null, packet.getMessage());
                                	if (resp == null) resp = "...";
                                	Component msg = Component.text("<mamkoeb bot> "+resp);
                                    event.getSession().send(new ServerChatPacket(msg));
                                    Main.write("[plr] "+profile.getName()+" ", packet.getMessage());
                                    Main.write("[ai] --> "+profile.getName()+" ", resp);
                                    System.out.println("<"+profile.getName()+"> write: "+StringU.translit(packet.getMessage()));
                                    System.out.println("<mamkoeb bot> "+resp);
                                }
                                
                            } else if (event.getPacket() instanceof LoginStartPacket) {
                            	GameProfile profile = event.getSession().getFlag(MinecraftConstants.PROFILE_KEY);
                            	
                            	if (profile != null && profile.getName() != null) {
                            		System.out.println(profile.getName()+" connected");
                            		players.add(1,profile);
                            	}
                            	
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
	                            				ch.set(x, y, z, 0);
	    	                            	}
		                            	}
	                            	}
	                            	//Chunk downch = new Chunk();
	                            	Chunk[] chunks = new Chunk[] {ch,ch,ch,ch,ch,ch,ch,ch,ch,ch,ch,ch,ch,ch,ch,ch};
	                            	CompoundTag[] tags = new CompoundTag[] {getPlainsTag()};
	                            	int[] bd = new int[1024];
	                            	for (int i = 0; i < bd.length; i++) {
	                            		bd[i] = 1;
	                            	}
	                            	
	                            	event.getSession().send(new ServerSpawnPositionPacket(new Position(2,246,2)));
	                            	event.getSession().send(new ServerPlayerPositionRotationPacket(0,999999,0,0,0,0,new ArrayList<PositionElement>()));
	                            	event.getSession().send(new ServerUpdateViewPositionPacket(1,1));
	                            	event.getSession().send(new ServerChunkDataPacket(new Column(1,1,chunks,tags, getPlainsTag(), bd)));
	                            	event.getSession().send(new ServerChunkDataPacket(new Column(1,0,chunks,tags, getPlainsTag(), bd)));
	                            	event.getSession().send(new ServerChunkDataPacket(new Column(0,1,chunks,tags, getPlainsTag(), bd)));
	                            	event.getSession().send(new ServerChunkDataPacket(new Column(0,0,chunks,tags, getPlainsTag(), bd)));
	                            	event.getSession().send(new ServerChunkDataPacket(new Column(-1,-1,chunks,tags, getPlainsTag(), bd)));
	                            	event.getSession().send(new ServerChunkDataPacket(new Column(-1,0,chunks,tags, getPlainsTag(), bd)));
	                            	event.getSession().send(new ServerChunkDataPacket(new Column(0,-1,chunks,tags, getPlainsTag(), bd)));
	                            	event.getSession().send(new ServerBlockChangePacket(new BlockChangeRecord(new Position(0,103,0),9)));
	                            	event.getSession().send(new ServerChatPacket(Component.text("<mamkoeb bot> привет хуесос. Я робот который ебет твою мать. Вопросы нахуй?")));
	                            	/*
	                            	event.getSession().send(new ServerPlayerListEntryPacket(
                            			PlayerListEntryAction.ADD_PLAYER,
                            			getPlayersLE()
                            		));
	                            	/*
	                            	event.getSession().send(new ServerPlayerListDataPacket(
	                            			Component.text("сосай ботик"),
	                            			Component.text("мамаша твоя прошмандовка")
	                            			));
	                            	event.getSession().send(new ServerPlayerPositionRotationPacket(0,100,0,0,0,0,new ArrayList<PositionElement>()));
	                            	event.getSession().send(new ServerBlockChangePacket(new BlockChangeRecord(new Position(0,100,5),9)));
	                            	event.getSession().send(new ServerBlockChangePacket(new BlockChangeRecord(new Position(0,101,5),9)));
	                            	event.getSession().send(new ServerBlockChangePacket(new BlockChangeRecord(new Position(-1,100,5),9)));
	                            	event.getSession().send(new ServerBlockChangePacket(new BlockChangeRecord(new Position(1,100,5),9)));
	                            	event.getSession().send(new ServerBlockChangePacket(new BlockChangeRecord(new Position(0,102,5),9)));
	                            	event.getSession().send(new ServerBlockChangePacket(new BlockChangeRecord(new Position(0,103,5),9)));
	                            	event.getSession().send(new ServerBlockChangePacket(new BlockChangeRecord(new Position(0,104,5),65)));
	                            	
	                            	
	                            	event.getSession().send(new ServerChatPacket(Component.text("§2[Pizdec RP]§f §cМАМКУ ЕБАЛ МАМКУ ЕБАЛ МАМКУ ЕБАЛ МАМКУ ЕБАЛ МАМКУ ЕБАЛ МАМКУ ЕБАЛ МАМКУ ЕБАЛ")));
	                            	event.getSession().send(new ServerTitlePacket(TitleAction.TITLE, Component.text("соси хуй лол")));
	                            	event.getSession().send(new ServerChatPacket(Component.text("§2[Pizdec RP]§f §cМАМКУ ЕБАЛ МАМКУ ЕБАЛ МАМКУ ЕБАЛ МАМКУ ЕБАЛ МАМКУ ЕБАЛ МАМКУ ЕБАЛ МАМКУ ЕБАЛ")));
	                            	event.getSession().send(new ServerChatPacket(Component.text("§2[Pizdec RP]§f §cМАМКУ ЕБАЛ МАМКУ ЕБАЛ МАМКУ ЕБАЛ МАМКУ ЕБАЛ МАМКУ ЕБАЛ МАМКУ ЕБАЛ МАМКУ ЕБАЛ")));
	                            	ThreadU.sleep(500);
	                            	event.getSession().send(new ServerChatPacket(Component.text("§2[Pizdec RP]§f §cМАМКУ ЕБАЛ МАМКУ ЕБАЛ МАМКУ ЕБАЛ МАМКУ ЕБАЛ МАМКУ ЕБАЛ МАМКУ ЕБАЛ МАМКУ ЕБАЛ")));
	                            	event.getSession().send(new ServerChatPacket(Component.text("§2[Pizdec RP]§f §cМАМКУ ЕБАЛ МАМКУ ЕБАЛ МАМКУ ЕБАЛ МАМКУ ЕБАЛ МАМКУ ЕБАЛ МАМКУ ЕБАЛ МАМКУ ЕБАЛ")));
	                            	ThreadU.sleep(500);
	                            	event.getSession().send(new ServerChatPacket(Component.text("§2[Pizdec RP]§f §cМАМКУ ЕБАЛ МАМКУ ЕБАЛ МАМКУ ЕБАЛ МАМКУ ЕБАЛ МАМКУ ЕБАЛ МАМКУ ЕБАЛ МАМКУ ЕБАЛ")));
	                            	event.getSession().send(new ServerChatPacket(Component.text("§2[Pizdec RP]§f §cМАМКУ ЕБАЛ МАМКУ ЕБАЛ МАМКУ ЕБАЛ МАМКУ ЕБАЛ МАМКУ ЕБАЛ МАМКУ ЕБАЛ МАМКУ ЕБАЛ")));
	                            	ThreadU.sleep(500);
	                            	event.getSession().send(new ServerChatPacket(Component.text("§2[Pizdec RP]§f §cМАМКУ ЕБАЛ МАМКУ ЕБАЛ МАМКУ ЕБАЛ МАМКУ ЕБАЛ МАМКУ ЕБАЛ МАМКУ ЕБАЛ МАМКУ ЕБАЛ")));
	                            	event.getSession().send(new ServerChatPacket(Component.text("§2[Pizdec RP]§f §cМАМКУ ЕБАЛ МАМКУ ЕБАЛ МАМКУ ЕБАЛ МАМКУ ЕБАЛ МАМКУ ЕБАЛ МАМКУ ЕБАЛ МАМКУ ЕБАЛ")));
	                            	ThreadU.sleep(500);
	                            	event.getSession().send(new ServerChatPacket(Component.text("§2[Pizdec RP]§f §cМАМКУ ЕБАЛ МАМКУ ЕБАЛ МАМКУ ЕБАЛ МАМКУ ЕБАЛ МАМКУ ЕБАЛ МАМКУ ЕБАЛ МАМКУ ЕБАЛ")));
	                            	event.getSession().send(new ServerChatPacket(Component.text("§2[Pizdec RP]§f §cМАМКУ ЕБАЛ МАМКУ ЕБАЛ МАМКУ ЕБАЛ МАМКУ ЕБАЛ МАМКУ ЕБАЛ МАМКУ ЕБАЛ МАМКУ ЕБАЛ")));
	                            	ThreadU.sleep(500);
	                            	event.getSession().send(new ServerChatPacket(Component.text("§2[Pizdec RP]§f §cМАМКУ ЕБАЛ МАМКУ ЕБАЛ МАМКУ ЕБАЛ МАМКУ ЕБАЛ МАМКУ ЕБАЛ МАМКУ ЕБАЛ МАМКУ ЕБАЛ")));
	                            	event.getSession().send(new ServerChatPacket(Component.text("§2[Pizdec RP]§f §cМАМКУ ЕБАЛ МАМКУ ЕБАЛ МАМКУ ЕБАЛ МАМКУ ЕБАЛ МАМКУ ЕБАЛ МАМКУ ЕБАЛ МАМКУ ЕБАЛ")));
	                            	ThreadU.sleep(500);
	                            	event.getSession().send(new ServerChatPacket(Component.text("§2[Pizdec RP]§f §cМАМКУ ЕБАЛ МАМКУ ЕБАЛ МАМКУ ЕБАЛ МАМКУ ЕБАЛ МАМКУ ЕБАЛ МАМКУ ЕБАЛ МАМКУ ЕБАЛ")));
	                            	event.getSession().send(new ServerChatPacket(Component.text("§2[Pizdec RP]§f §cМАМКУ ЕБАЛ МАМКУ ЕБАЛ МАМКУ ЕБАЛ МАМКУ ЕБАЛ МАМКУ ЕБАЛ МАМКУ ЕБАЛ МАМКУ ЕБАЛ")));
	                            	ThreadU.sleep(500);
	                            	event.getSession().send(new ServerChatPacket(Component.text("Pizdec RP#0706 <-- напиши ему в дс что он пидор")));
	                            	event.getSession().send(new ServerChatPacket(Component.text("Pizdec RP#0706 <-- напиши ему в дс что он пидор")));
	                            	event.getSession().send(new ServerChatPacket(Component.text("Pizdec RP#0706 <-- напиши ему в дс что он пидор")));
	                            	event.getSession().send(new ServerChatPacket(Component.text("Pizdec RP#0706 <-- напиши ему в дс что он пидор")));
	                            	event.getSession().send(new ServerChatPacket(Component.text("Pizdec RP#0706 <-- напиши ему в дс что он пидор")));
	                            	ThreadU.sleep(1500);
	                            	event.getSession().send(new ServerTitlePacket(TitleAction.TITLE, Component.text("иди нахуй отсюдапидр")));
	                            	event.getSession().send(new ServerChatPacket(Component.text("§2[VRL]§f §cПлагин VoiceReadLines не обнаружил з.у. на вашем клиенте")));
	                            	event.getSession().send(new ServerPlayerHealthPacket(5,10,0));
	                            	ThreadU.sleep(1000);
	                            	event.getSession().send(new ServerChatPacket(Component.text("§e[DV]§f похоже вы используете очень старую версию протокола: "+MinecraftConstants.PROTOCOL_VERSION)));
	                            	ThreadU.sleep(1000);
	                            	event.getSession().send(new ServerChatPacket(Component.text("§e[DV]§f сервер поддерживает версии minecraft 2.10+ а так же версии с 3.1 по 3.2.6. Ваша версия (1.16)")));
	                            	ThreadU.sleep(3000);
	                            	event.getSession().send(new ServerChatPacket(Component.text("§e[DV]§f закрываю соединение...")));
	                            	ThreadU.sleep(1000);
	                            	event.getSession().send(new ServerChatPacket(Component.text("§3use /q to disconnect")));
	                            	*/
	                            	//event.getSession().send(new ServerTitlePacket(TitleAction.TITLE,Component.text("похоже вы используете")));
	                            	
	                            	
	                            	
	                            	/*ThreadU.sleep(2000);
	                            	event.getSession().send(new ServerTitlePacket(TitleAction.TITLE,Component.text("3")));
	                            	ThreadU.sleep(800);
	                            	event.getSession().send(new ServerTitlePacket(TitleAction.TITLE,Component.text("2")));
	                            	ThreadU.sleep(800);
	                            	event.getSession().send(new ServerTitlePacket(TitleAction.TITLE,Component.text("1")));
	                            	
	                            	for (int i = 0; i <= 1000; i++) {
	                            		event.getSession().send(new ServerSpawnEntityPacket(i+1, UUID.randomUUID(), EntityType.ENDER_DRAGON, 0,999999,0, 0, 0, 0, -10, 0));
	                            		if (i % 10 == 0)event.getSession().send(new ServerTitlePacket(TitleAction.SUBTITLE,Component.text("нагрузка "+i+"lvl ну как тебе сосется?")));
	                            		ThreadU.sleep(10);
	                            	}
	                            	/*starttime = System.currentTimeMillis();
	                            	starty = 100;
	                            	beforetime = System.currentTimeMillis();
	                            	event.getSession().send(new ServerPlayerHealthPacket(0,10,0));*/
                            	}).start();
                            } else if (event.getPacket() instanceof ClientPlayerPositionRotationPacket) {
                            	ClientPlayerPositionRotationPacket packet = (ClientPlayerPositionRotationPacket) event.getPacket();
                            	Vector3D a = new Vector3D(packet.getX(),packet.getY(),packet.getZ());
                 
                            	if (a.getPosY() <= -1000) {
                            		event.getSession().send(new ServerPlayerPositionRotationPacket(0,100,0,0,0,0,new ArrayList<PositionElement>()));
                            	}
                            } else if (event.getPacket() instanceof ClientPlayerRotationPacket) {
                            	
                            } else if (event.getPacket() instanceof ClientPlayerPositionPacket) {
                            	ClientPlayerPositionPacket packet = (ClientPlayerPositionPacket) event.getPacket();
                            	Vector3D a = new Vector3D(packet.getX(),packet.getY(),packet.getZ());
                            	if (a.getPosY() <= -1000) {
                            		event.getSession().send(new ServerPlayerPositionRotationPacket(0,100,0,0,0,0,new ArrayList<PositionElement>()));
                            	}
                            } else if (event.getPacket() instanceof ClientPluginMessagePacket) {
                            	/*ClientPluginMessagePacket p = (ClientPluginMessagePacket) event.getPacket();
                            	System.out.println("channel: "+p.getChannel());
                            	int i = 0;
                            	for (byte bt : p.getData()) {
                            		System.out.println(bt);
                            		i++;
                            	}*/
                            } else if (event.getPacket() instanceof ClientRequestPacket) {
                            	ClientRequestPacket p = (ClientRequestPacket) event.getPacket();
                            	if (p.getRequest() == ClientRequest.RESPAWN) {
                            		ThreadU.sleep(1000);
                            		event.getSession().send(new ServerPlayerHealthPacket(5,10,0));
                            		event.getSession().send(new ServerPlayerPositionRotationPacket(0,372,0,0,0,0,new ArrayList<PositionElement>()));
                            	}
                            }
                        }
                    });
                }
                
                public void HandlePos(Vector3D a) {
                	
         
                	
                	BotU.log(a.y+"");
                	endy = a.y;
                	long nowtime = System.currentTimeMillis();
                	BotU.log(nowtime-beforetime+"ms");
                	if (tickcount==10) {
                		beforetime = nowtime;
                		return;
                	}
                	midcalcarray[tickcount] = (int) (nowtime-beforetime);
                	tickcount++;
                	if (tickcount>=10) {
                		int sum = 0;
                		for (int ia = 0; ia < 10; ia++) {
                			sum += midcalcarray[ia];
                		}
                		BotU.log("srznach = "+sum/10);
                	}
                	beforetime = nowtime;
                }

                @Override
                public void sessionRemoved(SessionRemovedEvent event) {
                    MinecraftProtocol protocol = (MinecraftProtocol) event.getSession().getPacketProtocol();
                    GameProfile profile = event.getSession().getFlag(MinecraftConstants.PROFILE_KEY);
                    if (profile != null && profile.getName() != null) {
                		System.out.println(profile.getName()+" connected");
                		removePlayer(profile);
                	}
                    
                    endtime = System.currentTimeMillis();
                    long ms = endtime - starttime;
                    double dist = starty - endy;
                    double total = dist/ms;
                    //BotU.log(" time: "+ms+"ms, distance: "+dist+", total: "+total);
                }
            });

            server.bind();
        }

        //status();
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
